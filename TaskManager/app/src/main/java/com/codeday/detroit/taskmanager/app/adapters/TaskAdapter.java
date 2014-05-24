package com.codeday.detroit.taskmanager.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.codeday.detroit.taskmanager.app.R;
import com.codeday.detroit.taskmanager.app.domain.Task;

import java.util.List;

/**
 * Created by kevin on 5/24/14.
 */
public class TaskAdapter extends BaseAdapter {

    List<Task> taskList;
    Context ctxt;
    LayoutInflater layoutInflater;

    public TaskAdapter(List<Task> t, Context c) {
        taskList = t;
        ctxt = c;
        layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        //TODO: implement task view

        view = layoutInflater.inflate(R.layout.adapter_task_item, parent, false);

        return view;

    }
}
