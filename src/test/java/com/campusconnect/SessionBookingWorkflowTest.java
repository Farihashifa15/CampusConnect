package com.campusconnect;

import com.campusconnect.dao.SkillDAO;
import com.campusconnect.dao.UserDAO;
import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.Session;
import com.campusconnect.model.Skill;
import com.campusconnect.model.User;
import com.campusconnect.service.CreditLedgerService;
import com.campusconnect.service.SessionBookingService;
import com.campusconnect.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class SessionBookingWorkflowTest {

    private SessionBookingService bookingService;
    private UserService userService;
    private CreditLedgerService creditService;
    private SkillDAO skillDAO;
    private UserDAO userDAO;

    private User mentor;
    private User learner;
    private Skill skill;

    @BeforeEach
    public void setUp() throws Exception {
        TestDatabaseHelper.setupTestDatabase();
        bookingService = new SessionBookingService();
        userService = new UserService();
        creditService = new CreditLedgerService();
        skillDAO = new SkillDAO();
        userDAO = new UserDAO();

        mentor = userService.registerUser("REG-M1", "mentor1@university.edu", "pass123", "Mentor Bob", User.Role.SENIOR, "CS", 3);
        learner = userService.registerUser("REG-L1", "learner1@university.edu", "pass123", "Learner Alice", User.Role.STUDENT, "IT", 1);

        skill = new Skill(0, "Java Testing", "Testing", "Unit testing with JUnit");
        int skillId = skillDAO.createSkill(skill);
        skill.setId(skillId);
    }

    @Test
    @DisplayName("TC05: Successful session booking with credit escrow")
    public void testSuccessfulBooking() throws SQLException, ValidationException {
        int initialLearnerCredits = creditService.getBalance(learner.getId());
        assertEquals(50, initialLearnerCredits);

        Date futureDate = Date.valueOf(LocalDate.now().plusDays(2));
        Time start = Time.valueOf("10:00:00");
        Time end = Time.valueOf("11:00:00");

        Session session = bookingService.requestBooking(
            learner.getId(),
            mentor.getId(),
            skill.getId(),
            futureDate,
            start,
            end,
            "Looking forward to the Java session!"
        );

        assertNotNull(session);
        assertEquals(Session.Status.PENDING, session.getStatus());
        assertEquals(10, session.getCreditCost());

        // Verify credits debited
        int updatedBalance = creditService.getBalance(learner.getId());
        assertEquals(initialLearnerCredits - 10, updatedBalance);
    }

    @Test
    @DisplayName("TC06: Booking conflict rejection (start1 < end2 AND start2 < end1)")
    public void testBookingConflictRejection() throws SQLException, ValidationException {
        Date targetDate = Date.valueOf(LocalDate.now().plusDays(3));

        // First booking: 14:00 to 15:00
        Session session1 = bookingService.requestBooking(
            learner.getId(),
            mentor.getId(),
            skill.getId(),
            targetDate,
            Time.valueOf("14:00:00"),
            Time.valueOf("15:00:00"),
            "First booking"
        );
        assertNotNull(session1);

        // Attempt second overlapping booking: 14:30 to 15:30 (overlaps with 14:00-15:00)
        User learner2 = userService.registerUser("REG-L2", "learner2@university.edu", "pass123", "Learner Charlie", User.Role.STUDENT, "CS", 2);
        ValidationException conflictEx = assertThrows(ValidationException.class, () ->
            bookingService.requestBooking(
                learner2.getId(),
                mentor.getId(),
                skill.getId(),
                targetDate,
                Time.valueOf("14:30:00"),
                Time.valueOf("15:30:00"),
                "Overlapping request"
            )
        );
        assertTrue(conflictEx.getMessage().contains("conflicting booking"));

        // Adjacent booking (15:00 to 16:00) MUST succeed because end1 == start2
        Session adjacentSession = bookingService.requestBooking(
            learner2.getId(),
            mentor.getId(),
            skill.getId(),
            targetDate,
            Time.valueOf("15:00:00"),
            Time.valueOf("16:00:00"),
            "Adjacent non-conflicting booking"
        );
        assertNotNull(adjacentSession);
    }

    @Test
    @DisplayName("Edge Case: Learner cannot book self & cannot book past dates")
    public void testInvalidBookingParameters() {
        Date futureDate = Date.valueOf(LocalDate.now().plusDays(1));

        // Learner cannot book self
        ValidationException selfEx = assertThrows(ValidationException.class, () ->
            bookingService.requestBooking(mentor.getId(), mentor.getId(), skill.getId(), futureDate, Time.valueOf("10:00:00"), Time.valueOf("11:00:00"), "Self")
        );
        assertTrue(selfEx.getMessage().contains("cannot book a mentorship session with yourself"));

        // Session cannot be in the past
        Date pastDate = Date.valueOf(LocalDate.now().minusDays(1));
        ValidationException pastEx = assertThrows(ValidationException.class, () ->
            bookingService.requestBooking(learner.getId(), mentor.getId(), skill.getId(), pastDate, Time.valueOf("10:00:00"), Time.valueOf("11:00:00"), "Past")
        );
        assertTrue(pastEx.getMessage().contains("past"));
    }

    @Test
    @DisplayName("Lifecycle: Confirm, complete, and refund workflows")
    public void testSessionLifecycleAndRefund() throws Exception {
        Date futureDate = Date.valueOf(LocalDate.now().plusDays(2));
        Session s = bookingService.requestBooking(learner.getId(), mentor.getId(), skill.getId(), futureDate, Time.valueOf("09:00:00"), Time.valueOf("10:00:00"), "Notes");

        // Mentor confirms
        boolean confirmed = bookingService.confirmBooking(mentor.getId(), s.getId());
        assertTrue(confirmed);

        // Mentor completes session -> awards credits to mentor
        int mentorBalanceBefore = creditService.getBalance(mentor.getId());
        boolean completed = bookingService.completeSession(mentor.getId(), s.getId());
        assertTrue(completed);

        int mentorBalanceAfter = creditService.getBalance(mentor.getId());
        assertEquals(mentorBalanceBefore + 10, mentorBalanceAfter);

        // New session that gets cancelled -> refund learner
        Session s2 = bookingService.requestBooking(learner.getId(), mentor.getId(), skill.getId(), futureDate, Time.valueOf("11:00:00"), Time.valueOf("12:00:00"), "Cancel test");
        int learnerBalAfterBooking = creditService.getBalance(learner.getId());

        bookingService.cancelBooking(learner.getId(), s2.getId(), "Schedule conflict");
        int learnerBalAfterCancel = creditService.getBalance(learner.getId());
        assertEquals(learnerBalAfterBooking + 10, learnerBalAfterCancel);
    }
}