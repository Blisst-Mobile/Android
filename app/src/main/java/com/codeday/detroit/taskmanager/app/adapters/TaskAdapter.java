package com.codeday.detroit.taskmanager.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.codeday.detroit.taskmanager.app.R;
import com.codeday.detroit.taskmanager.app.domain.Task;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by kevin on 5/24/14.
 */
public class TaskAdapter extends BaseAdapter {

    public interface CheckChangedListener {
        void OnCheckChanged(boolean isChecked, int position);
    }

    CheckChangedListener checkListener;

    List<Task> taskList;
    Context ctxt;
    LayoutInflater layoutInflater;

    SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");

    public TaskAdapter(List<Task> t, Context c) {
        taskList = t;
        ctxt = c;
    }

    public void setCheckChangedListener(CheckChangedListener listener) {
        this.checkListener = listener;
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
    public View getView(final int position, View view, ViewGroup parent) {
        if (layoutInflater == null)
            layoutInflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = layoutInflater.inflate(R.layout.adapter_task_item, parent, false);
        Task currentTask = taskList.get(position);
        TextView textView = (TextView) view.findViewById(R.id.taskName);
        textView.setText(currentTask.name);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.taskCheckbox);
        checkBox.setChecked(currentTask.isComplete);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkListener.OnCheckChanged(isChecked, position);
            }
        });

        TextView date = (TextView) view.findViewById(R.id.date);
        if (currentTask.date.getTimeInMillis() > 0)
            date.setText(sdf.format(currentTask.date.getTime()));
        else
            date.setText("N/A");

        return view;
    }
}
