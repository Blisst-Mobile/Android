package com.codeday.detroit.taskmanager.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.codeday.detroit.taskmanager.app.R;

import java.util.List;

/**
 * Created by timothymiko on 5/25/14.
 */
public class NavSortAdapter extends ArrayAdapter<String> {

    private LayoutInflater inflater;

    public NavSortAdapter(Context context, int resource, int textViewResourceId, List objects) {
        super(context, resource, textViewResourceId, objects);
        inflater = LayoutInflater.from(context);
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.sort_dropdown_item, parent, false);

        view.setBackgroundColor(getContext().getResources().getColor(R.color.white));
        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(getItem(position));
        text.setTextColor(getContext().getResources().getColor(android.R.color.black));

        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.sort_dropdown_item, parent, false);

        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(getItem(position));

        return view;
    }
}
