package com.udea.fe.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskAssignmentTest {

    @Test
    void testSettersAndGetters() {
        TaskAssignment assignment = new TaskAssignment();

        TaskAssignmentId assignmentId = new TaskAssignmentId();
        Task task = new Task();

        assignment.setId(assignmentId);
        assignment.setTask(task);

        assertEquals(assignmentId, assignment.getId());
        assertEquals(task, assignment.getTask());
    }

    @Test
    void testConstructorAndFields() {
        TaskAssignment assignment = new TaskAssignment();

        TaskAssignmentId id = new TaskAssignmentId();
        Task task = new Task();
        task.setTaskId(1L); // supondremos que tienes un método setId()

        assignment.setId(id);
        assignment.setTask(task);

        assertNotNull(assignment.getId());
        assertEquals(1L, assignment.getId().getTaskId());
        assertEquals(2L, assignment.getId().getUserId());
        assertEquals(task, assignment.getTask());
    }
}
