package com.campusconnect.dao;

import com.campusconnect.config.DatabaseConfig;
import com.campusconnect.model.Session;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO {

    public int createSession(Connection conn, Session session) throws SQLException {
        String sql = "INSERT INTO sessions (mentor_id, learner_id, skill_id, session_date, start_time, end_time, status, notes, credit_cost) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, session.getMentorId());
            ps.setInt(2, session.getLearnerId());
            ps.setInt(3, session.getSkillId());
            ps.setDate(4, session.getSessionDate());
            ps.setTime(5, session.getStartTime());
            ps.setTime(6, session.getEndTime());
            ps.setString(7, session.getStatus().name());
            ps.setString(8, session.getNotes());
            ps.setInt(9, session.getCreditCost());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    session.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public Session getSessionById(int id) throws SQLException {
        String sql = "SELECT s.*, m.full_name AS mentor_name, l.full_name AS learner_name, sk.name AS skill_name " +
                     "FROM sessions s " +
                     "JOIN users m ON s.mentor_id = m.id " +
                     "JOIN users l ON s.learner_id = l.id " +
                     "JOIN skills sk ON s.skill_id = sk.id " +
                     "WHERE s.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSession(rs);
                }
            }
        }
        return null;
    }

    /**
     * Checks if mentor has a conflicting booking.
     * Interval overlap formula: start1 < end2 AND start2 < end1
     * Adjacent sessions are allowed if one ends exactly when the other begins.
     */
    public boolean hasConflictingBooking(Connection conn, int mentorId, Date sessionDate, Time startTime, Time endTime, Integer excludeSessionId) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT id FROM sessions WHERE mentor_id = ? AND session_date = ? ")
            .append("AND status IN ('CONFIRMED', 'PENDING') ")
            .append("AND (? < end_time AND ? > start_time) ");
        if (excludeSessionId != null) {
            sql.append("AND id != ? ");
        }

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, mentorId);
            ps.setDate(2, sessionDate);
            ps.setTime(3, startTime);
            ps.setTime(4, endTime);
            if (excludeSessionId != null) {
                ps.setInt(5, excludeSessionId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true if overlap found
            }
        }
    }

    public boolean updateSessionStatus(Connection conn, int sessionId, Session.Status newStatus) throws SQLException {
        String sql = "UPDATE sessions SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus.name());
            ps.setInt(2, sessionId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Session> getSessionsForMentor(int mentorId, Session.Status status) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT s.*, m.full_name AS mentor_name, l.full_name AS learner_name, sk.name AS skill_name ")
            .append("FROM sessions s ")
            .append("JOIN users m ON s.mentor_id = m.id ")
            .append("JOIN users l ON s.learner_id = l.id ")
            .append("JOIN skills sk ON s.skill_id = sk.id ")
            .append("WHERE s.mentor_id = ? ");
        if (status != null) {
            sql.append("AND s.status = ? ");
        }
        sql.append("ORDER BY s.session_date DESC, s.start_time DESC");

        List<Session> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, mentorId);
            if (status != null) {
                ps.setString(2, status.name());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSession(rs));
                }
            }
        }
        return list;
    }

    public List<Session> getSessionsForLearner(int learnerId, Session.Status status) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT s.*, m.full_name AS mentor_name, l.full_name AS learner_name, sk.name AS skill_name ")
            .append("FROM sessions s ")
            .append("JOIN users m ON s.mentor_id = m.id ")
            .append("JOIN users l ON s.learner_id = l.id ")
            .append("JOIN skills sk ON s.skill_id = sk.id ")
            .append("WHERE s.learner_id = ? ");
        if (status != null) {
            sql.append("AND s.status = ? ");
        }
        sql.append("ORDER BY s.session_date DESC, s.start_time DESC");

        List<Session> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, learnerId);
            if (status != null) {
                ps.setString(2, status.name());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSession(rs));
                }
            }
        }
        return list;
    }

    private Session mapSession(ResultSet rs) throws SQLException {
        Session s = new Session();
        s.setId(rs.getInt("id"));
        s.setMentorId(rs.getInt("mentor_id"));
        s.setMentorName(rs.getString("mentor_name"));
        s.setLearnerId(rs.getInt("learner_id"));
        s.setLearnerName(rs.getString("learner_name"));
        s.setSkillId(rs.getInt("skill_id"));
        s.setSkillName(rs.getString("skill_name"));
        s.setSessionDate(rs.getDate("session_date"));
        s.setStartTime(rs.getTime("start_time"));
        s.setEndTime(rs.getTime("end_time"));
        s.setStatus(Session.Status.valueOf(rs.getString("status")));
        s.setNotes(rs.getString("notes"));
        s.setCreditCost(rs.getInt("credit_cost"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setUpdatedAt(rs.getTimestamp("updated_at"));
        return s;
    }
}