package com.campusconnect.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ContentReport implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum ContentType {
        QUESTION, ANSWER, RESOURCE, PROJECT, USER
    }

    public enum Status {
        PENDING, RESOLVED, DISMISSED
    }

    private int id;
    private int reporterId;
    private String reporterName;
    private ContentType contentType;
    private int contentId;
    private String reason;
    private Status status = Status.PENDING;
    private Timestamp createdAt;

    public ContentReport() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReporterId() { return reporterId; }
    public void setReporterId(int reporterId) { this.reporterId = reporterId; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    public ContentType getContentType() { return contentType; }
    public void setContentType(ContentType contentType) { this.contentType = contentType; }

    public int getContentId() { return contentId; }
    public void setContentId(int contentId) { this.contentId = contentId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}