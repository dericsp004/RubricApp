package com.plummer.deric.rubricapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.gson.Gson;

import org.w3c.dom.Text;


public class Grade_Activity extends AppCompatActivity {

    public ExpandableListView expandableListView;
    public Assignment _assignment;
    public TextView _tvAssignment;
    public TextView _tvStudent;
    public TextView _tvComments;

    // loop through rubric list to get list items
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_);

        //Get Data From Intent
        Log.d("Grade_Activity", "get data from intent");

        Intent intent = getIntent();
        String assignmentString = intent.getStringExtra(Student_Activity.EXTRA_ASSIGN);
        String studentName = intent.getStringExtra(Student_Activity.EXTRA_NAME);

        Log.d("Grade_Activity", "deserialize data");
        Gson gson = new Gson();
        _assignment = gson.fromJson(assignmentString, Assignment.class);

        Log.d("Grade_Activity", "get student text view");
        _tvStudent = (TextView) findViewById(R.id.textViewStudentName);
        if (_tvStudent == null) {
            Log.d("Grade_Activity", "student text view is null");
        }
        Log.d("Grade_Activity", "set student name to text view: " + studentName);
        _tvStudent.setText(studentName);

        Log.d("Grade_Activity", "get assignment text view");
        _tvAssignment = (TextView) findViewById(R.id.textViewAssignmentName);
        Log.d("Grade_Activity", "set assignment name to text view");
        _tvAssignment.setText(_assignment.getAssignmentName());
        Log.d("Grade_Activity", "Finished loading data");
        //expandableListView = (ExpandableListView) findViewById(R.id.GradeList);

        //ExpandableListViewAdapter adapter = new ExpandableListViewAdapter(Grade_Activity.this);

        //expandableListView.setAdapter(adapter);
    }

   /* @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent();
        i.putExtra("ASSIGNMENT_NAME", _assignment.toString());
        setResult(RESULT_OK, i);
        finish();
    }*/

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent i = new Intent();
        i.putExtra("ASSIGNMENT_NAME", _assignment.toString());
        setResult(RESULT_OK, i);
        finish();
    }
}