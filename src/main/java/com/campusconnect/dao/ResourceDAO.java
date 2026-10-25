package com.campusconnect.dao;

import com.campusconnect.config.DatabaseConfig;
import com.campusconnect.model.Resource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceDAO {

    public int createResource(Resource r) throws SQLException {
        String sql = "INSERT INTO resources (uploader_id, title, description, category, tags, stored_filename, original_filename, file_size, mime_type, download_count, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getUploaderId());
            ps.setString(2, r.getTitle().trim());
            ps.setString(3, r.getDescription());
            ps.setString(4, r.getCategory().getDisplayName());
            ps.setString(5, r.getTags());
            ps.setString(6, r.getStoredFilename());
            ps.setString(7, r.getOriginalFilename());
            ps.setLong(8, r.getFileSize());
            ps.setString(9, r.getMimeType());
            ps.setInt(10, r.getDownloadCount());
            ps.setString(11, r.getStatus().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    r.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public Resource getResourceById(int id) throws SQLException {
        String sql = "SELECT r.*, u.full_name AS uploader_name, u.role AS uploader_role FROM resources r " +
                     "JOIN users u ON r.uploader_id = u.id WHERE r.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResource(rs);
                }
            }
        }
        return null;
    }

    public List<Resource> getResources(String keyword, String category, int limit, int offset) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT r.*, u.full_name AS uploader_name, u.role AS uploader_role FROM resources r ")
            .append("JOIN users u ON r.uploader_id = u.id WHERE r.status = 'APPROVED' ");

        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(r.title) LIKE ? OR LOWER(r.description) LIKE ? OR LOWER(r.tags) LIKE ?) ");
            String pat = "%" + keyword.trim().toLowerCase() + "%";
            params.add(pat);
            params.add(pat);
            params.add(pat);
        }
        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("ALL")) {
            sql.append("AND LOWER(r.category) = LOWER(?) ");
            params.add(category.trim());
        }
        sql.append("ORDER BY r.created_at DESC LIMIT ? OFFSET ?");

        List<Resource> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object p : params) {
                ps.setObject(idx++, p);
            }
            ps.setInt(idx++, limit > 0 ? limit : 50);
            ps.setInt(idx, Math.max(offset, 0));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResource(rs));
                }
            }
        }
        return list;
    }

    public boolean incrementDownloadCount(int id) throws SQLException {
        String sql = "UPDATE resources SET download_count = download_count + 1 WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int id, Resource.Status status) throws SQLException {
        String sql = "UPDATE resources SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Resource mapResource(ResultSet rs) throws SQLException {
        Resource r = new Resource();
        r.setId(rs.getInt("id"));
        r.setUploaderId(rs.getInt("uploader_id"));
        r.setUploaderName(rs.getString("uploader_name"));
        r.setUploaderRole(rs.getString("uploader_role"));
        r.setTitle(rs.getString("title"));
        r.setDescription(rs.getString("description"));
        r.setCategory(Resource.Category.fromDisplayName(rs.getString("category")));
        r.setTags(rs.getString("tags"));
        r.setStoredFilename(rs.getString("stored_filename"));
        r.setOriginalFilename(rs.getString("original_filename"));
        r.setFileSize(rs.getLong("file_size"));
        r.setMimeType(rs.getString("mime_type"));
        r.setDownloadCount(rs.getInt("download_count"));
        r.setStatus(Resource.Status.valueOf(rs.getString("status")));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }
}