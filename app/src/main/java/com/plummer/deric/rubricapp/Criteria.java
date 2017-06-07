package com.plummer.deric.rubricapp;

import java.util.Collections;
import java.util.List;

class Criteria {
    String name;
    List<Points> grades;


    public void sort() {
        Collections.sort(grades);
    }
}
