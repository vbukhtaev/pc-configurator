package ru.bukhtaev.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.dictionary.CpuPowerConnector;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Модульные тесты репозитория коннекторов питания процессора.
 */
@DataJpaTest
class CpuPowerConnectorRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий коннекторов питания процессора.
     */
    @Autowired
    private ICpuPowerConnectorRepository underTest;

    private CpuPowerConnector connector4Pin;
    private CpuPowerConnector connector8Pin;

    @BeforeEach
    void setUp() {
        connector4Pin = CpuPowerConnector.builder()
                .name("4 pin")
                .build();
        connector8Pin = CpuPowerConnector.builder()
                .name("8 pin")
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByName_withExistentName_shouldReturnFoundEntity() {
        // given
        final var saved = underTest.save(connector8Pin);
        underTest.save(connector4Pin);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByName(connector8Pin.getName());

        // then
        assertThat(optConnector).isPresent();
        assertThat(optConnector.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optConnector.get().getName())
                .isEqualTo(connector8Pin.getName());
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "8 + 4 pin";
        underTest.save(connector8Pin);
        underTest.save(connector4Pin);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByName(anotherName);

        // then
        assertThat(optConnector).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndNonExistentId_shouldReturnFoundEntity() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final var saved = underTest.save(connector4Pin);
        underTest.save(connector8Pin);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByNameAndIdNot(
                connector4Pin.getName(),
                anotherId
        );

        // then
        assertThat(optConnector).isPresent();
        assertThat(optConnector.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optConnector.get().getName())
                .isEqualTo(connector4Pin.getName());
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final var saved = underTest.save(connector4Pin);
        underTest.save(connector8Pin);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByNameAndIdNot(
                connector4Pin.getName(),
                saved.getId()
        );

        // then
        assertThat(optConnector).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "8 + 4 pin";
        underTest.save(connector8Pin);
        underTest.save(connector4Pin);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByNameAndIdNot(
                anotherName,
                anotherId
        );

        // then
        assertThat(optConnector).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "8 + 4 pin";
        final var saved = underTest.save(connector8Pin);
        underTest.save(connector4Pin);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByNameAndIdNot(
                anotherName,
                saved.getId()
        );

        // then
        assertThat(optConnector).isNotPresent();
    }
}
