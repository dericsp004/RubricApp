package com.plummer.deric.rubricapp;

import java.util.List;

public class Assignment {
    private Rubric rubric;
    private List<Student> students;

    public Assignment(Rubric rubric) {
        this.rubric = rubric;
    }

    public List<Student> getStudents() {
        return students;
    }

    /*
         * Change the rubric for the entire assignment
         */
    public void setRubric(Rubric rubric) {
        //Replace own rubric
        this.rubric = rubric;
        //Replace children rubrics
        for (Student student : students) {
            student.replaceRubric(rubric);
        }
    }

    public void save() {
    }

    public void loadRubric() {
    }

    public void exportToGoogleSheets() {
    }

    /**
     * Create a new student with this Assignment's Rubric
     * @param first
     * @param last
     */
    public void addStudent(String first, String last) {
        students.add(new Student(first, last, this.rubric));
    }


    /**
     * Remove a student of the provided name
     * @param first
     * @param last
     * @return
     */
    public boolean removeStudent(String first, String last) {
        return students.remove(new Student(first, last, this.rubric));
    }

    public Rubric getRubric() {
        return rubric;
    }

    /**
     * Remove a student of X place in the list
     * @param stu
     * @return The result of students.remove()
     */
    public Student removeStudent(int stu) {
        return students.remove(stu);
    }
}
