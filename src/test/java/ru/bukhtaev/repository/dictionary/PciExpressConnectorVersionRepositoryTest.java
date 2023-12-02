package ru.bukhtaev.repository.dictionary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.dictionary.PciExpressConnectorVersion;

import java.util.HashSet;
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
                .lowerVersions(new HashSet<>())
                .build();
        version4 = PciExpressConnectorVersion.builder()
                .name("4.0")
                .lowerVersions(new HashSet<>())
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByName_withExistentName_shouldReturnFoundEntity() {
        // given
        final var savedVersion3 = underTest.save(version3);
        version4.getLowerVersions().add(savedVersion3);
        final var savedVersion4 = underTest.save(version4);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optVersion = underTest.findByName(version4.getName());

        // then
        assertThat(optVersion).isPresent();
        final var version = optVersion.get();
        assertThat(version.getId())
                .isEqualTo(savedVersion4.getId());
        assertThat(version.getName())
                .isEqualTo(version4.getName());
        assertThat(version.getLowerVersions())
                .hasSize(1);
        assertThat(version.getLowerVersions())
                .containsExactly(savedVersion3);
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "4.1";
        final var savedVersion3 = underTest.save(version3);
        version4.getLowerVersions().add(savedVersion3);
        underTest.save(version4);
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
        final var savedVersion3 = underTest.save(version3);
        version4.getLowerVersions().add(savedVersion3);
        underTest.save(version4);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optVersion = underTest.findByNameAndIdNot(
                version3.getName(),
                anotherId
        );

        // then
        assertThat(optVersion).isPresent();
        final var version = optVersion.get();
        assertThat(version.getId())
                .isEqualTo(savedVersion3.getId());
        assertThat(version.getName())
                .isEqualTo(version3.getName());
        assertThat(version.getLowerVersions())
                .isEmpty();
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final var savedVersion3 = underTest.save(version3);
        version4.getLowerVersions().add(savedVersion3);
        underTest.save(version4);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optVersion = underTest.findByNameAndIdNot(
                version3.getName(),
                savedVersion3.getId()
        );

        // then
        assertThat(optVersion).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "4.1";
        final var savedVersion3 = underTest.save(version3);
        version4.getLowerVersions().add(savedVersion3);
        underTest.save(version4);
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
        final String anotherName = "4.1";
        final var savedVersion3 = underTest.save(version3);
        version4.getLowerVersions().add(savedVersion3);
        final var savedVersion4 = underTest.save(version4);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optVersion = underTest.findByNameAndIdNot(
                anotherName,
                savedVersion4.getId()
        );

        // then
        assertThat(optVersion).isNotPresent();
    }
}
