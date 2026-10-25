package com.campusconnect.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {

    private static HikariDataSource dataSource;

    private DatabaseConfig() {
    }

    public static synchronized void initialize(String propertiesFile) {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }

        Properties props = new Properties();
        try (InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream(propertiesFile)) {
            if (is == null) {
                throw new RuntimeException("Could not find database properties file: " + propertiesFile);
            }
            props.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read " + propertiesFile, e);
        }

        HikariConfig config = new HikariConfig();
        
        // Allow environment variable overrides for security
        String url = System.getenv("DB_URL");
        if (url == null || url.trim().isEmpty()) {
            url = props.getProperty("db.url");
        }
        
        String username = System.getenv("DB_USERNAME");
        if (username == null || username.trim().isEmpty()) {
            username = props.getProperty("db.username");
        }
        
        String password = System.getenv("DB_PASSWORD");
        if (password == null) {
            password = props.getProperty("db.password", "");
        }

        config.setDriverClassName(props.getProperty("db.driver"));
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maximumPoolSize", "10")));
        config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minimumIdle", "2")));
        config.setIdleTimeout(Long.parseLong(props.getProperty("db.pool.idleTimeout", "30000")));
        config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.connectionTimeout", "10000")));

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            synchronized (DatabaseConfig.class) {
                if (dataSource == null || dataSource.isClosed()) {
                    initialize("db.properties");
                }
            }
        }
        return dataSource.getConnection();
    }

    public static synchronized void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            dataSource = null;
        }
    }
}