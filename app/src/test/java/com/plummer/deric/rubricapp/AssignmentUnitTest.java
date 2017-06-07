package com.plummer.deric.rubricapp;

/**
 * Created by Deric on 6/7/17.
 */
import org.junit.Test;
import static org.junit.Assert.*;


public class AssignmentUnitTest {

    @Test
    public void CreateNewAssignmentWithStudentsTest () {
        Assignment assignment =  new Assignment(new Rubric());
        for (int i = 0; i < 10; i++) {
            assignment.addStudent("John", "Doe");
        }

        for (Student students : assignment.getStudents()) {
            assertEquals(assignment.getRubric(), students.getRubric());
        }
    }

    @Test
    public void SaveAssingmentTest () {
        Assignment assignment =  new Assignment(new Rubric());
        assignment.save();

        //Check assingment is saved correctly

    }


}
