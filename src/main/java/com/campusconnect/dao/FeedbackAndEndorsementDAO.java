package com.campusconnect.dao;

import com.campusconnect.config.DatabaseConfig;
import com.campusconnect.model.Endorsement;
import com.campusconnect.model.SessionFeedback;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackAndEndorsementDAO {

    public int addFeedback(Connection conn, SessionFeedback feedback) throws SQLException {
        String sql = "INSERT INTO session_feedback (session_id, mentor_id, learner_id, rating, feedback_text) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, feedback.getSessionId());
            ps.setInt(2, feedback.getMentorId());
            ps.setInt(3, feedback.getLearnerId());
            ps.setInt(4, feedback.getRating());
            ps.setString(5, feedback.getFeedbackText());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    feedback.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public boolean hasFeedbackForSession(int sessionId) throws SQLException {
        String sql = "SELECT id FROM session_feedback WHERE session_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public SessionFeedback getFeedbackBySessionId(int sessionId) throws SQLException {
        String sql = "SELECT sf.*, l.full_name AS learner_name FROM session_feedback sf " +
                     "JOIN users l ON sf.learner_id = l.id WHERE sf.session_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SessionFeedback sf = new SessionFeedback();
                    sf.setId(rs.getInt("id"));
                    sf.setSessionId(rs.getInt("session_id"));
                    sf.setMentorId(rs.getInt("mentor_id"));
                    sf.setLearnerId(rs.getInt("learner_id"));
                    sf.setLearnerName(rs.getString("learner_name"));
                    sf.setRating(rs.getInt("rating"));
                    sf.setFeedbackText(rs.getString("feedback_text"));
                    sf.setCreatedAt(rs.getTimestamp("created_at"));
                    return sf;
                }
            }
        }
        return null;
    }

    public List<SessionFeedback> getFeedbackForMentor(int mentorId) throws SQLException {
        String sql = "SELECT sf.*, l.full_name AS learner_name FROM session_feedback sf " +
                     "JOIN users l ON sf.learner_id = l.id WHERE sf.mentor_id = ? ORDER BY sf.created_at DESC";
        List<SessionFeedback> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SessionFeedback sf = new SessionFeedback();
                    sf.setId(rs.getInt("id"));
                    sf.setSessionId(rs.getInt("session_id"));
                    sf.setMentorId(rs.getInt("mentor_id"));
                    sf.setLearnerId(rs.getInt("learner_id"));
                    sf.setLearnerName(rs.getString("learner_name"));
                    sf.setRating(rs.getInt("rating"));
                    sf.setFeedbackText(rs.getString("feedback_text"));
                    sf.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(sf);
                }
            }
        }
        return list;
    }

    public int addEndorsement(Connection conn, Endorsement endorsement) throws SQLException {
        String sql = "INSERT INTO endorsements (session_id, mentor_id, learner_id, skill_id, comment) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, endorsement.getSessionId());
            ps.setInt(2, endorsement.getMentorId());
            ps.setInt(3, endorsement.getLearnerId());
            ps.setInt(4, endorsement.getSkillId());
            ps.setString(5, endorsement.getComment());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    endorsement.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public boolean hasEndorsementForSession(int sessionId) throws SQLException {
        String sql = "SELECT id FROM endorsements WHERE session_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Endorsement> getEndorsementsForMentor(int mentorId) throws SQLException {
        String sql = "SELECT e.*, l.full_name AS learner_name, s.name AS skill_name FROM endorsements e " +
                     "JOIN users l ON e.learner_id = l.id " +
                     "JOIN skills s ON e.skill_id = s.id " +
                     "WHERE e.mentor_id = ? ORDER BY e.created_at DESC";
        List<Endorsement> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Endorsement e = new Endorsement();
                    e.setId(rs.getInt("id"));
                    e.setSessionId(rs.getInt("session_id"));
                    e.setMentorId(rs.getInt("mentor_id"));
                    e.setLearnerId(rs.getInt("learner_id"));
                    e.setLearnerName(rs.getString("learner_name"));
                    e.setSkillId(rs.getInt("skill_id"));
                    e.setSkillName(rs.getString("skill_name"));
                    e.setComment(rs.getString("comment"));
                    e.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(e);
                }
            }
        }
        return list;
    }

    /**
     * Recomputes average rating and updates mentor record
     */
    public BigDecimal calculateAverageRating(Connection conn, int mentorId) throws SQLException {
        String sql = "SELECT AVG(rating) as avg_rating, COUNT(id) as cnt FROM session_feedback WHERE mentor_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble("avg_rating");
                    return BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP);
                }
            }
        }
        return BigDecimal.ZERO;
    }
}