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
import ru.bukhtaev.model.Ssd;
import ru.bukhtaev.model.dictionary.ExpansionBayFormat;
import ru.bukhtaev.model.dictionary.StorageConnector;
import ru.bukhtaev.model.dictionary.StoragePowerConnector;
import ru.bukhtaev.model.dictionary.Vendor;
import ru.bukhtaev.repository.dictionary.IExpansionBayFormatRepository;
import ru.bukhtaev.repository.dictionary.IStorageConnectorRepository;
import ru.bukhtaev.repository.dictionary.IStoragePowerConnectorRepository;
import ru.bukhtaev.repository.dictionary.IVendorRepository;
import ru.bukhtaev.util.SsdSort;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.bukhtaev.TestUtils.SSD_PAGEABLE;

/**
 * Модульные тесты репозитория SSD накопителей.
 */
@DataJpaTest
class SsdRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий SSD накопителей.
     */
    @Autowired
    private ISsdRepository underTest;

    /**
     * Репозиторий вендоров.
     */
    @Autowired
    private IVendorRepository vendorRepository;

    /**
     * Репозиторий коннекторов подключения накопителей.
     */
    @Autowired
    private IStorageConnectorRepository connectorRepository;

    /**
     * Репозиторий коннекторов питания накопителей.
     */
    @Autowired
    private IStoragePowerConnectorRepository powerConnectorRepository;

    /**
     * Репозиторий форматов отсеков расширения.
     */
    @Autowired
    private IExpansionBayFormatRepository expansionBayFormatRepository;

    private Ssd ssd980;
    private Ssd ssdS700;

    @BeforeEach
    void setUp() {
        final Vendor vendorSamsung = vendorRepository.save(
                Vendor.builder()
                        .name("Samsung")
                        .build()
        );
        final Vendor vendorHp = vendorRepository.save(
                Vendor.builder()
                        .name("HP")
                        .build()
        );

        final StorageConnector connectorSata3 = connectorRepository.save(
                StorageConnector.builder()
                        .name("SATA 3")
                        .build()
        );
        final StorageConnector connectorM2 = connectorRepository.save(
                StorageConnector.builder()
                        .name("M.2")
                        .build()
        );

        final StoragePowerConnector powerConnectorFdd = powerConnectorRepository.save(
                StoragePowerConnector.builder()
                        .name("FDD")
                        .build()
        );

        final ExpansionBayFormat format25 = expansionBayFormatRepository.save(
                ExpansionBayFormat.builder()
                        .name("2.5")
                        .build()
        );

        ssd980 = Ssd.builder()
                .name("980")
                .capacity(1000)
                .readingSpeed(3500)
                .writingSpeed(3000)
                .vendor(vendorSamsung)
                .connector(connectorM2)
                .powerConnector(null)
                .expansionBayFormat(null)
                .build();
        ssdS700 = Ssd.builder()
                .name("S700")
                .capacity(120)
                .readingSpeed(550)
                .writingSpeed(480)
                .vendor(vendorHp)
                .connector(connectorSata3)
                .powerConnector(powerConnectorFdd)
                .expansionBayFormat(format25)
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
        vendorRepository.deleteAll();
        connectorRepository.deleteAll();
        powerConnectorRepository.deleteAll();
        expansionBayFormatRepository.deleteAll();
    }

    @Test
    void findAllBy_withAllEntitiesAtPage_shouldReturnAllEntitiesAsPage() {
        // given
        underTest.save(ssd980);
        underTest.save(ssdS700);

        // when
        final Slice<Ssd> ssds = underTest.findAllBy(SSD_PAGEABLE);

        // then
        assertThat(ssds.getSize())
                .isEqualTo(SSD_PAGEABLE.getPageSize());
        assertThat(ssds.getNumberOfElements())
                .isEqualTo(2);

        final Ssd ssd1 = ssds.getContent().get(0);
        assertThat(ssd1.getName())
                .isEqualTo(ssdS700.getName());
        assertThat(ssd1.getVendor().getId())
                .isEqualTo(ssdS700.getVendor().getId());
        assertThat(ssd1.getVendor().getName())
                .isEqualTo(ssdS700.getVendor().getName());
        assertThat(ssd1.getConnector().getId())
                .isEqualTo(ssdS700.getConnector().getId());
        assertThat(ssd1.getConnector().getName())
                .isEqualTo(ssdS700.getConnector().getName());
        assertThat(ssd1.getPowerConnector().getId())
                .isEqualTo(ssdS700.getPowerConnector().getId());
        assertThat(ssd1.getPowerConnector().getName())
                .isEqualTo(ssdS700.getPowerConnector().getName());
        assertThat(ssd1.getExpansionBayFormat().getId())
                .isEqualTo(ssdS700.getExpansionBayFormat().getId());
        assertThat(ssd1.getExpansionBayFormat().getName())
                .isEqualTo(ssdS700.getExpansionBayFormat().getName());
        assertThat(ssd1.getCapacity())
                .isEqualTo(ssdS700.getCapacity());
        assertThat(ssd1.getReadingSpeed())
                .isEqualTo(ssdS700.getReadingSpeed());
        assertThat(ssd1.getWritingSpeed())
                .isEqualTo(ssdS700.getWritingSpeed());

        final Ssd ssd2 = ssds.getContent().get(1);
        assertThat(ssd2.getName())
                .isEqualTo(ssd980.getName());
        assertThat(ssd2.getVendor().getId())
                .isEqualTo(ssd980.getVendor().getId());
        assertThat(ssd2.getVendor().getName())
                .isEqualTo(ssd980.getVendor().getName());
        assertThat(ssd2.getConnector().getId())
                .isEqualTo(ssd980.getConnector().getId());
        assertThat(ssd2.getConnector().getName())
                .isEqualTo(ssd980.getConnector().getName());
        assertThat(ssd2.getPowerConnector()).isNull();
        assertThat(ssd2.getExpansionBayFormat()).isNull();
        assertThat(ssd2.getCapacity())
                .isEqualTo(ssd980.getCapacity());
        assertThat(ssd2.getReadingSpeed())
                .isEqualTo(ssd980.getReadingSpeed());
        assertThat(ssd2.getWritingSpeed())
                .isEqualTo(ssd980.getWritingSpeed());
    }

    @Test
    void findAllBy_withNotAllEntitiesAtPage_shouldReturnNotAllEntitiesAsPage() {
        // given
        underTest.save(ssd980);
        underTest.save(ssdS700);
        final Pageable singleElementPageable = PageRequest.of(
                0,
                1,
                SsdSort.CAPACITY_ASC.getSortValue()
        );

        // when
        final Slice<Ssd> ssds = underTest.findAllBy(singleElementPageable);

        // then
        assertThat(ssds.getSize())
                .isEqualTo(singleElementPageable.getPageSize());
        assertThat(ssds.getNumberOfElements())
                .isEqualTo(1);
        final Ssd ssd = ssds.getContent().get(0);
        assertThat(ssd.getName())
                .isEqualTo(ssdS700.getName());
        assertThat(ssd.getVendor().getId())
                .isEqualTo(ssdS700.getVendor().getId());
        assertThat(ssd.getVendor().getName())
                .isEqualTo(ssdS700.getVendor().getName());
        assertThat(ssd.getConnector().getId())
                .isEqualTo(ssdS700.getConnector().getId());
        assertThat(ssd.getConnector().getName())
                .isEqualTo(ssdS700.getConnector().getName());
        assertThat(ssd.getPowerConnector().getId())
                .isEqualTo(ssdS700.getPowerConnector().getId());
        assertThat(ssd.getPowerConnector().getName())
                .isEqualTo(ssdS700.getPowerConnector().getName());
        assertThat(ssd.getExpansionBayFormat().getId())
                .isEqualTo(ssdS700.getExpansionBayFormat().getId());
        assertThat(ssd.getExpansionBayFormat().getName())
                .isEqualTo(ssdS700.getExpansionBayFormat().getName());
        assertThat(ssd.getCapacity())
                .isEqualTo(ssdS700.getCapacity());
        assertThat(ssd.getReadingSpeed())
                .isEqualTo(ssdS700.getReadingSpeed());
        assertThat(ssd.getWritingSpeed())
                .isEqualTo(ssdS700.getWritingSpeed());
    }

    @Test
    void findTheSame_withExistentEntity_shouldReturnFoundEntity() {
        // given
        final Ssd saved = underTest.save(ssdS700);
        underTest.save(ssd980);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optSsd = underTest.findTheSame(
                ssdS700.getName(),
                ssdS700.getCapacity()
        );

        // then
        assertThat(optSsd).isPresent();
        final Ssd ssd = optSsd.get();
        assertThat(ssd.getId())
                .isEqualTo(saved.getId());
        assertThat(ssd.getName())
                .isEqualTo(ssdS700.getName());
        assertThat(ssd.getVendor().getId())
                .isEqualTo(ssdS700.getVendor().getId());
        assertThat(ssd.getVendor().getName())
                .isEqualTo(ssdS700.getVendor().getName());
        assertThat(ssd.getConnector().getId())
                .isEqualTo(ssdS700.getConnector().getId());
        assertThat(ssd.getConnector().getName())
                .isEqualTo(ssdS700.getConnector().getName());
        assertThat(ssd.getPowerConnector().getId())
                .isEqualTo(ssdS700.getPowerConnector().getId());
        assertThat(ssd.getPowerConnector().getName())
                .isEqualTo(ssdS700.getPowerConnector().getName());
        assertThat(ssd.getExpansionBayFormat().getId())
                .isEqualTo(ssdS700.getExpansionBayFormat().getId());
        assertThat(ssd.getExpansionBayFormat().getName())
                .isEqualTo(ssdS700.getExpansionBayFormat().getName());
        assertThat(ssd.getCapacity())
                .isEqualTo(ssdS700.getCapacity());
        assertThat(ssd.getReadingSpeed())
                .isEqualTo(ssdS700.getReadingSpeed());
        assertThat(ssd.getWritingSpeed())
                .isEqualTo(ssdS700.getWritingSpeed());
    }

    @Test
    void findTheSame_withNonExistentEntity_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "Ultimate SU650";
        underTest.save(ssdS700);
        underTest.save(ssd980);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optSsd = underTest.findTheSame(
                anotherName,
                ssdS700.getCapacity()
        );

        // then
        assertThat(optSsd).isNotPresent();
    }

    @Test
    void findTheSameWithAnotherId_withExistentEntityAndNonExistentId_shouldReturnFoundEntity() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final Ssd saved = underTest.save(ssd980);
        underTest.save(ssdS700);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optSsd = underTest.findTheSameWithAnotherId(
                ssd980.getName(),
                ssd980.getCapacity(),
                anotherId
        );

        // then
        assertThat(optSsd).isPresent();
        final Ssd ssd = optSsd.get();
        assertThat(ssd.getId())
                .isEqualTo(saved.getId());
        assertThat(ssd.getName())
                .isEqualTo(ssd980.getName());
        assertThat(ssd.getVendor().getId())
                .isEqualTo(ssd980.getVendor().getId());
        assertThat(ssd.getVendor().getName())
                .isEqualTo(ssd980.getVendor().getName());
        assertThat(ssd.getConnector().getId())
                .isEqualTo(ssd980.getConnector().getId());
        assertThat(ssd.getConnector().getName())
                .isEqualTo(ssd980.getConnector().getName());
        assertThat(ssd.getPowerConnector()).isNull();
        assertThat(ssd.getExpansionBayFormat()).isNull();
        assertThat(ssd.getCapacity())
                .isEqualTo(ssd980.getCapacity());
        assertThat(ssd.getReadingSpeed())
                .isEqualTo(ssd980.getReadingSpeed());
        assertThat(ssd.getWritingSpeed())
                .isEqualTo(ssd980.getWritingSpeed());
    }

    @Test
    void findTheSameWithAnotherId_withExistentEntityAndExistentId_shouldReturnEmptyOptional() {
        // given
        final Ssd saved = underTest.save(ssd980);
        underTest.save(ssdS700);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optSsd = underTest.findTheSameWithAnotherId(
                ssd980.getName(),
                ssd980.getCapacity(),
                saved.getId()
        );

        // then
        assertThat(optSsd).isNotPresent();
    }

    @Test
    void findTheSameWithAnotherId_withNonExistentEntityAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "Ultimate SU650";
        underTest.save(ssdS700);
        underTest.save(ssd980);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optSsd = underTest.findTheSameWithAnotherId(
                anotherName,
                ssdS700.getCapacity(),
                anotherId
        );

        // then
        assertThat(optSsd).isNotPresent();
    }

    @Test
    void findTheSameWithAnotherId_withNonExistentEntityAndExistentId_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "Ultimate SU650";
        final Ssd saved = underTest.save(ssdS700);
        underTest.save(ssd980);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optSsd = underTest.findTheSameWithAnotherId(
                anotherName,
                ssdS700.getCapacity(),
                saved.getId()
        );

        // then
        assertThat(optSsd).isNotPresent();
    }
}