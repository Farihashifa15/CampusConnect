package com.campusconnect.controller;

import com.campusconnect.dao.SkillDAO;
import com.campusconnect.model.MentorMatchResult;
import com.campusconnect.service.MentorMatchingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Time;
import java.util.List;

@WebServlet("/mentors")
public class MentorController extends HttpServlet {

    private final MentorMatchingService mentorService = new MentorMatchingService();
    private final SkillDAO skillDAO = new SkillDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String skillParam = req.getParameter("skillId");
        String dayParam = req.getParameter("dayOfWeek");
        String startParam = req.getParameter("startTime");
        String endParam = req.getParameter("endTime");

        Integer skillId = (skillParam != null && !skillParam.trim().isEmpty())
                ? Integer.parseInt(skillParam.trim())
                : null;

        Time startTime = null;
        Time endTime = null;
        try {
            if (startParam != null && !startParam.trim().isEmpty()) {
                String s = startParam.trim();
                if (s.length() == 5) s += ":00";
                startTime = Time.valueOf(s);
            }
            if (endParam != null && !endParam.trim().isEmpty()) {
                String e = endParam.trim();
                if (e.length() == 5) e += ":00";
                endTime = Time.valueOf(e);
            }
        } catch (IllegalArgumentException ignored) {}

        try {
            req.setAttribute("allSkills", skillDAO.getAllSkills());
            List<MentorMatchResult> matches = mentorService.findMatches(skillId, dayParam, startTime, endTime);
            req.setAttribute("mentors", matches);
            req.setAttribute("selectedSkillId", skillId);
            req.setAttribute("selectedDay", dayParam);
            req.setAttribute("selectedStartTime", startParam);
            req.setAttribute("selectedEndTime", endParam);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error finding mentors: " + e.getMessage());
        }

        req.getRequestDispatcher("/WEB-INF/views/mentors/index.jsp").forward(req, resp);
    }
}