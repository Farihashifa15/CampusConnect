package com.campusconnect.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Resource implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Category {
        NOTES("Notes"),
        INTERVIEW_EXPERIENCE("Interview Experience"),
        QUESTION_PAPER("Question Paper"),
        PROJECT_REPORT("Project Report"),
        CAREER_ROADMAP("Career Roadmap"),
        INTERNSHIP_REFERRAL("Internship/Referral Posting");

        private final String displayName;
        Category(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }

        public static Category fromDisplayName(String name) {
            for (Category c : values()) {
                if (c.displayName.equalsIgnoreCase(name) || c.name().equalsIgnoreCase(name)) {
                    return c;
                }
            }
            return NOTES;
        }
    }

    public enum Status {
        APPROVED, PENDING, REPORTED, REMOVED
    }

    private int id;
    private int uploaderId;
    private String uploaderName;
    private String uploaderRole;
    private String title;
    private String description;
    private Category category = Category.NOTES;
    private String tags;
    private String storedFilename;
    private String originalFilename;
    private long fileSize;
    private String mimeType;
    private int downloadCount;
    private Status status = Status.APPROVED;
    private Timestamp createdAt;

    public Resource() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUploaderId() { return uploaderId; }
    public void setUploaderId(int uploaderId) { this.uploaderId = uploaderId; }

    public String getUploaderName() { return uploaderName; }
    public void setUploaderName(String uploaderName) { this.uploaderName = uploaderName; }

    public String getUploaderRole() { return uploaderRole; }
    public void setUploaderRole(String uploaderRole) { this.uploaderRole = uploaderRole; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getStoredFilename() { return storedFilename; }
    public void setStoredFilename(String storedFilename) { this.storedFilename = storedFilename; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public int getDownloadCount() { return downloadCount; }
    public void setDownloadCount(int downloadCount) { this.downloadCount = downloadCount; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}