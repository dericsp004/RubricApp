package com.plummer.deric.rubricapp;

import java.util.List;

public class Assignment {
    private Rubric rubric;
    private List<Student> students;

    public Assignment(Rubric rubric) {
        this.rubric = rubric;
    }

    /*
     * Change the rubric for the entire assignment
     */
    public void replaceRubric(Rubric rubric) {
        //Replace own rubric
        this.rubric = rubric;
        //Replace children rubrics
        for (Student student : students) {
            student.replaceRubric(rubric);
        }
    }

    public float getGrade() {
        return 0.0f;
    }

    public void save() {
    }

    public void loadRubric() {
    }

    public void exportToGoogleSheets() {
    }

    public void addStudent(String first, String last) {
        students.add(new Student(first, last, this.rubric));
    }

    public boolean removeStudent(Student stu) {
        return students.remove(stu);
    }
    public Student removeStudent(int stu) {
        return students.remove(stu);
    }
}
