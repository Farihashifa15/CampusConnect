package com.campusconnect.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class MentorAvailability implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int mentorId;
    private String mentorName;
    private String dayOfWeek; // e.g. MONDAY, TUESDAY...
    private Date specificDate;
    private Time startTime;
    private Time endTime;
    private boolean active = true;
    private Timestamp createdAt;

    public MentorAvailability() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMentorId() { return mentorId; }
    public void setMentorId(int mentorId) { this.mentorId = mentorId; }

    public String getMentorName() { return mentorName; }
    public void setMentorName(String mentorName) { this.mentorName = mentorName; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public Date getSpecificDate() { return specificDate; }
    public void setSpecificDate(Date specificDate) { this.specificDate = specificDate; }

    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }

    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}