package ru.bukhtaev.repository.dictionary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.dictionary.GraphicsCardPowerConnector;

import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Модульные тесты репозитория коннекторов питания видеокарты.
 */
@DataJpaTest
class GraphicsCardPowerConnectorRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий коннекторов питания видеокарты.
     */
    @Autowired
    private IGraphicsCardPowerConnectorRepository underTest;

    private GraphicsCardPowerConnector connector6Plus2Pin;
    private GraphicsCardPowerConnector connector8Pin;

    @BeforeEach
    void setUp() {
        connector6Plus2Pin = GraphicsCardPowerConnector.builder()
                .name("6 + 2 pin")
                .compatibleConnectors(new HashSet<>())
                .build();
        connector8Pin = GraphicsCardPowerConnector.builder()
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
        final var savedConnector6Plus2Pin = underTest.save(connector6Plus2Pin);
        connector8Pin.getCompatibleConnectors().add(savedConnector6Plus2Pin);
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
                .containsExactly(savedConnector6Plus2Pin);
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "6 pin";
        final var savedConnector6Plus2Pin = underTest.save(connector6Plus2Pin);
        connector8Pin.getCompatibleConnectors().add(savedConnector6Plus2Pin);
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
        final var savedConnector6Plus2Pin = underTest.save(connector6Plus2Pin);
        connector8Pin.getCompatibleConnectors().add(savedConnector6Plus2Pin);
        underTest.save(connector8Pin);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByNameAndIdNot(
                connector6Plus2Pin.getName(),
                anotherId
        );

        // then
        assertThat(optConnector).isPresent();
        final var connector = optConnector.get();
        assertThat(connector.getId())
                .isEqualTo(savedConnector6Plus2Pin.getId());
        assertThat(connector.getName())
                .isEqualTo(connector6Plus2Pin.getName());
        assertThat(connector.getCompatibleConnectors())
                .isEmpty();
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final var savedConnector6Plus2Pin = underTest.save(connector6Plus2Pin);
        connector8Pin.getCompatibleConnectors().add(savedConnector6Plus2Pin);
        underTest.save(connector8Pin);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByNameAndIdNot(
                connector6Plus2Pin.getName(),
                savedConnector6Plus2Pin.getId()
        );

        // then
        assertThat(optConnector).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "6 pin";
        final var savedConnector6Plus2Pin = underTest.save(connector6Plus2Pin);
        connector8Pin.getCompatibleConnectors().add(savedConnector6Plus2Pin);
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
        final String anotherName = "6 pin";
        final var savedConnector6Plus2Pin = underTest.save(connector6Plus2Pin);
        connector8Pin.getCompatibleConnectors().add(savedConnector6Plus2Pin);
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
