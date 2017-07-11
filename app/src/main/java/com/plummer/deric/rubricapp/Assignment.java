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
/*import com.google.android.gms.common.ConnectionResult;
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
import com.google.api.services.sheets.v4.model.*;*/

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

/*
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
*/


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

    //GoogleAccountCredential mCredential;
    private TextView mOutputText;
    private Button mCallApiButton;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call Google Sheets API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    //private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY };

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
public void exportToGoogleSheets() {
    /*  START GOOGLE EXPORT CODE(above) */
    /*  END GOOGLE EXPORT CODE(above) */
}


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

