package com.campusconnect.dao;

import com.campusconnect.config.DatabaseConfig;
import com.campusconnect.model.Project;
import com.campusconnect.model.ProjectApplication;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {

    public int createProject(Project p) throws SQLException {
        String sql = "INSERT INTO projects (creator_id, title, description, category, required_skills, available_positions, filled_positions, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getCreatorId());
            ps.setString(2, p.getTitle().trim());
            ps.setString(3, p.getDescription().trim());
            ps.setString(4, p.getCategory().trim());
            ps.setString(5, p.getRequiredSkills().trim());
            ps.setInt(6, p.getAvailablePositions());
            ps.setInt(7, p.getFilledPositions());
            ps.setString(8, p.getStatus().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    p.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public Project getProjectById(int id) throws SQLException {
        String sql = "SELECT p.*, u.full_name AS creator_name, u.role AS creator_role, " +
                     "(SELECT COUNT(*) FROM project_applications a WHERE a.project_id = p.id) AS application_count " +
                     "FROM projects p " +
                     "JOIN users u ON p.creator_id = u.id WHERE p.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapProject(rs);
                }
            }
        }
        return null;
    }

    public List<Project> getProjects(String keyword, String category, Project.Status status, int limit, int offset) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT p.*, u.full_name AS creator_name, u.role AS creator_role, ")
            .append("(SELECT COUNT(*) FROM project_applications a WHERE a.project_id = p.id) AS application_count ")
            .append("FROM projects p ")
            .append("JOIN users u ON p.creator_id = u.id WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(p.title) LIKE ? OR LOWER(p.description) LIKE ? OR LOWER(p.required_skills) LIKE ?) ");
            String pat = "%" + keyword.trim().toLowerCase() + "%";
            params.add(pat);
            params.add(pat);
            params.add(pat);
        }
        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("ALL")) {
            sql.append("AND LOWER(p.category) = LOWER(?) ");
            params.add(category.trim());
        }
        if (status != null) {
            sql.append("AND p.status = ? ");
            params.add(status.name());
        }
        sql.append("ORDER BY p.created_at DESC LIMIT ? OFFSET ?");

        List<Project> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object o : params) {
                ps.setObject(idx++, o);
            }
            ps.setInt(idx++, limit > 0 ? limit : 50);
            ps.setInt(idx, Math.max(offset, 0));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapProject(rs));
                }
            }
        }
        return list;
    }

    public List<Project> getProjectsByCreator(int creatorId) throws SQLException {
        String sql = "SELECT p.*, u.full_name AS creator_name, u.role AS creator_role, " +
                     "(SELECT COUNT(*) FROM project_applications a WHERE a.project_id = p.id) AS application_count " +
                     "FROM projects p JOIN users u ON p.creator_id = u.id WHERE p.creator_id = ? ORDER BY p.created_at DESC";
        List<Project> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, creatorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapProject(rs));
                }
            }
        }
        return list;
    }

    public int createApplication(ProjectApplication app) throws SQLException {
        String sql = "INSERT INTO project_applications (project_id, applicant_id, pitch, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, app.getProjectId());
            ps.setInt(2, app.getApplicantId());
            ps.setString(3, app.getPitch().trim());
            ps.setString(4, app.getStatus().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    app.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public ProjectApplication getApplication(int projectId, int applicantId) throws SQLException {
        String sql = "SELECT pa.*, p.title AS project_title, u.full_name AS applicant_name, u.department AS applicant_department " +
                     "FROM project_applications pa " +
                     "JOIN projects p ON pa.project_id = p.id " +
                     "JOIN users u ON pa.applicant_id = u.id " +
                     "WHERE pa.project_id = ? AND pa.applicant_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ps.setInt(2, applicantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapApplication(rs);
                }
            }
        }
        return null;
    }

    public ProjectApplication getApplicationById(int id) throws SQLException {
        String sql = "SELECT pa.*, p.title AS project_title, u.full_name AS applicant_name, u.department AS applicant_department " +
                     "FROM project_applications pa " +
                     "JOIN projects p ON pa.project_id = p.id " +
                     "JOIN users u ON pa.applicant_id = u.id " +
                     "WHERE pa.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapApplication(rs);
                }
            }
        }
        return null;
    }

    public List<ProjectApplication> getApplicationsForProject(int projectId) throws SQLException {
        String sql = "SELECT pa.*, p.title AS project_title, u.full_name AS applicant_name, u.department AS applicant_department " +
                     "FROM project_applications pa " +
                     "JOIN projects p ON pa.project_id = p.id " +
                     "JOIN users u ON pa.applicant_id = u.id " +
                     "WHERE pa.project_id = ? ORDER BY pa.created_at DESC";
        List<ProjectApplication> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapApplication(rs));
                }
            }
        }
        return list;
    }

    public List<ProjectApplication> getApplicationsByUser(int applicantId) throws SQLException {
        String sql = "SELECT pa.*, p.title AS project_title, u.full_name AS applicant_name, u.department AS applicant_department " +
                     "FROM project_applications pa " +
                     "JOIN projects p ON pa.project_id = p.id " +
                     "JOIN users u ON pa.applicant_id = u.id " +
                     "WHERE pa.applicant_id = ? ORDER BY pa.created_at DESC";
        List<ProjectApplication> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, applicantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapApplication(rs));
                }
            }
        }
        return list;
    }

    public boolean updateApplicationStatus(Connection conn, int applicationId, ProjectApplication.Status status) throws SQLException {
        String sql = "UPDATE project_applications SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, applicationId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean incrementFilledPositions(Connection conn, int projectId) throws SQLException {
        String sql = "UPDATE projects SET filled_positions = filled_positions + 1, " +
                     "status = CASE WHEN filled_positions + 1 >= available_positions THEN 'FILLED' ELSE status END " +
                     "WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            return ps.executeUpdate() > 0;
        }
    }

    private Project mapProject(ResultSet rs) throws SQLException {
        Project p = new Project();
        p.setId(rs.getInt("id"));
        p.setCreatorId(rs.getInt("creator_id"));
        p.setCreatorName(rs.getString("creator_name"));
        p.setCreatorRole(rs.getString("creator_role"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setCategory(rs.getString("category"));
        p.setRequiredSkills(rs.getString("required_skills"));
        p.setAvailablePositions(rs.getInt("available_positions"));
        p.setFilledPositions(rs.getInt("filled_positions"));
        p.setStatus(Project.Status.valueOf(rs.getString("status")));
        p.setApplicationCount(rs.getInt("application_count"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));
        return p;
    }

    private ProjectApplication mapApplication(ResultSet rs) throws SQLException {
        ProjectApplication app = new ProjectApplication();
        app.setId(rs.getInt("id"));
        app.setProjectId(rs.getInt("project_id"));
        app.setProjectTitle(rs.getString("project_title"));
        app.setApplicantId(rs.getInt("applicant_id"));
        app.setApplicantName(rs.getString("applicant_name"));
        app.setApplicantDepartment(rs.getString("applicant_department"));
        app.setPitch(rs.getString("pitch"));
        app.setStatus(ProjectApplication.Status.valueOf(rs.getString("status")));
        app.setCreatedAt(rs.getTimestamp("created_at"));
        return app;
    }
}