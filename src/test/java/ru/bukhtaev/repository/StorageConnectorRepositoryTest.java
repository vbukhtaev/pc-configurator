package ru.bukhtaev.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.StorageConnector;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Модульные тесты репозитория коннекторов подключения накопителей.
 */
@DataJpaTest
class StorageConnectorRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий коннекторов подключения накопителей.
     */
    @Autowired
    private IStorageConnectorRepository underTest;

    private StorageConnector connectorSata3;
    private StorageConnector connectorSata2;

    @BeforeEach
    void setUp() {
        connectorSata3 = StorageConnector.builder()
                .name("SATA 3")
                .build();
        connectorSata2 = StorageConnector.builder()
                .name("SATA 2")
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByName_withExistentName_shouldReturnFoundEntity() {
        // given
        final var saved = underTest.save(connectorSata2);
        underTest.save(connectorSata3);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByName(connectorSata2.getName());

        // then
        assertThat(optConnector).isPresent();
        assertThat(optConnector.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optConnector.get().getName())
                .isEqualTo(connectorSata2.getName());
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "SATA 1";
        underTest.save(connectorSata2);
        underTest.save(connectorSata3);
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
        final var saved = underTest.save(connectorSata3);
        underTest.save(connectorSata2);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByNameAndIdNot(
                connectorSata3.getName(),
                anotherId
        );

        // then
        assertThat(optConnector).isPresent();
        assertThat(optConnector.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optConnector.get().getName())
                .isEqualTo(connectorSata3.getName());
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final var saved = underTest.save(connectorSata3);
        underTest.save(connectorSata2);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByNameAndIdNot(
                connectorSata3.getName(),
                saved.getId()
        );

        // then
        assertThat(optConnector).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "SATA 1";
        underTest.save(connectorSata2);
        underTest.save(connectorSata3);
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
        final String anotherName = "SATA 1";
        final var saved = underTest.save(connectorSata2);
        underTest.save(connectorSata3);
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
