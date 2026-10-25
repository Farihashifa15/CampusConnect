package com.campusconnect.dao;

import com.campusconnect.config.DatabaseConfig;
import com.campusconnect.model.Skill;
import com.campusconnect.model.UserSkill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkillDAO {

    public List<Skill> getAllSkills() throws SQLException {
        String sql = "SELECT * FROM skills ORDER BY category, name";
        List<Skill> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Skill s = new Skill(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getString("description")
                );
                s.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(s);
            }
        }
        return list;
    }

    public Skill getSkillById(int id) throws SQLException {
        String sql = "SELECT * FROM skills WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Skill s = new Skill(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("description")
                    );
                    s.setCreatedAt(rs.getTimestamp("created_at"));
                    return s;
                }
            }
        }
        return null;
    }

    public int createSkill(Skill skill) throws SQLException {
        String sql = "INSERT INTO skills (name, category, description) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, skill.getName().trim());
            ps.setString(2, skill.getCategory().trim());
            ps.setString(3, skill.getDescription());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    skill.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public List<UserSkill> getUserSkills(int userId, UserSkill.SkillType type) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT us.*, s.name AS skill_name, s.category AS skill_category ")
            .append("FROM user_skills us JOIN skills s ON us.skill_id = s.id ")
            .append("WHERE us.user_id = ? ");
        if (type != null) {
            sql.append("AND us.skill_type = ? ");
        }
        sql.append("ORDER BY s.name");

        List<UserSkill> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, userId);
            if (type != null) {
                ps.setString(2, type.name());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserSkill us = new UserSkill();
                    us.setId(rs.getInt("id"));
                    us.setUserId(rs.getInt("user_id"));
                    us.setSkillId(rs.getInt("skill_id"));
                    us.setSkillName(rs.getString("skill_name"));
                    us.setSkillCategory(rs.getString("skill_category"));
                    us.setSkillType(UserSkill.SkillType.valueOf(rs.getString("skill_type")));
                    us.setProficiencyLevel(UserSkill.ProficiencyLevel.valueOf(rs.getString("proficiency_level")));
                    us.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(us);
                }
            }
        }
        return list;
    }

    public boolean addUserSkill(int userId, int skillId, UserSkill.SkillType type, UserSkill.ProficiencyLevel level) throws SQLException {
        String sql = "INSERT INTO user_skills (user_id, skill_id, skill_type, proficiency_level) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, skillId);
            ps.setString(3, type.name());
            ps.setString(4, level != null ? level.name() : UserSkill.ProficiencyLevel.INTERMEDIATE.name());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean removeUserSkill(int userId, int skillId, UserSkill.SkillType type) throws SQLException {
        String sql = "DELETE FROM user_skills WHERE user_id = ? AND skill_id = ? AND skill_type = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, skillId);
            ps.setString(3, type.name());
            return ps.executeUpdate() > 0;
        }
    }
}