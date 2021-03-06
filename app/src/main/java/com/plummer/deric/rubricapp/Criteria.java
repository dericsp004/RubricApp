package com.plummer.deric.rubricapp;

import android.util.Log;

import java.util.Collections;
import java.util.List;

public class Criteria implements Cloneable {
    private String _name;
    private int _grade;
    private int _maxGrade;
    private String _description;

    /********************************************************
     *  Constructors
     ********************************************************/
    public Criteria(String name, int maxGrade, String description) {
        this._name = name;
        this._maxGrade = maxGrade;
        this._grade = maxGrade;
        this._description = description;
    }

    public Criteria(Criteria criteria) {
        _name = criteria.getName();
        _grade = criteria.getMaxGrade();
        _maxGrade = criteria.getMaxGrade();
        _description = criteria.get_description();
    }

    /********************************************************
     *  Getters
     ********************************************************/
    public String getName() {
        return this._name;
    }

    public int getGrade() {
        return this._grade;
    }

    public int getMaxGrade() {
        return this._maxGrade;
    }

    public String get_description() {
        return this._description;

    }

    /********************************************************
     *  Setters
     ********************************************************/
    public void setName(String _name) {
        this._name = _name;
    }

    public void setMaxGrade(int _maxGrade) {
        this._maxGrade = _maxGrade;
    }

    public void setDescription(String _description) {
        this._description = _description;
    }

    public void setGrade(int grade) {
        if (grade >= _maxGrade)
        {
            this._grade = _maxGrade;
        }
        else {
            this._grade = grade;
        }
    }

    @Override
    protected Criteria clone() throws CloneNotSupportedException {
        Log.d("Rubric", "Criteria clone start");
        Criteria clone = (Criteria) super.clone();
        Log.d("Rubric", "Criteria clone end");
        return clone;
    }

    /********************************************************
     *  Member Methods
     ********************************************************/
    @Override
    public String toString() {
        return "Criteria{" +
                "_name='" + _name + '\'' +
                ", _grade=" + _grade +
                ", _maxGrade=" + _maxGrade +
                ", _description='" + _description + '\'' +
                '}';
    }

    //public void sort() {
    //    Collections.sort(grades);
    //}

    //public void addGrade(Points grade) {
    //    grades.add(grade);
    //}
}
