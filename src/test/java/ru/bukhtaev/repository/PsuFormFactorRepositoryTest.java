package ru.bukhtaev.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.dictionary.PsuFormFactor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Модульные тесты репозитория форм-факторов блоков питания.
 */
@DataJpaTest
class PsuFormFactorRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий форм-факторов блоков питания.
     */
    @Autowired
    private IPsuFormFactorRepository underTest;

    private PsuFormFactor formFactorAtx;
    private PsuFormFactor formFactorEps;

    @BeforeEach
    void setUp() {
        formFactorAtx = PsuFormFactor.builder()
                .name("ATX")
                .build();
        formFactorEps = PsuFormFactor.builder()
                .name("EPS")
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByName_withExistentName_shouldReturnFoundEntity() {
        // given
        final var saved = underTest.save(formFactorEps);
        underTest.save(formFactorAtx);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFormFactor = underTest.findByName(formFactorEps.getName());

        // then
        assertThat(optFormFactor).isPresent();
        assertThat(optFormFactor.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optFormFactor.get().getName())
                .isEqualTo(formFactorEps.getName());
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "FLEX";
        underTest.save(formFactorEps);
        underTest.save(formFactorAtx);
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
        final var saved = underTest.save(formFactorAtx);
        underTest.save(formFactorEps);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFormFactor = underTest.findByNameAndIdNot(
                formFactorAtx.getName(),
                anotherId
        );

        // then
        assertThat(optFormFactor).isPresent();
        assertThat(optFormFactor.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optFormFactor.get().getName())
                .isEqualTo(formFactorAtx.getName());
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final var saved = underTest.save(formFactorAtx);
        underTest.save(formFactorEps);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFormFactor = underTest.findByNameAndIdNot(
                formFactorAtx.getName(),
                saved.getId()
        );

        // then
        assertThat(optFormFactor).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "FLEX";
        underTest.save(formFactorEps);
        underTest.save(formFactorAtx);
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
        final String anotherName = "FLEX";
        final var saved = underTest.save(formFactorEps);
        underTest.save(formFactorAtx);
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
