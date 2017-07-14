package com.plummer.deric.rubricapp;

// GOOGLE SHEETS API IMPORTS
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

import com.google.api.services.sheets.v4.model.*;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
// END GOOGLE SHEETS API IMPORT

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import pub.devrel.easypermissions.EasyPermissions;

public class Student_Activity extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks {
    // GOOGLE SHEETS API Private variables
    GoogleAccountCredential mCredential;
    private TextView mOutputText;
    private Button mCallApiButton;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };

    // END GOOGLE SHEET VARIABLES

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

        mCallApiButton = (Button) findViewById(R.id.exportButton);
        //mCallApiButton.setText(BUTTON_TEXT);
        mCallApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallApiButton.setEnabled(false);
                mOutputText.setText("");
                getResultsFromApi();
                mCallApiButton.setEnabled(true);
            }
        });
        //activityLayout.addView(mCallApiButton);

        mOutputText = (TextView)findViewById(R.id.messageTextView);
        mOutputText.setVerticalScrollBarEnabled(true);
        mOutputText.setMovementMethod(new ScrollingMovementMethod());
        mOutputText.setText(
                "Click the export button to create google sheet");
        //activityLayout.addView(mOutputText);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Sheets API ...");

        //setContentView(activityLayout);*/

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
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
        View promptsView = li.inflate(R.layout.prompt_add_student, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompt_add_student.xml_prompt.xml to alertdialog builder
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
    * https://developers.google.com/sheets/api/quickstart/android
    * Much of the set up functions that work with the Google Sheets API were obtained from the
    * tutorial listed above
    */

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                Student_Activity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, String> {
        private String spreadsheetId;
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            Log.d("Export", "Started Make Request Task");

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected String doInBackground(Void... params) {
            try {
                createSpreadSheet();
                exportToGoogleSheets();
                return "Google Spreadsheet created for " + _assignment.getAssignmentName();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }



        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         * @return List of names and majors
         * @throws IOException
         */
        /*private List<String> getDataFromApi() throws IOException {
            spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
            String range = "Class Data!A2:E";
            List<String> results = new ArrayList<String>();
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values != null) {
                results.add("Name, Major");
                for (List row : values) {
                    results.add(row.get(0) + ", " + row.get(4));
                }
            }
            return results;
        }*/

        private void createSpreadSheet() throws IOException, GeneralSecurityException {
            Log.d("Export", "Started create spreadsheet");

            Spreadsheet requestBody = new Spreadsheet();
            Sheets.Spreadsheets.Create request = mService.spreadsheets().create(requestBody);
            Spreadsheet response = request.execute();
            spreadsheetId = response.getSpreadsheetId();
            Log.d("Export", "Exited create spreadsheet");
        }

        private void exportToGoogleSheets() throws IOException, GeneralSecurityException {
            Log.d("Export", "Started export to spreadsheet");
            List<List<Object>> excelData = new ArrayList<List<Object>>();

            String range = "Sheet1";
            String valueInputOption = "Raw";

            Log.d("Export", "Adding Assignment Info");
            List<Object> assignmentInfo = new ArrayList<>();
            assignmentInfo.add(new Date().toString());
            assignmentInfo.add("Class: " + _assignment.getClassName());
            assignmentInfo.add("Assignment: " + _assignment.getAssignmentName());
            assignmentInfo.add("Rubric: "+ _assignment.getRubric().get_name());
            excelData.add(assignmentInfo);
            excelData.add(new ArrayList<Object>());

            List<Object> headers = new ArrayList<>();
            headers.add("Student Last Name");
            headers.add("Student First Name");

            for(Criteria criteria : _assignment.getRubric().getCriteria()) {
                headers.add(criteria.getName());

            }
            headers.add("Average Grade");
            excelData.add(headers);

            List<Object> studentData;
            for (Student student : _assignment.getStudents()) {
                studentData = new ArrayList<>();
                studentData.add(student.getLastName());
                studentData.add(student.getFirstName());
                for (Criteria criteria : student.getRubric().getCriteria()) {
                    studentData.add(criteria.getGrade());
                }
                studentData.add(student.getAverageGrade());
                excelData.add(studentData);
            }
            //List<List<Object>> values = Arrays.asList(headers, headers);
            ValueRange requestBody = new ValueRange().setValues(excelData);

            Sheets.Spreadsheets.Values.Update request =
                    mService.spreadsheets().values().update(spreadsheetId, range, requestBody);
            request.setValueInputOption(valueInputOption);
            UpdateValuesResponse response = request.execute();
            Log.d("Export", "Exited export to spreadsheet");

        }

        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(String output) {
            mProgress.hide();
            if (output == null) {
                mOutputText.setText("Google Spreadsheet not created");
            } else {
                mOutputText.setText(output);
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            Student_Activity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }
}
