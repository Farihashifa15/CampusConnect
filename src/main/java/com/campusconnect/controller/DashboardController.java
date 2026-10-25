package com.campusconnect.controller;

import com.campusconnect.dao.*;
import com.campusconnect.model.*;
import com.campusconnect.service.CreditLedgerService;
import com.campusconnect.service.MentorMatchingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/dashboard")
public class DashboardController extends HttpServlet {

    private final SessionDAO sessionDAO = new SessionDAO();
    private final SkillDAO skillDAO = new SkillDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ResourceDAO resourceDAO = new ResourceDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();
    private final MentorAvailabilityDAO availabilityDAO = new MentorAvailabilityDAO();
    private final FeedbackAndEndorsementDAO feedbackDAO = new FeedbackAndEndorsementDAO();
    private final AdminAuditDAO adminAuditDAO = new AdminAuditDAO();
    private final CreditLedgerService creditService = new CreditLedgerService();
    private final MentorMatchingService mentorService = new MentorMatchingService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        try {
            // Refresh currentUser from DB to reflect latest credits/ratings
            User freshUser = userDAO.getUserById(currentUser.getId());
            if (freshUser != null) {
                currentUser = freshUser;
                session.setAttribute("currentUser", freshUser);
            }

            int userId = currentUser.getId();
            req.setAttribute("creditBalance", creditService.getBalance(userId));

            switch (currentUser.getRole()) {
                case STUDENT -> loadStudentDashboard(req, currentUser);
                case SENIOR -> loadSeniorDashboard(req, currentUser);
                case ALUMNI -> loadAlumniDashboard(req, currentUser);
                case ADMIN -> loadAdminDashboard(req, currentUser);
            }
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Failed to load dashboard data: " + e.getMessage());
        }

        req.getRequestDispatcher("/WEB-INF/views/dashboard/index.jsp").forward(req, resp);
    }

    private void loadStudentDashboard(HttpServletRequest req, User user) throws Exception {
        int uid = user.getId();
        req.setAttribute("upcomingSessions", sessionDAO.getSessionsForLearner(uid, Session.Status.CONFIRMED));
        req.setAttribute("pendingSessions", sessionDAO.getSessionsForLearner(uid, Session.Status.PENDING));
        req.setAttribute("myApplications", projectDAO.getApplicationsByUser(uid));
        req.setAttribute("recentResources", resourceDAO.getResources(null, null, 5, 0));

        // Find recommended mentors based on the first skill the student wants to learn
        List<UserSkill> wantSkills = skillDAO.getUserSkills(uid, UserSkill.SkillType.LEARN);
        Integer skillToLearn = wantSkills.isEmpty() ? null : wantSkills.get(0).getSkillId();
        List<MentorMatchResult> recommendations = mentorService.findMatches(skillToLearn, null, null, null);
        if (recommendations.size() > 4) {
            recommendations = recommendations.subList(0, 4);
        }
        req.setAttribute("recommendedMentors", recommendations);
    }

    private void loadSeniorDashboard(HttpServletRequest req, User user) throws Exception {
        int uid = user.getId();
        req.setAttribute("mentorSessions", sessionDAO.getSessionsForMentor(uid, Session.Status.CONFIRMED));
        req.setAttribute("pendingRequests", sessionDAO.getSessionsForMentor(uid, Session.Status.PENDING));
        req.setAttribute("availabilities", availabilityDAO.getAvailabilityByMentor(uid));
        req.setAttribute("endorsements", feedbackDAO.getEndorsementsForMentor(uid));
        req.setAttribute("recentFeedback", feedbackDAO.getFeedbackForMentor(uid));
        req.setAttribute("myProjects", projectDAO.getProjectsByCreator(uid));
    }

    private void loadAlumniDashboard(HttpServletRequest req, User user) throws Exception {
        int uid = user.getId();
        req.setAttribute("mentorSessions", sessionDAO.getSessionsForMentor(uid, null));
        req.setAttribute("availabilities", availabilityDAO.getAvailabilityByMentor(uid));
        req.setAttribute("endorsements", feedbackDAO.getEndorsementsForMentor(uid));
        req.setAttribute("myProjects", projectDAO.getProjectsByCreator(uid));
        req.setAttribute("recentResources", resourceDAO.getResources(null, "Career Roadmap", 5, 0));
    }

    private void loadAdminDashboard(HttpServletRequest req, User user) throws Exception {
        Map<String, Object> stats = userDAO.getPlatformStats();
        req.setAttribute("platformStats", stats);
        req.setAttribute("pendingReports", adminAuditDAO.getReports(ContentReport.Status.PENDING));
        req.setAttribute("recentAuditLogs", adminAuditDAO.getRecentLogs(10));
    }
}