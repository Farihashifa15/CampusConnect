package com.campusconnect.controller;

import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.ContentReport;
import com.campusconnect.model.Resource;
import com.campusconnect.model.User;
import com.campusconnect.service.AdminService;
import com.campusconnect.service.ResourceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@WebServlet("/resources/*")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB
    maxFileSize = 25 * 1024 * 1024,       // 25 MB
    maxRequestSize = 30 * 1024 * 1024     // 30 MB
)
public class ResourceController extends HttpServlet {

    private final ResourceService resourceService = new ResourceService();
    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            if ("/upload".equals(pathInfo)) {
                req.setAttribute("categories", Resource.Category.values());
                req.getRequestDispatcher("/WEB-INF/views/resources/upload.jsp").forward(req, resp);
            } else if ("/download".equals(pathInfo)) {
                handleDownload(req, resp);
            } else {
                handleList(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/resources/index.jsp").forward(req, resp);
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
            if ("/upload".equals(pathInfo)) {
                handleUpload(req, resp, currentUser);
            } else if ("/report".equals(pathInfo)) {
                handleReport(req, resp, currentUser);
            } else {
                resp.sendRedirect(req.getContextPath() + "/resources");
            }
        } catch (ValidationException e) {
            req.getSession().setAttribute("errorMessage", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/resources/upload");
        } catch (Exception e) {
            req.getSession().setAttribute("errorMessage", "Upload failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/resources");
        }
    }

    private void handleList(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String keyword = req.getParameter("q");
        String category = req.getParameter("category");

        List<Resource> list = resourceService.searchResources(keyword, category, 50, 0);
        req.setAttribute("resources", list);
        req.setAttribute("categories", Resource.Category.values());
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedCategory", category);

        req.getRequestDispatcher("/WEB-INF/views/resources/index.jsp").forward(req, resp);
    }

    private void handleUpload(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        String title = req.getParameter("title");
        String desc = req.getParameter("description");
        String catStr = req.getParameter("category");
        String tags = req.getParameter("tags");

        Part filePart = req.getPart("file");
        if (filePart == null || filePart.getSize() == 0) {
            throw new ValidationException("Please select a file to upload.");
        }

        String submittedFileName = filePart.getSubmittedFileName();
        long size = filePart.getSize();
        String mimeType = filePart.getContentType();

        Resource.Category cat = Resource.Category.fromDisplayName(catStr);

        Resource created = resourceService.uploadResource(user.getId(), title, desc, cat, tags, submittedFileName, size, mimeType, filePart.getInputStream());
        req.getSession().setAttribute("successMessage", "Resource published successfully! Skill Credits awarded.");
        resp.sendRedirect(req.getContextPath() + "/resources");
    }

    private void handleDownload(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int id = Integer.parseInt(req.getParameter("id"));
        Resource r = resourceService.getResource(id);
        if (r == null || r.getStatus() != Resource.Status.APPROVED) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not available.");
            return;
        }

        File file = resourceService.getResourceFile(r);
        resourceService.incrementDownloadCount(id);

        resp.setContentType(r.getMimeType() != null ? r.getMimeType() : "application/octet-stream");
        resp.setContentLengthLong(file.length());
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + r.getOriginalFilename().replace("\"", "") + "\"");

        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = resp.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    private void handleReport(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        int resourceId = Integer.parseInt(req.getParameter("resourceId"));
        String reason = req.getParameter("reason");

        adminService.fileReport(user.getId(), ContentReport.ContentType.RESOURCE, resourceId, reason);
        req.getSession().setAttribute("successMessage", "Resource reported for administrator moderation.");
        resp.sendRedirect(req.getContextPath() + "/resources");
    }
}