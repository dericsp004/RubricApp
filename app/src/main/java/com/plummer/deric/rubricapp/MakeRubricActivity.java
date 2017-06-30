package com.plummer.deric.rubricapp;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

public class MakeRubricActivity extends AppCompatActivity {
    private String name;
    private Rubric rubric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_rubric);

        rubric = new Rubric(name, "Debug description");
    }

    public void addCriteria(View v) {
        //TODO: get criteria name and description
        //The following is modified from:
        //https://www.mkyong.com/android/android-prompt-user-input-dialog-example/
        // get add_student_promptdent_prompt.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.add_criteria_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set add_student_prompt.xml_prompt.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        //Get the text fields
        final EditText critNameBox = (EditText) promptsView
                .findViewById(R.id.newCriteiaPromptEditText1);
        final NumberPicker maxValueBox = (NumberPicker) promptsView
                .findViewById(R.id.newCriteiaPromptNumberPicker1);
        final EditText descBox = (EditText) promptsView
                .findViewById(R.id.newCriteiaPromptEditText2);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                String critName = critNameBox.getText().toString();
                                int maxValue = maxValueBox.getValue();
                                String desc = descBox.getText().toString();
                                // edit text
/*TODO: READ COMMENT*/          rubric.addCriteria(critName, maxValue, desc);
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
        //TODO: add
    }
}
