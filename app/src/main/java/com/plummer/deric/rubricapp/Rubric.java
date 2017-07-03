package com.plummer.deric.rubricapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Rubric {
    private String _name;
    private String _description;
    private List<Criteria> _criteriaList;
    private static final String prefs_file = "com.plummer.deric.rubricapp.Rubrics";


    /********************************************************
     *  Constructors
     ********************************************************/
    public Rubric(String name, String description) {
        this._name = name;
        this._description = description;
        this._criteriaList = new ArrayList<>();
    }

    public Rubric(String name, String description, List<Criteria> criteriaList) {
        this._name = name;
        this._description = description;
        this._criteriaList = new ArrayList(criteriaList);
    }
    /********************************************************
     *  Getters
     ********************************************************/
    public List<Criteria> getCriteria() {
        return _criteriaList;
    }

    public String get_name() {
        return _name;
    }

    public String get_description() {
        return _description;
    }

    /********************************************************
     *  Setters
     ********************************************************/
    public void set_name(String _name) {
        this._name = _name;
    }

    public void set_description(String _description) {
        this._description = _description;
    }

    /********************************************************
     *  Member Methods
     ********************************************************/
    public void addCriteria(String name, int maxGrade, String description) {
        _criteriaList.add(new Criteria(name, maxGrade, description));
    }

    /**
     * Saves the rubric contents to shared preferences
     * @param context
     *
     */
    public void saveRubric(Context context) {
        Gson gson = new Gson();

        String rubric = gson.toJson(this);
        SharedPreferences prefs = context.getSharedPreferences(prefs_file, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(this._name + ": " + this._description, rubric);
        edit.commit();
    }

    /**
     *Loop through and display all rubrics
     * @param context
     * @return List Rubric
     */

    public static List<Rubric> loadAllRubric(Context context) {
        List<Rubric> rubrics = new ArrayList<>();
        Rubric rubric;
        Gson gson = new Gson();


        Log.d("loadAllRubrics", "Create SharedPrefs");
        SharedPreferences prefs = context.getSharedPreferences(prefs_file, Context.MODE_PRIVATE);
        Log.d("loadAllAssignments()", "Get all keys");
        Map<String, ?> keys = prefs.getAll();
        for(Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("loadAllAssignments()","Loading: " + entry.getKey());
            String temp = entry.getValue().toString();
            rubric = gson.fromJson(temp, Rubric.class);
            rubrics.add(rubric);
            Log.d("loadAllAssignments()", "Loaded: " + rubric.toString());
        }
        return rubrics;
    }

    /**
     *Returns a specific rubric
     * @return Rubric
     */
    @Override
    public String toString() {
        return "Rubric{" +
                "_name='" + _name + '\'' +
                ", _description='" + _description + '\'' +
                ", _criteriaList=" + _criteriaList +
                '}';
    }
}
