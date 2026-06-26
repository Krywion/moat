package com.moat.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "app.market.enabled=false")
public abstract class AbstractIntegrationTest {

    // Singleton container: startowany raz i NIGDY niezatrzymywany (sprząta Ryuk na końcu JVM).
    // Bez @Testcontainers/@Container — inaczej JUnit zatrzymuje kontener po każdej klasie i startuje
    // nowy (nowy port), a współdzielony, cache'owany kontekst Springa wskazuje na martwy port
    // → "Could not open JPA EntityManager". @ServiceConnection działa na statycznym polu bez @Container.
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    static {
        postgres.start();
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}
