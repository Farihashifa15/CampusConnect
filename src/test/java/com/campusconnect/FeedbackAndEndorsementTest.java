package com.campusconnect;

import com.campusconnect.dao.FeedbackAndEndorsementDAO;
import com.campusconnect.dao.SkillDAO;
import com.campusconnect.dao.UserDAO;
import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.Endorsement;
import com.campusconnect.model.Session;
import com.campusconnect.model.Skill;
import com.campusconnect.model.User;
import com.campusconnect.service.FeedbackService;
import com.campusconnect.service.SessionBookingService;
import com.campusconnect.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FeedbackAndEndorsementTest {

    private FeedbackService feedbackService;
    private SessionBookingService bookingService;
    private UserService userService;
    private UserDAO userDAO;
    private SkillDAO skillDAO;
    private FeedbackAndEndorsementDAO feedbackDAO;

    private User mentor;
    private User learner;
    private Skill skill;

    @BeforeEach
    public void setUp() throws Exception {
        TestDatabaseHelper.setupTestDatabase();
        feedbackService = new FeedbackService();
        bookingService = new SessionBookingService();
        userService = new UserService();
        userDAO = new UserDAO();
        skillDAO = new SkillDAO();
        feedbackDAO = new FeedbackAndEndorsementDAO();

        mentor = userService.registerUser("REG-FB-M", "fb.mentor@university.edu", "pass123", "Mentor Feedback", User.Role.SENIOR, "CS", 4);
        learner = userService.registerUser("REG-FB-L", "fb.learner@university.edu", "pass123", "Learner Feedback", User.Role.STUDENT, "CS", 2);

        skill = new Skill(0, "Algorithms Mastery", "CS Core", "Advanced graph algorithms");
        skill.setId(skillDAO.createSkill(skill));
    }

    @Test
    @DisplayName("TC12: Feedback and endorsements require COMPLETED session, rating update, duplicate prevention")
    public void testFeedbackAndEndorsementRules() throws Exception {
        Date sessionDate = Date.valueOf(LocalDate.now().plusDays(1));
        Session s = bookingService.requestBooking(learner.getId(), mentor.getId(), skill.getId(), sessionDate, Time.valueOf("10:00:00"), Time.valueOf("11:00:00"), "Notes");

        // 1. Attempting feedback on PENDING session must fail
        ValidationException pendingEx = assertThrows(ValidationException.class, () ->
            feedbackService.submitFeedback(learner.getId(), s.getId(), 5, "Great session", true, "Highly recommended")
        );
        assertTrue(pendingEx.getMessage().contains("COMPLETED sessions"));

        // Confirm and complete the session
        bookingService.confirmBooking(mentor.getId(), s.getId());
        bookingService.completeSession(mentor.getId(), s.getId());

        // 2. Submit valid feedback with 5-star rating and skill endorsement
        boolean submitted = feedbackService.submitFeedback(learner.getId(), s.getId(), 5, "Outstanding mentorship!", true, "Expert in graph algorithms");
        assertTrue(submitted);

        // 3. Verify mentor's rating is updated
        User updatedMentor = userDAO.getUserById(mentor.getId());
        assertEquals(new BigDecimal("5.00"), updatedMentor.getMentorRating());

        // 4. Verify verified endorsement exists
        List<Endorsement> endorsements = feedbackDAO.getEndorsementsForMentor(mentor.getId());
        assertEquals(1, endorsements.size());
        assertEquals("Expert in graph algorithms", endorsements.get(0).getComment());

        // 5. Duplicate feedback attempt for the same session must fail
        ValidationException dupEx = assertThrows(ValidationException.class, () ->
            feedbackService.submitFeedback(learner.getId(), s.getId(), 4, "Second feedback attempt", false, null)
        );
        assertTrue(dupEx.getMessage().contains("already been submitted"));
    }
}