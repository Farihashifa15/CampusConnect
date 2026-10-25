package com.campusconnect.controller;

import com.campusconnect.config.AppConfig;
import com.campusconnect.dao.MentorAvailabilityDAO;
import com.campusconnect.dao.SessionDAO;
import com.campusconnect.dao.SkillDAO;
import com.campusconnect.dao.UserDAO;
import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.MentorAvailability;
import com.campusconnect.model.Session;
import com.campusconnect.model.User;
import com.campusconnect.model.UserSkill;
import com.campusconnect.service.CreditLedgerService;
import com.campusconnect.service.SessionBookingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

@WebServlet("/sessions/*")
public class SessionController extends HttpServlet {

    private final SessionBookingService bookingService = new SessionBookingService();
    private final SessionDAO sessionDAO = new SessionDAO();
    private final UserDAO userDAO = new UserDAO();
    private final SkillDAO skillDAO = new SkillDAO();
    private final MentorAvailabilityDAO availabilityDAO = new MentorAvailabilityDAO();
    private final CreditLedgerService creditService = new CreditLedgerService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String pathInfo = req.getPathInfo();
        try {
            if ("/book".equals(pathInfo)) {
                handleBookingForm(req, resp, currentUser);
            } else if ("/view".equals(pathInfo)) {
                handleSessionView(req, resp, currentUser);
            } else {
                handleSessionList(req, resp, currentUser);
            }
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/sessions/index.jsp").forward(req, resp);
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
            if ("/book".equals(pathInfo)) {
                processBookingRequest(req, resp, currentUser);
            } else if ("/action".equals(pathInfo)) {
                processSessionAction(req, resp, currentUser);
            } else {
                resp.sendRedirect(req.getContextPath() + "/sessions");
            }
        } catch (ValidationException e) {
            req.getSession().setAttribute("errorMessage", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/sessions");
        } catch (Exception e) {
            req.getSession().setAttribute("errorMessage", "Operation failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/sessions");
        }
    }

    private void handleSessionList(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        int uid = user.getId();
        req.setAttribute("asLearnerSessions", sessionDAO.getSessionsForLearner(uid, null));
        req.setAttribute("asMentorSessions", sessionDAO.getSessionsForMentor(uid, null));
        req.setAttribute("creditBalance", creditService.getBalance(uid));
        req.getRequestDispatcher("/WEB-INF/views/sessions/index.jsp").forward(req, resp);
    }

    private void handleBookingForm(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        int mentorId = Integer.parseInt(req.getParameter("mentorId"));
        User mentor = userDAO.getUserById(mentorId);
        if (mentor == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Mentor not found.");
            return;
        }

        List<UserSkill> teachingSkills = skillDAO.getUserSkills(mentorId, UserSkill.SkillType.TEACH);
        List<MentorAvailability> availabilities = availabilityDAO.getAvailabilityByMentor(mentorId);

        req.setAttribute("mentor", mentor);
        req.setAttribute("teachingSkills", teachingSkills);
        req.setAttribute("availabilities", availabilities);
        req.setAttribute("creditCost", AppConfig.getSessionBookingCost());
        req.setAttribute("currentBalance", creditService.getBalance(user.getId()));

        req.getRequestDispatcher("/WEB-INF/views/sessions/book.jsp").forward(req, resp);
    }

    private void handleSessionView(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        int sessionId = Integer.parseInt(req.getParameter("id"));
        Session session = sessionDAO.getSessionById(sessionId);
        if (session == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Session not found.");
            return;
        }
        if (session.getMentorId() != user.getId() && session.getLearnerId() != user.getId() && user.getRole() != User.Role.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized to view this session.");
            return;
        }

        req.setAttribute("sessionDetail", session);
        req.setAttribute("isMentor", session.getMentorId() == user.getId());
        req.setAttribute("isLearner", session.getLearnerId() == user.getId());
        req.getRequestDispatcher("/WEB-INF/views/sessions/view.jsp").forward(req, resp);
    }

    private void processBookingRequest(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        int mentorId = Integer.parseInt(req.getParameter("mentorId"));
        int skillId = Integer.parseInt(req.getParameter("skillId"));
        String dateStr = req.getParameter("sessionDate");
        String startStr = req.getParameter("startTime");
        String endStr = req.getParameter("endTime");
        String notes = req.getParameter("notes");

        if (startStr.length() == 5) startStr += ":00";
        if (endStr.length() == 5) endStr += ":00";

        Date sessionDate = Date.valueOf(dateStr);
        Time startTime = Time.valueOf(startStr);
        Time endTime = Time.valueOf(endStr);

        Session createdSession = bookingService.requestBooking(user.getId(), mentorId, skillId, sessionDate, startTime, endTime, notes);
        req.getSession().setAttribute("successMessage", "Mentorship session requested! Credits escrowed successfully.");
        resp.sendRedirect(req.getContextPath() + "/sessions/view?id=" + createdSession.getId());
    }

    private void processSessionAction(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        int sessionId = Integer.parseInt(req.getParameter("sessionId"));
        String action = req.getParameter("action");
        String reason = req.getParameter("reason");

        if ("confirm".equalsIgnoreCase(action)) {
            bookingService.confirmBooking(user.getId(), sessionId);
            req.getSession().setAttribute("successMessage", "Mentorship session confirmed.");
        } else if ("reject".equalsIgnoreCase(action)) {
            bookingService.rejectBooking(user.getId(), sessionId, reason);
            req.getSession().setAttribute("successMessage", "Booking request declined and learner credits refunded.");
        } else if ("cancel".equalsIgnoreCase(action)) {
            bookingService.cancelBooking(user.getId(), sessionId, reason);
            req.getSession().setAttribute("successMessage", "Session cancelled and credits refunded.");
        } else if ("complete".equalsIgnoreCase(action)) {
            bookingService.completeSession(user.getId(), sessionId);
            req.getSession().setAttribute("successMessage", "Session marked as COMPLETED! Credits awarded to your account.");
        }

        resp.sendRedirect(req.getContextPath() + "/sessions/view?id=" + sessionId);
    }
}