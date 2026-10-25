package com.campusconnect.service;

import com.campusconnect.config.DatabaseConfig;
import com.campusconnect.dao.NotificationDAO;
import com.campusconnect.dao.ProjectDAO;
import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.Notification;
import com.campusconnect.model.Project;
import com.campusconnect.model.ProjectApplication;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProjectService {

    private final ProjectDAO projectDAO;
    private final NotificationDAO notificationDAO;

    public ProjectService() {
        this(new ProjectDAO(), new NotificationDAO());
    }

    public ProjectService(ProjectDAO projectDAO, NotificationDAO notificationDAO) {
        this.projectDAO = projectDAO;
        this.notificationDAO = notificationDAO;
    }

    public Project createProject(int creatorId, String title, String description,
                                 String category, String requiredSkills, int availablePositions)
            throws SQLException, ValidationException {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Project title is required.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Project description is required.");
        }
        if (requiredSkills == null || requiredSkills.trim().isEmpty()) {
            throw new ValidationException("Required skills must be specified.");
        }
        if (availablePositions < 1) {
            throw new ValidationException("Available positions must be at least 1.");
        }

        Project p = new Project();
        p.setCreatorId(creatorId);
        p.setTitle(title.trim());
        p.setDescription(description.trim());
        p.setCategory(category != null ? category.trim() : "General Engineering");
        p.setRequiredSkills(requiredSkills.trim());
        p.setAvailablePositions(availablePositions);
        p.setFilledPositions(0);
        p.setStatus(Project.Status.OPEN);

        int id = projectDAO.createProject(p);
        p.setId(id);
        return p;
    }

    public ProjectApplication applyToProject(int projectId, int applicantId, String pitch)
            throws SQLException, ValidationException {
        Project project = projectDAO.getProjectById(projectId);
        if (project == null) {
            throw new ValidationException("Project not found.");
        }
        if (project.getCreatorId() == applicantId) {
            throw new ValidationException("You cannot apply to your own project.");
        }
        if (!project.hasOpenPositions()) {
            throw new ValidationException("This project has no remaining open positions.");
        }
        if (pitch == null || pitch.trim().isEmpty()) {
            throw new ValidationException("Please provide a short pitch or description of your interest.");
        }

        // Prevent duplicate applications
        ProjectApplication existing = projectDAO.getApplication(projectId, applicantId);
        if (existing != null) {
            throw new ValidationException("You have already applied to this project.");
        }

        ProjectApplication app = new ProjectApplication();
        app.setProjectId(projectId);
        app.setApplicantId(applicantId);
        app.setPitch(pitch.trim());
        app.setStatus(ProjectApplication.Status.PENDING);

        int id = projectDAO.createApplication(app);
        app.setId(id);

        // Notify project creator
        Notification notif = new Notification();
        notif.setUserId(project.getCreatorId());
        notif.setTitle("New Project Application");
        notif.setMessage("A student has applied to join your project: '" + project.getTitle() + "'.");
        notif.setType("APPLICATION");
        notif.setLink("/projects/view?id=" + projectId);
        notificationDAO.createNotification(notif);

        return app;
    }

    public boolean respondToApplication(int creatorId, int applicationId, boolean accept)
            throws SQLException, ValidationException {
        ProjectApplication app = projectDAO.getApplicationById(applicationId);
        if (app == null) {
            throw new ValidationException("Application not found.");
        }
        Project project = projectDAO.getProjectById(app.getProjectId());
        if (project == null || project.getCreatorId() != creatorId) {
            throw new ValidationException("Unauthorized: Only the project creator can review applications.");
        }
        if (app.getStatus() != ProjectApplication.Status.PENDING) {
            throw new ValidationException("Application is already " + app.getStatus());
        }

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            if (accept) {
                if (!project.hasOpenPositions()) {
                    throw new ValidationException("Cannot accept application: Project positions are already full.");
                }
                projectDAO.updateApplicationStatus(conn, applicationId, ProjectApplication.Status.ACCEPTED);
                projectDAO.incrementFilledPositions(conn, project.getId());
            } else {
                projectDAO.updateApplicationStatus(conn, applicationId, ProjectApplication.Status.REJECTED);
            }

            // Notify applicant
            Notification notif = new Notification();
            notif.setUserId(app.getApplicantId());
            notif.setTitle(accept ? "Project Application Accepted!" : "Project Application Update");
            notif.setMessage("Your application to join '" + project.getTitle() + "' was " + (accept ? "accepted! Welcome to the team." : "not selected."));
            notif.setType("APPLICATION");
            notif.setLink("/projects/view?id=" + project.getId());
            notificationDAO.createNotification(notif);

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            if (e instanceof ValidationException) throw (ValidationException) e;
            throw new SQLException(e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public Project getProject(int id) throws SQLException {
        return projectDAO.getProjectById(id);
    }

    public List<Project> searchProjects(String keyword, String category, Project.Status status, int limit, int offset) throws SQLException {
        return projectDAO.getProjects(keyword, category, status, limit, offset);
    }

    public List<ProjectApplication> getApplicationsForProject(int projectId) throws SQLException {
        return projectDAO.getApplicationsForProject(projectId);
    }

    public List<ProjectApplication> getApplicationsByUser(int userId) throws SQLException {
        return projectDAO.getApplicationsByUser(userId);
    }

    public List<Project> getProjectsByCreator(int creatorId) throws SQLException {
        return projectDAO.getProjectsByCreator(creatorId);
    }
}