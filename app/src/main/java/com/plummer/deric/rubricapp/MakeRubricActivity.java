package com.plummer.deric.rubricapp;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.List;

public class MakeRubricActivity extends AppCompatActivity {
    private String name;
    private Rubric rubric;

    //Member variables for displaying criteria
    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_rubric);

        rubric = new Rubric(name, "Debug description");
        displayRubric();
    }

    private void displayRubric() {
        // Find the ListView resource.
        mainListView = (ListView) findViewById( R.id.CriteriaList );

        //Convert all criteria to its data
        List<String> critStrings = new ArrayList<>();
        for (Criteria crit : rubric.getCriteria()) {
            critStrings.add(
                    crit.getName() + " - " //TODO: why does .getMaxGrade break this? Like HORRIBLY! //  + String.valueOf(crit.getMaxGrade())
            );
        }

        // Create ArrayAdapter
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, critStrings);

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter(listAdapter);
    }

    public void addCriteria(View v) {
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
                                //Append to the rubric
                                rubric.addCriteria(critName, maxValue, desc);
                                //Save this copy of the rubric TODO: This should probably be moved to onPause
                                rubric.saveRubric(MakeRubricActivity.this);
                                //Display the rubric criteria to the user
                                displayRubric();
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
