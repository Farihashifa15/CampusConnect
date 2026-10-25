package com.campusconnect.controller;

import com.campusconnect.dao.FeedbackAndEndorsementDAO;
import com.campusconnect.dao.MentorAvailabilityDAO;
import com.campusconnect.dao.SkillDAO;
import com.campusconnect.dao.UserDAO;
import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.MentorAvailability;
import com.campusconnect.model.User;
import com.campusconnect.model.UserSkill;
import com.campusconnect.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Time;
import java.util.List;

@WebServlet("/profile/*")
public class ProfileController extends HttpServlet {

    private final UserService userService = new UserService();
    private final UserDAO userDAO = new UserDAO();
    private final SkillDAO skillDAO = new SkillDAO();
    private final MentorAvailabilityDAO availabilityDAO = new MentorAvailabilityDAO();
    private final FeedbackAndEndorsementDAO feedbackDAO = new FeedbackAndEndorsementDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        String pathInfo = req.getPathInfo();
        if ("/edit".equals(pathInfo)) {
            if (currentUser == null) {
                resp.sendRedirect(req.getContextPath() + "/auth/login");
                return;
            }
            try {
                req.setAttribute("user", userDAO.getUserById(currentUser.getId()));
                req.setAttribute("allSkills", skillDAO.getAllSkills());
                req.setAttribute("teachingSkills", skillDAO.getUserSkills(currentUser.getId(), UserSkill.SkillType.TEACH));
                req.setAttribute("learningSkills", skillDAO.getUserSkills(currentUser.getId(), UserSkill.SkillType.LEARN));
                req.setAttribute("availabilities", availabilityDAO.getAvailabilityByMentor(currentUser.getId()));
            } catch (Exception e) {
                req.setAttribute("errorMessage", "Error loading profile edit page: " + e.getMessage());
            }
            req.getRequestDispatcher("/WEB-INF/views/profile/edit.jsp").forward(req, resp);
            return;
        }

        // View profile
        String idParam = req.getParameter("id");
        int targetUserId = (idParam != null && !idParam.isEmpty())
                ? Integer.parseInt(idParam)
                : (currentUser != null ? currentUser.getId() : 0);

        if (targetUserId == 0) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        try {
            User targetUser = userDAO.getUserById(targetUserId);
            if (targetUser == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User profile not found.");
                return;
            }

            req.setAttribute("profileUser", targetUser);
            req.setAttribute("isOwnProfile", currentUser != null && currentUser.getId() == targetUserId);
            req.setAttribute("teachingSkills", skillDAO.getUserSkills(targetUserId, UserSkill.SkillType.TEACH));
            req.setAttribute("learningSkills", skillDAO.getUserSkills(targetUserId, UserSkill.SkillType.LEARN));
            req.setAttribute("availabilities", availabilityDAO.getAvailabilityByMentor(targetUserId));
            req.setAttribute("endorsements", feedbackDAO.getEndorsementsForMentor(targetUserId));
            req.setAttribute("feedbacks", feedbackDAO.getFeedbackForMentor(targetUserId));
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error loading profile: " + e.getMessage());
        }

        req.getRequestDispatcher("/WEB-INF/views/profile/view.jsp").forward(req, resp);
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
            if ("/edit".equals(pathInfo)) {
                handleProfileUpdate(req, resp, currentUser);
            } else if ("/skills/add".equals(pathInfo)) {
                handleSkillAdd(req, resp, currentUser);
            } else if ("/skills/remove".equals(pathInfo)) {
                handleSkillRemove(req, resp, currentUser);
            } else if ("/availability/add".equals(pathInfo)) {
                handleAvailabilityAdd(req, resp, currentUser);
            } else if ("/availability/delete".equals(pathInfo)) {
                handleAvailabilityDelete(req, resp, currentUser);
            } else {
                resp.sendRedirect(req.getContextPath() + "/profile");
            }
        } catch (Exception e) {
            req.getSession().setAttribute("errorMessage", "Action failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/profile/edit");
        }
    }

    private void handleProfileUpdate(HttpServletRequest req, HttpServletResponse resp, User currentUser) throws Exception {
        User user = userDAO.getUserById(currentUser.getId());
        user.setFullName(req.getParameter("fullName"));
        user.setDepartment(req.getParameter("department"));
        try {
            user.setAcademicYear(Integer.parseInt(req.getParameter("academicYear")));
        } catch (NumberFormatException ignored) {}
        user.setBio(req.getParameter("bio"));
        user.setCareerGoals(req.getParameter("careerGoals"));
        user.setProjectInterests(req.getParameter("projectInterests"));
        user.setClubMemberships(req.getParameter("clubMemberships"));

        userService.updateProfile(user);
        req.getSession().setAttribute("currentUser", user);
        req.getSession().setAttribute("successMessage", "Profile updated successfully!");
        resp.sendRedirect(req.getContextPath() + "/profile");
    }

    private void handleSkillAdd(HttpServletRequest req, HttpServletResponse resp, User currentUser) throws Exception {
        int skillId = Integer.parseInt(req.getParameter("skillId"));
        String typeStr = req.getParameter("skillType");
        String profStr = req.getParameter("proficiencyLevel");

        UserSkill.SkillType type = UserSkill.SkillType.valueOf(typeStr.toUpperCase());
        UserSkill.ProficiencyLevel level = UserSkill.ProficiencyLevel.valueOf(profStr.toUpperCase());

        skillDAO.addUserSkill(currentUser.getId(), skillId, type, level);
        req.getSession().setAttribute("successMessage", "Skill added to your profile.");
        resp.sendRedirect(req.getContextPath() + "/profile/edit");
    }

    private void handleSkillRemove(HttpServletRequest req, HttpServletResponse resp, User currentUser) throws Exception {
        int skillId = Integer.parseInt(req.getParameter("skillId"));
        String typeStr = req.getParameter("skillType");
        UserSkill.SkillType type = UserSkill.SkillType.valueOf(typeStr.toUpperCase());

        skillDAO.removeUserSkill(currentUser.getId(), skillId, type);
        req.getSession().setAttribute("successMessage", "Skill removed.");
        resp.sendRedirect(req.getContextPath() + "/profile/edit");
    }

    private void handleAvailabilityAdd(HttpServletRequest req, HttpServletResponse resp, User currentUser) throws Exception {
        String day = req.getParameter("dayOfWeek");
        String startStr = req.getParameter("startTime");
        String endStr = req.getParameter("endTime");

        if (startStr.length() == 5) startStr += ":00";
        if (endStr.length() == 5) endStr += ":00";

        Time start = Time.valueOf(startStr);
        Time end = Time.valueOf(endStr);

        if (!start.before(end)) {
            throw new ValidationException("Start time must be before end time.");
        }

        MentorAvailability slot = new MentorAvailability();
        slot.setMentorId(currentUser.getId());
        slot.setDayOfWeek(day);
        slot.setStartTime(start);
        slot.setEndTime(end);
        slot.setActive(true);

        availabilityDAO.addSlot(slot);
        req.getSession().setAttribute("successMessage", "Availability slot created.");
        resp.sendRedirect(req.getContextPath() + "/profile/edit");
    }

    private void handleAvailabilityDelete(HttpServletRequest req, HttpServletResponse resp, User currentUser) throws Exception {
        int slotId = Integer.parseInt(req.getParameter("slotId"));
        availabilityDAO.deleteSlot(slotId, currentUser.getId());
        req.getSession().setAttribute("successMessage", "Availability slot removed.");
        resp.sendRedirect(req.getContextPath() + "/profile/edit");
    }
}