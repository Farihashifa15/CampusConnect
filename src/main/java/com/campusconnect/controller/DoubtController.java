package com.campusconnect.controller;

import com.campusconnect.dao.SkillDAO;
import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.ContentReport;
import com.campusconnect.model.DoubtAnswer;
import com.campusconnect.model.DoubtQuestion;
import com.campusconnect.model.User;
import com.campusconnect.service.AdminService;
import com.campusconnect.service.DoubtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/doubts/*")
public class DoubtController extends HttpServlet {

    private final DoubtService doubtService = new DoubtService();
    private final SkillDAO skillDAO = new SkillDAO();
    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            if ("/new".equals(pathInfo)) {
                req.setAttribute("allSkills", skillDAO.getAllSkills());
                req.getRequestDispatcher("/WEB-INF/views/doubts/new.jsp").forward(req, resp);
            } else if ("/view".equals(pathInfo)) {
                handleViewQuestion(req, resp);
            } else {
                handleListQuestions(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/doubts/index.jsp").forward(req, resp);
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
                handleCreateQuestion(req, resp, currentUser);
            } else if ("/answer".equals(pathInfo)) {
                handleCreateAnswer(req, resp, currentUser);
            } else if ("/accept".equals(pathInfo)) {
                handleAcceptAnswer(req, resp, currentUser);
            } else if ("/report".equals(pathInfo)) {
                handleReportContent(req, resp, currentUser);
            } else {
                resp.sendRedirect(req.getContextPath() + "/doubts");
            }
        } catch (ValidationException e) {
            req.getSession().setAttribute("errorMessage", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/doubts");
        } catch (Exception e) {
            req.getSession().setAttribute("errorMessage", "Action failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/doubts");
        }
    }

    private void handleListQuestions(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String keyword = req.getParameter("q");
        String category = req.getParameter("category");

        List<DoubtQuestion> questions = doubtService.searchQuestions(keyword, category, null, 50, 0);
        req.setAttribute("questions", questions);
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedCategory", category);
        req.getRequestDispatcher("/WEB-INF/views/doubts/index.jsp").forward(req, resp);
    }

    private void handleViewQuestion(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int id = Integer.parseInt(req.getParameter("id"));
        DoubtQuestion q = doubtService.getQuestion(id);
        if (q == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Question not found.");
            return;
        }

        List<DoubtAnswer> answers = doubtService.getAnswers(id);
        req.setAttribute("question", q);
        req.setAttribute("answers", answers);
        req.getRequestDispatcher("/WEB-INF/views/doubts/view.jsp").forward(req, resp);
    }

    private void handleCreateQuestion(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        String title = req.getParameter("title");
        String desc = req.getParameter("description");
        String category = req.getParameter("category");
        String skillStr = req.getParameter("skillId");
        Integer skillId = (skillStr != null && !skillStr.isEmpty()) ? Integer.parseInt(skillStr) : null;

        DoubtQuestion created = doubtService.postQuestion(user.getId(), skillId, category, title, desc);
        req.getSession().setAttribute("successMessage", "Question posted successfully!");
        resp.sendRedirect(req.getContextPath() + "/doubts/view?id=" + created.getId());
    }

    private void handleCreateAnswer(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        int questionId = Integer.parseInt(req.getParameter("questionId"));
        String answerText = req.getParameter("answerText");

        doubtService.postAnswer(questionId, user.getId(), answerText);
        req.getSession().setAttribute("successMessage", "Your answer has been submitted!");
        resp.sendRedirect(req.getContextPath() + "/doubts/view?id=" + questionId);
    }

    private void handleAcceptAnswer(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        int questionId = Integer.parseInt(req.getParameter("questionId"));
        int answerId = Integer.parseInt(req.getParameter("answerId"));

        doubtService.acceptAnswer(user.getId(), questionId, answerId);
        req.getSession().setAttribute("successMessage", "Answer accepted! Skill Credits awarded to the author.");
        resp.sendRedirect(req.getContextPath() + "/doubts/view?id=" + questionId);
    }

    private void handleReportContent(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        String type = req.getParameter("type");
        int contentId = Integer.parseInt(req.getParameter("contentId"));
        String reason = req.getParameter("reason");

        ContentReport.ContentType cType = "ANSWER".equalsIgnoreCase(type) ? ContentReport.ContentType.ANSWER : ContentReport.ContentType.QUESTION;
        adminService.fileReport(user.getId(), cType, contentId, reason);
        req.getSession().setAttribute("successMessage", "Report submitted for administrator moderation.");
        resp.sendRedirect(req.getContextPath() + "/doubts");
    }
}