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
import ru.bukhtaev.model.Gpu;
import ru.bukhtaev.model.dictionary.Manufacturer;
import ru.bukhtaev.model.dictionary.VideoMemoryType;
import ru.bukhtaev.repository.dictionary.IManufacturerRepository;
import ru.bukhtaev.repository.dictionary.IVideoMemoryTypeRepository;
import ru.bukhtaev.util.GpuSort;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.bukhtaev.TestUtils.GPU_PAGEABLE;

/**
 * Модульные тесты репозитория графических процессоров.
 */
@DataJpaTest
class GpuRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий графических процессоров.
     */
    @Autowired
    private IGpuRepository underTest;

    /**
     * Репозиторий производителей.
     */
    @Autowired
    private IManufacturerRepository manufacturerRepository;

    /**
     * Репозиторий типов видеопамяти.
     */
    @Autowired
    private IVideoMemoryTypeRepository memoryTypeRepository;

    private Gpu gpuGtx1060;
    private Gpu gpuRx5700;

    @BeforeEach
    void setUp() {
        final Manufacturer manufacturerNvidia = manufacturerRepository.save(
                Manufacturer.builder()
                        .name("Nvidia")
                        .build()
        );
        final Manufacturer manufacturerAmd = manufacturerRepository.save(
                Manufacturer.builder()
                        .name("AMD")
                        .build()
        );

        final VideoMemoryType memoryTypeGddr5 = memoryTypeRepository.save(
                VideoMemoryType.builder()
                        .name("GDDR5")
                        .build()
        );
        final VideoMemoryType memoryTypeGddr6 = memoryTypeRepository.save(
                VideoMemoryType.builder()
                        .name("GDDR6")
                        .build()
        );

        gpuGtx1060 = Gpu.builder()
                .name("GeForce GTX 1060")
                .manufacturer(manufacturerNvidia)
                .memoryType(memoryTypeGddr5)
                .powerConsumption(120)
                .memorySize(6144)
                .build();
        gpuRx5700 = Gpu.builder()
                .name("Radeon RX 5700")
                .manufacturer(manufacturerAmd)
                .memoryType(memoryTypeGddr6)
                .powerConsumption(180)
                .memorySize(8192)
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
        manufacturerRepository.deleteAll();
        memoryTypeRepository.deleteAll();
    }

    @Test
    void findAllBy_withAllEntitiesAtPage_shouldReturnAllEntitiesAsPage() {
        // given
        underTest.save(gpuGtx1060);
        underTest.save(gpuRx5700);

        // when
        final Slice<Gpu> gpus = underTest.findAllBy(GPU_PAGEABLE);

        // then
        assertThat(gpus.getSize())
                .isEqualTo(GPU_PAGEABLE.getPageSize());
        assertThat(gpus.getNumberOfElements())
                .isEqualTo(2);

        final Gpu gpu1 = gpus.getContent().get(0);
        assertThat(gpu1.getName())
                .isEqualTo(gpuGtx1060.getName());
        assertThat(gpu1.getManufacturer().getId())
                .isEqualTo(gpuGtx1060.getManufacturer().getId());
        assertThat(gpu1.getManufacturer().getName())
                .isEqualTo(gpuGtx1060.getManufacturer().getName());
        assertThat(gpu1.getMemoryType().getId())
                .isEqualTo(gpuGtx1060.getMemoryType().getId());
        assertThat(gpu1.getMemoryType().getName())
                .isEqualTo(gpuGtx1060.getMemoryType().getName());
        assertThat(gpu1.getPowerConsumption())
                .isEqualTo(gpuGtx1060.getPowerConsumption());
        assertThat(gpu1.getMemorySize())
                .isEqualTo(gpuGtx1060.getMemorySize());

        final Gpu gpu2 = gpus.getContent().get(1);
        assertThat(gpu2.getName())
                .isEqualTo(gpuRx5700.getName());
        assertThat(gpu2.getManufacturer().getId())
                .isEqualTo(gpuRx5700.getManufacturer().getId());
        assertThat(gpu2.getManufacturer().getName())
                .isEqualTo(gpuRx5700.getManufacturer().getName());
        assertThat(gpu2.getMemoryType().getId())
                .isEqualTo(gpuRx5700.getMemoryType().getId());
        assertThat(gpu2.getMemoryType().getName())
                .isEqualTo(gpuRx5700.getMemoryType().getName());
        assertThat(gpu2.getPowerConsumption())
                .isEqualTo(gpuRx5700.getPowerConsumption());
        assertThat(gpu2.getMemorySize())
                .isEqualTo(gpuRx5700.getMemorySize());
    }

    @Test
    void findAllBy_withNotAllEntitiesAtPage_shouldReturnNotAllEntitiesAsPage() {
        // given
        underTest.save(gpuGtx1060);
        underTest.save(gpuRx5700);
        final Pageable singleElementPageable = PageRequest.of(
                0,
                1,
                GpuSort.MANUFACTURER_NAME_ASC.getSortValue()
        );

        // when
        final Slice<Gpu> gpus = underTest.findAllBy(singleElementPageable);

        // then
        assertThat(gpus.getSize())
                .isEqualTo(singleElementPageable.getPageSize());
        assertThat(gpus.getNumberOfElements())
                .isEqualTo(1);
        assertThat(gpus.getContent().get(0).getName())
                .isEqualTo(gpuRx5700.getName());
        assertThat(gpus.getContent().get(0).getManufacturer().getId())
                .isEqualTo(gpuRx5700.getManufacturer().getId());
        assertThat(gpus.getContent().get(0).getManufacturer().getName())
                .isEqualTo(gpuRx5700.getManufacturer().getName());
        assertThat(gpus.getContent().get(0).getMemoryType().getId())
                .isEqualTo(gpuRx5700.getMemoryType().getId());
        assertThat(gpus.getContent().get(0).getMemoryType().getName())
                .isEqualTo(gpuRx5700.getMemoryType().getName());
        assertThat(gpus.getContent().get(0).getPowerConsumption())
                .isEqualTo(gpuRx5700.getPowerConsumption());
        assertThat(gpus.getContent().get(0).getMemorySize())
                .isEqualTo(gpuRx5700.getMemorySize());
    }

    @Test
    void findTheSame_withExistentEntity_shouldReturnFoundEntity() {
        // given
        final Gpu saved = underTest.save(gpuRx5700);
        underTest.save(gpuGtx1060);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optGpu = underTest.findTheSame(
                gpuRx5700.getName(),
                gpuRx5700.getMemorySize(),
                gpuRx5700.getMemoryType()
        );

        // then
        assertThat(optGpu).isPresent();
        assertThat(optGpu.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optGpu.get().getName())
                .isEqualTo(gpuRx5700.getName());
        assertThat(optGpu.get().getManufacturer().getId())
                .isEqualTo(gpuRx5700.getManufacturer().getId());
        assertThat(optGpu.get().getManufacturer().getName())
                .isEqualTo(gpuRx5700.getManufacturer().getName());
        assertThat(optGpu.get().getMemoryType().getId())
                .isEqualTo(gpuRx5700.getMemoryType().getId());
        assertThat(optGpu.get().getMemoryType().getName())
                .isEqualTo(gpuRx5700.getMemoryType().getName());
        assertThat(optGpu.get().getPowerConsumption())
                .isEqualTo(gpuRx5700.getPowerConsumption());
        assertThat(optGpu.get().getMemorySize())
                .isEqualTo(gpuRx5700.getMemorySize());
    }

    @Test
    void findTheSame_withNonExistentEntity_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "Arc A770";
        underTest.save(gpuRx5700);
        underTest.save(gpuGtx1060);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optGpu = underTest.findTheSame(
                anotherName,
                gpuRx5700.getMemorySize(),
                gpuRx5700.getMemoryType()
        );

        // then
        assertThat(optGpu).isNotPresent();
    }

    @Test
    void findTheSameWithAnotherId_withExistentEntityAndNonExistentId_shouldReturnFoundEntity() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final Gpu saved = underTest.save(gpuGtx1060);
        underTest.save(gpuRx5700);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optGpu = underTest.findTheSameWithAnotherId(
                gpuGtx1060.getName(),
                gpuGtx1060.getMemorySize(),
                gpuGtx1060.getMemoryType(),
                anotherId
        );

        // then
        assertThat(optGpu).isPresent();
        assertThat(optGpu.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optGpu.get().getName())
                .isEqualTo(gpuGtx1060.getName());
        assertThat(optGpu.get().getManufacturer().getId())
                .isEqualTo(gpuGtx1060.getManufacturer().getId());
        assertThat(optGpu.get().getManufacturer().getName())
                .isEqualTo(gpuGtx1060.getManufacturer().getName());
        assertThat(optGpu.get().getMemoryType().getId())
                .isEqualTo(gpuGtx1060.getMemoryType().getId());
        assertThat(optGpu.get().getMemoryType().getName())
                .isEqualTo(gpuGtx1060.getMemoryType().getName());
        assertThat(optGpu.get().getPowerConsumption())
                .isEqualTo(gpuGtx1060.getPowerConsumption());
        assertThat(optGpu.get().getMemorySize())
                .isEqualTo(gpuGtx1060.getMemorySize());
    }

    @Test
    void findTheSameWithAnotherId_withExistentEntityAndExistentId_shouldReturnEmptyOptional() {
        // given
        final Gpu saved = underTest.save(gpuGtx1060);
        underTest.save(gpuRx5700);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optGpu = underTest.findTheSameWithAnotherId(
                gpuGtx1060.getName(),
                gpuGtx1060.getMemorySize(),
                gpuGtx1060.getMemoryType(),
                saved.getId()
        );

        // then
        assertThat(optGpu).isNotPresent();
    }

    @Test
    void findTheSameWithAnotherId_withNonExistentEntityAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "Arc A770";
        underTest.save(gpuRx5700);
        underTest.save(gpuGtx1060);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optGpu = underTest.findTheSameWithAnotherId(
                anotherName,
                gpuRx5700.getMemorySize(),
                gpuRx5700.getMemoryType(),
                anotherId
        );

        // then
        assertThat(optGpu).isNotPresent();
    }

    @Test
    void findTheSameWithAnotherId_withNonExistentEntityAndExistentId_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "Arc A770";
        final Gpu saved = underTest.save(gpuRx5700);
        underTest.save(gpuGtx1060);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optGpu = underTest.findTheSameWithAnotherId(
                anotherName,
                gpuRx5700.getMemorySize(),
                gpuRx5700.getMemoryType(),
                saved.getId()
        );

        // then
        assertThat(optGpu).isNotPresent();
    }
}