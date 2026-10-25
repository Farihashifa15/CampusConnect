package com.campusconnect.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class DoubtQuestion implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        OPEN, RESOLVED, CLOSED, REPORTED
    }

    private int id;
    private int authorId;
    private String authorName;
    private String authorRole;
    private Integer skillId;
    private String skillName;
    private String category;
    private String title;
    private String description;
    private Integer acceptedAnswerId;
    private Status status = Status.OPEN;
    private int answerCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public DoubtQuestion() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorRole() { return authorRole; }
    public void setAuthorRole(String authorRole) { this.authorRole = authorRole; }

    public Integer getSkillId() { return skillId; }
    public void setSkillId(Integer skillId) { this.skillId = skillId; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getAcceptedAnswerId() { return acceptedAnswerId; }
    public void setAcceptedAnswerId(Integer acceptedAnswerId) { this.acceptedAnswerId = acceptedAnswerId; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public int getAnswerCount() { return answerCount; }
    public void setAnswerCount(int answerCount) { this.answerCount = answerCount; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}