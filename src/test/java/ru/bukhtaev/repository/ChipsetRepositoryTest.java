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
import ru.bukhtaev.model.Chipset;
import ru.bukhtaev.model.Socket;
import ru.bukhtaev.util.ChipsetSort;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.bukhtaev.TestUtils.CHIPSET_PAGEABLE;

/**
 * Модульные тесты репозитория чипсетов.
 */
@DataJpaTest
class ChipsetRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий чипсетов.
     */
    @Autowired
    private IChipsetRepository underTest;

    /**
     * Репозиторий сокетов.
     */
    @Autowired
    private ISocketRepository socketRepository;

    private Chipset chipsetB660;
    private Chipset chipsetX670E;

    @BeforeEach
    void setUp() {
        final Socket socketLga1700 = socketRepository.save(
                Socket.builder()
                        .name("LGA 1700")
                        .build()
        );
        final Socket socketAm5 = socketRepository.save(
                Socket.builder()
                        .name("AM5")
                        .build()
        );

        chipsetB660 = Chipset.builder()
                .name("B660")
                .socket(socketLga1700)
                .build();
        chipsetX670E = Chipset.builder()
                .name("X670E")
                .socket(socketAm5)
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
        socketRepository.deleteAll();
    }

    @Test
    void findAllBy_withAllEntitiesAtPage_shouldReturnAllEntitiesAsPage() {
        // given
        underTest.save(chipsetB660);
        underTest.save(chipsetX670E);

        // when
        final Slice<Chipset> chipsets = underTest.findAllBy(CHIPSET_PAGEABLE);

        // then
        assertThat(chipsets.getSize())
                .isEqualTo(CHIPSET_PAGEABLE.getPageSize());
        assertThat(chipsets.getNumberOfElements())
                .isEqualTo(2);

        final Chipset chipset1 = chipsets.getContent().get(0);
        assertThat(chipset1.getName())
                .isEqualTo(chipsetX670E.getName());
        assertThat(chipset1.getSocket().getId())
                .isEqualTo(chipsetX670E.getSocket().getId());
        assertThat(chipset1.getSocket().getName())
                .isEqualTo(chipsetX670E.getSocket().getName());

        final Chipset chipset2 = chipsets.getContent().get(1);
        assertThat(chipset2.getName())
                .isEqualTo(chipsetB660.getName());
        assertThat(chipset2.getSocket().getId())
                .isEqualTo(chipsetB660.getSocket().getId());
        assertThat(chipset2.getSocket().getName())
                .isEqualTo(chipsetB660.getSocket().getName());
    }

    @Test
    void findAllBy_withNotAllEntitiesAtPage_shouldReturnNotAllEntitiesAsPage() {
        // given
        underTest.save(chipsetB660);
        underTest.save(chipsetX670E);
        final Pageable singleElementPageable = PageRequest.of(
                0,
                1,
                ChipsetSort.SOCKET_NAME_ASC.getSortValue()
        );

        // when
        final Slice<Chipset> chipsets = underTest.findAllBy(singleElementPageable);

        // then
        assertThat(chipsets.getSize())
                .isEqualTo(singleElementPageable.getPageSize());
        assertThat(chipsets.getNumberOfElements())
                .isEqualTo(1);
        assertThat(chipsets.getContent().get(0).getName())
                .isEqualTo(chipsetX670E.getName());
        assertThat(chipsets.getContent().get(0).getSocket().getId())
                .isEqualTo(chipsetX670E.getSocket().getId());
        assertThat(chipsets.getContent().get(0).getSocket().getName())
                .isEqualTo(chipsetX670E.getSocket().getName());
    }

    @Test
    void findByName_withExistentName_shouldReturnFoundEntity() {
        // given
        final Chipset saved = underTest.save(chipsetX670E);
        underTest.save(chipsetB660);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optChipset = underTest.findByName(chipsetX670E.getName());

        // then
        assertThat(optChipset).isPresent();
        assertThat(optChipset.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optChipset.get().getName())
                .isEqualTo(chipsetX670E.getName());
        assertThat(optChipset.get().getSocket().getId())
                .isEqualTo(chipsetX670E.getSocket().getId());
        assertThat(optChipset.get().getSocket().getName())
                .isEqualTo(chipsetX670E.getSocket().getName());
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "X99";
        underTest.save(chipsetX670E);
        underTest.save(chipsetB660);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optChipset = underTest.findByName(anotherName);

        // then
        assertThat(optChipset).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndNonExistentId_shouldReturnFoundEntity() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final Chipset saved = underTest.save(chipsetB660);
        underTest.save(chipsetX670E);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optChipset = underTest.findByNameAndIdNot(
                chipsetB660.getName(),
                anotherId
        );

        // then
        assertThat(optChipset).isPresent();
        assertThat(optChipset.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optChipset.get().getName())
                .isEqualTo(chipsetB660.getName());
        assertThat(optChipset.get().getSocket().getId())
                .isEqualTo(chipsetB660.getSocket().getId());
        assertThat(optChipset.get().getSocket().getName())
                .isEqualTo(chipsetB660.getSocket().getName());
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final Chipset saved = underTest.save(chipsetB660);
        underTest.save(chipsetX670E);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optChipset = underTest.findByNameAndIdNot(
                chipsetB660.getName(),
                saved.getId()
        );

        // then
        assertThat(optChipset).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "X99";
        underTest.save(chipsetX670E);
        underTest.save(chipsetB660);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optChipset = underTest.findByNameAndIdNot(
                anotherName,
                anotherId
        );

        // then
        assertThat(optChipset).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "X99";
        final Chipset saved = underTest.save(chipsetX670E);
        underTest.save(chipsetB660);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optChipset = underTest.findByNameAndIdNot(
                anotherName,
                saved.getId()
        );

        // then
        assertThat(optChipset).isNotPresent();
    }
}