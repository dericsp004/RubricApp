package com.plummer.deric.rubricapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.FROM_MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignments = Assignment.loadAllAssignments(this);
    }

    private List<Assignment> assignments;

    /**
     * Adds a new assignment to the list.
     * Asks user for assignment name and rubric to link.
     */
    public void addAssignment(View v) {
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
        final EditText assignName = (EditText) promptsView
                .findViewById(R.id.newStudentPromptEditText1);
        final EditText className = (EditText) promptsView
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
                                String title = assignName.getText().toString();
                                String classTitle = className.getText().toString();
                                Assignment newAssign = new Assignment(title, classTitle, /*HARDCODED NEW RUBRIC. TODO: REPLACE THIS!!!!*/ new Rubric("Hardcode Rubric",
                                        "A test rubric in MainActivity.java, addAssignment(). If you're seeing this, that's a problem."));
                                newAssign.save(MainActivity.this);  //Save the assignment
                                //assignments.add(newAssign);

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

    /**
     * Retrieve the Assignment object by the selected ID and pass
     * it to the next activity
     */
    public void selectAssignment(View v) {
        //Make the intent
        Intent intent = new Intent(this, AssignmentActivity.class);
        //Pass the text from the button clicked
        intent.putExtra(EXTRA_MESSAGE, ((TextView)v).getText().toString());
        startActivity(intent);
    }
}
