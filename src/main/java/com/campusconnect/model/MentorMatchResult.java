package com.campusconnect.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MentorMatchResult implements Serializable, Comparable<MentorMatchResult> {
    private static final long serialVersionUID = 1L;

    private User mentor;
    private List<UserSkill> offeredSkills = new ArrayList<>();
    private List<MentorAvailability> availableSlots = new ArrayList<>();
    private double skillMatch;      // 1.0 or 0.0
    private double normalizedRating; // rating / 5.0
    private double availability;    // 1.0 or 0.0
    private double totalScore;      // 0.5 * skillMatch + 0.3 * normalizedRating + 0.2 * availability

    public MentorMatchResult() {}

    public MentorMatchResult(User mentor, double skillMatch, double normalizedRating, double availability) {
        this.mentor = mentor;
        this.skillMatch = skillMatch;
        this.normalizedRating = normalizedRating;
        this.availability = availability;
        this.totalScore = (0.5 * skillMatch) + (0.3 * normalizedRating) + (0.2 * availability);
    }

    public User getMentor() { return mentor; }
    public void setMentor(User mentor) { this.mentor = mentor; }

    public List<UserSkill> getOfferedSkills() { return offeredSkills; }
    public void setOfferedSkills(List<UserSkill> offeredSkills) { this.offeredSkills = offeredSkills; }

    public List<MentorAvailability> getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(List<MentorAvailability> availableSlots) { this.availableSlots = availableSlots; }

    public double getSkillMatch() { return skillMatch; }
    public void setSkillMatch(double skillMatch) { this.skillMatch = skillMatch; }

    public double getNormalizedRating() { return normalizedRating; }
    public void setNormalizedRating(double normalizedRating) { this.normalizedRating = normalizedRating; }

    public double getAvailability() { return availability; }
    public void setAvailability(double availability) { this.availability = availability; }

    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }

    /**
     * Sort in descending order of totalScore.
     * Tie-breaker 1: Higher mentor rating.
     * Tie-breaker 2: Completed sessions count.
     * Tie-breaker 3: Mentor Full Name / ID (deterministic).
     */
    @Override
    public int compareTo(MentorMatchResult other) {
        if (other == null) return -1;
        // 1. Total score descending
        int scoreCmp = Double.compare(other.totalScore, this.totalScore);
        if (scoreCmp != 0) return scoreCmp;

        // 2. Mentor rating descending
        BigDecimal myRating = this.mentor != null ? this.mentor.getMentorRating() : BigDecimal.ZERO;
        BigDecimal otherRating = other.mentor != null ? other.mentor.getMentorRating() : BigDecimal.ZERO;
        int ratingCmp = otherRating.compareTo(myRating);
        if (ratingCmp != 0) return ratingCmp;

        // 3. Completed sessions count descending
        int myCompleted = this.mentor != null ? this.mentor.getCompletedSessionsCount() : 0;
        int otherCompleted = other.mentor != null ? other.mentor.getCompletedSessionsCount() : 0;
        int completedCmp = Integer.compare(otherCompleted, myCompleted);
        if (completedCmp != 0) return completedCmp;

        // 4. Deterministic tie-breaker: Name ascending
        String myName = this.mentor != null && this.mentor.getFullName() != null ? this.mentor.getFullName() : "";
        String otherName = other.mentor != null && other.mentor.getFullName() != null ? other.mentor.getFullName() : "";
        int nameCmp = myName.compareToIgnoreCase(otherName);
        if (nameCmp != 0) return nameCmp;

        // 5. Final fallback: ID ascending
        int myId = this.mentor != null ? this.mentor.getId() : 0;
        int otherId = other.mentor != null ? other.mentor.getId() : 0;
        return Integer.compare(myId, otherId);
    }
}