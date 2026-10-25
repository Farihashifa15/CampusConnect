package com.campusconnect;

import com.campusconnect.model.MentorMatchResult;
import com.campusconnect.model.User;
import com.campusconnect.service.MentorMatchingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MentorRankingAlgorithmTest {

    private MentorMatchingService matchingService;

    @BeforeEach
    public void setUp() {
        matchingService = new MentorMatchingService();
    }

    @Test
    @DisplayName("TC04.1: Weighted scoring formula calculation")
    public void testScoreCalculation() {
        // Full match, max rating (5/5 = 1.0), available:
        // 0.5 * 1.0 + 0.3 * 1.0 + 0.2 * 1.0 = 1.0
        double perfectScore = matchingService.calculateScore(1.0, 1.0, 1.0);
        assertEquals(1.0, perfectScore, 0.001);

        // Skill match (1.0), rating 4.0 (0.8), unavailable (0.0):
        // 0.5 * 1.0 + 0.3 * 0.8 + 0.2 * 0.0 = 0.5 + 0.24 = 0.74
        double partialScore = matchingService.calculateScore(1.0, 0.8, 0.0);
        assertEquals(0.74, partialScore, 0.001);

        // No skill match (0.0), rating 5.0 (1.0), available (1.0):
        // 0.5 * 0.0 + 0.3 * 1.0 + 0.2 * 1.0 = 0.50
        double noSkillScore = matchingService.calculateScore(0.0, 1.0, 1.0);
        assertEquals(0.50, noSkillScore, 0.001);
    }

    @Test
    @DisplayName("TC04.2: Mentor ranking in descending order of weighted score")
    public void testRankingOrder() {
        User u1 = new User(); u1.setId(1); u1.setFullName("Mentor Alpha"); u1.setMentorRating(new BigDecimal("4.00"));
        User u2 = new User(); u2.setId(2); u2.setFullName("Mentor Beta"); u2.setMentorRating(new BigDecimal("5.00"));
        User u3 = new User(); u3.setId(3); u3.setFullName("Mentor Gamma"); u3.setMentorRating(new BigDecimal("3.00"));

        MentorMatchResult m1 = new MentorMatchResult(u1, 1.0, 0.8, 1.0); // 0.5 + 0.24 + 0.20 = 0.94
        MentorMatchResult m2 = new MentorMatchResult(u2, 0.0, 1.0, 1.0); // 0.0 + 0.30 + 0.20 = 0.50
        MentorMatchResult m3 = new MentorMatchResult(u3, 1.0, 0.6, 0.0); // 0.5 + 0.18 + 0.00 = 0.68

        List<MentorMatchResult> list = new ArrayList<>(List.of(m2, m1, m3));
        List<MentorMatchResult> ranked = matchingService.rankMentors(list);

        assertEquals("Mentor Alpha", ranked.get(0).getMentor().getFullName()); // 0.94
        assertEquals("Mentor Gamma", ranked.get(1).getMentor().getFullName()); // 0.68
        assertEquals("Mentor Beta", ranked.get(2).getMentor().getFullName());  // 0.50
    }

    @Test
    @DisplayName("TC04.3: Tie-breaking rule (higher rating first, then completed sessions, then name)")
    public void testTieBreakingRules() {
        // Two mentors with the same total score
        User uA = new User(); uA.setId(10); uA.setFullName("Zach"); uA.setMentorRating(new BigDecimal("4.50")); uA.setCompletedSessionsCount(5);
        User uB = new User(); uB.setId(20); uB.setFullName("Adam"); uB.setMentorRating(new BigDecimal("4.00")); uB.setCompletedSessionsCount(15);

        // Scores artificially made equal
        MentorMatchResult mA = new MentorMatchResult(uA, 0.8, 0.9, 0.5); mA.setTotalScore(0.70);
        MentorMatchResult mB = new MentorMatchResult(uB, 0.8, 0.8, 0.5); mB.setTotalScore(0.70);

        List<MentorMatchResult> list = new ArrayList<>(List.of(mB, mA));
        List<MentorMatchResult> ranked = matchingService.rankMentors(list);

        // mA has higher rating (4.50 vs 4.00), so mA must be first
        assertEquals("Zach", ranked.get(0).getMentor().getFullName());

        // Same score and same rating, higher completed sessions breaks tie
        User uC = new User(); uC.setId(30); uC.setFullName("Carol"); uC.setMentorRating(new BigDecimal("4.50")); uC.setCompletedSessionsCount(10);
        MentorMatchResult mC = new MentorMatchResult(uC, 0.8, 0.9, 0.5); mC.setTotalScore(0.70);

        List<MentorMatchResult> tieRatingList = new ArrayList<>(List.of(mA, mC));
        List<MentorMatchResult> rankedTie = matchingService.rankMentors(tieRatingList);
        assertEquals("Carol", rankedTie.get(0).getMentor().getFullName()); // 10 completed vs 5 completed
    }

    @Test
    @DisplayName("TC04.4: Empty candidate lists handled gracefully")
    public void testEmptyCandidateList() {
        List<MentorMatchResult> ranked = matchingService.rankMentors(Collections.emptyList());
        assertNotNull(ranked);
        assertTrue(ranked.isEmpty());

        List<MentorMatchResult> nullRanked = matchingService.rankMentors(null);
        assertNotNull(nullRanked);
        assertTrue(nullRanked.isEmpty());
    }
}