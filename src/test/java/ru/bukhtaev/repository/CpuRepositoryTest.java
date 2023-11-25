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
import ru.bukhtaev.util.CpuSort;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.bukhtaev.TestUtils.CPU_PAGEABLE;

/**
 * Модульные тесты репозитория процессоров.
 */
@DataJpaTest
class CpuRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий процессоров.
     */
    @Autowired
    private ICpuRepository underTest;

    /**
     * Репозиторий производителей.
     */
    @Autowired
    private IManufacturerRepository manufacturerRepository;

    /**
     * Репозиторий типов оперативной памяти.
     */
    @Autowired
    private IRamTypeRepository ramTypeRepository;

    /**
     * Репозиторий сокетов.
     */
    @Autowired
    private ISocketRepository socketRepository;

    private Cpu cpuI512400F;
    private Cpu cpuR55600X;

    @BeforeEach
    void setUp() {
        final Manufacturer manufacturerIntel = manufacturerRepository.save(
                Manufacturer.builder()
                        .name("Intel")
                        .build()
        );
        final Manufacturer manufacturerAmd = manufacturerRepository.save(
                Manufacturer.builder()
                        .name("AMD")
                        .build()
        );

        final RamType typeDdr3 = ramTypeRepository.save(
                RamType.builder()
                        .name("DDR3")
                        .build()
        );
        final RamType typeDdr4 = ramTypeRepository.save(
                RamType.builder()
                        .name("DDR4")
                        .build()
        );
        final RamType typeDdr5 = ramTypeRepository.save(
                RamType.builder()
                        .name("DDR5")
                        .build()
        );

        final Socket socketLga1700 = socketRepository.save(
                Socket.builder()
                        .name("LGA 1700")
                        .build()
        );
        final Socket socketAm4 = socketRepository.save(
                Socket.builder()
                        .name("AM4")
                        .build()
        );

        cpuI512400F = Cpu.builder()
                .name("i5 12400F")
                .coreCount(6)
                .threadCount(12)
                .baseClock(2500)
                .maxClock(4000)
                .l3CacheSize(18)
                .maxTdp(117)
                .manufacturer(manufacturerIntel)
                .socket(socketLga1700)
                .build();

        cpuI512400F.addRamType(typeDdr4, 3200);
        cpuI512400F.addRamType(typeDdr5, 4800);

        cpuR55600X = Cpu.builder()
                .name("Ryzen 5 5600X")
                .coreCount(8)
                .threadCount(16)
                .baseClock(3600)
                .maxClock(4400)
                .l3CacheSize(32)
                .maxTdp(65)
                .manufacturer(manufacturerAmd)
                .socket(socketAm4)
                .build();

        cpuR55600X.addRamType(typeDdr3, 1866);
        cpuR55600X.addRamType(typeDdr4, 3200);
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
        socketRepository.deleteAll();
        ramTypeRepository.deleteAll();
        manufacturerRepository.deleteAll();
    }

    @Test
    void findAllBy_withAllEntitiesAtPage_shouldReturnAllEntitiesAsPage() {
        // given
        underTest.save(cpuI512400F);
        underTest.save(cpuR55600X);
        final List<Cpu> testData = List.of(cpuR55600X, cpuI512400F);

        // when
        final Slice<Cpu> cpus = underTest.findAllBy(CPU_PAGEABLE);

        // then
        assertThat(cpus.getSize())
                .isEqualTo(CPU_PAGEABLE.getPageSize());
        assertThat(cpus.getNumberOfElements())
                .isEqualTo(2);

        for (int i = 0; i < testData.size(); i++) {
            final Cpu foundCpu = cpus.getContent().get(i);
            final Cpu savedCpu = testData.get(i);
            assertThat(foundCpu.getName())
                    .isEqualTo(savedCpu.getName());
            assertThat(foundCpu.getCoreCount())
                    .isEqualTo(savedCpu.getCoreCount());
            assertThat(foundCpu.getThreadCount())
                    .isEqualTo(savedCpu.getThreadCount());
            assertThat(foundCpu.getBaseClock())
                    .isEqualTo(savedCpu.getBaseClock());
            assertThat(foundCpu.getMaxClock())
                    .isEqualTo(savedCpu.getMaxClock());
            assertThat(foundCpu.getL3CacheSize())
                    .isEqualTo(savedCpu.getL3CacheSize());
            assertThat(foundCpu.getMaxTdp())
                    .isEqualTo(savedCpu.getMaxTdp());
            assertThat(foundCpu.getManufacturer().getId())
                    .isEqualTo(savedCpu.getManufacturer().getId());
            assertThat(foundCpu.getManufacturer().getName())
                    .isEqualTo(savedCpu.getManufacturer().getName());
            assertThat(foundCpu.getSocket().getId())
                    .isEqualTo(savedCpu.getSocket().getId());
            assertThat(foundCpu.getSocket().getName())
                    .isEqualTo(savedCpu.getSocket().getName());
            assertThat(foundCpu.getSupportedRamTypes())
                    .containsOnly(savedCpu.getSupportedRamTypes().toArray(new CpuRamType[0]));
        }
    }

    @Test
    void findAllBy_withNotAllEntitiesAtPage_shouldReturnNotAllEntitiesAsPage() {
        // given
        underTest.save(cpuI512400F);
        underTest.save(cpuR55600X);
        final Pageable singleElementPageable = PageRequest.of(
                0,
                1,
                CpuSort.MANUFACTURER_NAME_ASC.getSortValue()
        );

        // when
        final Slice<Cpu> cpus = underTest.findAllBy(singleElementPageable);

        // then
        assertThat(cpus.getSize())
                .isEqualTo(singleElementPageable.getPageSize());
        assertThat(cpus.getNumberOfElements())
                .isEqualTo(1);
        final Cpu cpu = cpus.getContent().get(0);
        assertThat(cpu.getName())
                .isEqualTo(cpuR55600X.getName());
        assertThat(cpu.getCoreCount())
                .isEqualTo(cpuR55600X.getCoreCount());
        assertThat(cpu.getThreadCount())
                .isEqualTo(cpuR55600X.getThreadCount());
        assertThat(cpu.getBaseClock())
                .isEqualTo(cpuR55600X.getBaseClock());
        assertThat(cpu.getMaxClock())
                .isEqualTo(cpuR55600X.getMaxClock());
        assertThat(cpu.getL3CacheSize())
                .isEqualTo(cpuR55600X.getL3CacheSize());
        assertThat(cpu.getMaxTdp())
                .isEqualTo(cpuR55600X.getMaxTdp());
        assertThat(cpu.getManufacturer().getId())
                .isEqualTo(cpuR55600X.getManufacturer().getId());
        assertThat(cpu.getManufacturer().getName())
                .isEqualTo(cpuR55600X.getManufacturer().getName());
        assertThat(cpu.getSocket().getId())
                .isEqualTo(cpuR55600X.getSocket().getId());
        assertThat(cpu.getSocket().getName())
                .isEqualTo(cpuR55600X.getSocket().getName());
        assertThat(cpu.getSupportedRamTypes())
                .containsOnly(cpuR55600X.getSupportedRamTypes().toArray(new CpuRamType[0]));
    }

    @Test
    void findByName_withExistentName_shouldReturnFoundEntity() {
        // given
        final var saved = underTest.save(cpuR55600X);
        underTest.save(cpuI512400F);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optCpu = underTest.findByName(cpuR55600X.getName());

        // then
        assertThat(optCpu).isPresent();
        final Cpu cpu = optCpu.get();
        assertThat(cpu.getId())
                .isEqualTo(saved.getId());
        assertThat(cpu.getName())
                .isEqualTo(cpuR55600X.getName());
        assertThat(cpu.getCoreCount())
                .isEqualTo(cpuR55600X.getCoreCount());
        assertThat(cpu.getThreadCount())
                .isEqualTo(cpuR55600X.getThreadCount());
        assertThat(cpu.getBaseClock())
                .isEqualTo(cpuR55600X.getBaseClock());
        assertThat(cpu.getMaxClock())
                .isEqualTo(cpuR55600X.getMaxClock());
        assertThat(cpu.getL3CacheSize())
                .isEqualTo(cpuR55600X.getL3CacheSize());
        assertThat(cpu.getMaxTdp())
                .isEqualTo(cpuR55600X.getMaxTdp());
        assertThat(cpu.getManufacturer().getId())
                .isEqualTo(cpuR55600X.getManufacturer().getId());
        assertThat(cpu.getManufacturer().getName())
                .isEqualTo(cpuR55600X.getManufacturer().getName());
        assertThat(cpu.getSocket().getId())
                .isEqualTo(cpuR55600X.getSocket().getId());
        assertThat(cpu.getSocket().getName())
                .isEqualTo(cpuR55600X.getSocket().getName());
        assertThat(cpu.getSupportedRamTypes())
                .containsOnly(cpuR55600X.getSupportedRamTypes().toArray(new CpuRamType[0]));
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "i3 6100";
        underTest.save(cpuR55600X);
        underTest.save(cpuI512400F);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optCpu = underTest.findByName(anotherName);

        // then
        assertThat(optCpu).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndNonExistentId_shouldReturnFoundEntity() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final var saved = underTest.save(cpuI512400F);
        underTest.save(cpuR55600X);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optCpu = underTest.findByNameAndIdNot(
                cpuI512400F.getName(),
                anotherId
        );

        // then
        assertThat(optCpu).isPresent();
        final Cpu cpu = optCpu.get();
        assertThat(cpu.getId())
                .isEqualTo(saved.getId());
        assertThat(cpu.getName())
                .isEqualTo(cpuI512400F.getName());
        assertThat(cpu.getCoreCount())
                .isEqualTo(cpuI512400F.getCoreCount());
        assertThat(cpu.getThreadCount())
                .isEqualTo(cpuI512400F.getThreadCount());
        assertThat(cpu.getBaseClock())
                .isEqualTo(cpuI512400F.getBaseClock());
        assertThat(cpu.getMaxClock())
                .isEqualTo(cpuI512400F.getMaxClock());
        assertThat(cpu.getL3CacheSize())
                .isEqualTo(cpuI512400F.getL3CacheSize());
        assertThat(cpu.getMaxTdp())
                .isEqualTo(cpuI512400F.getMaxTdp());
        assertThat(cpu.getManufacturer().getId())
                .isEqualTo(cpuI512400F.getManufacturer().getId());
        assertThat(cpu.getManufacturer().getName())
                .isEqualTo(cpuI512400F.getManufacturer().getName());
        assertThat(cpu.getSocket().getId())
                .isEqualTo(cpuI512400F.getSocket().getId());
        assertThat(cpu.getSocket().getName())
                .isEqualTo(cpuI512400F.getSocket().getName());
        assertThat(cpu.getSupportedRamTypes())
                .containsOnly(cpuI512400F.getSupportedRamTypes().toArray(new CpuRamType[0]));
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final var saved = underTest.save(cpuI512400F);
        underTest.save(cpuR55600X);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optCpu = underTest.findByNameAndIdNot(
                cpuI512400F.getName(),
                saved.getId()
        );

        // then
        assertThat(optCpu).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "i3 6100";
        underTest.save(cpuR55600X);
        underTest.save(cpuI512400F);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optCpu = underTest.findByNameAndIdNot(
                anotherName,
                anotherId
        );

        // then
        assertThat(optCpu).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "i3 6100";
        final var saved = underTest.save(cpuR55600X);
        underTest.save(cpuI512400F);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optCpu = underTest.findByNameAndIdNot(
                anotherName,
                saved.getId()
        );

        // then
        assertThat(optCpu).isNotPresent();
    }
}
