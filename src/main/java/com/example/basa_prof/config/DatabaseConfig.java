package com.example.basa_prof.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Конфигурация для инициализации базы данных PostgreSQL.
 * Проверяет существование базы и схемы, создаёт при необходимости.
 */
@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    /**
     * Проверяет существование базы данных и создаёт схему при необходимости.
     */
    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            String[] urlParts = datasourceUrl.replace("jdbc:", "").split("//");
            String hostPort = urlParts[1];
            String[] hostPortParts = hostPort.split("/");
            String host = hostPortParts[0].split(":")[0];
            int port = hostPortParts[0].contains(":") 
                ? Integer.parseInt(hostPortParts[0].split(":")[1]) 
                : 5432;

            String dbName = "mybasa";

            // Подключаемся к PostgreSQL и проверяем/создаём базу
            String url = String.format("jdbc:postgresql://%s:%d/postgres", host, port);
            
            try (Connection conn = DriverManager.getConnection(url, datasourceUsername, datasourcePassword)) {
                Statement stmt = conn.createStatement();

                // Проверяем, существует ли база данных
                ResultSet rs = stmt.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'");
                if (!rs.next()) {
                    // База не существует - создаём
                    stmt.execute("CREATE DATABASE " + dbName);
                    log.info("База данных '{}' создана", dbName);
                    
                    // Закрываем и переподключаемся к новой базе
                    stmt.close();
                    conn.close();
                    
                    String newUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
                    try (Connection newConn = DriverManager.getConnection(newUrl, datasourceUsername, datasourcePassword)) {
                        Statement newStmt = newConn.createStatement();
                        newStmt.execute("CREATE SCHEMA IF NOT EXISTS myschema");
                        log.info("Схема 'myschema' создана или уже существует");
                        newStmt.close();
                    }
                } else {
                    // База существует - проверяем схему
                    log.info("База данных '{}' уже существует", dbName);
                    
                    String newUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
                    try (Connection newConn = DriverManager.getConnection(newUrl, datasourceUsername, datasourcePassword)) {
                        Statement newStmt = newConn.createStatement();
                        ResultSet schemaRs = newStmt.executeQuery(
                            "SELECT 1 FROM information_schema.schemata WHERE schema_name = 'myschema'"
                        );
                        if (!schemaRs.next()) {
                            newStmt.execute("CREATE SCHEMA myschema");
                            log.info("Схема 'myschema' создана");
                        } else {
                            log.info("Схема 'myschema' уже существует");
                        }
                        newStmt.close();
                    }
                }
            } catch (Exception e) {
                log.error("Ошибка инициализации базы данных: {}", e.getMessage(), e);
            }
        };
    }
}
