package com.campusconnect.controller;

import com.campusconnect.config.AppConfig;
import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.User;
import com.campusconnect.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/auth/*")
public class AuthController extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/login".equals(pathInfo)) {
            req.setAttribute("allowedDomain", AppConfig.getUniversityEmailDomain());
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        } else if ("/register".equals(pathInfo)) {
            req.setAttribute("allowedDomain", AppConfig.getUniversityEmailDomain());
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        } else if ("/logout".equals(pathInfo)) {
            doLogout(req, resp);
        } else if ("/password".equals(pathInfo)) {
            req.getRequestDispatcher("/WEB-INF/views/auth/change-password.jsp").forward(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if ("/login".equals(pathInfo)) {
            handleLogin(req, resp);
        } else if ("/register".equals(pathInfo)) {
            handleRegister(req, resp);
        } else if ("/logout".equals(pathInfo)) {
            doLogout(req, resp);
        } else if ("/password".equals(pathInfo)) {
            handlePasswordChange(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            User user = userService.authenticate(email, password);

            // Session fixation protection: invalidate old session and create new
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("currentUser", user);

            String redirect = (String) newSession.getAttribute("redirectAfterLogin");
            if (redirect != null && !redirect.isEmpty()) {
                newSession.removeAttribute("redirectAfterLogin");
                resp.sendRedirect(redirect);
            } else {
                resp.sendRedirect(req.getContextPath() + "/dashboard");
            }
        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("email", email);
            req.setAttribute("allowedDomain", AppConfig.getUniversityEmailDomain());
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "An internal error occurred: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String regNo = req.getParameter("registerNumber");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String fullName = req.getParameter("fullName");
        String roleStr = req.getParameter("role");
        String department = req.getParameter("department");
        String yearStr = req.getParameter("academicYear");

        int year = 1;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException ignored) {}

        User.Role role = User.Role.STUDENT;
        try {
            if (roleStr != null && !roleStr.trim().isEmpty()) {
                role = User.Role.valueOf(roleStr.trim().toUpperCase());
            }
        } catch (IllegalArgumentException ignored) {}

        try {
            User newUser = userService.registerUser(regNo, email, password, fullName, role, department, year);

            // Automatically log in the newly registered user
            HttpSession session = req.getSession(true);
            session.setAttribute("currentUser", newUser);
            session.setAttribute("successMessage", "Welcome to CampusConnect! You received 50 initial Skill Credits.");
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("registerNumber", regNo);
            req.setAttribute("email", email);
            req.setAttribute("fullName", fullName);
            req.setAttribute("department", department);
            req.setAttribute("academicYear", year);
            req.setAttribute("role", roleStr);
            req.setAttribute("allowedDomain", AppConfig.getUniversityEmailDomain());
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Registration failed: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        }
    }

    private void handlePasswordChange(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String oldPass = req.getParameter("oldPassword");
        String newPass = req.getParameter("newPassword");
        String confirmPass = req.getParameter("confirmPassword");

        if (newPass == null || !newPass.equals(confirmPass)) {
            req.setAttribute("errorMessage", "New passwords do not match.");
            req.getRequestDispatcher("/WEB-INF/views/auth/change-password.jsp").forward(req, resp);
            return;
        }

        try {
            userService.changePassword(currentUser.getId(), oldPass, newPass);
            req.setAttribute("successMessage", "Password updated successfully!");
            req.getRequestDispatcher("/WEB-INF/views/auth/change-password.jsp").forward(req, resp);
        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/auth/change-password.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Failed to update password: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/auth/change-password.jsp").forward(req, resp);
        }
    }

    private void doLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/auth/login?msg=logged_out");
    }
}