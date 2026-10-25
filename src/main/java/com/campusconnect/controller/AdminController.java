package com.campusconnect.controller;

import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.ContentReport;
import com.campusconnect.model.User;
import com.campusconnect.service.AdminService;
import com.campusconnect.service.CreditLedgerService;
import com.campusconnect.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/*")
public class AdminController extends HttpServlet {

    private final AdminService adminService = new AdminService();
    private final UserService userService = new UserService();
    private final CreditLedgerService creditService = new CreditLedgerService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            if ("/users".equals(pathInfo)) {
                handleUserList(req, resp);
            } else if ("/reports".equals(pathInfo)) {
                handleReportList(req, resp);
            } else if ("/audit".equals(pathInfo)) {
                handleAuditLogs(req, resp);
            } else {
                handleDashboard(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Admin error: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/admin/index.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentAdmin = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentAdmin == null || currentAdmin.getRole() != User.Role.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied.");
            return;
        }

        String pathInfo = req.getPathInfo();
        String ipAddress = req.getRemoteAddr();

        try {
            if ("/user/status".equals(pathInfo)) {
                int targetId = Integer.parseInt(req.getParameter("userId"));
                String statusStr = req.getParameter("status");
                String reason = req.getParameter("reason");
                User.Status newStatus = User.Status.valueOf(statusStr.toUpperCase());

                adminService.updateUserStatus(currentAdmin.getId(), targetId, newStatus, reason, ipAddress);
                req.getSession().setAttribute("successMessage", "User status changed to " + newStatus);
                resp.sendRedirect(req.getContextPath() + "/admin/users");
            } else if ("/user/role".equals(pathInfo)) {
                int targetId = Integer.parseInt(req.getParameter("userId"));
                String roleStr = req.getParameter("role");
                String reason = req.getParameter("reason");
                User.Role newRole = User.Role.valueOf(roleStr.toUpperCase());

                adminService.updateUserRole(currentAdmin.getId(), targetId, newRole, reason, ipAddress);
                req.getSession().setAttribute("successMessage", "User role changed to " + newRole);
                resp.sendRedirect(req.getContextPath() + "/admin/users");
            } else if ("/report/resolve".equals(pathInfo)) {
                int reportId = Integer.parseInt(req.getParameter("reportId"));
                boolean dismiss = "dismiss".equalsIgnoreCase(req.getParameter("action"));

                adminService.resolveReport(currentAdmin.getId(), reportId, dismiss, ipAddress);
                req.getSession().setAttribute("successMessage", "Report marked as " + (dismiss ? "DISMISSED" : "RESOLVED"));
                resp.sendRedirect(req.getContextPath() + "/admin/reports");
            } else if ("/credits/adjust".equals(pathInfo)) {
                int targetId = Integer.parseInt(req.getParameter("userId"));
                int amount = Integer.parseInt(req.getParameter("amount"));
                String reason = req.getParameter("reason");

                creditService.adminAdjustCredits(currentAdmin.getId(), targetId, amount, reason);
                req.getSession().setAttribute("successMessage", "Credit balance adjusted successfully.");
                resp.sendRedirect(req.getContextPath() + "/admin/users");
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin");
            }
        } catch (ValidationException e) {
            req.getSession().setAttribute("errorMessage", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        } catch (Exception e) {
            req.getSession().setAttribute("errorMessage", "Admin operation failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin");
        }
    }

    private void handleDashboard(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Map<String, Object> stats = adminService.getDashboardStatistics();
        req.setAttribute("stats", stats);
        req.setAttribute("pendingReports", adminService.getReports(ContentReport.Status.PENDING));
        req.setAttribute("recentLogs", adminService.getAuditLogs(10));
        req.getRequestDispatcher("/WEB-INF/views/admin/index.jsp").forward(req, resp);
    }

    private void handleUserList(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String search = req.getParameter("search");
        String roleStr = req.getParameter("role");
        String dept = req.getParameter("department");

        User.Role role = null;
        if (roleStr != null && !roleStr.trim().isEmpty() && !"ALL".equalsIgnoreCase(roleStr)) {
            try { role = User.Role.valueOf(roleStr.trim().toUpperCase()); } catch (Exception ignored) {}
        }

        List<User> users = userService.searchUsers(search, role, dept);
        req.setAttribute("users", users);
        req.setAttribute("search", search);
        req.setAttribute("selectedRole", roleStr);
        req.setAttribute("selectedDept", dept);
        req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
    }

    private void handleReportList(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String statusStr = req.getParameter("status");
        ContentReport.Status status = ContentReport.Status.PENDING;
        if (statusStr != null && !statusStr.isEmpty() && !"ALL".equalsIgnoreCase(statusStr)) {
            try { status = ContentReport.Status.valueOf(statusStr.toUpperCase()); } catch (Exception ignored) {}
        }

        req.setAttribute("reports", adminService.getReports("ALL".equalsIgnoreCase(statusStr) ? null : status));
        req.setAttribute("selectedStatus", statusStr);
        req.getRequestDispatcher("/WEB-INF/views/admin/reports.jsp").forward(req, resp);
    }

    private void handleAuditLogs(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.setAttribute("auditLogs", adminService.getAuditLogs(100));
        req.getRequestDispatcher("/WEB-INF/views/admin/audit.jsp").forward(req, resp);
    }
}