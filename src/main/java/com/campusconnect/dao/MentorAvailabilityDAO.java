
package com.campusconnect.dao;

import com.campusconnect.config.DatabaseConfig;
import com.campusconnect.model.MentorAvailability;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MentorAvailabilityDAO {

    /**
     * Retrieves all active availability slots for a mentor,
     * ordered by day of the week and start time.
     */
    public List<MentorAvailability> getAvailabilityByMentor(int mentorId)
            throws SQLException {

        String sql =
                "SELECT ma.*, u.full_name AS mentor_name " +
                "FROM mentor_availability ma " +
                "JOIN users u ON ma.mentor_id = u.id " +
                "WHERE ma.mentor_id = ? AND ma.is_active = TRUE " +
                "ORDER BY CASE UPPER(ma.day_of_week) " +
                "WHEN 'MONDAY' THEN 1 " +
                "WHEN 'TUESDAY' THEN 2 " +
                "WHEN 'WEDNESDAY' THEN 3 " +
                "WHEN 'THURSDAY' THEN 4 " +
                "WHEN 'FRIDAY' THEN 5 " +
                "WHEN 'SATURDAY' THEN 6 " +
                "WHEN 'SUNDAY' THEN 7 " +
                "ELSE 8 END, ma.start_time";

        List<MentorAvailability> list = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mentorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MentorAvailability ma = new MentorAvailability();

                    ma.setId(rs.getInt("id"));
                    ma.setMentorId(rs.getInt("mentor_id"));
                    ma.setMentorName(rs.getString("mentor_name"));
                    ma.setDayOfWeek(rs.getString("day_of_week"));
                    ma.setSpecificDate(rs.getDate("specific_date"));
                    ma.setStartTime(rs.getTime("start_time"));
                    ma.setEndTime(rs.getTime("end_time"));
                    ma.setActive(rs.getBoolean("is_active"));
                    ma.setCreatedAt(rs.getTimestamp("created_at"));

                    list.add(ma);
                }
            }
        }

        return list;
    }

    /**
     * Retrieves a specific availability slot by its ID.
     */
    public MentorAvailability getSlotById(int id) throws SQLException {

        String sql =
                "SELECT ma.*, u.full_name AS mentor_name " +
                "FROM mentor_availability ma " +
                "JOIN users u ON ma.mentor_id = u.id " +
                "WHERE ma.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MentorAvailability ma = new MentorAvailability();

                    ma.setId(rs.getInt("id"));
                    ma.setMentorId(rs.getInt("mentor_id"));
                    ma.setMentorName(rs.getString("mentor_name"));
                    ma.setDayOfWeek(rs.getString("day_of_week"));
                    ma.setSpecificDate(rs.getDate("specific_date"));
                    ma.setStartTime(rs.getTime("start_time"));
                    ma.setEndTime(rs.getTime("end_time"));
                    ma.setActive(rs.getBoolean("is_active"));
                    ma.setCreatedAt(rs.getTimestamp("created_at"));

                    return ma;
                }
            }
        }

        return null;
    }

    /**
     * Adds a new availability slot and returns its generated ID.
     */
    public int addSlot(MentorAvailability slot) throws SQLException {

        String sql =
                "INSERT INTO mentor_availability " +
                "(mentor_id, day_of_week, specific_date, start_time, " +
                "end_time, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, slot.getMentorId());

            String day = slot.getDayOfWeek();

            ps.setString(
                    2,
                    day != null && !day.trim().isEmpty()
                            ? day.trim().toUpperCase()
                            : "MONDAY"
            );

            ps.setDate(3, slot.getSpecificDate());
            ps.setTime(4, slot.getStartTime());
            ps.setTime(5, slot.getEndTime());
            ps.setBoolean(6, slot.isActive());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    slot.setId(id);
                    return id;
                }
            }
        }

        return -1;
    }

    /**
     * Deletes an availability slot only if it belongs to the specified mentor.
     */
    public boolean deleteSlot(int slotId, int mentorId) throws SQLException {

        String sql =
                "DELETE FROM mentor_availability " +
                "WHERE id = ? AND mentor_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, slotId);
            ps.setInt(2, mentorId);

            return ps.executeUpdate() > 0;
        }
    }
}

