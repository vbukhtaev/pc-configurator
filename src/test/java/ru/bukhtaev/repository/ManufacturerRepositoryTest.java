package ru.bukhtaev.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.Manufacturer;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Модульные тесты репозитория производителей.
 */
@DataJpaTest
class ManufacturerRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий производителей.
     */
    @Autowired
    private IManufacturerRepository underTest;

    private Manufacturer manufacturerIntel;
    private Manufacturer manufacturerAmd;

    @BeforeEach
    void setUp() {
        manufacturerIntel = Manufacturer.builder()
                .name("Intel")
                .build();
        manufacturerAmd = Manufacturer.builder()
                .name("AMD")
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByName_withExistentName_shouldReturnFoundEntity() {
        // given
        final Manufacturer saved = underTest.save(manufacturerAmd);
        underTest.save(manufacturerIntel);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optManufacturer = underTest.findByName(manufacturerAmd.getName());

        // then
        assertThat(optManufacturer).isPresent();
        assertThat(optManufacturer.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optManufacturer.get().getName())
                .isEqualTo(manufacturerAmd.getName());
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "Nvidia";
        underTest.save(manufacturerAmd);
        underTest.save(manufacturerIntel);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optManufacturer = underTest.findByName(anotherName);

        // then
        assertThat(optManufacturer).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndNonExistentId_shouldReturnFoundEntity() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final Manufacturer saved = underTest.save(manufacturerIntel);
        underTest.save(manufacturerAmd);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optManufacturer = underTest.findByNameAndIdNot(
                manufacturerIntel.getName(),
                anotherId
        );

        // then
        assertThat(optManufacturer).isPresent();
        assertThat(optManufacturer.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optManufacturer.get().getName())
                .isEqualTo(manufacturerIntel.getName());
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final Manufacturer saved = underTest.save(manufacturerIntel);
        underTest.save(manufacturerAmd);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optManufacturer = underTest.findByNameAndIdNot(
                manufacturerIntel.getName(),
                saved.getId()
        );

        // then
        assertThat(optManufacturer).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "Nvidia";
        underTest.save(manufacturerAmd);
        underTest.save(manufacturerIntel);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optManufacturer = underTest.findByNameAndIdNot(
                anotherName,
                anotherId
        );

        // then
        assertThat(optManufacturer).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "Nvidia";
        final Manufacturer saved = underTest.save(manufacturerAmd);
        underTest.save(manufacturerIntel);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optManufacturer = underTest.findByNameAndIdNot(
                anotherName,
                saved.getId()
        );

        // then
        assertThat(optManufacturer).isNotPresent();
    }
}