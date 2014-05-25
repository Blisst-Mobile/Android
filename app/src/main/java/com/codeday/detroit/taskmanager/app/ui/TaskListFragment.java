package com.codeday.detroit.taskmanager.app.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.codeday.detroit.taskmanager.app.*;
import com.codeday.detroit.taskmanager.app.adapters.TaskListAdapter;
import com.codeday.detroit.taskmanager.app.dao.DatabaseAccessor;
import com.codeday.detroit.taskmanager.app.domain.Task;
import com.codeday.detroit.taskmanager.app.domain.TaskList;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends BaseFragment {

    public static String TAG = "TaskListFragment";

    private View rootView;

    private AlertDialog dialog;
    private ListView list;
    private TaskListAdapter adapter;
    private List<TaskList> taskLists;

    public static TaskListFragment getInstance() {
        TaskListFragment frag = new TaskListFragment();
        return frag;
    }

    public TaskListFragment() {
        menuInteractionListener = new MainActivity.MenuInteractionListener() {
            @Override
            public void onAddButtonPressed() {
                CDLog.debugLog(TAG, "Add Button Pressed!");
                showNewListDialog();
            }

            @Override
            public boolean onBackButtonPressed() {
                return false;
            }

            @Override
            public boolean onMenuUpPressed() {
                return false;
            }
        };
    }

    public MainActivity.MenuInteractionListener getMenuInteractionListener() {
        return menuInteractionListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_task_list, container, false);

        list = (ListView) rootView.findViewById(R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                TasksFragment frag = TasksFragment.getInstance(taskLists.get(position).identifier);
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                transaction.hide(TaskListFragment.this);
                transaction.add(R.id.container, frag, TasksFragment.TAG);
                transaction.commit();
            }
        });

        return rootView;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation anim = super.onCreateAnimation(transit, enter, nextAnim);

        if ( enter )
            new RetrieveListsTask().execute();

        return anim;
    }

    @Override
    public void onStart() {
        super.onStart();
        if ( taskLists == null ) {
            taskLists = new ArrayList<TaskList>();
            adapter = new TaskListAdapter(taskLists, getActivity());
            list.setAdapter(adapter);


            SwipeDismissList.OnDismissCallback callback = new SwipeDismissList.OnDismissCallback(){

                @Override
                public SwipeDismissList.Undoable onDismiss(AbsListView listView, final int position) {
                    final TaskList taskList = taskLists.get(position);
                    taskLists.remove(position);
                    adapter.notifyDataSetChanged();

                    return new SwipeDismissList.Undoable() {

                        //called after undo click
                        public void undo() {

                            // Return the item at its previous position again
                            taskLists.add(position, taskList);
                            adapter.notifyDataSetChanged();
                        }
                        //called after toast goes away
                        public void discard(){
                            new DeleteListTask().execute(taskList.identifier);

                        }

                    };
                }
            };

            SwipeDismissList.UndoMode mode = SwipeDismissList.UndoMode.SINGLE_UNDO;
            SwipeDismissList swipeList = new SwipeDismissList(list, callback, mode);
            swipeList.setAutoHideDelay(10);






            new RetrieveListsTask().execute();
        }
        if ( dialog == null )
            createNewListDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void createNewListDialog() {
        AlertDialog.Builder builder;
        View layout = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_list, null);

        final EditText listName = (EditText) layout.findViewById(R.id.name);
        final TextView cancelButton = (TextView) layout.findViewById(R.id.cancel);
        final TextView saveButton = (TextView) layout.findViewById(R.id.save);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(listName.getWindowToken(), 0);
                }
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = listName.getText().toString();

                if ( name != null && name.length() > 0 ) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(listName.getWindowToken(), 0);
                    }
                    dialog.dismiss();

                    TaskList taskList = new TaskList();
                    taskList.name = name;
                    taskList.numberOfTasks = 0;
                    taskList.numberOfCompletedTasks = 0;
                    taskList.isComplete = false;

                    new AddListToDatabaseTask().execute(new TaskList[] { taskList });

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.list_name_not_valid), Toast.LENGTH_SHORT).show();
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
                    inputMethodManager.showSoftInput(listName, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                listName.setText("");
            }
        });
    }

    private void showNewListDialog() {
        if ( dialog != null ) {
            dialog.show();
        }
    }

    private class AddListToDatabaseTask extends AsyncTask<TaskList, Void, Boolean> {

        @Override
        protected Boolean doInBackground(TaskList... params) {
            if ( params.length > 0 )
                return new DatabaseAccessor().addList(params[0]);
            else
                return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (!aBoolean)
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.error_list_database), Toast.LENGTH_SHORT).show();
            else
                new RetrieveListsTask().execute();
        }
    }

    private class RetrieveListsTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            taskLists.clear();
            List<TaskList> result = new DatabaseAccessor().getAllLists();
            if ( result != null ) {
                taskLists.addAll(result);
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (!aBoolean)
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.error_retrieving_lists), Toast.LENGTH_SHORT).show();
            else {
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity().getApplicationContext(), "Number of lists: " + taskLists.size(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class DeleteListTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            DatabaseAccessor databaseAccessor = new DatabaseAccessor();
            boolean result = databaseAccessor.deleteList(params[0]);
            result = databaseAccessor.deleteAllTasksForList(params[0]) && result;
            return result;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (!aBoolean)
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.error_retrieving_lists), Toast.LENGTH_SHORT).show();

        }
    }
}
