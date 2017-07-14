package com.plummer.deric.rubricapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.NumberPicker;
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
    Assignment _assignment;
    List<String> _rubricItemName;
    String _studentName;
    HashMap<String, String> _rubricSubItem;
    HashMap<String, Integer> _CriteriaMaxGrade;
    HashMap<String, Integer> _CriteriaGrade;

    public ExpandableListViewAdapter(Context context, Assignment assignment, String studentName) {
        Log.d("ExpandableListView", "Start Constructor");
        Log.d("ExpandableListView", "Student Name: " + studentName);
        Student student = assignment.getStudent(studentName);
        Log.d("ExpandableListView", "Get Student");

        this.context = context;
        _assignment = assignment;
        _rubricItemName = new ArrayList<>();
        _rubricSubItem = new HashMap<>();
        _CriteriaGrade = new HashMap<>();
        _CriteriaMaxGrade = new HashMap<>();
        _studentName = studentName;
        for (Criteria criteria : student.getRubric().getCriteria()) {
            Log.d("ExpandableListView", "Add Criteria Name: " + criteria.getName());
            _rubricItemName.add(criteria.getName());
            Log.d("ExpandableListView", "Add Criteria Description: " + criteria.get_description());
            _rubricSubItem.put(criteria.getName(), criteria.get_description());
            Log.d("ExpandableListView", "Add Grade Value Name: " + criteria.getGrade());
            _CriteriaGrade.put(criteria.getName(), criteria.getGrade());
            Log.d("ExpandableListView", "Add Grade Max Grade Value Name: " + criteria.getMaxGrade());
            _CriteriaMaxGrade.put(criteria.getName(), criteria.getMaxGrade());
        }
        Log.d("ExpandableListView", "End Constructor");
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
        Log.d("ExpandableListView", "Get Group View Start");
        String criteriaName = (String) this.getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflator.inflate(R.layout.parent_exp_layout,null);

        }


        TextView textView = (TextView) convertView.findViewById(R.id.headingExpTextView);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(_rubricItemName.get(groupPosition));
        /*TextView textView = new TextView(context);
        textView.setText(_rubricItemName.get(groupPosition));
        textView.setPadding(100, 0, 0, 0);
        textView.setTextColor(Color.BLUE);
        textView.setTextSize(25);*/
        Log.d("ExpandableListView", "Get Group View End");
        //return textView;
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Log.d("ExpandableListView", "Get Child View Start");
        String criteriaDescription = (String) _rubricSubItem.get(_rubricItemName.get(groupPosition));
        if (convertView == null) {
            LayoutInflater layoutInflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflator.inflate(R.layout.child_exp_layout,null);

        }
        Log.d("ExpandableListView", "Created Layout Inflator");

        TextView textView = (TextView) convertView.findViewById(R.id.childtextView);
        textView.setText(criteriaDescription);
        textView.setMovementMethod(new ScrollingMovementMethod());
        Log.d("ExpandableListView", "Created and Set TextView");


        NumberPicker numberPicker = (NumberPicker) convertView.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(_CriteriaMaxGrade.get(_rubricItemName.get(groupPosition)));
        numberPicker.setValue(_CriteriaGrade.get(_rubricItemName.get(groupPosition)));

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                picker.setValue(newVal);
                _assignment.getStudent(_studentName).getRubric().getCriteria().get(groupPosition).setGrade(newVal);
                _assignment.save(context);
                _CriteriaGrade.put(_rubricItemName.get(groupPosition), newVal);
            }
        });

        /*String [] numbers = new String[numberPicker.getMaxValue()];
        for (int i = numberPicker.getMinValue(); i < numbers.length; i++) {
            numbers[i] = Integer.toString(i);
        }
        Log.d("ExpandableListView", "Created possible grade range");*/

        numberPicker.setWrapSelectorWheel(true);
        //numberPicker.setDisplayedValues(numbers);
        Log.d("ExpandableListView", "Created and Set NumberPicker");


        /*TextView textView = new TextView(context);
        textView.setText(_rubricSubItem.get(_rubricItemName.get(groupPosition)));
        textView.setPadding(100, 0, 0, 0);
        textView.setTextColor(Color.DKGRAY);
        textView.setTextSize(20);*/
        Log.d("ExpandableListView", "Get Child View End");
        //return textView;
        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
