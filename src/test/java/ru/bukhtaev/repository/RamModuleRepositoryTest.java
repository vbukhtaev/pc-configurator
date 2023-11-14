package ru.bukhtaev.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.*;
import ru.bukhtaev.util.RamModuleSort;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.bukhtaev.TestUtils.RAM_MODULE_PAGEABLE;

/**
 * Модульные тесты репозитория модулей оперативной памяти.
 */
@DataJpaTest
class RamModuleRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий модулей оперативной памяти.
     */
    @Autowired
    private IRamModuleRepository underTest;

    /**
     * Репозиторий типов оперативной памяти.
     */
    @Autowired
    private IRamTypeRepository typeRepository;

    /**
     * Репозиторий вариантов исполнения.
     */
    @Autowired
    private IDesignRepository designRepository;

    /**
     * Репозиторий вендоров.
     */
    @Autowired
    private IVendorRepository vendorRepository;

    private RamModule ramModuleDdr4;
    private RamModule ramModuleDdr5;

    @BeforeEach
    void setUp() {
        final Vendor vendorKingston = vendorRepository.save(
                Vendor.builder()
                        .name("Kingston")
                        .build()
        );
        final Vendor vendorCrucial = vendorRepository.save(
                Vendor.builder()
                        .name("Crucial")
                        .build()
        );

        final RamType typeDdr4 = typeRepository.save(
                RamType.builder()
                        .name("DDR4")
                        .build()
        );
        final RamType typeDdr5 = typeRepository.save(
                RamType.builder()
                        .name("DDR5")
                        .build()
        );

        final Design designHyperxFury = designRepository.save(
                Design.builder()
                        .name("HyperX Fury")
                        .vendor(vendorKingston)
                        .build()
        );
        final Design designBallistix = designRepository.save(
                Design.builder()
                        .name("Ballistix")
                        .vendor(vendorCrucial)
                        .build()
        );

        ramModuleDdr4 = RamModule.builder()
                .clock(3200)
                .capacity(8192)
                .type(typeDdr4)
                .design(designHyperxFury)
                .build();
        ramModuleDdr5 = RamModule.builder()
                .clock(6000)
                .capacity(16384)
                .type(typeDdr5)
                .design(designBallistix)
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
        typeRepository.deleteAll();
        designRepository.deleteAll();
        vendorRepository.deleteAll();
    }

    @Test
    void findAllBy_withAllEntitiesAtPage_shouldReturnAllEntitiesAsPage() {
        // given
        underTest.save(ramModuleDdr4);
        underTest.save(ramModuleDdr5);

        // when
        final Slice<RamModule> modules = underTest.findAllBy(RAM_MODULE_PAGEABLE);

        // then
        assertThat(modules.getSize())
                .isEqualTo(RAM_MODULE_PAGEABLE.getPageSize());
        assertThat(modules.getNumberOfElements())
                .isEqualTo(2);

        final RamModule module1 = modules.getContent().get(0);
        assertThat(module1.getType().getId())
                .isEqualTo(ramModuleDdr4.getType().getId());
        assertThat(module1.getType().getName())
                .isEqualTo(ramModuleDdr4.getType().getName());
        assertThat(module1.getDesign().getId())
                .isEqualTo(ramModuleDdr4.getDesign().getId());
        assertThat(module1.getDesign().getName())
                .isEqualTo(ramModuleDdr4.getDesign().getName());
        assertThat(module1.getCapacity())
                .isEqualTo(ramModuleDdr4.getCapacity());
        assertThat(module1.getClock())
                .isEqualTo(ramModuleDdr4.getClock());

        final RamModule module2 = modules.getContent().get(1);
        assertThat(module2.getType().getId())
                .isEqualTo(ramModuleDdr5.getType().getId());
        assertThat(module2.getType().getName())
                .isEqualTo(ramModuleDdr5.getType().getName());
        assertThat(module2.getDesign().getId())
                .isEqualTo(ramModuleDdr5.getDesign().getId());
        assertThat(module2.getDesign().getName())
                .isEqualTo(ramModuleDdr5.getDesign().getName());
        assertThat(module2.getCapacity())
                .isEqualTo(ramModuleDdr5.getCapacity());
        assertThat(module2.getClock())
                .isEqualTo(ramModuleDdr5.getClock());
    }

    @Test
    void findAllBy_withNotAllEntitiesAtPage_shouldReturnNotAllEntitiesAsPage() {
        // given
        underTest.save(ramModuleDdr4);
        underTest.save(ramModuleDdr5);
        final Pageable singleElementPageable = PageRequest.of(
                0,
                1,
                RamModuleSort.TYPE_NAME_ASC.getSortValue()
        );

        // when
        final Slice<RamModule> modules = underTest.findAllBy(singleElementPageable);

        // then
        assertThat(modules.getSize())
                .isEqualTo(singleElementPageable.getPageSize());
        assertThat(modules.getNumberOfElements())
                .isEqualTo(1);
        final RamModule module = modules.getContent().get(0);
        assertThat(module.getType().getId())
                .isEqualTo(ramModuleDdr4.getType().getId());
        assertThat(module.getType().getName())
                .isEqualTo(ramModuleDdr4.getType().getName());
        assertThat(module.getDesign().getId())
                .isEqualTo(ramModuleDdr4.getDesign().getId());
        assertThat(module.getDesign().getName())
                .isEqualTo(ramModuleDdr4.getDesign().getName());
        assertThat(module.getCapacity())
                .isEqualTo(ramModuleDdr4.getCapacity());
        assertThat(module.getClock())
                .isEqualTo(ramModuleDdr4.getClock());
    }

    @Test
    void findTheSame_withExistentEntity_shouldReturnFoundEntity() {
        // given
        final RamModule saved = underTest.save(ramModuleDdr5);
        underTest.save(ramModuleDdr4);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optRamModule = underTest.findTheSame(
                ramModuleDdr5.getClock(),
                ramModuleDdr5.getCapacity(),
                ramModuleDdr5.getType(),
                ramModuleDdr5.getDesign()
        );

        // then
        assertThat(optRamModule).isPresent();
        final RamModule module = optRamModule.get();
        assertThat(module.getId())
                .isEqualTo(saved.getId());
        assertThat(module.getType().getId())
                .isEqualTo(ramModuleDdr5.getType().getId());
        assertThat(module.getType().getName())
                .isEqualTo(ramModuleDdr5.getType().getName());
        assertThat(module.getDesign().getId())
                .isEqualTo(ramModuleDdr5.getDesign().getId());
        assertThat(module.getDesign().getName())
                .isEqualTo(ramModuleDdr5.getDesign().getName());
        assertThat(module.getCapacity())
                .isEqualTo(ramModuleDdr5.getCapacity());
        assertThat(module.getClock())
                .isEqualTo(ramModuleDdr5.getClock());
    }

    @Test
    void findTheSame_withNonExistentEntity_shouldReturnEmptyOptional() {
        // given
        final Integer anotherClock = 1600;
        underTest.save(ramModuleDdr5);
        underTest.save(ramModuleDdr4);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optRamModule = underTest.findTheSame(
                anotherClock,
                ramModuleDdr5.getCapacity(),
                ramModuleDdr5.getType(),
                ramModuleDdr5.getDesign()
        );

        // then
        assertThat(optRamModule).isNotPresent();
    }

    @Test
    void findTheSameWithAnotherId_withExistentEntityAndNonExistentId_shouldReturnFoundEntity() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final RamModule saved = underTest.save(ramModuleDdr4);
        underTest.save(ramModuleDdr5);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optRamModule = underTest.findTheSameWithAnotherId(
                ramModuleDdr4.getClock(),
                ramModuleDdr4.getCapacity(),
                ramModuleDdr4.getType(),
                ramModuleDdr4.getDesign(),
                anotherId
        );

        // then
        assertThat(optRamModule).isPresent();
        final RamModule module = optRamModule.get();
        assertThat(module.getId())
                .isEqualTo(saved.getId());
        assertThat(module.getType().getId())
                .isEqualTo(ramModuleDdr4.getType().getId());
        assertThat(module.getType().getName())
                .isEqualTo(ramModuleDdr4.getType().getName());
        assertThat(module.getDesign().getId())
                .isEqualTo(ramModuleDdr4.getDesign().getId());
        assertThat(module.getDesign().getName())
                .isEqualTo(ramModuleDdr4.getDesign().getName());
        assertThat(module.getCapacity())
                .isEqualTo(ramModuleDdr4.getCapacity());
        assertThat(module.getClock())
                .isEqualTo(ramModuleDdr4.getClock());
    }

    @Test
    void findTheSameWithAnotherId_withExistentEntityAndExistentId_shouldReturnEmptyOptional() {
        // given
        final RamModule saved = underTest.save(ramModuleDdr4);
        underTest.save(ramModuleDdr5);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optRamModule = underTest.findTheSameWithAnotherId(
                ramModuleDdr4.getClock(),
                ramModuleDdr4.getCapacity(),
                ramModuleDdr4.getType(),
                ramModuleDdr4.getDesign(),
                saved.getId()
        );

        // then
        assertThat(optRamModule).isNotPresent();
    }

    @Test
    void findTheSameWithAnotherId_withNonExistentEntityAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final Integer anotherClock = 1600;
        underTest.save(ramModuleDdr5);
        underTest.save(ramModuleDdr4);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optRamModule = underTest.findTheSameWithAnotherId(
                anotherClock,
                ramModuleDdr5.getCapacity(),
                ramModuleDdr5.getType(),
                ramModuleDdr5.getDesign(),
                anotherId
        );

        // then
        assertThat(optRamModule).isNotPresent();
    }

    @Test
    void findTheSameWithAnotherId_withNonExistentEntityAndExistentId_shouldReturnEmptyOptional() {
        // given
        final Integer anotherClock = 1600;
        final RamModule saved = underTest.save(ramModuleDdr5);
        underTest.save(ramModuleDdr4);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optRamModule = underTest.findTheSameWithAnotherId(
                anotherClock,
                ramModuleDdr5.getCapacity(),
                ramModuleDdr5.getType(),
                ramModuleDdr5.getDesign(),
                saved.getId()
        );

        // then
        assertThat(optRamModule).isNotPresent();
    }
}