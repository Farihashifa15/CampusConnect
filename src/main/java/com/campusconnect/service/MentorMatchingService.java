package com.campusconnect.service;

import com.campusconnect.dao.MentorAvailabilityDAO;
import com.campusconnect.dao.SkillDAO;
import com.campusconnect.dao.UserDAO;
import com.campusconnect.model.MentorAvailability;
import com.campusconnect.model.MentorMatchResult;
import com.campusconnect.model.User;
import com.campusconnect.model.UserSkill;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Time;
import java.util.*;

public class MentorMatchingService {

    private final UserDAO userDAO;
    private final SkillDAO skillDAO;
    private final MentorAvailabilityDAO availabilityDAO;

    public MentorMatchingService() {
        this(new UserDAO(), new SkillDAO(), new MentorAvailabilityDAO());
    }

    public MentorMatchingService(UserDAO userDAO, SkillDAO skillDAO, MentorAvailabilityDAO availabilityDAO) {
        this.userDAO = userDAO;
        this.skillDAO = skillDAO;
        this.availabilityDAO = availabilityDAO;
    }

    /**
     * Exact weighted scoring formula from the research paper:
     * Score = 0.5 * SkillMatch + 0.3 * NormalizedRating + 0.2 * Availability
     */
    public double calculateScore(double skillMatch, double normalizedRating, double availability) {
        return (0.5 * skillMatch) + (0.3 * normalizedRating) + (0.2 * availability);
    }

    public List<MentorMatchResult> rankMentors(List<MentorMatchResult> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return Collections.emptyList();
        }
        List<MentorMatchResult> sorted = new ArrayList<>(candidates);
        Collections.sort(sorted);
        return sorted;
    }

    public List<MentorMatchResult> findMatches(Integer requestedSkillId, String dayOfWeek, Time startTime, Time endTime) throws SQLException {
        List<User> allUsers = userDAO.getAllUsers(null, null, null);
        List<MentorMatchResult> results = new ArrayList<>();

        for (User u : allUsers) {
            if (u.getStatus() != User.Status.ACTIVE) continue;

            List<UserSkill> teachingSkills = skillDAO.getUserSkills(u.getId(), UserSkill.SkillType.TEACH);
            if (teachingSkills.isEmpty()) continue;

            // 1. SkillMatch (1.0 if mentor offers the requested skill, 0.0 otherwise)
            double skillMatch = 0.0;
            if (requestedSkillId != null && requestedSkillId > 0) {
                for (UserSkill us : teachingSkills) {
                    if (us.getSkillId() == requestedSkillId) {
                        skillMatch = 1.0;
                        break;
                    }
                }
            } else {
                skillMatch = 1.0;
            }

            // 2. NormalizedRating (Rating / 5.0)
            BigDecimal rating = u.getMentorRating() != null ? u.getMentorRating() : BigDecimal.ZERO;
            double normalizedRating = Math.min(Math.max(rating.doubleValue() / 5.0, 0.0), 1.0);

            // 3. Availability (1.0 if has slot matching requested time, 0.0 otherwise)
            List<MentorAvailability> slots = availabilityDAO.getAvailabilityByMentor(u.getId());
            double availability = 0.0;
            if (dayOfWeek != null && !dayOfWeek.trim().isEmpty() && startTime != null && endTime != null) {
                for (MentorAvailability slot : slots) {
                    if (slot.getDayOfWeek().equalsIgnoreCase(dayOfWeek.trim())) {
                        if (!slot.getStartTime().after(startTime) && !slot.getEndTime().before(endTime)) {
                            availability = 1.0;
                            break;
                        }
                    }
                }
            } else {
                availability = slots.isEmpty() ? 0.0 : 1.0;
            }

            MentorMatchResult match = new MentorMatchResult(u, skillMatch, normalizedRating, availability);
            match.setOfferedSkills(teachingSkills);
            match.setAvailableSlots(slots);
            results.add(match);
        }

        return rankMentors(results);
    }
}