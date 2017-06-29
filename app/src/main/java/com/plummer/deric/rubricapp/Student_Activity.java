package com.plummer.deric.rubricapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class Student_Activity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.STUDENT_ACTIVITY";
    Assignment _assignment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String assignmentName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
    }

    public void selectAssignment(View v) {
        //Make the intent
        Intent intent = new Intent(this, Grade_Activity.class);
        //Pass the text from the button clicked
        intent.putExtra(EXTRA_MESSAGE, ((TextView)v).getText().toString());
        startActivity(intent);
    }
}
