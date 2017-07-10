/*package com.plummer.deric.rubricapp;

import android.content.Context;
import android.widget.NumberPicker;

*//**
 * Created by Deric on 7/9/17.
 *//*

public class NumberPickerListener implements NumberPicker.OnValueChangeListener {
    public Assignment _assignment;
    public int _criteriaLocation;
    public Context context;

    public NumberPickerListener(Assignment assignment, int criteriaLocation, Context context) {
        _assignment = assignment;
        _criteriaLocation = criteriaLocation;
    }
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        _assignment.getRubric().getCriteria().get(_criteriaLocation).setGrade(newVal);
        _assignment.save(context);
    }
}*/
