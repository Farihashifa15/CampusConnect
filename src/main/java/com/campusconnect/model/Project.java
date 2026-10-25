package com.campusconnect.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        OPEN, FILLED, COMPLETED, CANCELLED
    }

    private int id;
    private int creatorId;
    private String creatorName;
    private String creatorRole;
    private String title;
    private String description;
    private String category;
    private String requiredSkills;
    private int availablePositions = 1;
    private int filledPositions = 0;
    private Status status = Status.OPEN;
    private int applicationCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Project() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCreatorId() { return creatorId; }
    public void setCreatorId(int creatorId) { this.creatorId = creatorId; }

    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    public String getCreatorRole() { return creatorRole; }
    public void setCreatorRole(String creatorRole) { this.creatorRole = creatorRole; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }

    public int getAvailablePositions() { return availablePositions; }
    public void setAvailablePositions(int availablePositions) { this.availablePositions = availablePositions; }

    public int getFilledPositions() { return filledPositions; }
    public void setFilledPositions(int filledPositions) { this.filledPositions = filledPositions; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public int getApplicationCount() { return applicationCount; }
    public void setApplicationCount(int applicationCount) { this.applicationCount = applicationCount; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public boolean hasOpenPositions() {
        return status == Status.OPEN && (availablePositions > filledPositions);
    }
}