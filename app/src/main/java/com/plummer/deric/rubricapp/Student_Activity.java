package com.plummer.deric.rubricapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Student_Activity extends AppCompatActivity {
    public static final String EXTRA_ASSIGN = "com.example.myfirstapp.STUDENT_ACTIVITY.ASSIGN";
    public static final String EXTRA_NAME = "com.example.myfirstapp.STUDENT_ACTIVITY.NAME";


    private Assignment _assignment;
    private ListView _lv;
    private ArrayAdapter<String> _listAdapter;
    private List<String> _studentNames;
    private String _assignmentName;
    private TextView _tv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        _assignmentName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        if (_assignmentName == null) {
            SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
            _assignmentName = preferences.getString("ASSIGNMENT_NAME", null);
        }
        displayStudents(_assignmentName);

    }

    @Override
    protected void onPause() {
        Log.d("Student_Activity", "Start onPause");
        super.onPause();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ASSIGNMENT_NAME", _assignmentName);
        editor.commit();
        Log.d("Student_Activity", "End onPause");

    }

   /* @Override
    protected void onResume() {
        Log.d("Student_Activity", "Start onResume");
        if(_assignmentName == null) {
            super.onResume();
            SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
            _assignmentName = preferences.getString("ASSIGNMENT_NAME", null);
            //displayStudents(_assignmentName);
        }
        Log.d("Student_Activity", "End onResume : " + _assignmentName);

    }*/

    public void displayStudents(String assignmentName) {
        _tv = (TextView) findViewById(R.id.AssignmentName);
        _lv = (ListView) findViewById(R.id.StudentList);
        _tv.setText(assignmentName);
        Log.d("Student_Activity", "load Assignment: " + assignmentName);
        _assignment = Assignment.load(this, assignmentName);
        Log.d("Student_Activity", _assignment.getData());
        _studentNames = new ArrayList<>();
        for (Student student : _assignment.getStudents()) {
            _studentNames.add(student.toString());
        }

        _listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _studentNames);
        _lv.setAdapter(_listAdapter);
        _lv.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Student_Activity", "create intent");
                Intent intentOut = new Intent(Student_Activity.this, Grade_Activity.class);
                Log.d("Student_Activity", "get item at new position");
                String name = _lv.getItemAtPosition(position).toString();
                Gson gson = new Gson();
                String assignmentString = gson.toJson(_assignment, Assignment.class);
                Log.d("Student_Activity", "Start Grade activity");
                intentOut.putExtra(EXTRA_ASSIGN, assignmentString);
                intentOut.putExtra(EXTRA_NAME, name);
                startActivityForResult(intentOut, 1);
                Log.d("Student_Activity", "Finished Activity");

            }
        });
    }


    /*
    public void selectAssignment(View v) {
        //Make the intent
        Intent intent = new Intent(this, Grade_Activity.class);
        //Pass the text from the button clicked
        intent.putExtra(EXTRA_MESSAGE, ((TextView)v).getText().toString());
        startActivity(intent);
    }*/

    public void addStudent(View v) {
        //The following is modified from:
        //https://www.mkyong.com/android/android-prompt-user-input-dialog-example/
        // get add_student_promptdent_prompt.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.add_student_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set add_student_prompt.xml_prompt.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        //Get the text fields
        final EditText firstNameEditText = (EditText) promptsView
                .findViewById(R.id.newStudentPromptEditText1);
        final EditText lastNameEditText = (EditText) promptsView
                .findViewById(R.id.newStudentPromptEditText2);
        //TODO: Add a rubric selection list

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                String firstName = firstNameEditText.getText().toString();
                                String lastName = lastNameEditText.getText().toString();
                                //Log.d("addAssignment()", "Raw title: " + title);
                                //Log.d("addAssignment()", "Assignment title: " + newAssign.getAssignmentName());
                                //Log.d("addAssignment()", "Raw class name: " + classTitle);
                                //Log.d("addAssignment()", "Assignment class name: " + newAssign);
                                Log.d("Student_Activity", "adding student");
                                _assignment.addStudent(firstName, lastName);  //Save the assignment
                                Log.d("Student_Activity", "save student");
                                _assignment.save(Student_Activity.this);
                                Log.d("Student_Activity", "call display students");
                                displayStudents(_assignment.toString());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Student_Activity", "Start onActivityResult()");
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                _assignmentName = data.getStringExtra("ASSIGNMENT_NAME");
                displayStudents(_assignmentName);
            }
        }
        Log.d("Student_Activity", "End onActivityResult()");
    }*/
}
