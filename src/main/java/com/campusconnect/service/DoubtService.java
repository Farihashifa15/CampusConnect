package com.campusconnect.service;

import com.campusconnect.config.DatabaseConfig;
import com.campusconnect.dao.DoubtDAO;
import com.campusconnect.dao.NotificationDAO;
import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.DoubtAnswer;
import com.campusconnect.model.DoubtQuestion;
import com.campusconnect.model.Notification;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DoubtService {

    private final DoubtDAO doubtDAO;
    private final CreditLedgerService creditService;
    private final NotificationDAO notificationDAO;

    public DoubtService() {
        this(new DoubtDAO(), new CreditLedgerService(), new NotificationDAO());
    }

    public DoubtService(DoubtDAO doubtDAO, CreditLedgerService creditService, NotificationDAO notificationDAO) {
        this.doubtDAO = doubtDAO;
        this.creditService = creditService;
        this.notificationDAO = notificationDAO;
    }

    public DoubtQuestion postQuestion(int authorId, Integer skillId, String category, String title, String description) throws SQLException, ValidationException {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Question title is required.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Question description is required.");
        }
        if (category == null || category.trim().isEmpty()) {
            category = "General Academic";
        }

        DoubtQuestion q = new DoubtQuestion();
        q.setAuthorId(authorId);
        q.setSkillId(skillId);
        q.setCategory(category.trim());
        q.setTitle(title.trim());
        q.setDescription(description.trim());
        q.setStatus(DoubtQuestion.Status.OPEN);

        int id = doubtDAO.createQuestion(q);
        q.setId(id);
        return q;
    }

    public DoubtAnswer postAnswer(int questionId, int authorId, String answerText) throws SQLException, ValidationException {
        if (answerText == null || answerText.trim().isEmpty()) {
            throw new ValidationException("Answer text cannot be empty.");
        }
        DoubtQuestion question = doubtDAO.getQuestionById(questionId);
        if (question == null) {
            throw new ValidationException("Question not found.");
        }
        if (question.getStatus() == DoubtQuestion.Status.CLOSED) {
            throw new ValidationException("This question has been closed.");
        }

        DoubtAnswer answer = new DoubtAnswer();
        answer.setQuestionId(questionId);
        answer.setAuthorId(authorId);
        answer.setAnswerText(answerText.trim());
        answer.setAccepted(false);

        int id = doubtDAO.createAnswer(answer);
        answer.setId(id);

        // Notify question author
        if (question.getAuthorId() != authorId) {
            Notification notif = new Notification();
            notif.setUserId(question.getAuthorId());
            notif.setTitle("New Answer on Your Doubt");
            notif.setMessage("Someone posted an answer to your question: '" + question.getTitle() + "'");
            notif.setType("ANSWER");
            notif.setLink("/doubts/view?id=" + questionId);
            notificationDAO.createNotification(notif);
        }

        return answer;
    }

    public boolean acceptAnswer(int questionAuthorId, int questionId, int answerId) throws SQLException, ValidationException {
        DoubtQuestion question = doubtDAO.getQuestionById(questionId);
        if (question == null) {
            throw new ValidationException("Question not found.");
        }
        if (question.getAuthorId() != questionAuthorId) {
            throw new ValidationException("Unauthorized: Only the question author can accept an answer.");
        }

        DoubtAnswer answer = doubtDAO.getAnswerById(answerId);
        if (answer == null || answer.getQuestionId() != questionId) {
            throw new ValidationException("Invalid answer for this question.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            doubtDAO.markAnswerAccepted(conn, questionId, answerId);
            conn.commit();

            // Reward answer author with Skill Credits (transactional duplicate-protected)
            creditService.awardAcceptedAnswer(answer.getAuthorId(), questionId, question.getTitle());

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

    public DoubtQuestion getQuestion(int id) throws SQLException {
        return doubtDAO.getQuestionById(id);
    }

    public List<DoubtAnswer> getAnswers(int questionId) throws SQLException {
        return doubtDAO.getAnswersForQuestion(questionId);
    }

    public List<DoubtQuestion> searchQuestions(String keyword, String category, DoubtQuestion.Status status, int limit, int offset) throws SQLException {
        return doubtDAO.getQuestions(keyword, category, status, limit, offset);
    }
}