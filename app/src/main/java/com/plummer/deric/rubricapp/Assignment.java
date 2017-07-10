package com.plummer.deric.rubricapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.text.Format;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//Stuff added for export feature
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


import static android.content.Context.MODE_PRIVATE;

/**
 *  This class represents a school assignment
 */
public class Assignment {
    private String _assignmentName;
    private String _className;
    private Rubric _rubric;
    private List<Student> _students;
    private static final String prefs_file = "com.plummer.deric.rubricapp.Assignments";

    GoogleAccountCredential mCredential;
    private TextView mOutputText;
    private Button mCallApiButton;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call Google Sheets API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY };

    /********************************************************
     *  Constructors
     ********************************************************/
    /**
     * Creates a new assignment. Generates an empty ArrayList for students.
     *
     * @param assignmentName
     * @param className
     * @param rubric
     */
    public Assignment(String assignmentName, String className, Rubric rubric) {
        this._assignmentName = assignmentName;
        this._className = className;
        this._rubric = rubric;
        this._students = new ArrayList<>();
    }

    /**
     * @param rubric
     */
    public Assignment(Rubric rubric) {
        this._assignmentName = "New Assignment";
        this._rubric = _rubric;
        this._students = new ArrayList<>();
    }

    /********************************************************
     *  Getters
     ********************************************************/
    public String getAssignmentName() { return _assignmentName; }
    public List<Student> getStudents() {
        return _students;
    }
    public Rubric getRubric() { return _rubric; }

    /********************************************************
     *  Setters
     ********************************************************/
    /**
     *
     * @param assignmentName
     */
    public void setAssignmentName(String assignmentName) {
        this._assignmentName = assignmentName;
    }

    /**
     * Set the Rubric Template and replace the rubrics in all the students
     * @param rubric
     */
    public void setRubric(Rubric rubric) {
        //Replace own rubric
        this._rubric = rubric;
        //Replace children rubrics
        for (Student student : _students) {
            student.replaceRubric(new Rubric(_rubric));


        }
    }

    /********************************************************
     *  Member Methods
     ********************************************************/
    /**
     * Create a new student with this Assignment's Rubric
     * @param first
     * @param last
     */
    public void addStudent(String first, String last) {

        _students.add(new Student(first, last, new Rubric(_rubric)));
    }

    public Student getStudent(String studentName) {
        Log.d("ExpandableListView", "Start Get Student");
        String[] names = studentName.split(", ");
        Log.d("ExpandableListView", "last Name: " + names[0]);
        Log.d("ExpandableListView", "first Name: " + names[1]);

        if (names.length == 2)
        {
            for(Student student : _students) {
                if (student.getLastName().equals(names[0]) && student.getFirstName().equals(names[1])) {
                    Log.d("ExpandableListView", "Returned Student");

                    return student;
                }
            }
        }
        Log.d("ExpandableListView", "Returned Null");
        return null;
     }

    /**
     * Remove a student of the provided name
     * @param firstName
     * @param lastName
     * @return boolean
     */
    public boolean containsStudent(String firstName, String lastName) {
        boolean contains = false;
        for (Student student : _students) {
            int result = student.compareTo(new Student(firstName, lastName, _rubric.clone() ));
            if (result == 1) {
                contains = true;
            }
        }

        return contains;
    }

    /**
     * Remove a student from the student list
     * @param firstName
     * @param lastName
     */
    public void removeStudent(String firstName, String lastName) {
        Iterator<Student> it = _students.iterator();
        while (it.hasNext()) {
            Student student = it.next();
            if (student.getFirstName().equals(firstName) && student.getLastName().equals(lastName)) {
                it.remove();
            }
        }
    }

    /**
     * Remove a student of X place in the list
     * @param stu
     * @return The result of students.remove()
     */
    public void removeStudent(int stu) {
        _students.remove(stu);
    }

    /**
     * Save the assignment to Shared Preferences
     * @param context
     */
    public void save(Context context) {
        Gson gson = new Gson();

        String assignment = gson.toJson(this);
        SharedPreferences prefs = context.getSharedPreferences(prefs_file, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(this.toString(), assignment);
        edit.commit();
    }

    /**
     * Load a specified Assignment from SharedPreferences
     * @param context
     * @param key
     * @return Assignment
     */
    public static Assignment load(Context context, String key) {
        Gson gson = new Gson();

        SharedPreferences prefs = context.getSharedPreferences(prefs_file, Context.MODE_PRIVATE);
        String assignmentString = prefs.getString(key, null);
        Assignment assignment = gson.fromJson(assignmentString, Assignment.class);
        return assignment;
    }

    /**
     * loads all saved Assignments from Shared Preferences
     * @param context
     * @return List<Assignment>
     */
    public static List<Assignment> loadAllAssignments(Context context) {
        List<Assignment> assignments = new ArrayList<>();
        Assignment assignment;
        Gson gson = new Gson();

        Log.d("loadAllAssignments()", "Create SharedPrefs");
        SharedPreferences prefs = context.getSharedPreferences(prefs_file, Context.MODE_PRIVATE);
        Log.d("loadAllAssignments()", "Get all keys");
        Map<String, ?> keys = prefs.getAll();
        for(Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("loadAllAssignments()","Loading: " + entry.getKey());
            String temp = entry.getValue().toString();
            assignment = gson.fromJson(temp, Assignment.class);
            assignments.add(assignment);
            Log.d("loadAllAssignments()", "Loaded: " + assignment.toString());
            Log.d("loadAllAssignments()", "Rubric Name: " + assignment.getRubric().get_name());
            for (Criteria criteria: assignment.getRubric().getCriteria()) {
                Log.d("loadAllAssignments()", "Criteria Name: " + criteria.getName());
                Log.d("loadAllAssignments()", "Max Value: " + criteria.getMaxGrade());
                Log.d("loadAllAssignments()", "Grade Value: " + criteria.getGrade());
            }

        }
        return assignments;
    }
    /*

    public void exportToGoogleSheets() {
<<<<<<< HEAD
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());


// BEGIN Export code from google
        /*public class MainActivity extends Activity
                implements EasyPermissions.PermissionCallbacks {
                */
            //GoogleAccountCredential mCredential;
            /*private TextView mOutputText;
            private Button mCallApiButton;
            ProgressDialog mProgress;

            static final int REQUEST_ACCOUNT_PICKER = 1000;
            static final int REQUEST_AUTHORIZATION = 1001;
            static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
            static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

            //private static final String BUTTON_TEXT = "Call Google Sheets API";
            private static final String PREF_ACCOUNT_NAME = "accountName";
            private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY };

            /**
             * Create the main activity.
             * @param savedInstanceState previously saved instance data.
             */
            //@Override
            /*protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                LinearLayout activityLayout = new LinearLayout(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                activityLayout.setLayoutParams(lp);
                activityLayout.setOrientation(LinearLayout.VERTICAL);
                activityLayout.setPadding(16, 16, 16, 16);

                ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                mCallApiButton = new Button(this);
                mCallApiButton.setText(BUTTON_TEXT);
                mCallApiButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallApiButton.setEnabled(false);
                        mOutputText.setText("");
                        getResultsFromApi();
                        mCallApiButton.setEnabled(true);
                    }
                });
                activityLayout.addView(mCallApiButton);

                mOutputText = new TextView(this);
                mOutputText.setLayoutParams(tlp);
                mOutputText.setPadding(16, 16, 16, 16);
                mOutputText.setVerticalScrollBarEnabled(true);
                mOutputText.setMovementMethod(new ScrollingMovementMethod());
                mOutputText.setText(
                        "Click the \'" + BUTTON_TEXT +"\' button to test the API.");
                activityLayout.addView(mOutputText);

                mProgress = new ProgressDialog(this);
                mProgress.setMessage("Calling Google Sheets API ...");

                setContentView(activityLayout);

                // Initialize credentials and service object.
                mCredential = GoogleAccountCredential.usingOAuth2(
                        getApplicationContext(), Arrays.asList(SCOPES))
                        .setBackOff(new ExponentialBackOff());
            }



            /**
             * Attempt to call the API, after verifying that all the preconditions are
             * satisfied. The preconditions are: Google Play Services installed, an
             * account was selected and the device currently has online access. If any
             * of the preconditions are not satisfied, the app will prompt the user as
             * appropriate.

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

            @Override
            public void onPermissionsDenied(int requestCode, List<String> list) {
                // Do nothing.
            }

            /**
             * Checks whether the device currently has a network connection.
             * @return true if the device has a network connection, false otherwise.

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

            void showGooglePlayServicesAvailabilityErrorDialog(
                    final int connectionStatusCode) {
                GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
                Dialog dialog = apiAvailability.getErrorDialog(
                        MainActivity.this,
                        connectionStatusCode,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }

            /**
             * An asynchronous task that handles the Google Sheets API call.
             * Placing the API calls in their own task ensures the UI stays responsive.

            private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
                private com.google.api.services.sheets.v4.Sheets mService = null;
                private Exception mLastError = null;

                MakeRequestTask(GoogleAccountCredential credential) {
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

                @Override
                protected List<String> doInBackground(Void... params) {
                    try {
                        return getDataFromApi();
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

                private List<String> getDataFromApi() throws IOException {
                    String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
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
                }



                @Override
                protected void onPreExecute() {
                    mOutputText.setText("");
                    mProgress.show();
                }

                @Override
                protected void onPostExecute(List<String> output) {
                    mProgress.hide();
                    if (output == null || output.size() == 0) {
                        mOutputText.setText("No results returned.");
                    } else {
                        output.add(0, "Data retrieved using the Google Sheets API:");
                        mOutputText.setText(TextUtils.join("\n", output));
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
                                    MainActivity.REQUEST_AUTHORIZATION);
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


=======

    }
>>>>>>> 28c1ddc65d478ee53a3607f2689e18d42f596049

    //END GOOGLE EXPORT CODE(above)
*/
    public String toString() {

        return this._className + " - " + this._assignmentName;
    }

    /*
     * Used for testing
     */
    public String getData() {
        String studentsString = "";
        for(Student student : _students){
            studentsString += "\t" + student.getLastName() + ", " + student.getFirstName() + "\n";
        }
        return String.format("Assignment Name: %s \nEnrollemnts: \n %s", _assignmentName, studentsString);
    }
}

