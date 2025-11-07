package com.mavpal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }

    private DatabasePopulator databasePopulator() {
        return new ResourceDatabasePopulator() {
            @Override
            public void populate(Connection connection) {
                try (Statement stmt = connection.createStatement()) {
                    // Read schema.sql from resources
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
                                    stmt.execute(trimmed);
                                }
                            }
                        }
                    } else {
                        // Fallback: execute schema inline
                        executeSchemaInline(stmt);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to initialize database", e);
                }
            }
        };
    }

    private void executeSchemaInline(Statement stmt) {
        // This is a fallback if schema.sql is not found
        // The actual schema should be in schema.sql file
        try {
            stmt.execute("PRAGMA foreign_keys = ON;");
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute schema", e);
        }
    }
}

