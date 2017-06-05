package com.plummer.deric.rubricapp;

import java.util.List;

public class Rubric {
    private List<Criteria> criteria;

    public void addCriteria(Criteria newCriteria) {
        criteria.add(newCriteria);
    }
}
