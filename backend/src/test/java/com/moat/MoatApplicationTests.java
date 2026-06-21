package com.moat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Smoke test: kontekst Spring ładuje się, a migracje Liquibase wykonują się
 * na realnym PostgreSQL (Testcontainers). Hibernate w trybie validate sprawdza
 * zgodność encji ze schematem — test wykrywa rozjazd migracja ↔ encje.
 *
 * Wymaga dostępnego Dockera podczas uruchamiania testów.
 */
@SpringBootTest
@Testcontainers
class MoatApplicationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Test
    void contextLoads() {
    }
}
