package ru.bukhtaev.repository.dictionary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.dictionary.VideoMemoryType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Модульные тесты репозитория типов видеопамяти.
 */
@DataJpaTest
class VideoMemoryTypeRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий типов видеопамяти.
     */
    @Autowired
    private IVideoMemoryTypeRepository underTest;

    private VideoMemoryType typeGddr5;
    private VideoMemoryType typeGddr6X;

    @BeforeEach
    void setUp() {
        typeGddr5 = VideoMemoryType.builder()
                .name("GDDR5")
                .build();
        typeGddr6X = VideoMemoryType.builder()
                .name("GDDR6X")
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByName_withExistentName_shouldReturnFoundEntity() {
        // given
        final var saved = underTest.save(typeGddr6X);
        underTest.save(typeGddr5);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optType = underTest.findByName(typeGddr6X.getName());

        // then
        assertThat(optType).isPresent();
        assertThat(optType.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optType.get().getName())
                .isEqualTo(typeGddr6X.getName());
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "GDDR7";
        underTest.save(typeGddr6X);
        underTest.save(typeGddr5);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optType = underTest.findByName(anotherName);

        // then
        assertThat(optType).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndNonExistentId_shouldReturnFoundEntity() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final var saved = underTest.save(typeGddr5);
        underTest.save(typeGddr6X);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optType = underTest.findByNameAndIdNot(
                typeGddr5.getName(),
                anotherId
        );

        // then
        assertThat(optType).isPresent();
        assertThat(optType.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optType.get().getName())
                .isEqualTo(typeGddr5.getName());
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final var saved = underTest.save(typeGddr5);
        underTest.save(typeGddr6X);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optType = underTest.findByNameAndIdNot(
                typeGddr5.getName(),
                saved.getId()
        );

        // then
        assertThat(optType).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "GDDR7";
        underTest.save(typeGddr6X);
        underTest.save(typeGddr5);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optType = underTest.findByNameAndIdNot(
                anotherName,
                anotherId
        );

        // then
        assertThat(optType).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "GDDR7";
        final var saved = underTest.save(typeGddr6X);
        underTest.save(typeGddr5);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optType = underTest.findByNameAndIdNot(
                anotherName,
                saved.getId()
        );

        // then
        assertThat(optType).isNotPresent();
    }
}
