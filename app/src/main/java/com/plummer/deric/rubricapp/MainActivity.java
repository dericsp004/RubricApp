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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.FROM_MAIN_ACTIVITY";
    private List<Assignment> assignments;
    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialize the List to avoid null reference pointer errors
        assignments = new ArrayList<>();
        Log.d("loadAssignments()", "Preload size: " + String.valueOf(assignments.size()));
        //Populate the list
        loadAssignments();
    }

    /**
     * Loads and displays all assignments saved on this device
     */
    private void loadAssignments() {
        Log.d("loadAssignments()", "Preload size: " + String.valueOf(assignments.size()));
        assignments = Assignment.loadAllAssignments(this);
        Log.d("loadAssignments()", "Postload size: " + String.valueOf(assignments.size()));

        // Find the ListView resource.
        mainListView = (ListView) findViewById( R.id.RubricList );

        //Convert all assignments to their names
        List<String> assignmentNames = new ArrayList<String>();
        for (Assignment assign : assignments) {
            assignmentNames.add(assign.getClass() + " - " + assign.getAssignmentName());
        }

        // Create ArrayAdapter using the planet list.
        listAdapter = new ArrayAdapter<String>(this, R.layout.rubricrow, assignmentNames);

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter(listAdapter);
    }

    /**
     * Adds a new assignment to the list.
     * Asks user for assignment name and rubric to link.
     */
    public void addAssignment(View v) {
        //The following is modified from:
        //https://www.mkyong.com/android/android-prompt-user-input-dialog-example/
        // get add_student_promptdent_prompt.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.add_assignment_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set add_student_prompt.xml_prompt.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        //Get the text fields
        final EditText assignName = (EditText) promptsView
                .findViewById(R.id.newAssignmentPromptEditText1);
        final EditText className = (EditText) promptsView
                .findViewById(R.id.newAssignmentPromptEditText2);
        //TODO: Add a rubric selection list

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                String title = assignName.getText().toString();
                                String classTitle = className.getText().toString();

                                Assignment newAssign = new Assignment(title, classTitle, /*HARDCODED NEW RUBRIC. TODO: REPLACE THIS!!!!*/ new Rubric( "name",  "description"));
                                //Assignment newAssign = new Assignment(title, classTitle, /*HARDCODED NEW RUBRIC. TODO: REPLACE THIS!!!!*/ new Rubric("Hardcode Rubric",

                                Log.d("addAssignment()", "Raw title: " + title);
                                Log.d("addAssignment()", "Assignment title: " + newAssign.getAssignmentName());
                                Log.d("addAssignment()", "Raw class name: " + title);
                                Log.d("addAssignment()", "Assignment class name: " + newAssign);

                                newAssign.save(MainActivity.this);  //Save the assignment
                                loadAssignments();
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
    }}//extra parenthese added to resolve error

    /**
     * Retrieve the Assignment object by the selected ID and pass
     * it to the next activity
     */
    /*
    public void selectAssignment(View v) {
        //Make the intent
        Intent intent = new Intent(this, AssignmentActivity.class);
        //Pass the text from the button clicked
        intent.putExtra(EXTRA_MESSAGE, ((TextView)v).getText().toString());
        startActivity(intent);
    }
}
*/