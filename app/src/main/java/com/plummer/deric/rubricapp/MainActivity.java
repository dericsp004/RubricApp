package com.plummer.deric.rubricapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Adds a new assignment to the list.
     * Asks user for assignment name and rubric to link.
     */
    public void addAssignment() {
        String first;
        String last;
        //TODO: Create dialog to get first last

        //Student student = new Student(first, last, ) //GET RUBRIC FROM ASSIGNMENT
        //TODO: Add student to assignment list of students
    }

    /**
     * Retrieve the Assignment object by the selected ID and pass
     * it to the next activity
     */
    public void selectAssignment() {

    }
}
