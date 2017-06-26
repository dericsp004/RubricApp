package com.plummer.deric.rubricapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toolbar;


public class Grade_Activity extends AppCompatActivity {

ExpandableListView expandableListView;




    // loop through rubric list to get list items
@Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


    expandableListView = (ExpandableListView) findViewById(R.id.GradeList);

    ExpandableListViewAdapter adapter = new ExpandableListViewAdapter(Grade_Activity.this);

    expandableListView.setAdapter(adapter);

}
}