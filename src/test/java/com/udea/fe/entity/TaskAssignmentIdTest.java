package com.udea.fe.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskAssignmentIdTest {

    @Test
    void testSettersAndGetters() {
        TaskAssignmentId id = new TaskAssignmentId();

        id.setTaskId(10L);
        id.setAssignedType("STUDENT");
        id.setAssignedId(5L);

        assertEquals(10L, id.getTaskId());
        assertEquals("STUDENT", id.getAssignedType());
        assertEquals(5L, id.getAssignedId());
    }

    @Test
    void testEqualityAndHashCode() {
        TaskAssignmentId id1 = new TaskAssignmentId();
        id1.setTaskId(1L);
        id1.setAssignedType("STUDENT");
        id1.setAssignedId(100L);

        TaskAssignmentId id2 = new TaskAssignmentId();
        id2.setTaskId(1L);
        id2.setAssignedType("STUDENT");
        id2.setAssignedId(100L);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }
}