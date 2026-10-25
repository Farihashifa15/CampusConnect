package com.campusconnect.controller;

import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.ContentReport;
import com.campusconnect.model.Project;
import com.campusconnect.model.ProjectApplication;
import com.campusconnect.model.User;
import com.campusconnect.service.AdminService;
import com.campusconnect.service.ProjectService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/projects/*")
public class ProjectController extends HttpServlet {

    private final ProjectService projectService = new ProjectService();
    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            if ("/new".equals(pathInfo)) {
                req.getRequestDispatcher("/WEB-INF/views/projects/new.jsp").forward(req, resp);
            } else if ("/view".equals(pathInfo)) {
                handleView(req, resp);
            } else {
                handleList(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/projects/index.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String pathInfo = req.getPathInfo();
        try {
            if ("/new".equals(pathInfo)) {
                handleCreate(req, resp, currentUser);
            } else if ("/apply".equals(pathInfo)) {
                handleApply(req, resp, currentUser);
            } else if ("/application/respond".equals(pathInfo)) {
                handleRespondApplication(req, resp, currentUser);
            } else if ("/report".equals(pathInfo)) {
                handleReport(req, resp, currentUser);
            } else {
                resp.sendRedirect(req.getContextPath() + "/projects");
            }
        } catch (ValidationException e) {
            req.getSession().setAttribute("errorMessage", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/projects");
        } catch (Exception e) {
            req.getSession().setAttribute("errorMessage", "Operation failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/projects");
        }
    }

    private void handleList(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String keyword = req.getParameter("q");
        String category = req.getParameter("category");

        List<Project> projects = projectService.searchProjects(keyword, category, null, 50, 0);
        req.setAttribute("projects", projects);
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedCategory", category);
        req.getRequestDispatcher("/WEB-INF/views/projects/index.jsp").forward(req, resp);
    }

    private void handleView(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int id = Integer.parseInt(req.getParameter("id"));
        Project p = projectService.getProject(id);
        if (p == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Project not found.");
            return;
        }

        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        boolean isOwner = currentUser != null && currentUser.getId() == p.getCreatorId();

        req.setAttribute("project", p);
        req.setAttribute("isOwner", isOwner);

        if (isOwner) {
            req.setAttribute("applications", projectService.getApplicationsForProject(id));
        } else if (currentUser != null) {
            // Check if current user already applied
            List<ProjectApplication> myApps = projectService.getApplicationsByUser(currentUser.getId());
            ProjectApplication myAppForThis = null;
            for (ProjectApplication a : myApps) {
                if (a.getProjectId() == id) {
                    myAppForThis = a;
                    break;
                }
            }
            req.setAttribute("myApplication", myAppForThis);
        }

        req.getRequestDispatcher("/WEB-INF/views/projects/view.jsp").forward(req, resp);
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        String title = req.getParameter("title");
        String desc = req.getParameter("description");
        String category = req.getParameter("category");
        String requiredSkills = req.getParameter("requiredSkills");
        int positions = Integer.parseInt(req.getParameter("availablePositions"));

        Project created = projectService.createProject(user.getId(), title, desc, category, requiredSkills, positions);
        req.getSession().setAttribute("successMessage", "Project listing created successfully!");
        resp.sendRedirect(req.getContextPath() + "/projects/view?id=" + created.getId());
    }

    private void handleApply(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        int projectId = Integer.parseInt(req.getParameter("projectId"));
        String pitch = req.getParameter("pitch");

        projectService.applyToProject(projectId, user.getId(), pitch);
        req.getSession().setAttribute("successMessage", "Application submitted to project lead!");
        resp.sendRedirect(req.getContextPath() + "/projects/view?id=" + projectId);
    }

    private void handleRespondApplication(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        int appId = Integer.parseInt(req.getParameter("applicationId"));
        int projectId = Integer.parseInt(req.getParameter("projectId"));
        boolean accept = "accept".equalsIgnoreCase(req.getParameter("decision"));

        projectService.respondToApplication(user.getId(), appId, accept);
        req.getSession().setAttribute("successMessage", "Application marked as " + (accept ? "ACCEPTED" : "REJECTED"));
        resp.sendRedirect(req.getContextPath() + "/projects/view?id=" + projectId);
    }

    private void handleReport(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        int projectId = Integer.parseInt(req.getParameter("projectId"));
        String reason = req.getParameter("reason");

        adminService.fileReport(user.getId(), ContentReport.ContentType.PROJECT, projectId, reason);
        req.getSession().setAttribute("successMessage", "Project reported for administrator moderation.");
        resp.sendRedirect(req.getContextPath() + "/projects");
    }
}