package ru.bukhtaev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.InvalidParamException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.Gpu;
import ru.bukhtaev.model.Manufacturer;
import ru.bukhtaev.model.VideoMemoryType;
import ru.bukhtaev.repository.IGpuRepository;
import ru.bukhtaev.repository.IManufacturerRepository;
import ru.bukhtaev.repository.IVideoMemoryTypeRepository;
import ru.bukhtaev.validation.Translator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.Gpu.*;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.validation.MessageUtils.*;

/**
 * Реализация сервиса CRUD операций над графическими процессорами.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class GpuCrudService implements IPagingCrudService<Gpu, UUID> {

    /**
     * Репозиторий графических процессоров.
     */
    private final IGpuRepository gpuRepository;

    /**
     * Репозиторий производителей.
     */
    private final IManufacturerRepository manufacturerRepository;

    /**
     * Репозиторий типов видеопамяти.
     */
    private final IVideoMemoryTypeRepository memoryTypeRepository;

    /**
     * Сервис предоставления сообщений.
     */
    private final Translator translator;

    /**
     * Конструктор.
     *
     * @param gpuRepository          репозиторий графических процессоров
     * @param manufacturerRepository репозиторий производителей
     * @param memoryTypeRepository   репозиторий типов видеопамяти
     * @param translator             сервис предоставления сообщений
     */
    @Autowired
    public GpuCrudService(
            final IGpuRepository gpuRepository,
            final IManufacturerRepository manufacturerRepository,
            final IVideoMemoryTypeRepository memoryTypeRepository,
            final Translator translator
    ) {
        this.gpuRepository = gpuRepository;
        this.manufacturerRepository = manufacturerRepository;
        this.memoryTypeRepository = memoryTypeRepository;
        this.translator = translator;
    }

    @Override
    public Gpu getById(final UUID id) {
        return findGpuById(id);
    }

    @Override
    public List<Gpu> getAll() {
        return gpuRepository.findAll();
    }

    @Override
    public Slice<Gpu> getAll(final Pageable pageable) {
        return gpuRepository.findAllBy(pageable);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Gpu create(final Gpu newGpu) {
        final Manufacturer manufacturer = newGpu.getManufacturer();
        if (manufacturer == null || manufacturer.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_MANUFACTURER
            );
        }

        final VideoMemoryType memoryType = newGpu.getMemoryType();
        if (memoryType == null || memoryType.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_MEMORY_TYPE
            );
        }

        final Manufacturer foundManufacturer = findManufacturerById(manufacturer.getId());
        newGpu.setManufacturer(foundManufacturer);

        final VideoMemoryType foundMemoryType = findMemoryTypeById(memoryType.getId());
        newGpu.setMemoryType(foundMemoryType);

        gpuRepository.findTheSame(
                newGpu.getName(),
                newGpu.getMemorySize(),
                newGpu.getMemoryType()
        ).ifPresent(gpu -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_GPU_UNIQUE,
                            gpu.getName(),
                            gpu.getMemorySize(),
                            gpu.getMemoryType().getName()
                    ),
                    FIELD_NAME,
                    FIELD_MEMORY_SIZE,
                    FIELD_MEMORY_TYPE
            );
        });

        return gpuRepository.save(newGpu);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        gpuRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Gpu update(final UUID id, final Gpu changedGpu) {
        final Gpu toBeUpdated = findGpuById(id);

        final Manufacturer foundManufacturer = Optional.ofNullable(changedGpu.getManufacturer())
                .map(Manufacturer::getId)
                .map(this::findManufacturerById)
                .orElse(toBeUpdated.getManufacturer());

        final VideoMemoryType foundMemoryType = Optional.ofNullable(changedGpu.getMemoryType())
                .map(VideoMemoryType::getId)
                .map(this::findMemoryTypeById)
                .orElse(toBeUpdated.getMemoryType());

        final String name = Objects.requireNonNullElse(
                changedGpu.getName(),
                toBeUpdated.getName()
        );

        final Integer memorySize = Objects.requireNonNullElse(
                changedGpu.getMemorySize(),
                toBeUpdated.getMemorySize()
        );

        final Integer powerConsumption = Objects.requireNonNullElse(
                changedGpu.getPowerConsumption(),
                toBeUpdated.getPowerConsumption()
        );

        gpuRepository.findTheSameWithAnotherId(
                name,
                memorySize,
                foundMemoryType,
                id
        ).ifPresent(gpu -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_GPU_UNIQUE,
                            gpu.getName(),
                            gpu.getMemorySize(),
                            gpu.getMemoryType().getName()
                    ),
                    FIELD_NAME,
                    FIELD_MEMORY_SIZE,
                    FIELD_MEMORY_TYPE
            );
        });

        toBeUpdated.setName(name);
        toBeUpdated.setMemorySize(memorySize);
        toBeUpdated.setPowerConsumption(powerConsumption);
        toBeUpdated.setManufacturer(foundManufacturer);
        toBeUpdated.setMemoryType(foundMemoryType);

        return gpuRepository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Gpu replace(final UUID id, final Gpu newGpu) {
        final Manufacturer manufacturer = newGpu.getManufacturer();
        if (manufacturer == null || manufacturer.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_MANUFACTURER
            );
        }

        final VideoMemoryType memoryType = newGpu.getMemoryType();
        if (memoryType == null || memoryType.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_MEMORY_TYPE
            );
        }

        final Gpu existent = findGpuById(id);

        final Manufacturer foundManufacturer = findManufacturerById(manufacturer.getId());
        final VideoMemoryType foundMemoryType = findMemoryTypeById(memoryType.getId());

        gpuRepository.findTheSameWithAnotherId(
                newGpu.getName(),
                newGpu.getMemorySize(),
                foundMemoryType,
                id
        ).ifPresent(gpu -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_GPU_UNIQUE,
                            gpu.getName(),
                            gpu.getMemorySize(),
                            gpu.getMemoryType().getName()
                    ),
                    FIELD_NAME,
                    FIELD_MEMORY_SIZE,
                    FIELD_MEMORY_TYPE
            );
        });

        existent.setName(newGpu.getName());
        existent.setMemorySize(newGpu.getMemorySize());
        existent.setPowerConsumption(newGpu.getPowerConsumption());
        existent.setManufacturer(foundManufacturer);
        existent.setMemoryType(foundMemoryType);

        return gpuRepository.save(existent);
    }

    /**
     * Возвращает графический процессор с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return графический процессор с указанным ID, если он существует
     */
    private Gpu findGpuById(final UUID id) {
        return gpuRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_GPU_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает производителя с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return производителя с указанным ID, если он существует
     */
    private Manufacturer findManufacturerById(final UUID id) {
        return manufacturerRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_MANUFACTURER_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает тип видеопамяти с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return тип видеопамяти с указанным ID, если он существует
     */
    private VideoMemoryType findMemoryTypeById(final UUID id) {
        return memoryTypeRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_VIDEO_MEMORY_TYPE_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
