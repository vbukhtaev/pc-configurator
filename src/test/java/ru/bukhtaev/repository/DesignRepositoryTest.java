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
import ru.bukhtaev.model.Design;
import ru.bukhtaev.model.Vendor;
import ru.bukhtaev.util.DesignSort;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.bukhtaev.TestUtils.DESIGN_PAGEABLE;

/**
 * Модульные тесты репозитория вариантов исполнения.
 */
@DataJpaTest
class DesignRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий вариантов исполнения.
     */
    @Autowired
    private IDesignRepository underTest;

    /**
     * Репозиторий вендоров.
     */
    @Autowired
    private IVendorRepository vendorRepository;

    private Design designVentus3X;
    private Design designEagle;

    @BeforeEach
    void setUp() {
        final Vendor vendorMsi = vendorRepository.save(
                Vendor.builder()
                        .name("MSI")
                        .build()
        );
        final Vendor vendorGigabyte = vendorRepository.save(
                Vendor.builder()
                        .name("GIGABYTE")
                        .build()
        );

        designVentus3X = Design.builder()
                .name("VENTUS 3X")
                .vendor(vendorMsi)
                .build();
        designEagle = Design.builder()
                .name("EAGLE")
                .vendor(vendorGigabyte)
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
        vendorRepository.deleteAll();
    }

    @Test
    void findAllBy_withAllEntitiesAtPage_shouldReturnAllEntitiesAsPage() {
        // given
        underTest.save(designVentus3X);
        underTest.save(designEagle);

        // when
        final Slice<Design> designs = underTest.findAllBy(DESIGN_PAGEABLE);

        // then
        assertThat(designs.getSize())
                .isEqualTo(DESIGN_PAGEABLE.getPageSize());
        assertThat(designs.getNumberOfElements())
                .isEqualTo(2);

        final Design design1 = designs.getContent().get(0);
        assertThat(design1.getName())
                .isEqualTo(designEagle.getName());
        assertThat(design1.getVendor().getId())
                .isEqualTo(designEagle.getVendor().getId());
        assertThat(design1.getVendor().getName())
                .isEqualTo(designEagle.getVendor().getName());

        final Design design2 = designs.getContent().get(1);
        assertThat(design2.getName())
                .isEqualTo(designVentus3X.getName());
        assertThat(design2.getVendor().getId())
                .isEqualTo(designVentus3X.getVendor().getId());
        assertThat(design2.getVendor().getName())
                .isEqualTo(designVentus3X.getVendor().getName());
    }

    @Test
    void findAllBy_withNotAllEntitiesAtPage_shouldReturnNotAllEntitiesAsPage() {
        // given
        underTest.save(designVentus3X);
        underTest.save(designEagle);
        final Pageable singleElementPageable = PageRequest.of(
                0,
                1,
                DesignSort.VENDOR_NAME_ASC.getSortValue()
        );

        // when
        final Slice<Design> designs = underTest.findAllBy(singleElementPageable);

        // then
        assertThat(designs.getSize())
                .isEqualTo(singleElementPageable.getPageSize());
        assertThat(designs.getNumberOfElements())
                .isEqualTo(1);
        assertThat(designs.getContent().get(0).getName())
                .isEqualTo(designEagle.getName());
        assertThat(designs.getContent().get(0).getVendor().getId())
                .isEqualTo(designEagle.getVendor().getId());
        assertThat(designs.getContent().get(0).getVendor().getName())
                .isEqualTo(designEagle.getVendor().getName());
    }

    @Test
    void findByName_withExistentName_shouldReturnFoundEntity() {
        // given
        final Design saved = underTest.save(designEagle);
        underTest.save(designVentus3X);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optDesign = underTest.findByName(designEagle.getName());

        // then
        assertThat(optDesign).isPresent();
        assertThat(optDesign.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optDesign.get().getName())
                .isEqualTo(designEagle.getName());
        assertThat(optDesign.get().getVendor().getId())
                .isEqualTo(designEagle.getVendor().getId());
        assertThat(optDesign.get().getVendor().getName())
                .isEqualTo(designEagle.getVendor().getName());
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "Ballistix";
        underTest.save(designEagle);
        underTest.save(designVentus3X);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optDesign = underTest.findByName(anotherName);

        // then
        assertThat(optDesign).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndNonExistentId_shouldReturnFoundEntity() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final Design saved = underTest.save(designVentus3X);
        underTest.save(designEagle);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optDesign = underTest.findByNameAndIdNot(
                designVentus3X.getName(),
                anotherId
        );

        // then
        assertThat(optDesign).isPresent();
        assertThat(optDesign.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optDesign.get().getName())
                .isEqualTo(designVentus3X.getName());
        assertThat(optDesign.get().getVendor().getId())
                .isEqualTo(designVentus3X.getVendor().getId());
        assertThat(optDesign.get().getVendor().getName())
                .isEqualTo(designVentus3X.getVendor().getName());
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final Design saved = underTest.save(designVentus3X);
        underTest.save(designEagle);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optDesign = underTest.findByNameAndIdNot(
                designVentus3X.getName(),
                saved.getId()
        );

        // then
        assertThat(optDesign).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "Ballistix";
        underTest.save(designEagle);
        underTest.save(designVentus3X);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optDesign = underTest.findByNameAndIdNot(
                anotherName,
                anotherId
        );

        // then
        assertThat(optDesign).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "Ballistix";
        final Design saved = underTest.save(designEagle);
        underTest.save(designVentus3X);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optDesign = underTest.findByNameAndIdNot(
                anotherName,
                saved.getId()
        );

        // then
        assertThat(optDesign).isNotPresent();
    }
}