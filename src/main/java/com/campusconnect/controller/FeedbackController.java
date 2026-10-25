package com.campusconnect.controller;

import com.campusconnect.dao.FeedbackAndEndorsementDAO;
import com.campusconnect.dao.SessionDAO;
import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.Session;
import com.campusconnect.model.User;
import com.campusconnect.service.FeedbackService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/feedback/*")
public class FeedbackController extends HttpServlet {

    private final FeedbackService feedbackService = new FeedbackService();
    private final SessionDAO sessionDAO = new SessionDAO();
    private final FeedbackAndEndorsementDAO feedbackDAO = new FeedbackAndEndorsementDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String pathInfo = req.getPathInfo();
        if ("/add".equals(pathInfo)) {
            try {
                int sessionId = Integer.parseInt(req.getParameter("sessionId"));
                Session s = sessionDAO.getSessionById(sessionId);
                if (s == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Session not found.");
                    return;
                }
                if (s.getLearnerId() != currentUser.getId()) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized: Only the student can submit feedback.");
                    return;
                }
                if (s.getStatus() != Session.Status.COMPLETED) {
                    req.getSession().setAttribute("errorMessage", "Feedback can only be submitted for completed sessions.");
                    resp.sendRedirect(req.getContextPath() + "/sessions/view?id=" + sessionId);
                    return;
                }
                if (feedbackDAO.hasFeedbackForSession(sessionId)) {
                    req.getSession().setAttribute("infoMessage", "You have already submitted feedback for this session.");
                    resp.sendRedirect(req.getContextPath() + "/sessions/view?id=" + sessionId);
                    return;
                }

                req.setAttribute("targetSession", s);
                req.getRequestDispatcher("/WEB-INF/views/sessions/feedback.jsp").forward(req, resp);
            } catch (Exception e) {
                req.getSession().setAttribute("errorMessage", "Error: " + e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/sessions");
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/sessions");
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
        if ("/submit".equals(pathInfo)) {
            try {
                int sessionId = Integer.parseInt(req.getParameter("sessionId"));
                int rating = Integer.parseInt(req.getParameter("rating"));
                String feedbackText = req.getParameter("feedbackText");
                boolean endorse = "true".equalsIgnoreCase(req.getParameter("endorseSkill")) || "on".equalsIgnoreCase(req.getParameter("endorseSkill"));
                String comment = req.getParameter("endorsementComment");

                feedbackService.submitFeedback(currentUser.getId(), sessionId, rating, feedbackText, endorse, comment);
                req.getSession().setAttribute("successMessage", "Feedback and endorsement submitted! Thank you for supporting peer mentorship.");
                resp.sendRedirect(req.getContextPath() + "/sessions/view?id=" + sessionId);
            } catch (ValidationException e) {
                req.getSession().setAttribute("errorMessage", e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/sessions");
            } catch (Exception e) {
                req.getSession().setAttribute("errorMessage", "Failed to submit feedback: " + e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/sessions");
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/sessions");
        }
    }
}