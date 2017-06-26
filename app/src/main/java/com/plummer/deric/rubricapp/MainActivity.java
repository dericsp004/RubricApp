package com.plummer.deric.rubricapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;


public class MainActivity extends AppCompatActivity {
    ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        expandableListView = (ExpandableListView) findViewById(R.id.MainList);

        ExpandableListViewAdapter adapter = new ExpandableListViewAdapter(MainActivity.this);

        expandableListView.setAdapter(adapter);
    }
}
