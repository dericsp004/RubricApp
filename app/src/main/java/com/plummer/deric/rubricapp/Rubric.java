package com.plummer.deric.rubricapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Rubric implements Cloneable {
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

    public Rubric(Rubric rubric) {
        this._name = rubric.get_name();
        this._description = rubric.get_description();
        this._criteriaList = new ArrayList();

        for (int i = 0; i < rubric.getCriteria().size(); i++) {
            _criteriaList.add(new Criteria(rubric.getCriteria().get(i)));
        }
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

    public void saveRubric(Context context) {
        Gson gson = new Gson();

        String rubric = gson.toJson(this);
        SharedPreferences prefs = context.getSharedPreferences(prefs_file, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(this.toString(), rubric);
        edit.commit();
    }

    public static Rubric load(Context context, String key) {
        Gson gson = new Gson();

        SharedPreferences prefs = context.getSharedPreferences(prefs_file, Context.MODE_PRIVATE);
        String rubricString = prefs.getString(key, null);
        Rubric rubric = gson.fromJson(rubricString, Rubric.class);
        return rubric;
    }

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

    @Override
    public String toString() {
        return this._name + ": " + this._description;
    }

    public String getData() {
        return "Rubric{" +
                "_name='" + _name + '\'' +
                ", _description='" + _description + '\'' +
                ", _criteriaList=" + _criteriaList +
                '}';
    }

    /* found pseodocode at
    https://stackoverflow.com/questions/14795199/how-to-deep-clone-an-object-list-that-contains-several-objects-in-java */
    public Rubric clone() {
        Log.d("Rubric", "start clone");

        try {
            Rubric copy = (Rubric) super.clone();
            if (_criteriaList != null) {
                copy._criteriaList = new ArrayList<>(_criteriaList.size());
                for (int i = 0; i < _criteriaList.size(); i++) {
                    copy._criteriaList.add((Criteria) _criteriaList.get(i).clone());
                }
            } else {
                copy._criteriaList = new ArrayList<Criteria>();
            }
            Log.d("Rubric", "end clone");
            return copy;
        } catch (CloneNotSupportedException e) {
            Log.d("Rubric", "rubric clone failed: " + e.getMessage());
            return null;
        }
    }
}
