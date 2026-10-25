package com.campusconnect.service;

import com.campusconnect.config.AppConfig;
import com.campusconnect.config.DatabaseConfig;
import com.campusconnect.dao.NotificationDAO;
import com.campusconnect.dao.SkillCreditDAO;
import com.campusconnect.dao.UserDAO;
import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.Notification;
import com.campusconnect.model.SkillCreditTransaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CreditLedgerService {

    private final SkillCreditDAO creditDAO;
    private final UserDAO userDAO;
    private final NotificationDAO notificationDAO;

    public CreditLedgerService() {
        this(new SkillCreditDAO(), new UserDAO(), new NotificationDAO());
    }

    public CreditLedgerService(SkillCreditDAO creditDAO, UserDAO userDAO, NotificationDAO notificationDAO) {
        this.creditDAO = creditDAO;
        this.userDAO = userDAO;
        this.notificationDAO = notificationDAO;
    }

    public int getBalance(int userId) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return userDAO.getCreditBalance(conn, userId);
        }
    }

    public List<SkillCreditTransaction> getLedgerHistory(int userId, int limit, int offset) throws SQLException {
        return creditDAO.getTransactionsByUser(userId, limit, offset);
    }

    public int getTotalEarned(int userId) throws SQLException {
        return creditDAO.getTotalEarned(userId);
    }

    public int getTotalSpent(int userId) throws SQLException {
        return creditDAO.getTotalSpent(userId);
    }

    public boolean awardResourceUpload(int uploaderId, int resourceId, String resourceTitle) throws SQLException {
        int rewardAmount = AppConfig.getResourceUploadReward();

        if (creditDAO.hasRewardBeenClaimed("RESOURCE", resourceId, SkillCreditTransaction.Type.RESOURCE_UPLOAD)) {
            return false;
        }

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            int currentBalance = userDAO.getCreditBalance(conn, uploaderId);
            userDAO.updateCreditBalance(conn, uploaderId, currentBalance + rewardAmount);

            SkillCreditTransaction tx = new SkillCreditTransaction();
            tx.setUserId(uploaderId);
            tx.setAmount(rewardAmount);
            tx.setTransactionType(SkillCreditTransaction.Type.RESOURCE_UPLOAD);
            tx.setReason("Eligible learning resource contribution: " + resourceTitle);
            tx.setRelatedEntityType("RESOURCE");
            tx.setRelatedEntityId(resourceId);
            creditDAO.recordTransaction(conn, tx);

            Notification notif = new Notification();
            notif.setUserId(uploaderId);
            notif.setTitle("Skill Credits Earned!");
            notif.setMessage("You earned " + rewardAmount + " Skill Credits for publishing an educational resource.");
            notif.setType("CREDIT");
            notif.setLink("/credits");
            notificationDAO.createNotification(notif);

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new SQLException(e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public boolean awardAcceptedAnswer(int answerAuthorId, int questionId, String questionTitle) throws SQLException {
        int rewardAmount = AppConfig.getAcceptedAnswerReward();

        if (creditDAO.hasRewardBeenClaimed("DOUBT", questionId, SkillCreditTransaction.Type.ACCEPTED_ANSWER)) {
            return false;
        }

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            int currentBalance = userDAO.getCreditBalance(conn, answerAuthorId);
            userDAO.updateCreditBalance(conn, answerAuthorId, currentBalance + rewardAmount);

            SkillCreditTransaction tx = new SkillCreditTransaction();
            tx.setUserId(answerAuthorId);
            tx.setAmount(rewardAmount);
            tx.setTransactionType(SkillCreditTransaction.Type.ACCEPTED_ANSWER);
            tx.setReason("Accepted answer on Doubt Board: " + questionTitle);
            tx.setRelatedEntityType("DOUBT");
            tx.setRelatedEntityId(questionId);
            creditDAO.recordTransaction(conn, tx);

            Notification notif = new Notification();
            notif.setUserId(answerAuthorId);
            notif.setTitle("Answer Accepted (+ " + rewardAmount + " Credits)!");
            notif.setMessage("Your answer to '" + questionTitle + "' was marked as accepted! " + rewardAmount + " credits added.");
            notif.setType("CREDIT");
            notif.setLink("/credits");
            notificationDAO.createNotification(notif);

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new SQLException(e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public boolean adminAdjustCredits(int adminId, int targetUserId, int amount, String reason) throws SQLException, ValidationException {
        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException("Adjustment reason is required.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            int currentBalance = userDAO.getCreditBalance(conn, targetUserId);
            int newBalance = currentBalance + amount;
            if (newBalance < 0) {
                throw new ValidationException("Adjustment would cause negative balance (Current: " + currentBalance + ", Adjustment: " + amount + ")");
            }

            userDAO.updateCreditBalance(conn, targetUserId, newBalance);

            SkillCreditTransaction tx = new SkillCreditTransaction();
            tx.setUserId(targetUserId);
            tx.setAmount(amount);
            tx.setTransactionType(SkillCreditTransaction.Type.ADMIN_ADJUSTMENT);
            tx.setReason("Administrative adjustment: " + reason.trim());
            tx.setRelatedEntityType("ADMIN");
            tx.setRelatedEntityId(adminId);
            creditDAO.recordTransaction(conn, tx);

            Notification notif = new Notification();
            notif.setUserId(targetUserId);
            notif.setTitle("Skill Credit Balance Adjustment");
            notif.setMessage("An administrator adjusted your credit balance by " + (amount >= 0 ? "+" : "") + amount + " credits. Reason: " + reason);
            notif.setType("CREDIT");
            notif.setLink("/credits");
            notificationDAO.createNotification(notif);

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            if (e instanceof ValidationException) throw (ValidationException) e;
            throw new SQLException(e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }
}