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

