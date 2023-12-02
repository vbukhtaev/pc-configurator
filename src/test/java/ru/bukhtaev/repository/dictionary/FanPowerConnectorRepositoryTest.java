package ru.bukhtaev.repository.dictionary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.dictionary.FanPowerConnector;

import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Модульные тесты репозитория коннекторов питания вентиляторов.
 */
@DataJpaTest
class FanPowerConnectorRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий коннекторов питания вентиляторов.
     */
    @Autowired
    private IFanPowerConnectorRepository underTest;

    private FanPowerConnector connector2Pin;
    private FanPowerConnector connector3Pin;

    @BeforeEach
    void setUp() {
        connector2Pin = FanPowerConnector.builder()
                .name("2 pin")
                .compatibleConnectors(new HashSet<>())
                .build();
        connector3Pin = FanPowerConnector.builder()
                .name("3 pin")
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
        final var savedConnector2Pin = underTest.save(connector2Pin);
        connector3Pin.getCompatibleConnectors().add(savedConnector2Pin);
        final var savedConnector3Pin = underTest.save(connector3Pin);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByName(connector3Pin.getName());

        // then
        assertThat(optConnector).isPresent();
        final var connector = optConnector.get();
        assertThat(connector.getId())
                .isEqualTo(savedConnector3Pin.getId());
        assertThat(connector.getName())
                .isEqualTo(connector3Pin.getName());
        assertThat(connector.getCompatibleConnectors())
                .hasSize(1);
        assertThat(connector.getCompatibleConnectors())
                .containsExactly(savedConnector2Pin);
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "3 pin (Wow!)";
        final var savedConnector2Pin = underTest.save(connector2Pin);
        connector3Pin.getCompatibleConnectors().add(savedConnector2Pin);
        underTest.save(connector3Pin);
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
        final var savedConnector2Pin = underTest.save(connector2Pin);
        connector3Pin.getCompatibleConnectors().add(savedConnector2Pin);
        underTest.save(connector3Pin);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByNameAndIdNot(
                connector2Pin.getName(),
                anotherId
        );

        // then
        assertThat(optConnector).isPresent();
        final var connector = optConnector.get();
        assertThat(connector.getId())
                .isEqualTo(savedConnector2Pin.getId());
        assertThat(connector.getName())
                .isEqualTo(connector2Pin.getName());
        assertThat(connector.getCompatibleConnectors())
                .isEmpty();
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final var savedConnector2Pin = underTest.save(connector2Pin);
        connector3Pin.getCompatibleConnectors().add(savedConnector2Pin);
        underTest.save(connector3Pin);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByNameAndIdNot(
                connector2Pin.getName(),
                savedConnector2Pin.getId()
        );

        // then
        assertThat(optConnector).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "3 pin (Wow!)";
        final var savedConnector2Pin = underTest.save(connector2Pin);
        connector3Pin.getCompatibleConnectors().add(savedConnector2Pin);
        underTest.save(connector3Pin);
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
        final String anotherName = "3 pin (Wow!)";
        final var savedConnector2Pin = underTest.save(connector2Pin);
        connector3Pin.getCompatibleConnectors().add(savedConnector2Pin);
        final var savedConnector3Pin = underTest.save(connector3Pin);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByNameAndIdNot(
                anotherName,
                savedConnector3Pin.getId()
        );

        // then
        assertThat(optConnector).isNotPresent();
    }
}
