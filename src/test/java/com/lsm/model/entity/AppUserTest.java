package com.lsm.model.entity;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;

public class AppUserTest {

    private AppUser appUser;

    @BeforeEach
    public void setUp() {
        // Create a sample AppUser for testing
        appUser = new AppUser("testUser", "test@example.com", "password123", Role.ROLE_STUDENT);
    }

    @Test
    public void testGetUsername() {
        assertEquals("testUser", appUser.getUsername());
    }

    @Test
    public void testGetEmail() {
        assertEquals("test@example.com", appUser.getEmail());
    }

    @Test
    public void testPasswordHashing() {
        // Ensure that password is stored as a hashed value
        assertNotEquals("password123", appUser.getPassword());

        // Ensure that password can be validated correctly
        assertTrue(appUser.checkPassword("password123"));
        assertFalse(appUser.checkPassword("wrongPassword"));
    }

    @Test
    public void testSetPassword() {
        appUser.setPassword("newPassword");
        assertTrue(appUser.checkPassword("newPassword"));
        assertFalse(appUser.checkPassword("password123"));
    }

    @Test
    public void testGetRole() {
        assertEquals(Role.ROLE_STUDENT, appUser.getRole());
    }

    @Test
    public void testGetAuthorities() {
        Collection<? extends GrantedAuthority> authorities = appUser.getAuthorities();
        assertEquals(1, authorities.size());
        assertEquals("ROLE_STUDENT", authorities.iterator().next().getAuthority());
    }

    @Test
    public void testUserDetailsMethods() {
        // These methods return true by default as specified in the AppUser implementation
        assertTrue(appUser.isAccountNonExpired());
        assertTrue(appUser.isAccountNonLocked());
        assertTrue(appUser.isCredentialsNonExpired());
        assertTrue(appUser.isEnabled());
    }

    @Test
    public void testSetRole() {
        appUser.setRole(Role.ROLE_TEACHER);
        assertEquals(Role.ROLE_TEACHER, appUser.getRole());
    }
}
