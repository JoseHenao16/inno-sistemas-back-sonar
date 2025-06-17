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
        Task task = new Task();
        task.setTaskId(1L);
        task.setName("Test Task");

        TaskAssignmentId id = new TaskAssignmentId();
        id.setTaskId(1L);
        id.setAssignedType("USER");
        id.setAssignedId(2L);

        TaskAssignment assignment = new TaskAssignment();
        assignment.setId(id);
        assignment.setTask(task);

        assertEquals(1L, assignment.getId().getTaskId());
        assertEquals("USER", assignment.getId().getAssignedType());
        assertEquals(2L, assignment.getId().getAssignedId());
        assertEquals(task, assignment.getTask());
    }
}
