package com.campusconnect.dao;

import com.campusconnect.config.DatabaseConfig;
import com.campusconnect.model.AdminAuditLog;
import com.campusconnect.model.ContentReport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminAuditDAO {

    public int createReport(ContentReport r) throws SQLException {
        String sql = "INSERT INTO content_reports (reporter_id, content_type, content_id, reason, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getReporterId());
            ps.setString(2, r.getContentType().name());
            ps.setInt(3, r.getContentId());
            ps.setString(4, r.getReason());
            ps.setString(5, r.getStatus().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    r.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public List<ContentReport> getReports(ContentReport.Status status) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT cr.*, u.full_name AS reporter_name FROM content_reports cr ")
            .append("JOIN users u ON cr.reporter_id = u.id WHERE 1=1 ");
        if (status != null) {
            sql.append("AND cr.status = ? ");
        }
        sql.append("ORDER BY cr.created_at DESC");

        List<ContentReport> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (status != null) {
                ps.setString(1, status.name());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ContentReport r = new ContentReport();
                    r.setId(rs.getInt("id"));
                    r.setReporterId(rs.getInt("reporter_id"));
                    r.setReporterName(rs.getString("reporter_name"));
                    r.setContentType(ContentReport.ContentType.valueOf(rs.getString("content_type")));
                    r.setContentId(rs.getInt("content_id"));
                    r.setReason(rs.getString("reason"));
                    r.setStatus(ContentReport.Status.valueOf(rs.getString("status")));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(r);
                }
            }
        }
        return list;
    }

    public boolean updateReportStatus(int reportId, ContentReport.Status status) throws SQLException {
        String sql = "UPDATE content_reports SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, reportId);
            return ps.executeUpdate() > 0;
        }
    }

    public int logAction(AdminAuditLog log) throws SQLException {
        String sql = "INSERT INTO admin_audit_logs (admin_id, action, target_type, target_id, details, ip_address) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, log.getAdminId());
            ps.setString(2, log.getAction());
            ps.setString(3, log.getTargetType());
            if (log.getTargetId() != null) {
                ps.setInt(4, log.getTargetId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, log.getDetails());
            ps.setString(6, log.getIpAddress());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    log.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public List<AdminAuditLog> getRecentLogs(int limit) throws SQLException {
        String sql = "SELECT al.*, u.full_name AS admin_name FROM admin_audit_logs al " +
                     "JOIN users u ON al.admin_id = u.id ORDER BY al.created_at DESC LIMIT ?";
        List<AdminAuditLog> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit > 0 ? limit : 50);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AdminAuditLog l = new AdminAuditLog();
                    l.setId(rs.getInt("id"));
                    l.setAdminId(rs.getInt("admin_id"));
                    l.setAdminName(rs.getString("admin_name"));
                    l.setAction(rs.getString("action"));
                    l.setTargetType(rs.getString("target_type"));
                    int targetId = rs.getInt("target_id");
                    if (!rs.wasNull()) {
                        l.setTargetId(targetId);
                    }
                    l.setDetails(rs.getString("details"));
                    l.setIpAddress(rs.getString("ip_address"));
                    l.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(l);
                }
            }
        }
        return list;
    }
}