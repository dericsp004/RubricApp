package com.plummer.deric.rubricapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AssignmentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
    }

    /**
     * Adds a new student to the list.
     * Asks user for student name.
     * @param v: Only takes this because buttons pass it
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
        final EditText firstName = (EditText) promptsView
                .findViewById(R.id.newStudentPromptEditText1);
        final EditText lastName = (EditText) promptsView
                .findViewById(R.id.newStudentPromptEditText2);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                //LIST OF STUDENTS.append(new student(firstName.getText().toString(), lastName.getText().toString(), assignment.getRubric));
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
     * Exports the assignment data
     */
    public void export() {
        //TODO: Connect to google drive
    }
}
