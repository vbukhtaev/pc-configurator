package ru.bukhtaev.repository.dictionary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.dictionary.CpuPowerConnector;

import java.util.HashSet;
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

    private CpuPowerConnector connector4Plus4Pin;
    private CpuPowerConnector connector8Pin;

    @BeforeEach
    void setUp() {
        connector4Plus4Pin = CpuPowerConnector.builder()
                .name("4 + 4 pin")
                .compatibleConnectors(new HashSet<>())
                .build();
        connector8Pin = CpuPowerConnector.builder()
                .name("8 pin")
                .compatibleConnectors(new HashSet<>())
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByName_withExistentName_shouldReturnFoundEntity() {
        // given
        final var savedConnector4Plus4Pin = underTest.save(connector4Plus4Pin);
        connector8Pin.getCompatibleConnectors().add(savedConnector4Plus4Pin);
        final var savedConnector8Pin = underTest.save(connector8Pin);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByName(connector8Pin.getName());

        // then
        assertThat(optConnector).isPresent();
        final var connector = optConnector.get();
        assertThat(connector.getId())
                .isEqualTo(savedConnector8Pin.getId());
        assertThat(connector.getName())
                .isEqualTo(connector8Pin.getName());
        assertThat(connector.getCompatibleConnectors())
                .hasSize(1);
        assertThat(connector.getCompatibleConnectors())
                .containsExactly(savedConnector4Plus4Pin);
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "8 pin (Wow!)";
        final var savedConnector4Plus4Pin = underTest.save(connector4Plus4Pin);
        connector8Pin.getCompatibleConnectors().add(savedConnector4Plus4Pin);
        underTest.save(connector8Pin);
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
        final var savedConnector4Plus4Pin = underTest.save(connector4Plus4Pin);
        connector8Pin.getCompatibleConnectors().add(savedConnector4Plus4Pin);
        underTest.save(connector8Pin);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByNameAndIdNot(
                connector4Plus4Pin.getName(),
                anotherId
        );

        // then
        assertThat(optConnector).isPresent();
        final var connector = optConnector.get();
        assertThat(connector.getId())
                .isEqualTo(savedConnector4Plus4Pin.getId());
        assertThat(connector.getName())
                .isEqualTo(connector4Plus4Pin.getName());
        assertThat(connector.getCompatibleConnectors())
                .isEmpty();
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final var savedConnector4Plus4Pin = underTest.save(connector4Plus4Pin);
        connector8Pin.getCompatibleConnectors().add(savedConnector4Plus4Pin);
        underTest.save(connector8Pin);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByNameAndIdNot(
                connector4Plus4Pin.getName(),
                savedConnector4Plus4Pin.getId()
        );

        // then
        assertThat(optConnector).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "8 pin (Wow!)";
        final var savedConnector4Plus4Pin = underTest.save(connector4Plus4Pin);
        connector8Pin.getCompatibleConnectors().add(savedConnector4Plus4Pin);
        underTest.save(connector8Pin);
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
        final String anotherName = "8 pin (Wow!)";
        final var savedConnector4Plus4Pin = underTest.save(connector4Plus4Pin);
        connector8Pin.getCompatibleConnectors().add(savedConnector4Plus4Pin);
        final var savedConnector8Pin = underTest.save(connector8Pin);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByNameAndIdNot(
                anotherName,
                savedConnector8Pin.getId()
        );

        // then
        assertThat(optConnector).isNotPresent();
    }
}
