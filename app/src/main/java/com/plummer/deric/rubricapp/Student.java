package com.plummer.deric.rubricapp;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

class Student implements Comparable<Student> {

    /***********
     * Student will always be a child of an Assignment.
     * So Student's Rubric will == Assignment's Rubric
     ************/
    private Rubric _rubric;
    private String _firstName;
    private String _lastName;

    /********************************************************
     *  Constructors
     ********************************************************/
    Student(String first, String last, Rubric rubric) {
        this._rubric = rubric;
        this._firstName = first;
        this._lastName = last;
    }

    /********************************************************
     *  Getters
     ********************************************************/
    public String getFirstName() {
        return _firstName;
    }

    public String getLastName() {
        return _lastName;
    }

    public double getAverageGrade() {
        double sum = 0;
        for(Criteria criteria : _rubric.getCriteria()) {
            sum += criteria.getGrade();
        }
        return sum / _rubric.getCriteria().size();
    }

    public Rubric getRubric() {
        return _rubric;
    }


    /********************************************************
     *  Setters
     ********************************************************/
    public void replaceRubric(Rubric rubric) {
        this._rubric = rubric;
    }

    /********************************************************
     *  Member Methods
     ********************************************************/
    @Override
    /**
     * Return 1 if students are the same and return -1 if they arent the same
     */
    public int compareTo(@NonNull Student student) {
        if(this._firstName.equals(student._firstName) && this._lastName.equals(student._lastName)){
           return 1;
        }
        else {
            return 0;
        }
    }
}
