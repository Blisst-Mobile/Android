package com.codeday.detroit.taskmanager.app.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.codeday.detroit.taskmanager.app.CDLog;
import com.codeday.detroit.taskmanager.app.MainActivity;
import com.codeday.detroit.taskmanager.app.R;

public class TaskListFragment extends Fragment {

    public static String TAG = "TaskListFragment";

    private View rootView;

    private AlertDialog dialog;
    private ListView list;

    private MainActivity.MenuInteractionListener menuInteractionListener;

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
        };
    }

    public MainActivity.MenuInteractionListener getMenuInteractionListener() {
        return menuInteractionListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_task_list, container, false);

        list = (ListView) rootView.findViewById(R.id.list);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
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

        builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);
        dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                listName.setText("");
            }
        });
    }

    public void showNewListDialog() {
        if ( dialog != null )
            dialog.show();
    }
}
