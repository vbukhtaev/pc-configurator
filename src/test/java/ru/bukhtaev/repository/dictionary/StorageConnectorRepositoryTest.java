package ru.bukhtaev.repository.dictionary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.dictionary.StorageConnector;

import java.util.HashSet;
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

    private StorageConnector connectorSata1;
    private StorageConnector connectorSata2;

    @BeforeEach
    void setUp() {
        connectorSata1 = StorageConnector.builder()
                .name("SATA 1")
                .compatibleConnectors(new HashSet<>())
                .build();
        connectorSata2 = StorageConnector.builder()
                .name("SATA 2")
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
        final var savedConnectorSata1 = underTest.save(connectorSata1);
        connectorSata2.getCompatibleConnectors().add(savedConnectorSata1);
        final var savedConnectorSata2 = underTest.save(connectorSata2);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByName(connectorSata2.getName());

        // then
        assertThat(optConnector).isPresent();
        final var connector = optConnector.get();
        assertThat(connector.getId())
                .isEqualTo(savedConnectorSata2.getId());
        assertThat(connector.getName())
                .isEqualTo(connectorSata2.getName());
        assertThat(connector.getCompatibleConnectors())
                .hasSize(1);
        assertThat(connector.getCompatibleConnectors())
                .containsExactly(savedConnectorSata1);
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "SATA 2.1";
        final var savedConnectorSata1 = underTest.save(connectorSata1);
        connectorSata2.getCompatibleConnectors().add(savedConnectorSata1);
        underTest.save(connectorSata2);
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
        final var savedConnectorSata1 = underTest.save(connectorSata1);
        connectorSata2.getCompatibleConnectors().add(savedConnectorSata1);
        underTest.save(connectorSata2);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByNameAndIdNot(
                connectorSata1.getName(),
                anotherId
        );

        // then
        assertThat(optConnector).isPresent();
        final var connector = optConnector.get();
        assertThat(connector.getId())
                .isEqualTo(savedConnectorSata1.getId());
        assertThat(connector.getName())
                .isEqualTo(connectorSata1.getName());
        assertThat(connector.getCompatibleConnectors())
                .isEmpty();
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final var savedConnectorSata1 = underTest.save(connectorSata1);
        connectorSata2.getCompatibleConnectors().add(savedConnectorSata1);
        underTest.save(connectorSata2);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByNameAndIdNot(
                connectorSata1.getName(),
                savedConnectorSata1.getId()
        );

        // then
        assertThat(optConnector).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "SATA 2.1";
        final var savedConnectorSata1 = underTest.save(connectorSata1);
        connectorSata2.getCompatibleConnectors().add(savedConnectorSata1);
        underTest.save(connectorSata2);
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
        final String anotherName = "SATA 2.1";
        final var savedConnectorSata1 = underTest.save(connectorSata1);
        connectorSata2.getCompatibleConnectors().add(savedConnectorSata1);
        final var savedConnectorSata2 = underTest.save(connectorSata2);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optConnector = underTest.findByNameAndIdNot(
                anotherName,
                savedConnectorSata2.getId()
        );

        // then
        assertThat(optConnector).isNotPresent();
    }
}
