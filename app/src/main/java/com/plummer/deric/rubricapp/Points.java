package com.plummer.deric.rubricapp;

import android.support.annotation.NonNull;

public class Points  implements Comparable {
    private int value;      //Number of points available
    private String level;   //How proficient is this
    private String requirement; //What it takes to earn these points

    public Points(int value, String level, String requirement) {
        this.value = value;
        this.level = level;
        this.requirement = requirement;
    }

    /**
     * Allows Points to be compared by point value
     * @param o
     * @return
     */
    public int compareTo(@NonNull Points o) {
        if (this.value == o.value)
            return 0;
        else
            return this.value > o.value ? 1 : -1;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return 0;   //Dunno what this is
    }
}
