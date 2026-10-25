package com.campusconnect;

import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.User;
import com.campusconnect.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class AuthWorkflowTest {

    private UserService userService;

    @BeforeEach
    public void setUp() {
        TestDatabaseHelper.setupTestDatabase();
        userService = new UserService();
    }

    @Test
    @DisplayName("TC01: Valid user registration with initial credit grant")
    public void testValidRegistration() throws SQLException, ValidationException {
        User user = userService.registerUser(
            "REG-2024-001",
            "john.doe@university.edu",
            "securePass123",
            "John Doe",
            User.Role.STUDENT,
            "Computer Science",
            2
        );

        assertNotNull(user);
        assertTrue(user.getId() > 0);
        assertEquals("JOHN DOE", user.getFullName().toUpperCase());
        assertEquals(50, user.getCreditBalance());
        assertEquals(User.Role.STUDENT, user.getRole());

        // Verify successful login
        User authenticated = userService.authenticate("john.doe@university.edu", "securePass123");
        assertNotNull(authenticated);
        assertEquals(user.getId(), authenticated.getId());
    }

    @Test
    @DisplayName("TC02: Invalid login attempts (bad password, non-existent user)")
    public void testInvalidLogin() throws SQLException, ValidationException {
        userService.registerUser(
            "REG-2024-002",
            "alice@university.edu",
            "mypassword",
            "Alice Smith",
            User.Role.STUDENT,
            "Information Technology",
            1
        );

        // Wrong password
        ValidationException ex1 = assertThrows(ValidationException.class, () ->
            userService.authenticate("alice@university.edu", "wrongpass")
        );
        assertTrue(ex1.getMessage().contains("Invalid email or password"));

        // Non-existent user
        ValidationException ex2 = assertThrows(ValidationException.class, () ->
            userService.authenticate("unknown@university.edu", "mypassword")
        );
        assertTrue(ex2.getMessage().contains("Invalid email or password"));
    }

    @Test
    @DisplayName("Security: Enforce university email domain & prevent public admin registration")
    public void testRegistrationRestrictions() {
        // Non-university email domain rejected
        ValidationException domainEx = assertThrows(ValidationException.class, () ->
            userService.registerUser("REG-999", "hacker@gmail.com", "pass123", "Hacker", User.Role.STUDENT, "CS", 1)
        );
        assertTrue(domainEx.getMessage().contains("authorized university email"));

        // Attempting to register as ADMIN via public registration must be rejected
        ValidationException adminEx = assertThrows(ValidationException.class, () ->
            userService.registerUser("REG-ADM", "newadmin@university.edu", "pass123", "Fake Admin", User.Role.ADMIN, "CS", 4)
        );
        assertTrue(adminEx.getMessage().contains("Administrator accounts cannot be created via public registration"));
    }

    @Test
    @DisplayName("Duplicate Prevention: Email and Register Number must be unique")
    public void testDuplicateRegistrationPrevention() throws SQLException, ValidationException {
        userService.registerUser("REG-DUP1", "user1@university.edu", "pass123", "User One", User.Role.STUDENT, "CS", 1);

        // Duplicate email
        ValidationException dupEmailEx = assertThrows(ValidationException.class, () ->
            userService.registerUser("REG-DIFF", "user1@university.edu", "pass123", "User Diff", User.Role.STUDENT, "CS", 1)
        );
        assertTrue(dupEmailEx.getMessage().contains("email address already exists"));

        // Duplicate register number
        ValidationException dupRegEx = assertThrows(ValidationException.class, () ->
            userService.registerUser("REG-DUP1", "user2@university.edu", "pass123", "User Two", User.Role.STUDENT, "CS", 1)
        );
        assertTrue(dupRegEx.getMessage().contains("register number already exists"));
    }
}