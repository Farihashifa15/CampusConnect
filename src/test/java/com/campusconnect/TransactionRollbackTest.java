package com.campusconnect;

import com.campusconnect.dao.SkillDAO;
import com.campusconnect.exception.ValidationException;
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

public class TransactionRollbackTest {

    private SessionBookingService bookingService;
    private UserService userService;
    private CreditLedgerService creditService;
    private SkillDAO skillDAO;

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

        mentor = userService.registerUser("REG-TB-M", "tb.mentor@university.edu", "pass123", "Mentor TB", User.Role.SENIOR, "CS", 3);
        learner = userService.registerUser("REG-TB-L", "tb.learner@university.edu", "pass123", "Learner TB", User.Role.STUDENT, "CS", 1);
        skill = new Skill(0, "Transaction Testing", "Testing", "Testing ACID transactions");
        skill.setId(skillDAO.createSkill(skill));
    }

    @Test
    @DisplayName("TC11: Database failure handling & atomic transaction rollback on booking conflict")
    public void testAtomicRollbackOnConflict() throws SQLException, ValidationException {
        int initialBalance = creditService.getBalance(learner.getId());
        assertEquals(50, initialBalance);

        Date targetDate = Date.valueOf(LocalDate.now().plusDays(5));
        Time start = Time.valueOf("10:00:00");
        Time end = Time.valueOf("11:00:00");

        // 1. Initial valid booking
        bookingService.requestBooking(learner.getId(), mentor.getId(), skill.getId(), targetDate, start, end, "Initial booking");
        assertEquals(40, creditService.getBalance(learner.getId()));

        // 2. Second booking with exact same slot (will trigger conflict exception)
        assertThrows(ValidationException.class, () ->
            bookingService.requestBooking(learner.getId(), mentor.getId(), skill.getId(), targetDate, start, end, "Duplicate booking")
        );

        // 3. Verify learner balance was NOT deducted for failed second attempt (remains 40)
        assertEquals(40, creditService.getBalance(learner.getId()));
    }
}