package com.udea.fe.entity;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testAuthoritiesForStudent() {
        List<GrantedAuthority> authorities = Role.STUDENT.getAuthorities();
        assertEquals(1, authorities.size());
        assertEquals("ROLE_STUDENT", authorities.get(0).getAuthority());
    }

    @Test
    void testAuthoritiesForTeacher() {
        List<GrantedAuthority> authorities = Role.TEACHER.getAuthorities();
        assertEquals(1, authorities.size());
        assertEquals("ROLE_TEACHER", authorities.get(0).getAuthority());
    }

    @Test
    void testAuthoritiesForAdmin() {
        List<GrantedAuthority> authorities = Role.ADMIN.getAuthorities();
        assertEquals(1, authorities.size());
        assertEquals("ROLE_ADMIN", authorities.get(0).getAuthority());
    }
}
