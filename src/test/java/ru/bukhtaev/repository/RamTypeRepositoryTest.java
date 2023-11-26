package ru.bukhtaev.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.dictionary.RamType;
import ru.bukhtaev.repository.dictionary.IRamTypeRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Модульные тесты репозитория типов оперативной памяти.
 */
@DataJpaTest
class RamTypeRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий типов оперативной памяти.
     */
    @Autowired
    private IRamTypeRepository underTest;

    private RamType typeDdr4;
    private RamType typeDdr5;

    @BeforeEach
    void setUp() {
        typeDdr4 = RamType.builder()
                .name("DDR4")
                .build();
        typeDdr5 = RamType.builder()
                .name("DDR5")
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByName_withExistentName_shouldReturnFoundEntity() {
        // given
        final var saved = underTest.save(typeDdr5);
        underTest.save(typeDdr4);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optType = underTest.findByName(typeDdr5.getName());

        // then
        assertThat(optType).isPresent();
        assertThat(optType.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optType.get().getName())
                .isEqualTo(typeDdr5.getName());
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "DDR3";
        underTest.save(typeDdr5);
        underTest.save(typeDdr4);
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
        final var saved = underTest.save(typeDdr4);
        underTest.save(typeDdr5);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optType = underTest.findByNameAndIdNot(
                typeDdr4.getName(),
                anotherId
        );

        // then
        assertThat(optType).isPresent();
        assertThat(optType.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optType.get().getName())
                .isEqualTo(typeDdr4.getName());
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final var saved = underTest.save(typeDdr4);
        underTest.save(typeDdr5);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optType = underTest.findByNameAndIdNot(
                typeDdr4.getName(),
                saved.getId()
        );

        // then
        assertThat(optType).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "DDR3";
        underTest.save(typeDdr5);
        underTest.save(typeDdr4);
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
        final String anotherName = "DDR3";
        final var saved = underTest.save(typeDdr5);
        underTest.save(typeDdr4);
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
