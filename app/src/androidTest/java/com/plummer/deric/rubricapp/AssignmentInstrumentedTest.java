package com.plummer.deric.rubricapp;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.content.Context;
import android.util.Log;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Deric on 6/19/17.
 */
@RunWith(AndroidJUnit4.class)
public class AssignmentInstrumentedTest {
    @Test
    public void SaveLoadAssignments() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        Assignment assignment1 =  new Assignment("Midterm", "English Section 1", new Rubric("MidtermRubric", "description"));
        for (Integer i = 0; i < 10; i++) {
            assignment1.addStudent("John", i.toString());
        }

        Log.d("SaveLoadAssignments", "Begin to Save Assignment 1");
        assignment1.save(appContext);

        Assignment assignment2 =  new Assignment("Final", "English Section 1", new Rubric("Final", "Description"));
        for (Integer i = 0; i < 5; i++) {
            assignment2.addStudent("John", i.toString());
        }

        Log.d("SaveLoadAssignments", "Begin to Save Assignment 2");
        assignment2.save(appContext);

        List<Assignment> assignments = Assignment.loadAllAssignments(appContext);
        Assignment newAssigment1 = assignments.get(0);
        Assignment newAssigment2 = assignments.get(1);

        for(int i = 0; i < newAssigment1.getStudents().size(); i++){
            Student newStudent = newAssigment1.getStudents().get(i);
            Student oldStudent = assignment1.getStudents().get(i);
            assertEquals(oldStudent.getFirstName(), newStudent.getFirstName());
            assertEquals(oldStudent.getLastName(), newStudent.getLastName());
        }

        for(int i = 0; i < newAssigment2.getStudents().size(); i++){
            Student newStudent = newAssigment2.getStudents().get(i);
            Student oldStudent = assignment2.getStudents().get(i - 1);
            assertEquals(oldStudent.getFirstName(), newStudent.getFirstName());
            assertEquals(oldStudent.getLastName(), newStudent.getLastName());
        }

    }
}
