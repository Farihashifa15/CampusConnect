package com.campusconnect.service;

import com.campusconnect.dao.AdminAuditDAO;
import com.campusconnect.dao.UserDAO;
import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.AdminAuditLog;
import com.campusconnect.model.ContentReport;
import com.campusconnect.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AdminService {

    private final UserDAO userDAO;
    private final AdminAuditDAO adminAuditDAO;

    public AdminService() {
        this(new UserDAO(), new AdminAuditDAO());
    }

    public AdminService(UserDAO userDAO, AdminAuditDAO adminAuditDAO) {
        this.userDAO = userDAO;
        this.adminAuditDAO = adminAuditDAO;
    }

    public Map<String, Object> getDashboardStatistics() throws SQLException {
        return userDAO.getPlatformStats();
    }

    public boolean updateUserStatus(int adminId, int targetUserId, User.Status status, String reason, String ipAddress)
            throws SQLException, ValidationException {
        if (adminId == targetUserId) {
            throw new ValidationException("Administrators cannot suspend their own account.");
        }
        boolean ok = userDAO.updateStatus(targetUserId, status);
        if (ok) {
            AdminAuditLog log = new AdminAuditLog();
            log.setAdminId(adminId);
            log.setAction("USER_STATUS_CHANGE");
            log.setTargetType("USER");
            log.setTargetId(targetUserId);
            log.setDetails("Changed user status to " + status + ". Reason: " + reason);
            log.setIpAddress(ipAddress);
            adminAuditDAO.logAction(log);
        }
        return ok;
    }

    public boolean updateUserRole(int adminId, int targetUserId, User.Role newRole, String reason, String ipAddress)
            throws SQLException, ValidationException {
        boolean ok = userDAO.updateRole(targetUserId, newRole);
        if (ok) {
            AdminAuditLog log = new AdminAuditLog();
            log.setAdminId(adminId);
            log.setAction("USER_ROLE_CHANGE");
            log.setTargetType("USER");
            log.setTargetId(targetUserId);
            log.setDetails("Changed user role to " + newRole + ". Reason: " + reason);
            log.setIpAddress(ipAddress);
            adminAuditDAO.logAction(log);
        }
        return ok;
    }

    public void fileReport(int reporterId, ContentReport.ContentType contentType, int contentId, String reason) throws SQLException {
        ContentReport r = new ContentReport();
        r.setReporterId(reporterId);
        r.setContentType(contentType);
        r.setContentId(contentId);
        r.setReason(reason != null ? reason.trim() : "Unspecified violation");
        r.setStatus(ContentReport.Status.PENDING);
        adminAuditDAO.createReport(r);
    }

    public List<ContentReport> getReports(ContentReport.Status status) throws SQLException {
        return adminAuditDAO.getReports(status);
    }

    public boolean resolveReport(int adminId, int reportId, boolean dismiss, String ipAddress) throws SQLException {
        ContentReport.Status newStatus = dismiss ? ContentReport.Status.DISMISSED : ContentReport.Status.RESOLVED;
        boolean ok = adminAuditDAO.updateReportStatus(reportId, newStatus);
        if (ok) {
            AdminAuditLog log = new AdminAuditLog();
            log.setAdminId(adminId);
            log.setAction("REPORT_MODERATION");
            log.setTargetType("REPORT");
            log.setTargetId(reportId);
            log.setDetails("Report marked as " + newStatus);
            log.setIpAddress(ipAddress);
            adminAuditDAO.logAction(log);
        }
        return ok;
    }

    public List<AdminAuditLog> getAuditLogs(int limit) throws SQLException {
        return adminAuditDAO.getRecentLogs(limit);
    }
}