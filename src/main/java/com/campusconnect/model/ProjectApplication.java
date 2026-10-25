package com.campusconnect.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ProjectApplication implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        PENDING, ACCEPTED, REJECTED
    }

    private int id;
    private int projectId;
    private String projectTitle;
    private int applicantId;
    private String applicantName;
    private String applicantDepartment;
    private String pitch;
    private Status status = Status.PENDING;
    private Timestamp createdAt;

    public ProjectApplication() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public int getApplicantId() { return applicantId; }
    public void setApplicantId(int applicantId) { this.applicantId = applicantId; }

    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

    public String getApplicantDepartment() { return applicantDepartment; }
    public void setApplicantDepartment(String applicantDepartment) { this.applicantDepartment = applicantDepartment; }

    public String getPitch() { return pitch; }
    public void setPitch(String pitch) { this.pitch = pitch; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}