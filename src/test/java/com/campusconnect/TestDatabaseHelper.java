package com.campusconnect;

import com.campusconnect.config.DatabaseConfig;
import org.h2.tools.RunScript;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

public class TestDatabaseHelper {

    private static boolean initialized = false;

    public static synchronized void setupTestDatabase() {
        if (!initialized) {
            DatabaseConfig.initialize("test-db.properties");
            initialized = true;
        }
        resetSchema();
    }

    public static void resetSchema() {
        try (Connection conn = DatabaseConfig.getConnection();
             InputStream is = TestDatabaseHelper.class.getClassLoader().getResourceAsStream("test-schema.sql")) {
            if (is == null) {
                throw new RuntimeException("Could not find test-schema.sql");
            }
            RunScript.execute(conn, new InputStreamReader(is, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Failed to reset test schema: " + e.getMessage(), e);
        }
    }
}