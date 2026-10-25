package com.campusconnect.service;

import com.campusconnect.config.AppConfig;
import com.campusconnect.config.DatabaseConfig;
import com.campusconnect.dao.MentorAvailabilityDAO;
import com.campusconnect.dao.NotificationDAO;
import com.campusconnect.dao.SessionDAO;
import com.campusconnect.dao.SkillCreditDAO;
import com.campusconnect.dao.UserDAO;
import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;

public class SessionBookingService {

    private final SessionDAO sessionDAO;
    private final UserDAO userDAO;
    private final SkillCreditDAO creditDAO;
    private final MentorAvailabilityDAO availabilityDAO;
    private final NotificationDAO notificationDAO;

    public SessionBookingService() {
        this(new SessionDAO(), new UserDAO(), new SkillCreditDAO(), new MentorAvailabilityDAO(), new NotificationDAO());
    }

    public SessionBookingService(SessionDAO sessionDAO, UserDAO userDAO, SkillCreditDAO creditDAO,
                                 MentorAvailabilityDAO availabilityDAO, NotificationDAO notificationDAO) {
        this.sessionDAO = sessionDAO;
        this.userDAO = userDAO;
        this.creditDAO = creditDAO;
        this.availabilityDAO = availabilityDAO;
        this.notificationDAO = notificationDAO;
    }

    public Session requestBooking(int learnerId, int mentorId, int skillId, Date sessionDate,
                                  Time startTime, Time endTime, String notes) throws SQLException, ValidationException {
        if (learnerId == mentorId) {
            throw new ValidationException("You cannot book a mentorship session with yourself.");
        }

        LocalDate today = LocalDate.now();
        if (sessionDate.toLocalDate().isBefore(today)) {
            throw new ValidationException("Cannot book a mentorship session in the past.");
        }

        if (!startTime.before(endTime)) {
            throw new ValidationException("Session start time must be before end time.");
        }

        int creditCost = AppConfig.getSessionBookingCost();

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            // Recheck conflict on server with transaction isolation
            // Interval overlap formula: start1 < end2 AND start2 < end1
            boolean hasConflict = sessionDAO.hasConflictingBooking(conn, mentorId, sessionDate, startTime, endTime, null);
            if (hasConflict) {
                throw new ValidationException("The mentor already has a conflicting booking during this time interval.");
            }

            int currentBalance = userDAO.getCreditBalance(conn, learnerId);
            if (currentBalance < creditCost) {
                throw new ValidationException("Insufficient Skill Credits. Required: " + creditCost + ", Available: " + currentBalance);
            }

            // Debit learner balance
            int newBalance = currentBalance - creditCost;
            userDAO.updateCreditBalance(conn, learnerId, newBalance);

            // Create session record
            Session session = new Session();
            session.setMentorId(mentorId);
            session.setLearnerId(learnerId);
            session.setSkillId(skillId);
            session.setSessionDate(sessionDate);
            session.setStartTime(startTime);
            session.setEndTime(endTime);
            session.setStatus(Session.Status.PENDING);
            session.setNotes(notes);
            session.setCreditCost(creditCost);

            int sessionId = sessionDAO.createSession(conn, session);
            session.setId(sessionId);

            // Record transaction in credit ledger
            SkillCreditTransaction tx = new SkillCreditTransaction();
            tx.setUserId(learnerId);
            tx.setAmount(-creditCost);
            tx.setTransactionType(SkillCreditTransaction.Type.SESSION_BOOKING);
            tx.setReason("Mentorship session booking fee (Escrowed)");
            tx.setRelatedEntityType("SESSION");
            tx.setRelatedEntityId(sessionId);
            creditDAO.recordTransaction(conn, tx);

            // Notify mentor
            Notification notif = new Notification();
            notif.setUserId(mentorId);
            notif.setTitle("New Mentorship Request");
            notif.setMessage("You have a new mentorship session request for " + sessionDate + " at " + startTime + ".");
            notif.setType("BOOKING");
            notif.setLink("/sessions/view?id=" + sessionId);
            notificationDAO.createNotification(notif);

            conn.commit();
            return session;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            if (e instanceof ValidationException) {
                throw (ValidationException) e;
            }
            throw new SQLException("Failed to process session booking: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public boolean confirmBooking(int mentorId, int sessionId) throws SQLException, ValidationException {
        Session session = sessionDAO.getSessionById(sessionId);
        if (session == null) {
            throw new ValidationException("Session not found.");
        }
        if (session.getMentorId() != mentorId) {
            throw new ValidationException("Unauthorized: You are not the mentor for this session.");
        }
        if (session.getStatus() != Session.Status.PENDING) {
            throw new ValidationException("Only PENDING sessions can be confirmed.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            boolean hasConflict = sessionDAO.hasConflictingBooking(conn, mentorId, session.getSessionDate(), session.getStartTime(), session.getEndTime(), sessionId);
            if (hasConflict) {
                throw new ValidationException("Cannot confirm: Overlapping confirmed booking detected.");
            }

            sessionDAO.updateSessionStatus(conn, sessionId, Session.Status.CONFIRMED);

            Notification notif = new Notification();
            notif.setUserId(session.getLearnerId());
            notif.setTitle("Session Request Confirmed");
            notif.setMessage("Your mentorship session for " + session.getSessionDate() + " has been confirmed by your mentor.");
            notif.setType("SESSION");
            notif.setLink("/sessions/view?id=" + sessionId);
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

    public boolean rejectBooking(int mentorId, int sessionId, String reason) throws SQLException, ValidationException {
        Session session = sessionDAO.getSessionById(sessionId);
        if (session == null) throw new ValidationException("Session not found.");
        if (session.getMentorId() != mentorId) throw new ValidationException("Unauthorized: You are not the mentor.");
        if (session.getStatus() != Session.Status.PENDING) {
            throw new ValidationException("Only PENDING sessions can be rejected.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            sessionDAO.updateSessionStatus(conn, sessionId, Session.Status.REJECTED);

            int learnerBalance = userDAO.getCreditBalance(conn, session.getLearnerId());
            userDAO.updateCreditBalance(conn, session.getLearnerId(), learnerBalance + session.getCreditCost());

            SkillCreditTransaction refundTx = new SkillCreditTransaction();
            refundTx.setUserId(session.getLearnerId());
            refundTx.setAmount(session.getCreditCost());
            refundTx.setTransactionType(SkillCreditTransaction.Type.SESSION_REFUND);
            refundTx.setReason("Refund for rejected session: " + (reason != null ? reason : "Declined by mentor"));
            refundTx.setRelatedEntityType("SESSION");
            refundTx.setRelatedEntityId(sessionId);
            creditDAO.recordTransaction(conn, refundTx);

            Notification notif = new Notification();
            notif.setUserId(session.getLearnerId());
            notif.setTitle("Session Request Declined");
            notif.setMessage("Your booking request for " + session.getSessionDate() + " was declined. Your credits have been refunded.");
            notif.setType("SESSION");
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

    public boolean cancelBooking(int requestingUserId, int sessionId, String reason) throws SQLException, ValidationException {
        Session session = sessionDAO.getSessionById(sessionId);
        if (session == null) throw new ValidationException("Session not found.");
        if (session.getLearnerId() != requestingUserId && session.getMentorId() != requestingUserId) {
            throw new ValidationException("Unauthorized: You are not a participant in this session.");
        }
        if (session.getStatus() != Session.Status.PENDING && session.getStatus() != Session.Status.CONFIRMED) {
            throw new ValidationException("Session cannot be cancelled in its current state: " + session.getStatus());
        }

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            sessionDAO.updateSessionStatus(conn, sessionId, Session.Status.CANCELLED);

            int learnerBalance = userDAO.getCreditBalance(conn, session.getLearnerId());
            userDAO.updateCreditBalance(conn, session.getLearnerId(), learnerBalance + session.getCreditCost());

            SkillCreditTransaction refundTx = new SkillCreditTransaction();
            refundTx.setUserId(session.getLearnerId());
            refundTx.setAmount(session.getCreditCost());
            refundTx.setTransactionType(SkillCreditTransaction.Type.SESSION_REFUND);
            refundTx.setReason("Refund for cancelled session (" + session.getStatus() + ")");
            refundTx.setRelatedEntityType("SESSION");
            refundTx.setRelatedEntityId(sessionId);
            creditDAO.recordTransaction(conn, refundTx);

            int notifyRecipient = (requestingUserId == session.getLearnerId()) ? session.getMentorId() : session.getLearnerId();
            Notification notif = new Notification();
            notif.setUserId(notifyRecipient);
            notif.setTitle("Session Cancelled");
            notif.setMessage("The mentorship session scheduled for " + session.getSessionDate() + " was cancelled.");
            notif.setType("SESSION");
            notif.setLink("/sessions");
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

    public boolean completeSession(int mentorId, int sessionId) throws SQLException, ValidationException {
        Session session = sessionDAO.getSessionById(sessionId);
        if (session == null) throw new ValidationException("Session not found.");
        if (session.getMentorId() != mentorId) throw new ValidationException("Unauthorized: Only the mentor can complete the session.");
        if (session.getStatus() != Session.Status.CONFIRMED) {
            throw new ValidationException("Only CONFIRMED sessions can be marked as COMPLETED.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            sessionDAO.updateSessionStatus(conn, sessionId, Session.Status.COMPLETED);

            int rewardAmount = AppConfig.getSessionCompletionReward();
            int mentorBalance = userDAO.getCreditBalance(conn, mentorId);
            userDAO.updateCreditBalance(conn, mentorId, mentorBalance + rewardAmount);

            if (!creditDAO.hasRewardBeenClaimed("SESSION", sessionId, SkillCreditTransaction.Type.SESSION_COMPLETION)) {
                SkillCreditTransaction rewardTx = new SkillCreditTransaction();
                rewardTx.setUserId(mentorId);
                rewardTx.setAmount(rewardAmount);
                rewardTx.setTransactionType(SkillCreditTransaction.Type.SESSION_COMPLETION);
                rewardTx.setReason("Mentorship session completion reward");
                rewardTx.setRelatedEntityType("SESSION");
                rewardTx.setRelatedEntityId(sessionId);
                creditDAO.recordTransaction(conn, rewardTx);
            }

            User mentor = userDAO.getUserById(mentorId);
            int newCount = mentor.getCompletedSessionsCount() + 1;
            userDAO.updateRatingAndCount(conn, mentorId, mentor.getMentorRating(), newCount);

            Notification notif = new Notification();
            notif.setUserId(session.getLearnerId());
            notif.setTitle("Session Completed! Leave Feedback");
            notif.setMessage("Your session on " + session.getSkillName() + " has completed. Please rate your mentor and endorse their skill!");
            notif.setType("SESSION");
            notif.setLink("/feedback/add?sessionId=" + sessionId);
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
}