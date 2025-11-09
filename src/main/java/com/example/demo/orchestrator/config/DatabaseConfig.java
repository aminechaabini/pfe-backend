package com.example.demo.orchestrator.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Database configuration class.
 * Ensures that the database directory exists and provides utility methods
 * for database file management.
 */
@Configuration
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    /**
     * Initialize database directory on application startup.
     * Extracts the file path from the JDBC URL and creates the directory if it doesn't exist.
     */
    @PostConstruct
    public void initializeDatabaseDirectory() {
        try {
            String filePath = extractFilePathFromJdbcUrl(datasourceUrl);
            if (filePath != null) {
                Path dbPath = Paths.get(filePath).getParent();
                if (dbPath != null && !Files.exists(dbPath)) {
                    Files.createDirectories(dbPath);
                    logger.info("Created database directory: {}", dbPath.toAbsolutePath());
                } else {
                    logger.info("Database directory already exists: {}",
                            dbPath != null ? dbPath.toAbsolutePath() : "root");
                }
            }
        } catch (IOException e) {
            logger.error("Failed to create database directory", e);
            throw new RuntimeException("Failed to initialize database directory", e);
        }
    }

    /**
     * Extracts the file path from a JDBC URL.
     * For example: jdbc:h2:file:./data/testorchestrator -> ./data/testorchestrator
     *
     * @param jdbcUrl the JDBC URL
     * @return the file path, or null if not a file-based database
     */
    private String extractFilePathFromJdbcUrl(String jdbcUrl) {
        if (jdbcUrl == null || !jdbcUrl.contains(":file:")) {
            return null;
        }

        // Extract the part after ":file:"
        int fileIndex = jdbcUrl.indexOf(":file:");
        String pathPart = jdbcUrl.substring(fileIndex + 6);

        // Remove any connection parameters (after semicolon)
        int paramIndex = pathPart.indexOf(';');
        if (paramIndex != -1) {
            pathPart = pathPart.substring(0, paramIndex);
        }

        return pathPart;
    }

    /**
     * Gets the platform-specific database path for a desktop application.
     *
     * @return the recommended database path for the current OS
     */
    public static String getPlatformSpecificDatabasePath() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            String localAppData = System.getenv("LOCALAPPDATA");
            return (localAppData != null ? localAppData : userHome) + "/TestOrchestrator/data";
        } else if (os.contains("mac")) {
            return userHome + "/Library/Application Support/TestOrchestrator/data";
        } else {
            // Linux and others
            return userHome + "/.local/share/TestOrchestrator/data";
        }
    }
}
