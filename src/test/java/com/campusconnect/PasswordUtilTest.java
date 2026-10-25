package com.campusconnect.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    public void testHashAndCheckPassword() {
        String raw = "password123";
        String hashed = PasswordUtil.hashPassword(raw);
        assertNotNull(hashed);
        assertTrue(PasswordUtil.checkPassword(raw, hashed));
        assertFalse(PasswordUtil.checkPassword("wrongpassword", hashed));
        System.out.println("BCrypt hash for password123: " + hashed);
    }
}
