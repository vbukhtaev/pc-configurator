package ru.bukhtaev.repository.dictionary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.dictionary.Vendor;
import ru.bukhtaev.util.NameableSort;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.bukhtaev.TestUtils.NAMEABLE_PAGEABLE;

/**
 * Модульные тесты репозитория вендоров.
 */
@DataJpaTest
class VendorRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий вендоров.
     */
    @Autowired
    private IVendorRepository underTest;

    private Vendor vendorMsi;
    private Vendor vendorGigabyte;

    @BeforeEach
    void setUp() {
        vendorMsi = Vendor.builder()
                .name("MSI")
                .build();
        vendorGigabyte = Vendor.builder()
                .name("GIGABYTE")
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findAllBy_withAllEntitiesAtPage_shouldReturnAllEntitiesAsPage() {
        // given
        underTest.save(vendorMsi);
        underTest.save(vendorGigabyte);

        // when
        final Slice<Vendor> vendors = underTest.findAllBy(NAMEABLE_PAGEABLE);

        // then
        assertThat(vendors.getSize())
                .isEqualTo(NAMEABLE_PAGEABLE.getPageSize());
        assertThat(vendors.getNumberOfElements())
                .isEqualTo(2);
        assertThat(vendors.getContent().get(0).getName())
                .isEqualTo(vendorGigabyte.getName());
        assertThat(vendors.getContent().get(1).getName())
                .isEqualTo(vendorMsi.getName());
    }

    @Test
    void findAllBy_withNotAllEntitiesAtPage_shouldReturnNotAllEntitiesAsPage() {
        // given
        underTest.save(vendorMsi);
        underTest.save(vendorGigabyte);
        final Pageable singleElementPageable = PageRequest.of(
                0,
                1,
                NameableSort.NAME_ASC.getSortValue()
        );

        // when
        final Slice<Vendor> vendors = underTest.findAllBy(singleElementPageable);

        // then
        assertThat(vendors.getSize())
                .isEqualTo(singleElementPageable.getPageSize());
        assertThat(vendors.getNumberOfElements())
                .isEqualTo(1);
        assertThat(vendors.getContent().get(0).getName())
                .isEqualTo(vendorGigabyte.getName());
    }

    @Test
    void findByName_withExistentName_shouldReturnFoundEntity() {
        // given
        final var saved = underTest.save(vendorGigabyte);
        underTest.save(vendorMsi);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optVendor = underTest.findByName(vendorGigabyte.getName());

        // then
        assertThat(optVendor).isPresent();
        assertThat(optVendor.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optVendor.get().getName())
                .isEqualTo(vendorGigabyte.getName());
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "ASUS";
        underTest.save(vendorGigabyte);
        underTest.save(vendorMsi);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optVendor = underTest.findByName(anotherName);

        // then
        assertThat(optVendor).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndNonExistentId_shouldReturnFoundEntity() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final var saved = underTest.save(vendorMsi);
        underTest.save(vendorGigabyte);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optVendor = underTest.findByNameAndIdNot(
                vendorMsi.getName(),
                anotherId
        );

        // then
        assertThat(optVendor).isPresent();
        assertThat(optVendor.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optVendor.get().getName())
                .isEqualTo(vendorMsi.getName());
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final var saved = underTest.save(vendorMsi);
        underTest.save(vendorGigabyte);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optVendor = underTest.findByNameAndIdNot(
                vendorMsi.getName(),
                saved.getId()
        );

        // then
        assertThat(optVendor).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "ASUS";
        underTest.save(vendorGigabyte);
        underTest.save(vendorMsi);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optVendor = underTest.findByNameAndIdNot(
                anotherName,
                anotherId
        );

        // then
        assertThat(optVendor).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "ASUS";
        final var saved = underTest.save(vendorGigabyte);
        underTest.save(vendorMsi);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optVendor = underTest.findByNameAndIdNot(
                anotherName,
                saved.getId()
        );

        // then
        assertThat(optVendor).isNotPresent();
    }
}
