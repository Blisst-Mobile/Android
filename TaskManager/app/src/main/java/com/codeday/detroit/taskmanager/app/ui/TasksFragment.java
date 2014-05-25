package com.codeday.detroit.taskmanager.app.ui;



import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codeday.detroit.taskmanager.app.CDLog;
import com.codeday.detroit.taskmanager.app.MainActivity;
import com.codeday.detroit.taskmanager.app.R;
import com.codeday.detroit.taskmanager.app.adapters.TaskAdapter;
import com.codeday.detroit.taskmanager.app.dao.DatabaseAccessor;
import com.codeday.detroit.taskmanager.app.domain.Task;
import com.codeday.detroit.taskmanager.app.domain.TaskList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 *
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
                showNewTaskDialog();
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

        return  rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if ( tasks == null ) {
            tasks = new ArrayList<Task>();
            adapter = new TaskAdapter(tasks, getActivity());
            adapter.setCheckChangedListener(new TaskAdapter.CheckChangedListener() {
                @Override
                public void OnCheckChanged(boolean isChecked, int position) {
                    Task task = tasks.get(position);
                    task.isComplete = isChecked;
                    new UpdateTaskInDatabaseTask().execute(new Task[] { task });
                }
            });
            list.setAdapter(adapter);
            new RetrieveTasksTask().execute(parentIdentifier);
        } if ( dialog == null )
            createNewTaskDialog();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation anim = super.onCreateAnimation(transit, enter, nextAnim);

        if ( enter ) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        } else
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);

        return anim;
    }

    public void createNewTaskDialog() {
        AlertDialog.Builder builder;
        View layout = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_task, null);

        final EditText taskName = (EditText) layout.findViewById(R.id.name);
        final Button date = (Button) layout.findViewById(R.id.date);
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
        for ( int i = 0; i < 12; i++ ) {
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

        date.setHint("  " + getResources().getString(R.string.date_hint));
        date.setOnClickListener(new View.OnClickListener() {
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
                    numPickerAlpha.setDuration(defaultAnimTime * 2);
                    numPickerAlpha.setInterpolator(new DecelerateInterpolator());

                    ObjectAnimator dateAlpha = ObjectAnimator.ofFloat(date, View.ALPHA, 1f, 0f);
                    ObjectAnimator taskAlpha = ObjectAnimator.ofFloat(taskName, View.ALPHA, 1f, 0f);

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
                if ( isDatePickerDisplayed ) {

                    isDatePickerDisplayed = false;

                    saveButton.setText(getResources().getString(R.string.save));

                    int defaultAnimTime = getResources().getInteger(R.integer.default_anim_time);

                    ObjectAnimator dateAlpha = ObjectAnimator.ofFloat(date, View.ALPHA, 0f, 1f);
                    ObjectAnimator taskAlpha = ObjectAnimator.ofFloat(taskName, View.ALPHA, 0f, 1f);

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
                            lp.addRule(RelativeLayout.BELOW, date.getId());
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
                        inputMethodManager.hideSoftInputFromWindow(taskName.getWindowToken(), 0);
                    }
                    dialog.dismiss();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( isDatePickerDisplayed ) {

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
                    date.setText("  " + today.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) +" " + day + ", " + year);
                    isDatePickerDisplayed = false;

                    saveButton.setText(getResources().getString(R.string.save));

                    int defaultAnimTime = getResources().getInteger(R.integer.default_anim_time);

                    ObjectAnimator dateAlpha = ObjectAnimator.ofFloat(date, View.ALPHA, 0f, 1f);
                    ObjectAnimator taskAlpha = ObjectAnimator.ofFloat(taskName, View.ALPHA, 0f, 1f);

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
                            lp.addRule(RelativeLayout.BELOW, date.getId());
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
                    String name = taskName.getText().toString();

                    if (name != null && name.length() > 0) {

                        if ( chosenDate != null ) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (inputMethodManager != null) {
                                inputMethodManager.hideSoftInputFromWindow(taskName.getWindowToken(), 0);
                            }
                            dialog.dismiss();

                            Task task = new Task();
                            task.name = name;
                            task.date = chosenDate;
                            task.parent = parentIdentifier;

                            new AddTaskToDatabaseTask().execute(new Task[]{task});
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
                    inputMethodManager.showSoftInput(taskName, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                taskName.setText("");
                date.setText("");

                if ( isDatePickerDisplayed ) {

                    isDatePickerDisplayed = false;

                    numberPickerLayout.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) buttonLayout.getLayoutParams();
                    lp.addRule(RelativeLayout.BELOW, date.getId());
                    buttonLayout.requestLayout();

                    taskName.setAlpha(1.0f);
                    date.setAlpha(1.0f);
                }

                Calendar today = Calendar.getInstance();
                monthPicker.setValue(today.get(Calendar.MONTH));
                dayPicker.setValue(today.get(Calendar.DAY_OF_MONTH));
                yearPicker.setValue(today.get(Calendar.YEAR));
            }
        });
    }

    private void showNewTaskDialog() {
        if ( dialog != null ) {
            dialog.show();
        }
    }

    private class AddTaskToDatabaseTask extends AsyncTask<Task, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Task... params) {
            if ( params != null && params.length > 0 ) {
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
            if (!aBoolean)
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.error_list_database), Toast.LENGTH_SHORT).show();
            else
                new RetrieveTasksTask().execute(parentIdentifier);
        }
    }

    private class UpdateTaskInDatabaseTask extends AsyncTask<Task, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Task... params) {
            DatabaseAccessor accessor = new DatabaseAccessor();
            boolean result = accessor.updateTask(params[0]);
            TaskList list = accessor.getList(params[0].parent);
            if ( params[0].isComplete )
                list.numberOfCompletedTasks++;
            else
                list.numberOfCompletedTasks--;
            result = accessor.updateList(list) && result;
            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (!aBoolean)
                Toast.makeText(getActivity().getApplicationContext(), "Error updating task and list", Toast.LENGTH_SHORT).show();
        }
    }

    private class RetrieveTasksTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            tasks.clear();
            List<Task> result = new DatabaseAccessor().getTasksForList(params[0]);
            if ( result != null ) {
                tasks.addAll(result);
                return true;
            } else
                return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (!aBoolean)
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.error_retrieving_tasks), Toast.LENGTH_SHORT).show();
            else {
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity().getApplicationContext(), "Number of lists: " + tasks.size(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
