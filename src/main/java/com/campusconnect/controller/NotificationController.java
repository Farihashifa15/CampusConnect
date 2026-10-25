package com.campusconnect.controller;

import com.campusconnect.dao.NotificationDAO;
import com.campusconnect.model.Notification;
import com.campusconnect.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/notifications/*")
public class NotificationController extends HttpServlet {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        try {
            List<Notification> list = notificationDAO.getNotificationsByUser(currentUser.getId(), false);
            req.setAttribute("notifications", list);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error loading notifications: " + e.getMessage());
        }

        req.getRequestDispatcher("/WEB-INF/views/notifications/index.jsp").forward(req, resp);
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
            if ("/read".equals(pathInfo)) {
                String idParam = req.getParameter("id");
                if (idParam != null && !idParam.isEmpty()) {
                    int notifId = Integer.parseInt(idParam);
                    notificationDAO.markAsRead(notifId, currentUser.getId());
                } else {
                    notificationDAO.markAllAsRead(currentUser.getId());
                }
            }
        } catch (Exception ignored) {}

        String returnUrl = req.getParameter("returnUrl");
        if (returnUrl != null && !returnUrl.isEmpty()) {
            resp.sendRedirect(returnUrl);
        } else {
            resp.sendRedirect(req.getContextPath() + "/notifications");
        }
    }
}