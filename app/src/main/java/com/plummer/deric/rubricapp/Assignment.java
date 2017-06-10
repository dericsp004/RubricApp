package com.plummer.deric.rubricapp;

import java.text.Format;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Assignment {
    private String _assignmentName;
    private Rubric _rubric;
    private List<Student> _students;

    /********************************************************
     *  Constructors
     ********************************************************/
    /**
     * @param assignmentName
     * @param rubric
     */
    public Assignment(String assignmentName, Rubric rubric) {
        this._assignmentName = assignmentName;
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
            student.replaceRubric(rubric);
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
        _students.add(new Student(first, last, this._rubric));
    }


    /**
     * Remove a student of the provided name
     * @param firstName
     * @param lastName
     * @return
     */
    public boolean containsStudent(String firstName, String lastName) {
        boolean contains = false;
        for (Student student : _students) {
            int result = student.compareTo(new Student(firstName, lastName, _rubric));
            if (result == 1) {
                contains = true;
            }
        }

        return contains;
    }

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

    public void save() {
    }

    public void loadRubric() {
    }

    public void exportToGoogleSheets() {
    }

    public String toString() {
        String studentsString = "";
        for(Student student : _students){
            studentsString += "\t" + student.getLastName() + ", " + student.getFirstName() + "\n";
        }
        return String.format("Assignment Name: %s \nEnrollemnts: \n %s", _assignmentName, studentsString);
    }
}
