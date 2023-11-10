package ru.bukhtaev.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.MainPowerConnector;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Модульные тесты репозитория основных коннекторов питания.
 */
@DataJpaTest
class MainPowerConnectorRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий основных коннекторов питания.
     */
    @Autowired
    private IMainPowerConnectorRepository underTest;

    private MainPowerConnector connector20Pin;
    private MainPowerConnector connector24Pin;

    @BeforeEach
    void setUp() {
        connector20Pin = MainPowerConnector.builder()
                .name("20 pin")
                .build();
        connector24Pin = MainPowerConnector.builder()
                .name("24 pin")
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByName_withExistentName_shouldReturnFoundEntity() {
        // given
        final var saved = underTest.save(connector24Pin);
        underTest.save(connector20Pin);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByName(connector24Pin.getName());

        // then
        assertThat(optConnector).isPresent();
        assertThat(optConnector.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optConnector.get().getName())
                .isEqualTo(connector24Pin.getName());
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "20 + 4 pin";
        underTest.save(connector24Pin);
        underTest.save(connector20Pin);
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
        final var saved = underTest.save(connector20Pin);
        underTest.save(connector24Pin);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByNameAndIdNot(
                connector20Pin.getName(),
                anotherId
        );

        // then
        assertThat(optConnector).isPresent();
        assertThat(optConnector.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optConnector.get().getName())
                .isEqualTo(connector20Pin.getName());
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final var saved = underTest.save(connector20Pin);
        underTest.save(connector24Pin);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByNameAndIdNot(
                connector20Pin.getName(),
                saved.getId()
        );

        // then
        assertThat(optConnector).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "20 + 4 pin";
        underTest.save(connector24Pin);
        underTest.save(connector20Pin);
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
        final String anotherName = "20 + 4 pin";
        final var saved = underTest.save(connector24Pin);
        underTest.save(connector20Pin);
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
