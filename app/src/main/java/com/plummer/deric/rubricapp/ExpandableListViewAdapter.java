package com.plummer.deric.rubricapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import static com.plummer.deric.rubricapp.R.id.textView;

/**
 * Created by Sterling on 6/21/2017.
 */

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    //put a for each loop in here(inside the String rubric item arrays) that iterates through the rubric items
    /*String[] rubricItemName = {"Plot", "Presentation", "Irony", "Extra Credit"};
    String[][] rubricSubItem = {{"Plot item 1", "Plot item 2", "Plot item 3", "Plot item 4", "Plot item 5"}, {"Presentation item 1",
            "Presentation item 2", "Presentation item 2", "Presentation item 3", "Presentation item 4", "Presentation item 5"},
            {"Irony item 1", "Irony item 2", "Irony item 3", "Irony item 4", "Irony item 5"}, {"Extra Credit item 1",
            "Extra Credit item 2", "Extra Credit item 3", "Extra Credit item 4", "Extra Credit item 5",}};*/
    Context context;
    List<String> _rubricItemName;
    HashMap<String, String> _rubricSubItem;
    public ExpandableListViewAdapter(Context context, Rubric rubric) {
        this.context = context;
        _rubricItemName = new ArrayList<>();
        _rubricSubItem = new HashMap<>();
        for (Criteria criteria : rubric.getCriteria()) {
            _rubricItemName.add(criteria.getName());
            _rubricSubItem.put(criteria.getName(), criteria.get_description());
        }
    }

    @Override
    public int getGroupCount() {
        return _rubricItemName.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return _rubricItemName.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return _rubricSubItem.get(_rubricItemName.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //String criteriaName = (String) this.getGroup(groupPosition);
        //if (convertView == null) {
        //    LayoutInflater layoutInflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //}
        TextView textView = new TextView(context);
        textView.setText(_rubricItemName.get(groupPosition));
        textView.setPadding(100, 0, 0, 0);
        textView.setTextColor(Color.BLUE);
        textView.setTextSize(25);
        return textView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);
        textView.setText(_rubricSubItem.get(_rubricItemName.get(groupPosition)));
        textView.setPadding(100, 0, 0, 0);
        textView.setTextColor(Color.DKGRAY);
        textView.setTextSize(20);
        return textView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
