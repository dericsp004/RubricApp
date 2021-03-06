package com.plummer.deric.rubricapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.FROM_MAIN_ACTIVITY";
    private List<Assignment> assignments;
    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter;
    private String selectedRubric;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Main Activtity", "Activty Started");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the List to avoid null reference pointer errors
        assignments = new ArrayList<>();
        Log.d("loadAssignments()", "Preload size: " + String.valueOf(assignments.size()));
        loadAssignments();

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Make the intent
                Log.d("Main Activtity", "Started Intent");
                Intent intent = new Intent(MainActivity.this, Student_Activity.class);
                //Pass the text from the button clicked
                Log.d("Main Activtity", "get Item Position");
                intent.putExtra(EXTRA_MESSAGE, mainListView.getItemAtPosition(position).toString());
                Log.d("Main Activtity", "Start next Activity");
                startActivity(intent);
                Log.d("Main Activtity", "Finished Activity");
            }
        });

        mainListView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Integer index = (Integer) v.getTag();
                assignments.remove(index);
                loadAssignments();
                return false;
            }
        });
    }

    /**
     * Loads and displays all assignments saved on this device
     */
    private void loadAssignments() {
        Log.d("loadAssignments()", "Preload size: " + String.valueOf(assignments.size()));
        assignments = Assignment.loadAllAssignments(this);
        Log.d("loadAssignments()", "Postload size: " + String.valueOf(assignments.size()));

        // Find the ListView resource.
        mainListView = (ListView) findViewById( R.id.AssignmentList );

        //Convert all assignments to their names
        List<String> assignmentNames = new ArrayList<String>();
        for (Assignment assign : assignments) {
            assignmentNames.add(assign.toString());
        }

        // Create ArrayAdapter
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, assignmentNames);

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter(listAdapter);
    }

    private ListView loadRubrics(View promptsView) {
        List<Rubric> rubrics = Rubric.loadAllRubric(MainActivity.this);

        //Convert all assignments to their names
        List<String> rubricNames = new ArrayList<String>();
        for (Rubric rubric : rubrics) {
            rubricNames.add(rubric.get_name() + ": " + rubric.get_description());
        }

        // Create ArrayAdapter
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rubricNames);

        // Find the ListView resource.
        final ListView rubricListView = (ListView) promptsView.findViewById( R.id.RubricList );
        // Set the ArrayAdapter as the ListView's adapter.
        rubricListView.setAdapter(listAdapter);

        //Give the list cool features
        rubricListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Set the selection color
                rubricListView.setFocusableInTouchMode(true);
                rubricListView.setSelector(R.color.colorPrimary);

                selectedRubric = rubricListView.getItemAtPosition(position).toString();
            }
        });

        //Return the view so that the calling method doesn't have
        //to search it serparetely
        return rubricListView;
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
        View promptsView = li.inflate(R.layout.prompt_add_assignment, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompt_add_student.xml_prompt.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        //Get the text fields
        final EditText assignName = (EditText) promptsView
                .findViewById(R.id.newAssignmentPromptEditText1);
        final EditText className = (EditText) promptsView
                .findViewById(R.id.newAssignmentPromptEditText2);
        //Add the available rubric rows
        final ListView rubrics = loadRubrics(promptsView);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                Rubric rubric = Rubric.load(MainActivity.this, selectedRubric);
                                String title = assignName.getText().toString();
                                String classTitle = className.getText().toString();
                                //Assignment newAssign = new Assignment(title, classTitle, new Rubric("test name", "test description"));
                                Assignment newAssign = new Assignment(title, classTitle, rubric);
                                Log.d("addAssignment()", "Assignment title: " + newAssign.getAssignmentName());
                                Log.d("addAssignment()", "Raw class name: " + classTitle);
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
    }

    public void createRubric(View v) {
        //Make the intent
        Log.d("Main Activtity", "Started Intent");
        //The following is modified from:
        //https://www.mkyong.com/android/android-prompt-user-input-dialog-example/
        // get add_student_promptdent_prompt.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt_rubric_name, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        //Get the text fields
        final EditText rubricName = (EditText) promptsView
                .findViewById(R.id.newRubricPromptEditText1);
        final EditText rubricDesc = (EditText) promptsView
                .findViewById(R.id.newRubricPromptEditText2);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                String rName = rubricName.getText().toString();
                                String rDesc = rubricDesc.getText().toString();

                                //Start the Make Rubric Activity
                                Intent intent = new Intent(MainActivity.this, MakeRubricActivity.class);
                                //Pass the name and description with a :: deliminator
                                intent.putExtra(EXTRA_MESSAGE, rName + "::" + rDesc);
                                startActivity(intent);
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
    /*public void selectAssignment(View v) {
        //Make the intent
        Intent intent = new Intent(this, AssignmentActivity.class);
        //Pass the text from the button clicked
        intent.putExtra(EXTRA_MESSAGE, ((TextView)v).getText().toString());
        startActivity(intent);
    }*/
}
