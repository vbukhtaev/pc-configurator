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
import ru.bukhtaev.model.Fan;
import ru.bukhtaev.model.dictionary.FanPowerConnector;
import ru.bukhtaev.model.dictionary.FanSize;
import ru.bukhtaev.model.dictionary.Vendor;
import ru.bukhtaev.repository.dictionary.IFanPowerConnectorRepository;
import ru.bukhtaev.repository.dictionary.IFanSizeRepository;
import ru.bukhtaev.repository.dictionary.IVendorRepository;
import ru.bukhtaev.util.FanSort;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.bukhtaev.TestUtils.FAN_PAGEABLE;

/**
 * Модульные тесты репозитория вентиляторов.
 */
@DataJpaTest
class FanRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий вентиляторов.
     */
    @Autowired
    private IFanRepository underTest;

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
     * Репозиторий коннекторов питания вентиляторов.
     */
    @Autowired
    private IFanPowerConnectorRepository powerConnectorRepository;

    private Fan fanRf120B;
    private Fan fanPureWings2;

    @BeforeEach
    void setUp() {
        final Vendor vendorDeepcool = vendorRepository.save(
                Vendor.builder()
                        .name("DEEPCOOL")
                        .build()
        );
        final Vendor vendorBeQuiet = vendorRepository.save(
                Vendor.builder()
                        .name("be quiet!")
                        .build()
        );

        final FanSize size120 = sizeRepository.save(
                FanSize.builder()
                        .length(120)
                        .width(120)
                        .height(25)
                        .build()
        );
        final FanSize size140 = sizeRepository.save(
                FanSize.builder()
                        .length(140)
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

        fanRf120B = Fan.builder()
                .name("RF120B")
                .vendor(vendorDeepcool)
                .size(size120)
                .powerConnector(connector3Pin)
                .build();
        fanPureWings2 = Fan.builder()
                .name("PURE WINGS 2")
                .vendor(vendorBeQuiet)
                .size(size140)
                .powerConnector(connector4Pin)
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
        sizeRepository.deleteAll();
        vendorRepository.deleteAll();
        powerConnectorRepository.deleteAll();
    }

    @Test
    void findAllBy_withAllEntitiesAtPage_shouldReturnAllEntitiesAsPage() {
        // given
        underTest.save(fanRf120B);
        underTest.save(fanPureWings2);

        // when
        final Slice<Fan> fans = underTest.findAllBy(FAN_PAGEABLE);

        // then
        assertThat(fans.getSize())
                .isEqualTo(FAN_PAGEABLE.getPageSize());
        assertThat(fans.getNumberOfElements())
                .isEqualTo(2);

        final Fan fan1 = fans.getContent().get(0);
        assertThat(fan1.getName())
                .isEqualTo(fanRf120B.getName());
        assertThat(fan1.getVendor().getId())
                .isEqualTo(fanRf120B.getVendor().getId());
        assertThat(fan1.getVendor().getName())
                .isEqualTo(fanRf120B.getVendor().getName());
        assertThat(fan1.getPowerConnector().getId())
                .isEqualTo(fanRf120B.getPowerConnector().getId());
        assertThat(fan1.getPowerConnector().getName())
                .isEqualTo(fanRf120B.getPowerConnector().getName());
        assertThat(fan1.getSize().getId())
                .isEqualTo(fanRf120B.getSize().getId());
        assertThat(fan1.getSize().getLength())
                .isEqualTo(fanRf120B.getSize().getLength());
        assertThat(fan1.getSize().getWidth())
                .isEqualTo(fanRf120B.getSize().getWidth());
        assertThat(fan1.getSize().getHeight())
                .isEqualTo(fanRf120B.getSize().getHeight());

        final Fan fan2 = fans.getContent().get(1);
        assertThat(fan2.getName())
                .isEqualTo(fanPureWings2.getName());
        assertThat(fan2.getVendor().getId())
                .isEqualTo(fanPureWings2.getVendor().getId());
        assertThat(fan2.getVendor().getName())
                .isEqualTo(fanPureWings2.getVendor().getName());
        assertThat(fan2.getPowerConnector().getId())
                .isEqualTo(fanPureWings2.getPowerConnector().getId());
        assertThat(fan2.getPowerConnector().getName())
                .isEqualTo(fanPureWings2.getPowerConnector().getName());
        assertThat(fan2.getSize().getId())
                .isEqualTo(fanPureWings2.getSize().getId());
        assertThat(fan2.getSize().getLength())
                .isEqualTo(fanPureWings2.getSize().getLength());
        assertThat(fan2.getSize().getWidth())
                .isEqualTo(fanPureWings2.getSize().getWidth());
        assertThat(fan2.getSize().getHeight())
                .isEqualTo(fanPureWings2.getSize().getHeight());
    }

    @Test
    void findAllBy_withNotAllEntitiesAtPage_shouldReturnNotAllEntitiesAsPage() {
        // given
        underTest.save(fanRf120B);
        underTest.save(fanPureWings2);
        final Pageable singleElementPageable = PageRequest.of(
                0,
                1,
                FanSort.VENDOR_NAME_ASC.getSortValue()
        );

        // when
        final Slice<Fan> fans = underTest.findAllBy(singleElementPageable);

        // then
        assertThat(fans.getSize())
                .isEqualTo(singleElementPageable.getPageSize());
        assertThat(fans.getNumberOfElements())
                .isEqualTo(1);
        assertThat(fans.getContent().get(0).getName())
                .isEqualTo(fanRf120B.getName());
        assertThat(fans.getContent().get(0).getVendor().getId())
                .isEqualTo(fanRf120B.getVendor().getId());
        assertThat(fans.getContent().get(0).getVendor().getName())
                .isEqualTo(fanRf120B.getVendor().getName());
        assertThat(fans.getContent().get(0).getPowerConnector().getId())
                .isEqualTo(fanRf120B.getPowerConnector().getId());
        assertThat(fans.getContent().get(0).getPowerConnector().getName())
                .isEqualTo(fanRf120B.getPowerConnector().getName());
        assertThat(fans.getContent().get(0).getSize().getId())
                .isEqualTo(fanRf120B.getSize().getId());
        assertThat(fans.getContent().get(0).getSize().getLength())
                .isEqualTo(fanRf120B.getSize().getLength());
        assertThat(fans.getContent().get(0).getSize().getWidth())
                .isEqualTo(fanRf120B.getSize().getWidth());
        assertThat(fans.getContent().get(0).getSize().getHeight())
                .isEqualTo(fanRf120B.getSize().getHeight());
    }

    @Test
    void findTheSame_withExistentEntity_shouldReturnFoundEntity() {
        // given
        final Fan saved = underTest.save(fanPureWings2);
        underTest.save(fanRf120B);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFan = underTest.findTheSame(
                fanPureWings2.getName(),
                fanPureWings2.getSize()
        );

        // then
        assertThat(optFan).isPresent();
        assertThat(optFan.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optFan.get().getName())
                .isEqualTo(fanPureWings2.getName());
        assertThat(optFan.get().getVendor().getId())
                .isEqualTo(fanPureWings2.getVendor().getId());
        assertThat(optFan.get().getVendor().getName())
                .isEqualTo(fanPureWings2.getVendor().getName());
        assertThat(optFan.get().getPowerConnector().getId())
                .isEqualTo(fanPureWings2.getPowerConnector().getId());
        assertThat(optFan.get().getPowerConnector().getName())
                .isEqualTo(fanPureWings2.getPowerConnector().getName());
        assertThat(optFan.get().getSize().getId())
                .isEqualTo(fanPureWings2.getSize().getId());
        assertThat(optFan.get().getSize().getLength())
                .isEqualTo(fanPureWings2.getSize().getLength());
        assertThat(optFan.get().getSize().getWidth())
                .isEqualTo(fanPureWings2.getSize().getWidth());
        assertThat(optFan.get().getSize().getHeight())
                .isEqualTo(fanPureWings2.getSize().getHeight());
    }

    @Test
    void findTheSame_withNonExistentEntity_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "Frost 14";
        underTest.save(fanPureWings2);
        underTest.save(fanRf120B);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFan = underTest.findTheSame(
                anotherName,
                fanPureWings2.getSize()
        );

        // then
        assertThat(optFan).isNotPresent();
    }

    @Test
    void findTheSameWithAnotherId_withExistentEntityAndNonExistentId_shouldReturnFoundEntity() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final Fan saved = underTest.save(fanRf120B);
        underTest.save(fanPureWings2);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFan = underTest.findTheSameWithAnotherId(
                fanRf120B.getName(),
                fanRf120B.getSize(),
                anotherId
        );

        // then
        assertThat(optFan).isPresent();
        assertThat(optFan.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optFan.get().getName())
                .isEqualTo(fanRf120B.getName());
        assertThat(optFan.get().getVendor().getId())
                .isEqualTo(fanRf120B.getVendor().getId());
        assertThat(optFan.get().getVendor().getName())
                .isEqualTo(fanRf120B.getVendor().getName());
        assertThat(optFan.get().getPowerConnector().getId())
                .isEqualTo(fanRf120B.getPowerConnector().getId());
        assertThat(optFan.get().getPowerConnector().getName())
                .isEqualTo(fanRf120B.getPowerConnector().getName());
        assertThat(optFan.get().getSize().getId())
                .isEqualTo(fanRf120B.getSize().getId());
        assertThat(optFan.get().getSize().getLength())
                .isEqualTo(fanRf120B.getSize().getLength());
        assertThat(optFan.get().getSize().getWidth())
                .isEqualTo(fanRf120B.getSize().getWidth());
        assertThat(optFan.get().getSize().getHeight())
                .isEqualTo(fanRf120B.getSize().getHeight());
    }

    @Test
    void findTheSameWithAnotherId_withExistentEntityAndExistentId_shouldReturnEmptyOptional() {
        // given
        final Fan saved = underTest.save(fanRf120B);
        underTest.save(fanPureWings2);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFan = underTest.findTheSameWithAnotherId(
                fanRf120B.getName(),
                fanRf120B.getSize(),
                saved.getId()
        );

        // then
        assertThat(optFan).isNotPresent();
    }

    @Test
    void findTheSameWithAnotherId_withNonExistentEntityAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "Frost 14";
        underTest.save(fanPureWings2);
        underTest.save(fanRf120B);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFan = underTest.findTheSameWithAnotherId(
                anotherName,
                fanPureWings2.getSize(),
                anotherId
        );

        // then
        assertThat(optFan).isNotPresent();
    }

    @Test
    void findTheSameWithAnotherId_withNonExistentEntityAndExistentId_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "Frost 14";
        final Fan saved = underTest.save(fanPureWings2);
        underTest.save(fanRf120B);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optFan = underTest.findTheSameWithAnotherId(
                anotherName,
                fanPureWings2.getSize(),
                saved.getId()
        );

        // then
        assertThat(optFan).isNotPresent();
    }
}