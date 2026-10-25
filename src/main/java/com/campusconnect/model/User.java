package com.campusconnect.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Role {
        STUDENT, SENIOR, ALUMNI, ADMIN
    }

    public enum Status {
        ACTIVE, SUSPENDED
    }

    private int id;
    private String registerNumber;
    private String email;
    private String passwordHash;
    private String fullName;
    private Role role;
    private String department;
    private int academicYear;
    private String careerGoals;
    private String projectInterests;
    private String clubMemberships;
    private String bio;
    private BigDecimal mentorRating = BigDecimal.ZERO;
    private int completedSessionsCount;
    private int creditBalance = 50;
    private Status status = Status.ACTIVE;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public User() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRegisterNumber() { return registerNumber; }
    public void setRegisterNumber(String registerNumber) { this.registerNumber = registerNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getAcademicYear() { return academicYear; }
    public void setAcademicYear(int academicYear) { this.academicYear = academicYear; }

    public String getCareerGoals() { return careerGoals; }
    public void setCareerGoals(String careerGoals) { this.careerGoals = careerGoals; }

    public String getProjectInterests() { return projectInterests; }
    public void setProjectInterests(String projectInterests) { this.projectInterests = projectInterests; }

    public String getClubMemberships() { return clubMemberships; }
    public void setClubMemberships(String clubMemberships) { this.clubMemberships = clubMemberships; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public BigDecimal getMentorRating() { return mentorRating; }
    public void setMentorRating(BigDecimal mentorRating) { this.mentorRating = mentorRating; }

    public int getCompletedSessionsCount() { return completedSessionsCount; }
    public void setCompletedSessionsCount(int completedSessionsCount) { this.completedSessionsCount = completedSessionsCount; }

    public int getCreditBalance() { return creditBalance; }
    public void setCreditBalance(int creditBalance) { this.creditBalance = creditBalance; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public boolean isMentorEligible() {
        return (role == Role.SENIOR || role == Role.ALUMNI || role == Role.STUDENT) && status == Status.ACTIVE;
    }
}