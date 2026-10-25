package com.campusconnect.service;

import com.campusconnect.config.AppConfig;
import com.campusconnect.config.DatabaseConfig;
import com.campusconnect.dao.SkillCreditDAO;
import com.campusconnect.dao.UserDAO;
import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.SkillCreditTransaction;
import com.campusconnect.model.User;
import com.campusconnect.util.PasswordUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {

    private final UserDAO userDAO;
    private final SkillCreditDAO creditDAO;

    public UserService() {
        this(new UserDAO(), new SkillCreditDAO());
    }

    public UserService(UserDAO userDAO, SkillCreditDAO creditDAO) {
        this.userDAO = userDAO;
        this.creditDAO = creditDAO;
    }

    public User registerUser(String registerNumber, String email, String password, String fullName,
                             User.Role role, String department, int academicYear) throws SQLException, ValidationException {
        // Validation
        if (registerNumber == null || registerNumber.trim().isEmpty()) {
            throw new ValidationException("University register number is required.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("University email is required.");
        }
        email = email.trim().toLowerCase();

        // Enforce university email domain
        String allowedDomain = AppConfig.getUniversityEmailDomain().toLowerCase();
        if (!email.endsWith(allowedDomain)) {
            throw new ValidationException("Registration requires an authorized university email ending with: " + allowedDomain);
        }

        if (password == null || password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ValidationException("Full name is required.");
        }
        if (department == null || department.trim().isEmpty()) {
            throw new ValidationException("Department is required.");
        }

        // Server-side security check: public registration CANNOT create ADMIN
        if (role == User.Role.ADMIN) {
            throw new ValidationException("Administrator accounts cannot be created via public registration.");
        }
        if (role == null) {
            role = User.Role.STUDENT;
        }

        // Duplicate checks
        if (userDAO.getUserByEmail(email) != null) {
            throw new ValidationException("An account with this email address already exists.");
        }
        if (userDAO.getUserByRegisterNumber(registerNumber) != null) {
            throw new ValidationException("An account with this register number already exists.");
        }

        String hashedPassword = PasswordUtil.hashPassword(password);

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            User user = new User();
            user.setRegisterNumber(registerNumber.trim().toUpperCase());
            user.setEmail(email);
            user.setPasswordHash(hashedPassword);
            user.setFullName(fullName.trim());
            user.setRole(role);
            user.setDepartment(department.trim());
            user.setAcademicYear(Math.max(academicYear, 1));
            user.setCreditBalance(50); // Welcome bonus credits
            user.setStatus(User.Status.ACTIVE);

            int userId = userDAO.createUser(user);
            user.setId(userId);

            // Record initial welcome credit transaction in ledger
            SkillCreditTransaction tx = new SkillCreditTransaction();
            tx.setUserId(userId);
            tx.setAmount(50);
            tx.setTransactionType(SkillCreditTransaction.Type.INITIAL_GRANT);
            tx.setReason("Welcome bonus upon university registration");
            tx.setRelatedEntityType("USER");
            tx.setRelatedEntityId(userId);
            creditDAO.recordTransaction(conn, tx);

            conn.commit();
            return user;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            if (e instanceof ValidationException) throw (ValidationException) e;
            throw new SQLException("Registration failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public User authenticate(String email, String plainPassword) throws SQLException, ValidationException {
        if (email == null || plainPassword == null) {
            throw new ValidationException("Email and password are required.");
        }
        User user = userDAO.getUserByEmail(email.trim());
        if (user == null) {
            throw new ValidationException("Invalid email or password.");
        }
        if (user.getStatus() == User.Status.SUSPENDED) {
            throw new ValidationException("Your account has been suspended by an administrator. Please contact support.");
        }
        if (!PasswordUtil.checkPassword(plainPassword, user.getPasswordHash())) {
            throw new ValidationException("Invalid email or password.");
        }
        return user;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) throws SQLException, ValidationException {
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new ValidationException("User not found.");
        }
        if (!PasswordUtil.checkPassword(oldPassword, user.getPasswordHash())) {
            throw new ValidationException("Current password is incorrect.");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new ValidationException("New password must be at least 6 characters.");
        }
        String hashed = PasswordUtil.hashPassword(newPassword);
        return userDAO.updatePassword(userId, hashed);
    }

    public boolean updateProfile(User user) throws SQLException, ValidationException {
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new ValidationException("Full name cannot be empty.");
        }
        return userDAO.updateUser(user);
    }

    public User getUserById(int id) throws SQLException {
        return userDAO.getUserById(id);
    }

    public List<User> searchUsers(String query, User.Role role, String department) throws SQLException {
        return userDAO.getAllUsers(query, role, department);
    }
}