package com.campusconnect.dao;

import com.campusconnect.config.DatabaseConfig;
import com.campusconnect.model.SkillCreditTransaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkillCreditDAO {

    public int recordTransaction(Connection conn, SkillCreditTransaction tx) throws SQLException {
        String sql = "INSERT INTO skill_credit_transactions (user_id, amount, transaction_type, reason, related_entity_type, related_entity_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, tx.getUserId());
            ps.setInt(2, tx.getAmount());
            ps.setString(3, tx.getTransactionType().name());
            ps.setString(4, tx.getReason());
            ps.setString(5, tx.getRelatedEntityType());
            if (tx.getRelatedEntityId() != null) {
                ps.setInt(6, tx.getRelatedEntityId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    tx.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public List<SkillCreditTransaction> getTransactionsByUser(int userId, int limit, int offset) throws SQLException {
        String sql = "SELECT t.*, u.full_name AS user_name FROM skill_credit_transactions t " +
                     "JOIN users u ON t.user_id = u.id WHERE t.user_id = ? " +
                     "ORDER BY t.created_at DESC, t.id DESC LIMIT ? OFFSET ?";
        List<SkillCreditTransaction> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit > 0 ? limit : 50);
            ps.setInt(3, Math.max(offset, 0));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SkillCreditTransaction t = new SkillCreditTransaction();
                    t.setId(rs.getInt("id"));
                    t.setUserId(rs.getInt("user_id"));
                    t.setUserName(rs.getString("user_name"));
                    t.setAmount(rs.getInt("amount"));
                    t.setTransactionType(SkillCreditTransaction.Type.valueOf(rs.getString("transaction_type")));
                    t.setReason(rs.getString("reason"));
                    t.setRelatedEntityType(rs.getString("related_entity_type"));
                    int relId = rs.getInt("related_entity_id");
                    if (!rs.wasNull()) {
                        t.setRelatedEntityId(relId);
                    }
                    t.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(t);
                }
            }
        }
        return list;
    }

    public int getTotalEarned(int userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM skill_credit_transactions WHERE user_id = ? AND amount > 0";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTotalSpent(int userId) throws SQLException {
        String sql = "SELECT COALESCE(ABS(SUM(amount)), 0) FROM skill_credit_transactions WHERE user_id = ? AND amount < 0";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public boolean hasRewardBeenClaimed(String relatedType, int relatedId, SkillCreditTransaction.Type txType) throws SQLException {
        String sql = "SELECT id FROM skill_credit_transactions WHERE related_entity_type = ? AND related_entity_id = ? AND transaction_type = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, relatedType);
            ps.setInt(2, relatedId);
            ps.setString(3, txType.name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}