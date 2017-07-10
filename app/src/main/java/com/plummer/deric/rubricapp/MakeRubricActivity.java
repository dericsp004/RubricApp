package com.plummer.deric.rubricapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MakeRubricActivity extends AppCompatActivity {
    private String _name;
    private String _desc;
    private Rubric _rubric;

    //Member variables for displaying criteria
    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_rubric);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        //First part should be name, second should be description. :: delimiter
        String[] intentMessage = intent.getStringExtra(MainActivity.EXTRA_MESSAGE).split("::");
        _name = intentMessage[0];
        _desc = intentMessage[1];

        _rubric = new Rubric(_name, _desc);
        displayRubric();

        ((TextView)findViewById(R.id.AssignmentName)).setText(_name);
    }

    private void displayRubric() {
        // Find the ListView resource.
        mainListView = (ListView) findViewById( R.id.CriteriaList );

        //Convert all criteria to its data
        List<String> critStrings = new ArrayList<>();
        for (Criteria crit : _rubric.getCriteria()) {
            //Setup fancy text
            String line = crit.getName() + " - " + crit.get_description();
            if (line.length() > 33) { line = line.substring(0, 30) + "..."; }

            //Add the criteria to the list
            critStrings.add(
                    String.valueOf((int)crit.getMaxGrade()) + ": " + line
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
        View promptsView = li.inflate(R.layout.prompt_add_criteria, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompt_add_student.xml_prompt.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        //Get the text fields
        final EditText critNameBox = (EditText) promptsView
                .findViewById(R.id.newCriteiaPromptEditText1);
        final NumberPicker maxValueBox = (NumberPicker) promptsView
                .findViewById(R.id.newCriteiaPromptNumberPicker1);
        final EditText descBox = (EditText) promptsView
                .findViewById(R.id.newCriteiaPromptEditText2);

        populateNumberPicker(maxValueBox, 50);

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
                                _rubric.addCriteria(critName, maxValue, desc);
                                //Save this copy of the rubric TODO: This should probably be moved to onPause
                                _rubric.saveRubric(MakeRubricActivity.this);
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
    }

    private void populateNumberPicker(NumberPicker np, int max) {
        String[] nums = new String[max+1];
        for(int i=0; i<nums.length; i++)
            nums[i] = Integer.toString(i);

        np.setMinValue(0);
        np.setMaxValue(max);
        np.setWrapSelectorWheel(false);
        np.setDisplayedValues(nums);
        np.setValue(1);
    }
}
