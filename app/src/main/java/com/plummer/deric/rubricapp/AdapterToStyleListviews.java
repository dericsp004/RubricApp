package com.plummer.deric.rubricapp;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * Created by Sterling on 7/7/2017.
 */

public class AdapterToStyleListviews extends BaseAdapter {

    Context context;

    public AdapterToStyleListviews(Context context){
        this.context=context;
    }


    @Override
    public int getCount() {
        return assignmentNames.length;
    }

    @Override
    public Object getItem(int position) {
        return assignmentNames[position].length;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListView listView = new ListView(context);
        listView.setView(assignmentNames[position]);
        listView.setTextSize(20);
        listView.setTextColor(Color.BLACK);
        listView.setBackgroundColor();
        return listView;

    }
}
