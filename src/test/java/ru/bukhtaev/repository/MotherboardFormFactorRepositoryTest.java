package ru.bukhtaev.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.dictionary.MotherboardFormFactor;
import ru.bukhtaev.repository.dictionary.IMotherboardFormFactorRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Модульные тесты репозитория форм-факторов материнских плат.
 */
@DataJpaTest
class MotherboardFormFactorRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий форм-факторов материнских плат.
     */
    @Autowired
    private IMotherboardFormFactorRepository underTest;

    private MotherboardFormFactor formFactorMicroAtx;
    private MotherboardFormFactor formFactorStandardAtx;

    @BeforeEach
    void setUp() {
        formFactorMicroAtx = MotherboardFormFactor.builder()
                .name("Micro-ATX")
                .build();
        formFactorStandardAtx = MotherboardFormFactor.builder()
                .name("Standard-ATX")
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByName_withExistentName_shouldReturnFoundEntity() {
        // given
        final var saved = underTest.save(formFactorStandardAtx);
        underTest.save(formFactorMicroAtx);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFormFactor = underTest.findByName(formFactorStandardAtx.getName());

        // then
        assertThat(optFormFactor).isPresent();
        assertThat(optFormFactor.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optFormFactor.get().getName())
                .isEqualTo(formFactorStandardAtx.getName());
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "E-ATX";
        underTest.save(formFactorStandardAtx);
        underTest.save(formFactorMicroAtx);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFormFactor = underTest.findByName(anotherName);

        // then
        assertThat(optFormFactor).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndNonExistentId_shouldReturnFoundEntity() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final var saved = underTest.save(formFactorMicroAtx);
        underTest.save(formFactorStandardAtx);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFormFactor = underTest.findByNameAndIdNot(
                formFactorMicroAtx.getName(),
                anotherId
        );

        // then
        assertThat(optFormFactor).isPresent();
        assertThat(optFormFactor.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optFormFactor.get().getName())
                .isEqualTo(formFactorMicroAtx.getName());
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final var saved = underTest.save(formFactorMicroAtx);
        underTest.save(formFactorStandardAtx);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFormFactor = underTest.findByNameAndIdNot(
                formFactorMicroAtx.getName(),
                saved.getId()
        );

        // then
        assertThat(optFormFactor).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "E-ATX";
        underTest.save(formFactorStandardAtx);
        underTest.save(formFactorMicroAtx);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFormFactor = underTest.findByNameAndIdNot(
                anotherName,
                anotherId
        );

        // then
        assertThat(optFormFactor).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "E-ATX";
        final var saved = underTest.save(formFactorStandardAtx);
        underTest.save(formFactorMicroAtx);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFormFactor = underTest.findByNameAndIdNot(
                anotherName,
                saved.getId()
        );

        // then
        assertThat(optFormFactor).isNotPresent();
    }
}
