package com.codeday.detroit.taskmanager.app.adapters;

import android.content.Context;
import android.graphics.Point;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.codeday.detroit.taskmanager.app.R;
import com.codeday.detroit.taskmanager.app.domain.TaskList;

import java.util.List;

/**
 * Created by kevin on 5/24/14.
 */
public class TaskListAdapter extends BaseAdapter {

    List<TaskList> taskLists;
    Context ctxt;
    LayoutInflater layoutInflater;

    public TaskListAdapter(List<TaskList> t, Context c) {
        taskLists = t;
        ctxt = c;
    }

    @Override
    public int getCount() {
        return taskLists.size();
    }

    @Override
    public Object getItem(int position) {
        return taskLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            //view = layoutInflater.inflate(R.layout., parent, false);
        }
        //inflate view
        if ( layoutInflater == null )
            layoutInflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = layoutInflater.inflate(R.layout.adapter_tasklist_item, parent, false);
        TaskList currentTaskList = taskLists.get(position);
        //set list name
        TextView textViewName = (TextView) view.findViewById(R.id.taskListItemName);
        textViewName.setText(currentTaskList.name);
        //set list task amount
        TextView completedNumber = (TextView) view.findViewById(R.id.completed_tasks);
        TextView totalNumber = (TextView) view.findViewById(R.id.number_of_tasks);
        int numberOfTasksCompleted = currentTaskList.numberOfCompletedTasks;
        int numberOfTasksTotal = currentTaskList.numberOfTasks;
        completedNumber.setText(String.valueOf(numberOfTasksCompleted));
        totalNumber.setText(String.valueOf(numberOfTasksTotal));

        //get screen height and width
        WindowManager wm = (WindowManager) ctxt.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        //int screenHeight = size.y;

        //set new width for progress bar layout
        View taskProgressBar = view.findViewById(R.id.taskProgressBar);

        //rotate the line in the fraction
        View fractionSlash = view.findViewById(R.id.separator);
        fractionSlash.setRotation(-45);



        ViewGroup.LayoutParams params = taskProgressBar.getLayoutParams();


        if ( numberOfTasksTotal > 0 )
            params.width = Math.round((float) screenWidth * ((float) numberOfTasksCompleted / (float) numberOfTasksTotal));

        else
            params.width = ctxt.getResources().getDimensionPixelOffset(R.dimen.listAdapter_min_progress);

        taskProgressBar.requestLayout();

        return view;
    }
}
