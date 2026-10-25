package com.campusconnect.config;

import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private static final Properties props = new Properties();

    static {
        try (InputStream is = AppConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (Exception e) {
            System.err.println("Warning: Unable to load db.properties: " + e.getMessage());
        }
    }

    public static String getUniversityEmailDomain() {
        String env = System.getenv("APP_UNIVERSITY_DOMAIN");
        if (env != null && !env.trim().isEmpty()) {
            return env.trim();
        }
        return props.getProperty("app.university.domain", "@university.edu");
    }

    public static int getSessionBookingCost() {
        String env = System.getenv("APP_CREDIT_SESSION_COST");
        if (env != null) {
            try { return Integer.parseInt(env); } catch (NumberFormatException ignored) {}
        }
        return Integer.parseInt(props.getProperty("app.credit.session.cost", "10"));
    }

    public static int getSessionCompletionReward() {
        String env = System.getenv("APP_CREDIT_SESSION_REWARD");
        if (env != null) {
            try { return Integer.parseInt(env); } catch (NumberFormatException ignored) {}
        }
        return Integer.parseInt(props.getProperty("app.credit.session.reward", "10"));
    }

    public static int getResourceUploadReward() {
        String env = System.getenv("APP_CREDIT_RESOURCE_REWARD");
        if (env != null) {
            try { return Integer.parseInt(env); } catch (NumberFormatException ignored) {}
        }
        return Integer.parseInt(props.getProperty("app.credit.resource.reward", "5"));
    }

    public static int getAcceptedAnswerReward() {
        String env = System.getenv("APP_CREDIT_ANSWER_REWARD");
        if (env != null) {
            try { return Integer.parseInt(env); } catch (NumberFormatException ignored) {}
        }
        return Integer.parseInt(props.getProperty("app.credit.answer.reward", "15"));
    }

    public static String getUploadDir() {
        String env = System.getenv("APP_UPLOAD_DIR");
        if (env != null && !env.trim().isEmpty()) {
            return env.trim();
        }
        return props.getProperty("app.upload.dir", "uploads");
    }
}