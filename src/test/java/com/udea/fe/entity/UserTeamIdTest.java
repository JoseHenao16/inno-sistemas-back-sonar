package com.udea.fe.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTeamIdTest {

    @Test
    void testNoArgsConstructor() {
        UserTeamId id = new UserTeamId();
        assertNotNull(id);
    }

    @Test
    void testAllArgsConstructor() {
        UserTeamId id = new UserTeamId(10L, 20L);
        assertEquals(10L, id.getUserId());
        assertEquals(20L, id.getTeamId());
    }

    @Test
    void testSettersAndGetters() {
        UserTeamId id = new UserTeamId();
        id.setUserId(5L);
        id.setTeamId(15L);

        assertEquals(5L, id.getUserId());
        assertEquals(15L, id.getTeamId());
    }
}
