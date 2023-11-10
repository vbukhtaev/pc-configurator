package ru.bukhtaev.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.PciExpressConnectorVersion;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Модульные тесты репозитория версий коннектора PCI-Express.
 */
@DataJpaTest
class PciExpressConnectorVersionRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий версий коннектора PCI-Express.
     */
    @Autowired
    private IPciExpressConnectorVersionRepository underTest;

    private PciExpressConnectorVersion version3;
    private PciExpressConnectorVersion version4;

    @BeforeEach
    void setUp() {
        version3 = PciExpressConnectorVersion.builder()
                .name("3.0")
                .build();
        version4 = PciExpressConnectorVersion.builder()
                .name("4.0")
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByName_withExistentName_shouldReturnFoundEntity() {
        // given
        final var saved = underTest.save(version4);
        underTest.save(version3);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optVersion = underTest.findByName(version4.getName());

        // then
        assertThat(optVersion).isPresent();
        assertThat(optVersion.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optVersion.get().getName())
                .isEqualTo(version4.getName());
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "5.0";
        underTest.save(version4);
        underTest.save(version3);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optVersion = underTest.findByName(anotherName);

        // then
        assertThat(optVersion).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndNonExistentId_shouldReturnFoundEntity() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final var saved = underTest.save(version3);
        underTest.save(version4);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optVersion = underTest.findByNameAndIdNot(
                version3.getName(),
                anotherId
        );

        // then
        assertThat(optVersion).isPresent();
        assertThat(optVersion.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optVersion.get().getName())
                .isEqualTo(version3.getName());
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final var saved = underTest.save(version3);
        underTest.save(version4);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optVersion = underTest.findByNameAndIdNot(
                version3.getName(),
                saved.getId()
        );

        // then
        assertThat(optVersion).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "5.0";
        underTest.save(version4);
        underTest.save(version3);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optVersion = underTest.findByNameAndIdNot(
                anotherName,
                anotherId
        );

        // then
        assertThat(optVersion).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "5.0";
        final var saved = underTest.save(version4);
        underTest.save(version3);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optVersion = underTest.findByNameAndIdNot(
                anotherName,
                saved.getId()
        );

        // then
        assertThat(optVersion).isNotPresent();
    }
}
