package com.plummer.deric.rubricapp;

class Student {
    /***********
     * Student will always be a child of an Assignment.
     * So Student's Rubric will == Assignment's Rubric
     ************/
    private Rubric rubric;
    private String first;
    private String last;

    Student(String first, String last, Rubric rubric) {
        this.rubric = rubric;
        this.first = first;
        this.last = last;
    }

    public void replaceRubric(Rubric rubric) {
        this.rubric = rubric;
    }

    public float getGrade() {
        return 0.0f;
    }

    public Rubric getRubric() {
        return rubric;
    }
}
