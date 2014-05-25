package com.codeday.detroit.taskmanager.app.ui;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;

import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.*;


import com.codeday.detroit.taskmanager.app.CDLog;
import com.codeday.detroit.taskmanager.app.MainActivity;
import com.codeday.detroit.taskmanager.app.R;

import com.codeday.detroit.taskmanager.app.adapters.NavSortAdapter;

import com.codeday.detroit.taskmanager.app.SwipeDismissList;

import com.codeday.detroit.taskmanager.app.adapters.TaskAdapter;
import com.codeday.detroit.taskmanager.app.dao.DatabaseAccessor;
import com.codeday.detroit.taskmanager.app.domain.Task;
import com.codeday.detroit.taskmanager.app.domain.TaskList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class TasksFragment extends BaseFragment {

    public static String TAG = "TasksFragment";

    private View rootView;

    private String parentIdentifier;

    private AlertDialog dialog;
    private boolean isDatePickerDisplayed = false;
    private Calendar chosenDate;
    private ListView list;
    private TaskAdapter adapter;
    private List<Task> tasks;
    private TaskList taskList;

    private EditText taskNameField;
    private Button taskDateField;
    private boolean isEditingTask;
    private int clickedPosition;

    public static TasksFragment getInstance(String identifier) {
        TasksFragment frag = new TasksFragment();
        frag.parentIdentifier = identifier;
        return frag;
    }

    public TasksFragment() {
        menuInteractionListener = new MainActivity.MenuInteractionListener() {
            @Override
            public void onAddButtonPressed() {
                CDLog.debugLog(TAG, "Add Button Pressed!");
                showNewTaskDialog(null, null);
            }

            @Override
            public boolean onBackButtonPressed() {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                TaskListFragment frag = (TaskListFragment) getActivity().getSupportFragmentManager().findFragmentByTag(TaskListFragment.TAG);
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                transaction.remove(TasksFragment.this);
                transaction.show(frag);
                transaction.commit();
                return true;
            }

            @Override
            public boolean onMenuUpPressed() {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                TaskListFragment frag = (TaskListFragment) getActivity().getSupportFragmentManager().findFragmentByTag(TaskListFragment.TAG);
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                transaction.remove(TasksFragment.this);
                transaction.show(frag);
                transaction.commit();
                return true;
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_tasks, container, false);

        list = (ListView) rootView.findViewById(R.id.list);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (tasks == null) {

            tasks = new ArrayList<Task>();
            adapter = new TaskAdapter(tasks, getActivity());
            adapter.setCheckChangedListener(new TaskAdapter.CheckChangedListener() {
                @Override
                public void OnCheckChanged(boolean isChecked, int position) {
                    Task task = tasks.get(position);
                    task.isComplete = isChecked;
                    new UpdateTaskInDatabaseTask().execute(new Task[]{task});
                }
            });
            list.setAdapter(adapter);

            SwipeDismissList.OnDismissCallback callback = new SwipeDismissList.OnDismissCallback() {

                @Override
                public SwipeDismissList.Undoable onDismiss(AbsListView listView, final int position) {
                    final Task task = tasks.get(position);
                    tasks.remove(position);
                    adapter.notifyDataSetChanged();

                    return new SwipeDismissList.Undoable() {

                        //called after undo click
                        public void undo() {
                            tasks.add(position, task);
                            adapter.notifyDataSetChanged();
                        }

                        //called after toast goes away
                        public void discard() {

                            new DeleteTask().execute(new Task[]{task});
                        }


                    };

                }
            };

            SwipeDismissList.UndoMode mode = SwipeDismissList.UndoMode.SINGLE_UNDO;
            SwipeDismissList swipeList = new SwipeDismissList(list, callback, mode, "Task Deleted");
            swipeList.setAutoHideDelay(1500);



            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    isEditingTask = true;
                    clickedPosition = position;
                    Task task = tasks.get(position);
                    showNewTaskDialog(task.name, task.date);
                    return true;
                }
            });
            new RetrieveTasksTask().execute(parentIdentifier);
        }
        if (dialog == null)
            createNewTaskDialog();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation anim = super.onCreateAnimation(transit, enter, nextAnim);
        ActionBar actionBar = getActivity().getActionBar();

        if (enter) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            final ArrayList<String> sortByItems = new ArrayList<String>();
            sortByItems.add("Date");
            sortByItems.add("Name");

            NavSortAdapter spinnerAdapter = new NavSortAdapter(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, sortByItems);
            actionBar.setListNavigationCallbacks(spinnerAdapter, new ActionBar.OnNavigationListener() {
                @Override
                public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                    switch (itemPosition) {
                        case 0: // Date
                            Collections.sort(tasks);
                            adapter.notifyDataSetChanged();
                            return true;
                        case 1: // Name
                            Collections.sort(tasks, new Comparator<Task>() {
                                @Override
                                public int compare(Task task1, Task task2) {
                                    return task1.name.compareToIgnoreCase(task2.name);
                                }
                            });
                            adapter.notifyDataSetChanged();
                            return true;
                    }

                    return false;
                }
            });
        } else {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        return anim;
    }

    public void createNewTaskDialog() {
        AlertDialog.Builder builder;
        View layout = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_task, null);

        taskNameField = (EditText) layout.findViewById(R.id.name);
        taskDateField  = (Button) layout.findViewById(R.id.date);
        final LinearLayout buttonLayout = (LinearLayout) layout.findViewById(R.id.button_layout);
        final TextView cancelButton = (TextView) layout.findViewById(R.id.cancel);
        final TextView saveButton = (TextView) layout.findViewById(R.id.save);

        final LinearLayout numberPickerLayout = (LinearLayout) layout.findViewById(R.id.number_picker);
        final NumberPicker monthPicker = (NumberPicker) layout.findViewById(R.id.num_picker_month);
        final NumberPicker dayPicker = (NumberPicker) layout.findViewById(R.id.num_picker_day);
        final NumberPicker yearPicker = (NumberPicker) layout.findViewById(R.id.num_picker_year);

        final Calendar today = Calendar.getInstance();

        monthPicker.setMinValue(today.getActualMinimum(Calendar.MONTH));
        monthPicker.setMaxValue(today.getActualMaximum(Calendar.MONTH));
        int thisMonth = today.get(Calendar.MONTH);
        String[] displayValues = new String[12];
        for (int i = 0; i < 12; i++) {
            today.set(Calendar.MONTH, i);
            displayValues[i] = today.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
        }
        today.set(Calendar.MONTH, thisMonth);
        monthPicker.setDisplayedValues(displayValues);
        monthPicker.setWrapSelectorWheel(false);
        monthPicker.setValue(today.get(Calendar.MONTH));

        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                today.set(Calendar.MONTH, newVal);
                dayPicker.setMinValue(today.getActualMinimum(Calendar.DAY_OF_MONTH));
                dayPicker.setMaxValue(today.getActualMaximum(Calendar.DAY_OF_MONTH));
                dayPicker.setWrapSelectorWheel(false);
            }
        });

        dayPicker.setMinValue(today.getActualMinimum(Calendar.DAY_OF_MONTH));
        dayPicker.setMaxValue(today.getActualMaximum(Calendar.DAY_OF_MONTH));
        dayPicker.setWrapSelectorWheel(false);
        dayPicker.setValue(today.get(Calendar.DAY_OF_MONTH));

        yearPicker.setMinValue(today.get(Calendar.YEAR));
        yearPicker.setMaxValue(today.get(Calendar.YEAR) + 100);
        yearPicker.setWrapSelectorWheel(false);
        yearPicker.setValue(today.get(Calendar.YEAR));

        taskDateField.setHint("  " + getResources().getString(R.string.date_hint));
        taskDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isDatePickerDisplayed) {
                    isDatePickerDisplayed = true;

                    saveButton.setText(getResources().getString(R.string.set));

                    int defaultAnimTime = getResources().getInteger(R.integer.default_anim_time);

                    ObjectAnimator monthAlpha = ObjectAnimator.ofFloat(monthPicker, View.ALPHA, 0f, 1f);
                    ObjectAnimator dayAlpha = ObjectAnimator.ofFloat(dayPicker, View.ALPHA, 0f, 1f);
                    ObjectAnimator yearAlpha = ObjectAnimator.ofFloat(yearPicker, View.ALPHA, 0f, 1f);
                    final AnimatorSet numPickerAlpha = new AnimatorSet();
                    numPickerAlpha.playTogether(monthAlpha, dayAlpha, yearAlpha);
                    numPickerAlpha.setDuration(defaultAnimTime);
                    numPickerAlpha.setInterpolator(new DecelerateInterpolator());

                    ObjectAnimator dateAlpha = ObjectAnimator.ofFloat(taskDateField, View.ALPHA, 1f, 0f);
                    ObjectAnimator taskAlpha = ObjectAnimator.ofFloat(taskNameField, View.ALPHA, 1f, 0f);

                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(dateAlpha, taskAlpha);
                    set.setDuration(defaultAnimTime);
                    set.setInterpolator(new DecelerateInterpolator());
                    set.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                            numPickerAlpha.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    numberPickerLayout.setVisibility(View.VISIBLE);
                                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) buttonLayout.getLayoutParams();
                                    lp.addRule(RelativeLayout.BELOW, numberPickerLayout.getId());
                                    buttonLayout.requestLayout();

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                            numPickerAlpha.start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    set.start();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDatePickerDisplayed) {

                    isDatePickerDisplayed = false;

                    saveButton.setText(getResources().getString(R.string.save));

                    int defaultAnimTime = getResources().getInteger(R.integer.default_anim_time);

                    ObjectAnimator dateAlpha = ObjectAnimator.ofFloat(taskDateField, View.ALPHA, 0f, 1f);
                    ObjectAnimator taskAlpha = ObjectAnimator.ofFloat(taskNameField, View.ALPHA, 0f, 1f);

                    final AnimatorSet dateTaskSet = new AnimatorSet();
                    dateTaskSet.playTogether(dateAlpha, taskAlpha);
                    dateTaskSet.setDuration(defaultAnimTime);
                    dateTaskSet.setInterpolator(new DecelerateInterpolator());

                    ObjectAnimator monthAlpha = ObjectAnimator.ofFloat(monthPicker, View.ALPHA, 1f, 0f);
                    ObjectAnimator dayAlpha = ObjectAnimator.ofFloat(dayPicker, View.ALPHA, 1f, 0f);
                    ObjectAnimator yearAlpha = ObjectAnimator.ofFloat(yearPicker, View.ALPHA, 1f, 0f);

                    AnimatorSet numPickerAlpha = new AnimatorSet();
                    numPickerAlpha.playTogether(monthAlpha, dayAlpha, yearAlpha);
                    numPickerAlpha.setDuration(defaultAnimTime);
                    numPickerAlpha.setInterpolator(new DecelerateInterpolator());

                    numPickerAlpha.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            numberPickerLayout.setVisibility(View.GONE);
                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) buttonLayout.getLayoutParams();
                            lp.addRule(RelativeLayout.BELOW, taskDateField.getId());
                            buttonLayout.requestLayout();
                            dateTaskSet.start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    numPickerAlpha.start();

                } else {
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(taskNameField.getWindowToken(), 0);
                    }
                    dialog.dismiss();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isDatePickerDisplayed) {

                    int month = monthPicker.getValue();
                    int day = dayPicker.getValue();
                    int year = yearPicker.getValue();

                    chosenDate = Calendar.getInstance();
                    chosenDate.set(Calendar.MONTH, month);
                    chosenDate.set(Calendar.DAY_OF_MONTH, day);
                    chosenDate.set(Calendar.YEAR, year);
                    chosenDate.set(Calendar.MINUTE, 0);
                    chosenDate.set(Calendar.SECOND, 0);
                    chosenDate.set(Calendar.MILLISECOND, 0);

                    today.set(Calendar.MONTH, month);
                    taskDateField.setText("  " + today.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) +" " + day + ", " + year);
                    isDatePickerDisplayed = false;

                    saveButton.setText(getResources().getString(R.string.save));

                    int defaultAnimTime = getResources().getInteger(R.integer.default_anim_time);

                    ObjectAnimator dateAlpha = ObjectAnimator.ofFloat(taskDateField, View.ALPHA, 0f, 1f);
                    ObjectAnimator taskAlpha = ObjectAnimator.ofFloat(taskNameField, View.ALPHA, 0f, 1f);

                    final AnimatorSet dateTaskSet = new AnimatorSet();
                    dateTaskSet.playTogether(dateAlpha, taskAlpha);
                    dateTaskSet.setDuration(defaultAnimTime);
                    dateTaskSet.setInterpolator(new DecelerateInterpolator());

                    ObjectAnimator monthAlpha = ObjectAnimator.ofFloat(monthPicker, View.ALPHA, 1f, 0f);
                    ObjectAnimator dayAlpha = ObjectAnimator.ofFloat(dayPicker, View.ALPHA, 1f, 0f);
                    ObjectAnimator yearAlpha = ObjectAnimator.ofFloat(yearPicker, View.ALPHA, 1f, 0f);

                    AnimatorSet numPickerAlpha = new AnimatorSet();
                    numPickerAlpha.playTogether(monthAlpha, dayAlpha, yearAlpha);
                    numPickerAlpha.setDuration(defaultAnimTime);
                    numPickerAlpha.setInterpolator(new DecelerateInterpolator());

                    numPickerAlpha.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            numberPickerLayout.setVisibility(View.GONE);
                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) buttonLayout.getLayoutParams();
                            lp.addRule(RelativeLayout.BELOW, taskDateField.getId());
                            buttonLayout.requestLayout();
                            dateTaskSet.start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    numPickerAlpha.start();

                } else {
                    String name = taskNameField.getText().toString();

                    if (name != null && name.length() > 0) {

                        if (chosenDate != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (inputMethodManager != null) {
                                inputMethodManager.hideSoftInputFromWindow(taskNameField.getWindowToken(), 0);
                            }
                            dialog.dismiss();

                            if ( isEditingTask ) {

                                Task task = tasks.get(clickedPosition);
                                task.name = name;
                                task.date = chosenDate;

                                new UpdateTaskInDatabaseTask().execute(task);

                            } else {
                                Task task = new Task();
                                task.name = name;
                                task.date = chosenDate;
                                task.parent = parentIdentifier;

                                new AddTaskToDatabaseTask().execute(new Task[]{task});
                            }
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.task_date_not_valid), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.task_name_not_valid), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);
        dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.showSoftInput(taskNameField, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                taskNameField.setText("");
                taskDateField.setText("");

                if (isDatePickerDisplayed) {

                    isDatePickerDisplayed = false;

                    numberPickerLayout.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) buttonLayout.getLayoutParams();
                    lp.addRule(RelativeLayout.BELOW, taskDateField.getId());
                    buttonLayout.requestLayout();

                    taskNameField.setAlpha(1.0f);
                    taskDateField.setAlpha(1.0f);
                }

                Calendar today = Calendar.getInstance();
                monthPicker.setValue(today.get(Calendar.MONTH));
                dayPicker.setValue(today.get(Calendar.DAY_OF_MONTH));
                yearPicker.setValue(today.get(Calendar.YEAR));
            }
        });
    }

    private void showNewTaskDialog(String name, Calendar date) {
        if ( dialog != null ) {

            if ( name != null && date != null ) {
                taskNameField.setText(name);
                taskDateField.setText("  " + date.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " + date.get(Calendar.DAY_OF_MONTH) + ", " + date.get(Calendar.YEAR));
                if ( chosenDate == null ) {
                    chosenDate = Calendar.getInstance();
                    chosenDate.setTime(date.getTime());
                }
            }

            dialog.show();
        }
    }

    private class AddTaskToDatabaseTask extends AsyncTask<Task, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Task... params) {
            if (params != null && params.length > 0) {
                DatabaseAccessor databaseAccessor = new DatabaseAccessor();
                boolean result = databaseAccessor.addTask(params[0]);
                TaskList list = databaseAccessor.getList(params[0].parent);
                list.numberOfTasks++;
                result = databaseAccessor.updateList(list) && result;
                return result;
            } else
                return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) new RetrieveTasksTask().execute(parentIdentifier);
        }
    }

    private class UpdateTaskInDatabaseTask extends AsyncTask<Task, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Task... params) {
            DatabaseAccessor accessor = new DatabaseAccessor();
            boolean result = accessor.updateTask(params[0]);
            if ( !isEditingTask) {
                TaskList list = accessor.getList(params[0].parent);
                if (params[0].isComplete)
                    list.numberOfCompletedTasks++;
                else
                    list.numberOfCompletedTasks--;
                result = accessor.updateList(list) && result;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if ( aBoolean && isEditingTask )
                new RetrieveTasksTask().execute(parentIdentifier);
        }
    }

    private class RetrieveTasksTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            tasks.clear();
            List<Task> result = new DatabaseAccessor().getTasksForList(params[0]);
            if (result != null) {
                tasks.addAll(result);
                return true;
            } else
                return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) adapter.notifyDataSetChanged();
        }
    }

    private class DeleteTask extends AsyncTask<Task, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Task... params) {
            DatabaseAccessor databaseAccessor = new DatabaseAccessor();
            boolean result = databaseAccessor.deleteTask(params[0].identifier);
            TaskList list = databaseAccessor.getList(params[0].parent);
            if (params[0].isComplete)
                list.numberOfCompletedTasks--;
            list.numberOfTasks--;
            result = databaseAccessor.updateList(list) && result;

            return result;
        }
    }

    private class UpdateListTask extends AsyncTask<TaskList, Void, Boolean> {

        @Override
        protected Boolean doInBackground(TaskList... params) {
            if (params.length > 0) {
                return new DatabaseAccessor().updateList(params[0]);
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }
    }


}
