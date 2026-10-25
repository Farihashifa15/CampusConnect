package com.campusconnect.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class SessionFeedback implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int sessionId;
    private int mentorId;
    private int learnerId;
    private String learnerName;
    private int rating; // 1 to 5
    private String feedbackText;
    private Timestamp createdAt;

    public SessionFeedback() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public int getMentorId() { return mentorId; }
    public void setMentorId(int mentorId) { this.mentorId = mentorId; }

    public int getLearnerId() { return learnerId; }
    public void setLearnerId(int learnerId) { this.learnerId = learnerId; }

    public String getLearnerName() { return learnerName; }
    public void setLearnerName(String learnerName) { this.learnerName = learnerName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getFeedbackText() { return feedbackText; }
    public void setFeedbackText(String feedbackText) { this.feedbackText = feedbackText; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}