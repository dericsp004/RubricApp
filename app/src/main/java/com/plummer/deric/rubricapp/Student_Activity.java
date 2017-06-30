package com.plummer.deric.rubricapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Student_Activity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.STUDENT_ACTIVITY";
    private Assignment _assignment;
    private ListView _lv;
    private ArrayAdapter<String> _listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String assignmentName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView tv = (TextView) findViewById(R.id.AssignmentName);
        tv.setText(assignmentName);
        displayStudents(assignmentName);
    }


    public void displayStudents(String assignmentName) {
        _assignment = Assignment.load(this, assignmentName);
        _lv = (ListView) findViewById(R.id.StudentList);
        List<String> studentNames = new ArrayList<>();
        for(Student student : _assignment.getStudents()) {
            studentNames.add(student.toString());
        }

        _listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, studentNames);
    }

    //
    public void selectAssignment(View v) {
        //Make the intent
        Intent intent = new Intent(this, Grade_Activity.class);
        //Pass the text from the button clicked
        intent.putExtra(EXTRA_MESSAGE, ((TextView)v).getText().toString());
        startActivity(intent);
    }

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
                                _assignment.addStudent(firstName, lastName);  //Save the assignment
                                _assignment.save(Student_Activity.this);
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
}
