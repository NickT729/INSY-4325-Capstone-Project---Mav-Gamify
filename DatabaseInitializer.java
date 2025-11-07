package com.mavpal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // Enable foreign keys
        jdbcTemplate.execute("PRAGMA foreign_keys = ON;");

        // Check if tables exist
        try {
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
            // Tables exist, skip initialization
            return;
        } catch (Exception e) {
            // Tables don't exist, initialize schema
        }

        // Read and execute schema.sql
        var resource = new ClassPathResource("schema.sql");
        if (resource.exists()) {
            try (var reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder sql = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sql.append(line).append("\n");
                }
                
                // Execute SQL statements
                String[] statements = sql.toString().split(";");
                for (String statement : statements) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                        try {
                            jdbcTemplate.execute(trimmed);
                        } catch (Exception ex) {
                            // Ignore errors for IF NOT EXISTS statements
                            if (!ex.getMessage().contains("already exists")) {
                                System.err.println("Error executing: " + trimmed);
                                System.err.println(ex.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }
}
