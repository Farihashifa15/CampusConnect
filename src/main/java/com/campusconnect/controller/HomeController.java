package com.campusconnect.controller;

import com.campusconnect.dao.UserDAO;
import com.campusconnect.model.MentorMatchResult;
import com.campusconnect.service.MentorMatchingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"", "/home"})
public class HomeController extends HttpServlet {

    private final MentorMatchingService mentorService = new MentorMatchingService();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Map<String, Object> stats = userDAO.getPlatformStats();
            req.setAttribute("stats", stats);

            // Fetch top 3 featured mentors for the landing page showcase
            List<MentorMatchResult> topMentors = mentorService.findMatches(null, null, null, null);
            if (topMentors.size() > 3) {
                topMentors = topMentors.subList(0, 3);
            }
            req.setAttribute("featuredMentors", topMentors);
        } catch (Exception e) {
            req.setAttribute("error", "Unable to load landing stats: " + e.getMessage());
        }

        req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
    }
}