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
import ru.bukhtaev.util.CoolerSort;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.bukhtaev.TestUtils.COOLER_PAGEABLE;

/**
 * Модульные тесты репозитория процессорных кулеров.
 */
@DataJpaTest
class CoolerRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий процессорных кулеров.
     */
    @Autowired
    private ICoolerRepository underTest;

    /**
     * Репозиторий вендоров.
     */
    @Autowired
    private IVendorRepository vendorRepository;

    /**
     * Репозиторий размеров вентиляторов.
     */
    @Autowired
    private IFanSizeRepository sizeRepository;

    /**
     * Репозиторий сокетов.
     */
    @Autowired
    private ISocketRepository socketRepository;

    /**
     * Репозиторий коннекторов питания вентиляторов.
     */
    @Autowired
    private IFanPowerConnectorRepository powerConnectorRepository;

    private Cooler coolerGammaxx400;
    private Cooler coolerNhd15;

    @BeforeEach
    void setUp() {
        final Vendor vendorDeepCool = vendorRepository.save(
                Vendor.builder()
                        .name("DEEPCOOL")
                        .build()
        );
        final Vendor vendorNoctua = vendorRepository.save(
                Vendor.builder()
                        .name("Noctua")
                        .build()
        );

        final FanSize size120 = sizeRepository.save(
                FanSize.builder()
                        .length(120)
                        .width(120)
                        .height(25)
                        .build()
        );
        final FanSize size150 = sizeRepository.save(
                FanSize.builder()
                        .length(150)
                        .width(140)
                        .height(25)
                        .build()
        );

        final FanPowerConnector connector3Pin = powerConnectorRepository.save(
                FanPowerConnector.builder()
                        .name("3 pin")
                        .build()
        );
        final FanPowerConnector connector4Pin = powerConnectorRepository.save(
                FanPowerConnector.builder()
                        .name("4 pin")
                        .build()
        );

        final Socket socketLga1700 = socketRepository.save(
                Socket.builder()
                        .name("LGA 1700")
                        .build()
        );
        final Socket socketLga2011V3 = socketRepository.save(
                Socket.builder()
                        .name("LGA 2011 V3")
                        .build()
        );
        final Socket socketAm5 = socketRepository.save(
                Socket.builder()
                        .name("AM5")
                        .build()
        );

        coolerGammaxx400 = Cooler.builder()
                .name("GAMMAXX 400 V2")
                .height(155)
                .powerDissipation(180)
                .vendor(vendorDeepCool)
                .fanSize(size120)
                .powerConnector(connector3Pin)
                .supportedSockets(Set.of(
                        socketLga1700,
                        socketAm5
                ))
                .build();
        coolerNhd15 = Cooler.builder()
                .name("NH-D15")
                .height(165)
                .powerDissipation(250)
                .vendor(vendorNoctua)
                .fanSize(size150)
                .powerConnector(connector4Pin)
                .supportedSockets(Set.of(
                        socketAm5,
                        socketLga2011V3
                ))
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
        sizeRepository.deleteAll();
        vendorRepository.deleteAll();
        socketRepository.deleteAll();
        powerConnectorRepository.deleteAll();
    }

    @Test
    void findAllBy_withAllEntitiesAtPage_shouldReturnAllEntitiesAsPage() {
        // given
        underTest.save(coolerGammaxx400);
        underTest.save(coolerNhd15);
        final List<Cooler> testData = List.of(coolerGammaxx400, coolerNhd15);

        // when
        final Slice<Cooler> coolers = underTest.findAllBy(COOLER_PAGEABLE);

        // then
        assertThat(coolers.getSize())
                .isEqualTo(COOLER_PAGEABLE.getPageSize());
        assertThat(coolers.getNumberOfElements())
                .isEqualTo(2);

        for (int i = 0; i < testData.size(); i++) {
            final Cooler foundCooler = coolers.getContent().get(i);
            final Cooler savedCooler = testData.get(i);
            assertThat(foundCooler.getName())
                    .isEqualTo(savedCooler.getName());
            assertThat(foundCooler.getVendor().getId())
                    .isEqualTo(savedCooler.getVendor().getId());
            assertThat(foundCooler.getVendor().getName())
                    .isEqualTo(savedCooler.getVendor().getName());
            assertThat(foundCooler.getFanSize().getId())
                    .isEqualTo(savedCooler.getFanSize().getId());
            assertThat(foundCooler.getFanSize().getLength())
                    .isEqualTo(savedCooler.getFanSize().getLength());
            assertThat(foundCooler.getFanSize().getWidth())
                    .isEqualTo(savedCooler.getFanSize().getWidth());
            assertThat(foundCooler.getFanSize().getHeight())
                    .isEqualTo(savedCooler.getFanSize().getHeight());
            assertThat(foundCooler.getPowerConnector().getId())
                    .isEqualTo(savedCooler.getPowerConnector().getId());
            assertThat(foundCooler.getPowerConnector().getName())
                    .isEqualTo(savedCooler.getPowerConnector().getName());
            assertThat(foundCooler.getPowerDissipation())
                    .isEqualTo(savedCooler.getPowerDissipation());
            assertThat(foundCooler.getHeight())
                    .isEqualTo(savedCooler.getHeight());
            assertThat(foundCooler.getSupportedSockets())
                    .containsOnly(savedCooler.getSupportedSockets().toArray(new Socket[0]));
        }
    }

    @Test
    void findAllBy_withNotAllEntitiesAtPage_shouldReturnNotAllEntitiesAsPage() {
        // given
        underTest.save(coolerGammaxx400);
        underTest.save(coolerNhd15);
        final Pageable singleElementPageable = PageRequest.of(
                0,
                1,
                CoolerSort.POWER_DISSIPATION_DESC.getSortValue()
        );

        // when
        final Slice<Cooler> coolers = underTest.findAllBy(singleElementPageable);

        // then
        assertThat(coolers.getSize())
                .isEqualTo(singleElementPageable.getPageSize());
        assertThat(coolers.getNumberOfElements())
                .isEqualTo(1);
        final Cooler cooler = coolers.getContent().get(0);
        assertThat(cooler.getName())
                .isEqualTo(coolerNhd15.getName());
        assertThat(cooler.getVendor().getId())
                .isEqualTo(coolerNhd15.getVendor().getId());
        assertThat(cooler.getVendor().getName())
                .isEqualTo(coolerNhd15.getVendor().getName());
        assertThat(cooler.getFanSize().getId())
                .isEqualTo(coolerNhd15.getFanSize().getId());
        assertThat(cooler.getFanSize().getLength())
                .isEqualTo(coolerNhd15.getFanSize().getLength());
        assertThat(cooler.getFanSize().getWidth())
                .isEqualTo(coolerNhd15.getFanSize().getWidth());
        assertThat(cooler.getFanSize().getHeight())
                .isEqualTo(coolerNhd15.getFanSize().getHeight());
        assertThat(cooler.getPowerConnector().getId())
                .isEqualTo(coolerNhd15.getPowerConnector().getId());
        assertThat(cooler.getPowerConnector().getName())
                .isEqualTo(coolerNhd15.getPowerConnector().getName());
        assertThat(cooler.getPowerDissipation())
                .isEqualTo(coolerNhd15.getPowerDissipation());
        assertThat(cooler.getHeight())
                .isEqualTo(coolerNhd15.getHeight());
        assertThat(cooler.getSupportedSockets())
                .containsOnly(coolerNhd15.getSupportedSockets().toArray(new Socket[0]));
    }

    @Test
    void findByName_withExistentName_shouldReturnFoundEntity() {
        // given
        final var saved = underTest.save(coolerNhd15);
        underTest.save(coolerGammaxx400);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optCooler = underTest.findByName(coolerNhd15.getName());

        // then
        assertThat(optCooler).isPresent();
        final Cooler cooler = optCooler.get();
        assertThat(cooler.getId())
                .isEqualTo(saved.getId());
        assertThat(cooler.getName())
                .isEqualTo(coolerNhd15.getName());
        assertThat(cooler.getVendor().getId())
                .isEqualTo(coolerNhd15.getVendor().getId());
        assertThat(cooler.getVendor().getName())
                .isEqualTo(coolerNhd15.getVendor().getName());
        assertThat(cooler.getFanSize().getId())
                .isEqualTo(coolerNhd15.getFanSize().getId());
        assertThat(cooler.getFanSize().getLength())
                .isEqualTo(coolerNhd15.getFanSize().getLength());
        assertThat(cooler.getFanSize().getWidth())
                .isEqualTo(coolerNhd15.getFanSize().getWidth());
        assertThat(cooler.getFanSize().getHeight())
                .isEqualTo(coolerNhd15.getFanSize().getHeight());
        assertThat(cooler.getPowerConnector().getId())
                .isEqualTo(coolerNhd15.getPowerConnector().getId());
        assertThat(cooler.getPowerConnector().getName())
                .isEqualTo(coolerNhd15.getPowerConnector().getName());
        assertThat(cooler.getPowerDissipation())
                .isEqualTo(coolerNhd15.getPowerDissipation());
        assertThat(cooler.getHeight())
                .isEqualTo(coolerNhd15.getHeight());
        assertThat(cooler.getSupportedSockets())
                .containsOnly(coolerNhd15.getSupportedSockets().toArray(new Socket[0]));
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "NH-U9S";
        underTest.save(coolerNhd15);
        underTest.save(coolerGammaxx400);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optCooler = underTest.findByName(anotherName);

        // then
        assertThat(optCooler).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndNonExistentId_shouldReturnFoundEntity() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final var saved = underTest.save(coolerGammaxx400);
        underTest.save(coolerNhd15);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optCooler = underTest.findByNameAndIdNot(
                coolerGammaxx400.getName(),
                anotherId
        );

        // then
        assertThat(optCooler).isPresent();
        final Cooler cooler = optCooler.get();
        assertThat(cooler.getId())
                .isEqualTo(saved.getId());
        assertThat(cooler.getName())
                .isEqualTo(coolerGammaxx400.getName());
        assertThat(cooler.getVendor().getId())
                .isEqualTo(coolerGammaxx400.getVendor().getId());
        assertThat(cooler.getVendor().getName())
                .isEqualTo(coolerGammaxx400.getVendor().getName());
        assertThat(cooler.getFanSize().getId())
                .isEqualTo(coolerGammaxx400.getFanSize().getId());
        assertThat(cooler.getFanSize().getLength())
                .isEqualTo(coolerGammaxx400.getFanSize().getLength());
        assertThat(cooler.getFanSize().getWidth())
                .isEqualTo(coolerGammaxx400.getFanSize().getWidth());
        assertThat(cooler.getFanSize().getHeight())
                .isEqualTo(coolerGammaxx400.getFanSize().getHeight());
        assertThat(cooler.getPowerConnector().getId())
                .isEqualTo(coolerGammaxx400.getPowerConnector().getId());
        assertThat(cooler.getPowerConnector().getName())
                .isEqualTo(coolerGammaxx400.getPowerConnector().getName());
        assertThat(cooler.getPowerDissipation())
                .isEqualTo(coolerGammaxx400.getPowerDissipation());
        assertThat(cooler.getHeight())
                .isEqualTo(coolerGammaxx400.getHeight());
        assertThat(cooler.getSupportedSockets())
                .containsOnly(coolerGammaxx400.getSupportedSockets().toArray(new Socket[0]));
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final var saved = underTest.save(coolerGammaxx400);
        underTest.save(coolerNhd15);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optCooler = underTest.findByNameAndIdNot(
                coolerGammaxx400.getName(),
                saved.getId()
        );

        // then
        assertThat(optCooler).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "NH-U9S";
        underTest.save(coolerNhd15);
        underTest.save(coolerGammaxx400);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optCooler = underTest.findByNameAndIdNot(
                anotherName,
                anotherId
        );

        // then
        assertThat(optCooler).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "NH-U9S";
        final var saved = underTest.save(coolerNhd15);
        underTest.save(coolerGammaxx400);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optCooler = underTest.findByNameAndIdNot(
                anotherName,
                saved.getId()
        );

        // then
        assertThat(optCooler).isNotPresent();
    }
}
