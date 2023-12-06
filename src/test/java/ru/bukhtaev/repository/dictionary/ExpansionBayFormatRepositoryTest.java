package ru.bukhtaev.repository.dictionary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.dictionary.ExpansionBayFormat;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Модульные тесты репозитория форматов отсеков расширения.
 */
@DataJpaTest
class ExpansionBayFormatRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий форматов отсеков расширения.
     */
    @Autowired
    private IExpansionBayFormatRepository underTest;

    private ExpansionBayFormat format25;
    private ExpansionBayFormat format35;

    @BeforeEach
    void setUp() {
        format25 = ExpansionBayFormat.builder()
                .name("2.5")
                .build();
        format35 = ExpansionBayFormat.builder()
                .name("3.5")
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findBySize_withExistentName_shouldReturnFoundEntity() {
        // given
        final var saved = underTest.save(format35);
        underTest.save(format25);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFormat = underTest.findByName(format35.getName());

        // then
        assertThat(optFormat).isPresent();
        assertThat(optFormat.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optFormat.get().getName())
                .isEqualTo(format35.getName());
    }

    @Test
    void findBySize_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "5.25";
        underTest.save(format35);
        underTest.save(format25);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFormat = underTest.findByName(anotherName);

        // then
        assertThat(optFormat).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndNonExistentId_shouldReturnFoundEntity() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final var saved = underTest.save(format25);
        underTest.save(format35);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFormat = underTest.findByNameAndIdNot(
                format25.getName(),
                anotherId
        );

        // then
        assertThat(optFormat).isPresent();
        assertThat(optFormat.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optFormat.get().getName())
                .isEqualTo(format25.getName());
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final var saved = underTest.save(format25);
        underTest.save(format35);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFormat = underTest.findByNameAndIdNot(
                format25.getName(),
                saved.getId()
        );

        // then
        assertThat(optFormat).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "5.25";
        underTest.save(format35);
        underTest.save(format25);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFormat = underTest.findByNameAndIdNot(
                anotherName,
                anotherId
        );

        // then
        assertThat(optFormat).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "5.25";
        final var saved = underTest.save(format35);
        underTest.save(format25);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFormat = underTest.findByNameAndIdNot(
                anotherName,
                saved.getId()
        );

        // then
        assertThat(optFormat).isNotPresent();
    }
}
