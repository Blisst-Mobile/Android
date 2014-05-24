package com.codeday.detroit.taskmanager.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.codeday.detroit.taskmanager.app.domain.Task;
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
        layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        //create the cell (View) and populate it with
        //an element of the array
        if (view == null) {
            //convertView = myInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            //convertView = layoutInflater.inflate(R.layout., parent, false);
        }



        return view;
    }
}
