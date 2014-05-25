package com.codeday.detroit.taskmanager.app.ui;



import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codeday.detroit.taskmanager.app.CDLog;
import com.codeday.detroit.taskmanager.app.MainActivity;
import com.codeday.detroit.taskmanager.app.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class TasksFragment extends BaseFragment {

    public static String TAG = "TasksFragment";

    private View rootView;

    public TasksFragment() {
        menuInteractionListener = new MainActivity.MenuInteractionListener() {
            @Override
            public void onAddButtonPressed() {
                CDLog.debugLog(TAG, "Add Button Pressed!");
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_tasks, container, false);

        return  rootView;
    }


}
