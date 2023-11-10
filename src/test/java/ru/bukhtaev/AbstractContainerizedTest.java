package ru.bukhtaev;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Абстрактный тест с использованием Testcontainers.
 */
public abstract class AbstractContainerizedTest {

    /**
     * Docket-контейнер с базой данных PostgreSQL.
     */
    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    @BeforeAll
    static void startContainers() {
        postgres.start();
    }

    @DynamicPropertySource
    public static void overrideProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }
}
