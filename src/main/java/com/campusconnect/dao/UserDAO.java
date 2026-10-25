package com.campusconnect.dao;

import com.campusconnect.config.DatabaseConfig;
import com.campusconnect.model.User;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO {

    public int createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (register_number, email, password_hash, full_name, role, department, academic_year, career_goals, project_interests, club_memberships, bio, mentor_rating, completed_sessions_count, credit_balance, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getRegisterNumber());
            ps.setString(2, user.getEmail().toLowerCase());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getRole().name());
            ps.setString(6, user.getDepartment());
            ps.setInt(7, user.getAcademicYear());
            ps.setString(8, user.getCareerGoals());
            ps.setString(9, user.getProjectInterests());
            ps.setString(10, user.getClubMemberships());
            ps.setString(11, user.getBio());
            ps.setBigDecimal(12, user.getMentorRating() != null ? user.getMentorRating() : BigDecimal.ZERO);
            ps.setInt(13, user.getCompletedSessionsCount());
            ps.setInt(14, user.getCreditBalance());
            ps.setString(15, user.getStatus() != null ? user.getStatus().name() : "ACTIVE");

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    user.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public User getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    public User getUserByEmail(String email) throws SQLException {
        if (email == null) return null;
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    public User getUserByRegisterNumber(String registerNumber) throws SQLException {
        if (registerNumber == null) return null;
        String sql = "SELECT * FROM users WHERE LOWER(register_number) = LOWER(?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, registerNumber.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, department = ?, academic_year = ?, career_goals = ?, project_interests = ?, club_memberships = ?, bio = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getDepartment());
            ps.setInt(3, user.getAcademicYear());
            ps.setString(4, user.getCareerGoals());
            ps.setString(5, user.getProjectInterests());
            ps.setString(6, user.getClubMemberships());
            ps.setString(7, user.getBio());
            ps.setInt(8, user.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePassword(int userId, String newHashedPassword) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHashedPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateRatingAndCount(Connection conn, int userId, BigDecimal newRating, int newCount) throws SQLException {
        String sql = "UPDATE users SET mentor_rating = ?, completed_sessions_count = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newRating);
            ps.setInt(2, newCount);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public int getCreditBalance(Connection conn, int userId) throws SQLException {
        String sql = "SELECT credit_balance FROM users WHERE id = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("credit_balance");
                }
            }
        }
        throw new SQLException("User not found: " + userId);
    }

    public boolean updateCreditBalance(Connection conn, int userId, int newBalance) throws SQLException {
        String sql = "UPDATE users SET credit_balance = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newBalance);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int userId, User.Status status) throws SQLException {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateRole(int userId, User.Role role) throws SQLException {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.name());
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<User> getAllUsers(String search, User.Role role, String department) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (LOWER(full_name) LIKE ? OR LOWER(email) LIKE ? OR LOWER(register_number) LIKE ?) ");
            String pat = "%" + search.trim().toLowerCase() + "%";
            params.add(pat);
            params.add(pat);
            params.add(pat);
        }
        if (role != null) {
            sql.append("AND role = ? ");
            params.add(role.name());
        }
        if (department != null && !department.trim().isEmpty()) {
            sql.append("AND LOWER(department) = LOWER(?) ");
            params.add(department.trim());
        }
        sql.append("ORDER BY id DESC");

        List<User> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapUser(rs));
                }
            }
        }
        return list;
    }

    public Map<String, Object> getPlatformStats() throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT " +
                     "  (SELECT COUNT(*) FROM users) AS total_users, " +
                     "  (SELECT COUNT(*) FROM users WHERE role = 'STUDENT') AS student_count, " +
                     "  (SELECT COUNT(*) FROM users WHERE role = 'SENIOR') AS senior_count, " +
                     "  (SELECT COUNT(*) FROM users WHERE role = 'ALUMNI') AS alumni_count, " +
                     "  (SELECT COUNT(DISTINCT user_id) FROM user_skills WHERE skill_type = 'TEACH') AS active_mentors, " +
                     "  (SELECT COUNT(*) FROM sessions) AS total_sessions, " +
                     "  (SELECT COUNT(*) FROM sessions WHERE status = 'COMPLETED') AS completed_sessions, " +
                     "  (SELECT COUNT(*) FROM resources WHERE status = 'APPROVED') AS total_resources, " +
                     "  (SELECT COUNT(*) FROM doubt_questions) AS total_questions, " +
                     "  (SELECT COUNT(*) FROM projects WHERE status = 'OPEN') AS open_projects, " +
                     "  (SELECT COUNT(*) FROM content_reports WHERE status = 'PENDING') AS pending_reports";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.put("totalUsers", rs.getInt("total_users"));
                stats.put("studentCount", rs.getInt("student_count"));
                stats.put("seniorCount", rs.getInt("senior_count"));
                stats.put("alumniCount", rs.getInt("alumni_count"));
                stats.put("activeMentors", rs.getInt("active_mentors"));
                stats.put("totalSessions", rs.getInt("total_sessions"));
                stats.put("completedSessions", rs.getInt("completed_sessions"));
                stats.put("totalResources", rs.getInt("total_resources"));
                stats.put("totalQuestions", rs.getInt("total_questions"));
                stats.put("openProjects", rs.getInt("open_projects"));
                stats.put("pendingReports", rs.getInt("pending_reports"));
            }
        }
        return stats;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setRegisterNumber(rs.getString("register_number"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getString("full_name"));
        u.setRole(User.Role.valueOf(rs.getString("role")));
        u.setDepartment(rs.getString("department"));
        u.setAcademicYear(rs.getInt("academic_year"));
        u.setCareerGoals(rs.getString("career_goals"));
        u.setProjectInterests(rs.getString("project_interests"));
        u.setClubMemberships(rs.getString("club_memberships"));
        u.setBio(rs.getString("bio"));
        u.setMentorRating(rs.getBigDecimal("mentor_rating"));
        u.setCompletedSessionsCount(rs.getInt("completed_sessions_count"));
        u.setCreditBalance(rs.getInt("credit_balance"));
        u.setStatus(User.Status.valueOf(rs.getString("status")));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        u.setUpdatedAt(rs.getTimestamp("updated_at"));
        return u;
    }
}