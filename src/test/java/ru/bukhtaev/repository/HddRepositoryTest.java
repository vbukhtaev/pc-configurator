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
import ru.bukhtaev.model.Hdd;
import ru.bukhtaev.model.StorageConnector;
import ru.bukhtaev.model.StoragePowerConnector;
import ru.bukhtaev.model.Vendor;
import ru.bukhtaev.util.HddSort;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.bukhtaev.TestUtils.HDD_PAGEABLE;

/**
 * Модульные тесты репозитория жестких дисков.
 */
@DataJpaTest
class HddRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий жестких дисков.
     */
    @Autowired
    private IHddRepository underTest;

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

    private Hdd hddBarracuda;
    private Hdd hddP300;

    @BeforeEach
    void setUp() {
        final Vendor vendorSeagate = vendorRepository.save(
                Vendor.builder()
                        .name("Seagate")
                        .build()
        );
        final Vendor vendorToshiba = vendorRepository.save(
                Vendor.builder()
                        .name("Toshiba")
                        .build()
        );

        final StorageConnector connectorSata3 = connectorRepository.save(
                StorageConnector.builder()
                        .name("SATA 3")
                        .build()
        );
        final StorageConnector connectorSata2 = connectorRepository.save(
                StorageConnector.builder()
                        .name("SATA 2")
                        .build()
        );

        final StoragePowerConnector powerConnectorFdd = powerConnectorRepository.save(
                StoragePowerConnector.builder()
                        .name("FDD")
                        .build()
        );
        final StoragePowerConnector powerConnectorMolex = powerConnectorRepository.save(
                StoragePowerConnector.builder()
                        .name("Molex")
                        .build()
        );

        hddBarracuda = Hdd.builder()
                .name("BarraCuda")
                .capacity(1024)
                .readingSpeed(210)
                .writingSpeed(210)
                .spindleSpeed(7200)
                .cacheSize(64)
                .vendor(vendorSeagate)
                .connector(connectorSata3)
                .powerConnector(powerConnectorFdd)
                .build();
        hddP300 = Hdd.builder()
                .name("P300")
                .capacity(2000)
                .readingSpeed(190)
                .writingSpeed(190)
                .spindleSpeed(5400)
                .cacheSize(128)
                .vendor(vendorToshiba)
                .connector(connectorSata2)
                .powerConnector(powerConnectorMolex)
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
        vendorRepository.deleteAll();
        connectorRepository.deleteAll();
        powerConnectorRepository.deleteAll();
    }

    @Test
    void findAllBy_withAllEntitiesAtPage_shouldReturnAllEntitiesAsPage() {
        // given
        underTest.save(hddBarracuda);
        underTest.save(hddP300);
        final List<Hdd> testData = List.of(hddBarracuda, hddP300);

        // when
        final Slice<Hdd> hdds = underTest.findAllBy(HDD_PAGEABLE);

        // then
        assertThat(hdds.getSize())
                .isEqualTo(HDD_PAGEABLE.getPageSize());
        assertThat(hdds.getNumberOfElements())
                .isEqualTo(2);

        for (int i = 0; i < testData.size(); i++) {
            final Hdd foundHdd = hdds.getContent().get(i);
            final Hdd savedHdd = testData.get(i);
            assertThat(foundHdd.getName())
                    .isEqualTo(savedHdd.getName());
            assertThat(foundHdd.getVendor().getId())
                    .isEqualTo(savedHdd.getVendor().getId());
            assertThat(foundHdd.getVendor().getName())
                    .isEqualTo(savedHdd.getVendor().getName());
            assertThat(foundHdd.getConnector().getId())
                    .isEqualTo(savedHdd.getConnector().getId());
            assertThat(foundHdd.getConnector().getName())
                    .isEqualTo(savedHdd.getConnector().getName());
            assertThat(foundHdd.getPowerConnector().getId())
                    .isEqualTo(savedHdd.getPowerConnector().getId());
            assertThat(foundHdd.getPowerConnector().getName())
                    .isEqualTo(savedHdd.getPowerConnector().getName());
            assertThat(foundHdd.getCapacity())
                    .isEqualTo(savedHdd.getCapacity());
            assertThat(foundHdd.getReadingSpeed())
                    .isEqualTo(savedHdd.getReadingSpeed());
            assertThat(foundHdd.getWritingSpeed())
                    .isEqualTo(savedHdd.getWritingSpeed());
            assertThat(foundHdd.getCacheSize())
                    .isEqualTo(savedHdd.getCacheSize());
            assertThat(foundHdd.getSpindleSpeed())
                    .isEqualTo(savedHdd.getSpindleSpeed());
        }
    }

    @Test
    void findAllBy_withNotAllEntitiesAtPage_shouldReturnNotAllEntitiesAsPage() {
        // given
        underTest.save(hddBarracuda);
        underTest.save(hddP300);
        final Pageable singleElementPageable = PageRequest.of(
                0,
                1,
                HddSort.CAPACITY_ASC.getSortValue()
        );

        // when
        final Slice<Hdd> hdds = underTest.findAllBy(singleElementPageable);

        // then
        assertThat(hdds.getSize())
                .isEqualTo(singleElementPageable.getPageSize());
        assertThat(hdds.getNumberOfElements())
                .isEqualTo(1);
        final Hdd hdd = hdds.getContent().get(0);
        assertThat(hdd.getName())
                .isEqualTo(hddBarracuda.getName());
        assertThat(hdd.getVendor().getId())
                .isEqualTo(hddBarracuda.getVendor().getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(hddBarracuda.getVendor().getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(hddBarracuda.getConnector().getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(hddBarracuda.getConnector().getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(hddBarracuda.getPowerConnector().getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(hddBarracuda.getPowerConnector().getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddBarracuda.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddBarracuda.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddBarracuda.getWritingSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddBarracuda.getCacheSize());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddBarracuda.getSpindleSpeed());
    }

    @Test
    void findTheSame_withExistentEntity_shouldReturnFoundEntity() {
        // given
        final Hdd saved = underTest.save(hddP300);
        underTest.save(hddBarracuda);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optHdd = underTest.findTheSame(
                hddP300.getName(),
                hddP300.getCapacity(),
                hddP300.getSpindleSpeed(),
                hddP300.getCacheSize()
        );

        // then
        assertThat(optHdd).isPresent();
        final Hdd hdd = optHdd.get();
        assertThat(hdd.getId())
                .isEqualTo(saved.getId());
        assertThat(hdd.getName())
                .isEqualTo(hddP300.getName());
        assertThat(hdd.getVendor().getId())
                .isEqualTo(hddP300.getVendor().getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(hddP300.getVendor().getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(hddP300.getConnector().getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(hddP300.getConnector().getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(hddP300.getPowerConnector().getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(hddP300.getPowerConnector().getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddP300.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddP300.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddP300.getWritingSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddP300.getCacheSize());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddP300.getSpindleSpeed());
    }

    @Test
    void findTheSame_withNonExistentEntity_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "SkyHawk";
        underTest.save(hddP300);
        underTest.save(hddBarracuda);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optHdd = underTest.findTheSame(
                anotherName,
                hddP300.getCapacity(),
                hddP300.getSpindleSpeed(),
                hddP300.getCacheSize()
        );

        // then
        assertThat(optHdd).isNotPresent();
    }

    @Test
    void findTheSameWithAnotherId_withExistentEntityAndNonExistentId_shouldReturnFoundEntity() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final Hdd saved = underTest.save(hddBarracuda);
        underTest.save(hddP300);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optHdd = underTest.findTheSameWithAnotherId(
                hddBarracuda.getName(),
                hddBarracuda.getCapacity(),
                hddBarracuda.getSpindleSpeed(),
                hddBarracuda.getCacheSize(),
                anotherId
        );

        // then
        assertThat(optHdd).isPresent();
        final Hdd hdd = optHdd.get();
        assertThat(hdd.getId())
                .isEqualTo(saved.getId());
        assertThat(hdd.getName())
                .isEqualTo(hddBarracuda.getName());
        assertThat(hdd.getVendor().getId())
                .isEqualTo(hddBarracuda.getVendor().getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(hddBarracuda.getVendor().getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(hddBarracuda.getConnector().getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(hddBarracuda.getConnector().getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(hddBarracuda.getPowerConnector().getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(hddBarracuda.getPowerConnector().getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddBarracuda.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddBarracuda.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddBarracuda.getWritingSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddBarracuda.getCacheSize());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddBarracuda.getSpindleSpeed());
    }

    @Test
    void findTheSameWithAnotherId_withExistentEntityAndExistentId_shouldReturnEmptyOptional() {
        // given
        final Hdd saved = underTest.save(hddBarracuda);
        underTest.save(hddP300);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optHdd = underTest.findTheSameWithAnotherId(
                hddBarracuda.getName(),
                hddBarracuda.getCapacity(),
                hddBarracuda.getSpindleSpeed(),
                hddBarracuda.getCacheSize(),
                saved.getId()
        );

        // then
        assertThat(optHdd).isNotPresent();
    }

    @Test
    void findTheSameWithAnotherId_withNonExistentEntityAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "SkyHawk";
        underTest.save(hddP300);
        underTest.save(hddBarracuda);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optHdd = underTest.findTheSameWithAnotherId(
                anotherName,
                hddP300.getCapacity(),
                hddP300.getSpindleSpeed(),
                hddP300.getCacheSize(),
                anotherId
        );

        // then
        assertThat(optHdd).isNotPresent();
    }

    @Test
    void findTheSameWithAnotherId_withNonExistentEntityAndExistentId_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "SkyHawk";
        final Hdd saved = underTest.save(hddP300);
        underTest.save(hddBarracuda);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optHdd = underTest.findTheSameWithAnotherId(
                anotherName,
                hddP300.getCapacity(),
                hddP300.getSpindleSpeed(),
                hddP300.getCacheSize(),
                saved.getId()
        );

        // then
        assertThat(optHdd).isNotPresent();
    }
}