package com.campusconnect;

import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.SkillCreditTransaction;
import com.campusconnect.model.User;
import com.campusconnect.service.CreditLedgerService;
import com.campusconnect.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SkillCreditWorkflowTest {

    private CreditLedgerService creditService;
    private UserService userService;
    private User testUser;

    @BeforeEach
    public void setUp() throws Exception {
        TestDatabaseHelper.setupTestDatabase();
        creditService = new CreditLedgerService();
        userService = new UserService();
        testUser = userService.registerUser("REG-CR1", "credits@university.edu", "pass123", "Credit Test User", User.Role.STUDENT, "CS", 1);
    }

    @Test
    @DisplayName("TC07: Skill credit earning for qualifying activities & duplicate prevention")
    public void testCreditEarning() throws SQLException {
        int initialBalance = creditService.getBalance(testUser.getId());
        assertEquals(50, initialBalance);

        // 1. Earn credits for resource upload (+5)
        boolean earnedResource = creditService.awardResourceUpload(testUser.getId(), 101, "Java Memory Model Cheatsheet");
        assertTrue(earnedResource);
        assertEquals(initialBalance + 5, creditService.getBalance(testUser.getId()));

        // Duplicate upload reward attempt must be prevented
        boolean dupResource = creditService.awardResourceUpload(testUser.getId(), 101, "Java Memory Model Cheatsheet");
        assertFalse(dupResource);
        assertEquals(initialBalance + 5, creditService.getBalance(testUser.getId()));

        // 2. Earn credits for accepted answer (+15)
        boolean earnedAnswer = creditService.awardAcceptedAnswer(testUser.getId(), 202, "How CAS works in Java");
        assertTrue(earnedAnswer);
        assertEquals(initialBalance + 5 + 15, creditService.getBalance(testUser.getId()));

        // Duplicate answer reward attempt must be prevented
        boolean dupAnswer = creditService.awardAcceptedAnswer(testUser.getId(), 202, "How CAS works in Java");
        assertFalse(dupAnswer);
        assertEquals(initialBalance + 5 + 15, creditService.getBalance(testUser.getId()));
    }

    @Test
    @DisplayName("TC08: Credit redemption, balance consistency, and insufficient balance rejection")
    public void testCreditRedemptionAndInsufficientBalance() throws Exception {
        User poorUser = userService.registerUser("REG-POOR", "poor@university.edu", "pass123", "Poor Student", User.Role.STUDENT, "CS", 1);

        // Deduct 50 credits via admin adjustment to bring balance to 0
        creditService.adminAdjustCredits(1, poorUser.getId(), -50, "Setting balance to zero for test");
        assertEquals(0, creditService.getBalance(poorUser.getId()));

        // Attempting to adjust further below zero must fail
        ValidationException negEx = assertThrows(ValidationException.class, () ->
            creditService.adminAdjustCredits(1, poorUser.getId(), -5, "Illegal negative balance")
        );
        assertTrue(negEx.getMessage().contains("negative balance"));

        // Verify ledger transactions
        List<SkillCreditTransaction> txs = creditService.getLedgerHistory(poorUser.getId(), 10, 0);
        assertNotNull(txs);
        assertTrue(txs.size() >= 2); // Initial grant (50) + Admin adjustment (-50)
    }
}