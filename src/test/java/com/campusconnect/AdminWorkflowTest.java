package com.campusconnect;

import com.campusconnect.dao.UserDAO;
import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.AdminAuditLog;
import com.campusconnect.model.User;
import com.campusconnect.service.AdminService;
import com.campusconnect.service.UserService;
import com.campusconnect.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AdminWorkflowTest {

    private AdminService adminService;
    private UserService userService;
    private UserDAO userDAO;
    private User adminUser;
    private User testStudent;

    @BeforeEach
    public void setUp() throws Exception {
        TestDatabaseHelper.setupTestDatabase();
        adminService = new AdminService();
        userService = new UserService();
        userDAO = new UserDAO();

        // Create Admin user directly in DB
        adminUser = new User();
        adminUser.setRegisterNumber("REG-ADMIN-01");
        adminUser.setEmail("admin@university.edu");
        adminUser.setPasswordHash(PasswordUtil.hashPassword("adminPass123"));
        adminUser.setFullName("Dr. Alan Turing");
        adminUser.setRole(User.Role.ADMIN);
        adminUser.setDepartment("Computer Science");
        adminUser.setAcademicYear(5);
        int adminId = userDAO.createUser(adminUser);
        adminUser.setId(adminId);

        // Create target student
        testStudent = userService.registerUser("REG-ST1", "student.admin@university.edu", "pass123", "Target Student", User.Role.STUDENT, "CS", 2);
    }

    @Test
    @DisplayName("TC10: Admin operations, account suspension, role change, and audit logging")
    public void testAdminOperationsAndAuditLogs() throws SQLException, ValidationException {
        int adminId = adminUser.getId();

        // 1. Suspend student account
        boolean suspended = adminService.updateUserStatus(adminId, testStudent.getId(), User.Status.SUSPENDED, "Violated code of conduct", "127.0.0.1");
        assertTrue(suspended);

        // Verify suspended user cannot log in
        ValidationException loginEx = assertThrows(ValidationException.class, () ->
            userService.authenticate("student.admin@university.edu", "pass123")
        );
        assertTrue(loginEx.getMessage().contains("suspended"));

        // 2. Promote to SENIOR role
        boolean promoted = adminService.updateUserRole(adminId, testStudent.getId(), User.Role.SENIOR, "Eligible 4th year senior", "127.0.0.1");
        assertTrue(promoted);

        User refreshed = userService.getUserById(testStudent.getId());
        assertEquals(User.Role.SENIOR, refreshed.getRole());

        // 3. Verify audit log entry was created
        List<AdminAuditLog> logs = adminService.getAuditLogs(10);
        assertNotNull(logs);
        assertTrue(logs.size() >= 2);
        assertEquals("USER_ROLE_CHANGE", logs.get(0).getAction());

        // 4. Test platform dashboard statistics
        Map<String, Object> stats = adminService.getDashboardStatistics();
        assertNotNull(stats);
        assertTrue((int) stats.get("totalUsers") >= 2);
    }
}