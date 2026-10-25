package com.campusconnect.dao;

import com.campusconnect.config.DatabaseConfig;
import com.campusconnect.model.DoubtAnswer;
import com.campusconnect.model.DoubtQuestion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoubtDAO {

    public int createQuestion(DoubtQuestion q) throws SQLException {
        String sql = "INSERT INTO doubt_questions (author_id, skill_id, category, title, description, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, q.getAuthorId());
            if (q.getSkillId() != null) {
                ps.setInt(2, q.getSkillId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, q.getCategory());
            ps.setString(4, q.getTitle().trim());
            ps.setString(5, q.getDescription().trim());
            ps.setString(6, q.getStatus().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    q.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public DoubtQuestion getQuestionById(int id) throws SQLException {
        String sql = "SELECT q.*, u.full_name AS author_name, u.role AS author_role, s.name AS skill_name, " +
                     "(SELECT COUNT(*) FROM doubt_answers a WHERE a.question_id = q.id) AS answer_count " +
                     "FROM doubt_questions q " +
                     "JOIN users u ON q.author_id = u.id " +
                     "LEFT JOIN skills s ON q.skill_id = s.id " +
                     "WHERE q.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapQuestion(rs);
                }
            }
        }
        return null;
    }

    public List<DoubtQuestion> getQuestions(String keyword, String category, DoubtQuestion.Status status, int limit, int offset) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT q.*, u.full_name AS author_name, u.role AS author_role, s.name AS skill_name, ")
            .append("(SELECT COUNT(*) FROM doubt_answers a WHERE a.question_id = q.id) AS answer_count ")
            .append("FROM doubt_questions q ")
            .append("JOIN users u ON q.author_id = u.id ")
            .append("LEFT JOIN skills s ON q.skill_id = s.id ")
            .append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(q.title) LIKE ? OR LOWER(q.description) LIKE ?) ");
            String pat = "%" + keyword.trim().toLowerCase() + "%";
            params.add(pat);
            params.add(pat);
        }
        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("ALL")) {
            sql.append("AND LOWER(q.category) = LOWER(?) ");
            params.add(category.trim());
        }
        if (status != null) {
            sql.append("AND q.status = ? ");
            params.add(status.name());
        }
        sql.append("ORDER BY q.created_at DESC LIMIT ? OFFSET ?");

        List<DoubtQuestion> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object param : params) {
                ps.setObject(idx++, param);
            }
            ps.setInt(idx++, limit > 0 ? limit : 50);
            ps.setInt(idx, Math.max(offset, 0));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapQuestion(rs));
                }
            }
        }
        return list;
    }

    public int createAnswer(DoubtAnswer answer) throws SQLException {
        String sql = "INSERT INTO doubt_answers (question_id, author_id, answer_text, is_accepted) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, answer.getQuestionId());
            ps.setInt(2, answer.getAuthorId());
            ps.setString(3, answer.getAnswerText().trim());
            ps.setBoolean(4, answer.isAccepted());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    answer.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public List<DoubtAnswer> getAnswersForQuestion(int questionId) throws SQLException {
        String sql = "SELECT a.*, u.full_name AS author_name, u.role AS author_role FROM doubt_answers a " +
                     "JOIN users u ON a.author_id = u.id WHERE a.question_id = ? " +
                     "ORDER BY a.is_accepted DESC, a.created_at ASC";
        List<DoubtAnswer> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DoubtAnswer a = new DoubtAnswer();
                    a.setId(rs.getInt("id"));
                    a.setQuestionId(rs.getInt("question_id"));
                    a.setAuthorId(rs.getInt("author_id"));
                    a.setAuthorName(rs.getString("author_name"));
                    a.setAuthorRole(rs.getString("author_role"));
                    a.setAnswerText(rs.getString("answer_text"));
                    a.setAccepted(rs.getBoolean("is_accepted"));
                    a.setCreatedAt(rs.getTimestamp("created_at"));
                    a.setUpdatedAt(rs.getTimestamp("updated_at"));
                    list.add(a);
                }
            }
        }
        return list;
    }

    public DoubtAnswer getAnswerById(int answerId) throws SQLException {
        String sql = "SELECT a.*, u.full_name AS author_name, u.role AS author_role FROM doubt_answers a " +
                     "JOIN users u ON a.author_id = u.id WHERE a.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, answerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DoubtAnswer a = new DoubtAnswer();
                    a.setId(rs.getInt("id"));
                    a.setQuestionId(rs.getInt("question_id"));
                    a.setAuthorId(rs.getInt("author_id"));
                    a.setAuthorName(rs.getString("author_name"));
                    a.setAuthorRole(rs.getString("author_role"));
                    a.setAnswerText(rs.getString("answer_text"));
                    a.setAccepted(rs.getBoolean("is_accepted"));
                    a.setCreatedAt(rs.getTimestamp("created_at"));
                    a.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return a;
                }
            }
        }
        return null;
    }

    public boolean markAnswerAccepted(Connection conn, int questionId, int answerId) throws SQLException {
        // Reset previously accepted answers
        String resetSql = "UPDATE doubt_answers SET is_accepted = FALSE WHERE question_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(resetSql)) {
            ps.setInt(1, questionId);
            ps.executeUpdate();
        }

        // Set this answer as accepted
        String acceptSql = "UPDATE doubt_answers SET is_accepted = TRUE WHERE id = ? AND question_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(acceptSql)) {
            ps.setInt(1, answerId);
            ps.setInt(2, questionId);
            ps.executeUpdate();
        }

        // Update question status to RESOLVED and set accepted_answer_id
        String qSql = "UPDATE doubt_questions SET status = 'RESOLVED', accepted_answer_id = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(qSql)) {
            ps.setInt(1, answerId);
            ps.setInt(2, questionId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateQuestionStatus(int questionId, DoubtQuestion.Status status) throws SQLException {
        String sql = "UPDATE doubt_questions SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, questionId);
            return ps.executeUpdate() > 0;
        }
    }

    private DoubtQuestion mapQuestion(ResultSet rs) throws SQLException {
        DoubtQuestion q = new DoubtQuestion();
        q.setId(rs.getInt("id"));
        q.setAuthorId(rs.getInt("author_id"));
        q.setAuthorName(rs.getString("author_name"));
        q.setAuthorRole(rs.getString("author_role"));
        int skillId = rs.getInt("skill_id");
        if (!rs.wasNull()) {
            q.setSkillId(skillId);
        }
        q.setSkillName(rs.getString("skill_name"));
        q.setCategory(rs.getString("category"));
        q.setTitle(rs.getString("title"));
        q.setDescription(rs.getString("description"));
        int accId = rs.getInt("accepted_answer_id");
        if (!rs.wasNull()) {
            q.setAcceptedAnswerId(accId);
        }
        q.setStatus(DoubtQuestion.Status.valueOf(rs.getString("status")));
        q.setAnswerCount(rs.getInt("answer_count"));
        q.setCreatedAt(rs.getTimestamp("created_at"));
        q.setUpdatedAt(rs.getTimestamp("updated_at"));
        return q;
    }
}