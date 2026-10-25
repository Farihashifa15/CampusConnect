package com.campusconnect.service;

import com.campusconnect.config.DatabaseConfig;
import com.campusconnect.dao.FeedbackAndEndorsementDAO;
import com.campusconnect.dao.NotificationDAO;
import com.campusconnect.dao.SessionDAO;
import com.campusconnect.dao.UserDAO;
import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.Endorsement;
import com.campusconnect.model.Notification;
import com.campusconnect.model.Session;
import com.campusconnect.model.SessionFeedback;
import com.campusconnect.model.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class FeedbackService {

    private final FeedbackAndEndorsementDAO feedbackDAO;
    private final SessionDAO sessionDAO;
    private final UserDAO userDAO;
    private final NotificationDAO notificationDAO;

    public FeedbackService() {
        this(new FeedbackAndEndorsementDAO(), new SessionDAO(), new UserDAO(), new NotificationDAO());
    }

    public FeedbackService(FeedbackAndEndorsementDAO feedbackDAO, SessionDAO sessionDAO, UserDAO userDAO, NotificationDAO notificationDAO) {
        this.feedbackDAO = feedbackDAO;
        this.sessionDAO = sessionDAO;
        this.userDAO = userDAO;
        this.notificationDAO = notificationDAO;
    }

    public boolean submitFeedback(int learnerId, int sessionId, int rating, String feedbackText, boolean endorseSkill, String endorsementComment) throws SQLException, ValidationException {
        Session session = sessionDAO.getSessionById(sessionId);
        if (session == null) {
            throw new ValidationException("Session not found.");
        }
        if (session.getLearnerId() != learnerId) {
            throw new ValidationException("Unauthorized: Only the learner from this session can submit feedback.");
        }
        if (session.getStatus() != Session.Status.COMPLETED) {
            throw new ValidationException("Feedback can only be submitted for COMPLETED sessions.");
        }
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Rating must be between 1 and 5.");
        }
        if (feedbackDAO.hasFeedbackForSession(sessionId)) {
            throw new ValidationException("Feedback has already been submitted for this session.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            SessionFeedback feedback = new SessionFeedback();
            feedback.setSessionId(sessionId);
            feedback.setMentorId(session.getMentorId());
            feedback.setLearnerId(learnerId);
            feedback.setRating(rating);
            feedback.setFeedbackText(feedbackText != null ? feedbackText.trim() : "");
            feedbackDAO.addFeedback(conn, feedback);

            if (endorseSkill && !feedbackDAO.hasEndorsementForSession(sessionId)) {
                Endorsement endorsement = new Endorsement();
                endorsement.setSessionId(sessionId);
                endorsement.setMentorId(session.getMentorId());
                endorsement.setLearnerId(learnerId);
                endorsement.setSkillId(session.getSkillId());
                endorsement.setComment(endorsementComment != null ? endorsementComment.trim() : "Skill demonstrated during mentorship session.");
                feedbackDAO.addEndorsement(conn, endorsement);
            }

            BigDecimal newAvgRating = feedbackDAO.calculateAverageRating(conn, session.getMentorId());
            User mentor = userDAO.getUserById(session.getMentorId());
            userDAO.updateRatingAndCount(conn, session.getMentorId(), newAvgRating, mentor.getCompletedSessionsCount());

            Notification notif = new Notification();
            notif.setUserId(session.getMentorId());
            notif.setTitle("New Feedback & Rating Received");
            notif.setMessage("You received a " + rating + "-star rating from your student for session #" + sessionId + ".");
            notif.setType("FEEDBACK");
            notif.setLink("/profile?id=" + session.getMentorId());
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