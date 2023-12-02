package ru.bukhtaev.service.crud;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.InvalidParamException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.Cpu;
import ru.bukhtaev.model.cross.CpuToRamType;
import ru.bukhtaev.model.dictionary.Manufacturer;
import ru.bukhtaev.model.dictionary.RamType;
import ru.bukhtaev.model.dictionary.Socket;
import ru.bukhtaev.repository.ICpuRepository;
import ru.bukhtaev.repository.dictionary.IManufacturerRepository;
import ru.bukhtaev.repository.dictionary.IRamTypeRepository;
import ru.bukhtaev.repository.dictionary.ISocketRepository;
import ru.bukhtaev.validation.Translator;

import java.util.*;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.Cpu.*;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.validation.MessageUtils.*;

/**
 * Реализация сервиса CRUD операций над процессорами.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class CpuCrudService implements IPagingCrudService<Cpu, UUID> {

    /**
     * Репозиторий процессоров.
     */
    private final ICpuRepository cpuRepository;

    /**
     * Репозиторий производителей.
     */
    private final IManufacturerRepository manufacturerRepository;

    /**
     * Репозиторий типов оперативной памяти.
     */
    private final IRamTypeRepository ramTypeRepository;

    /**
     * Репозиторий сокетов.
     */
    private final ISocketRepository socketRepository;

    /**
     * Сервис предоставления сообщений.
     */
    private final Translator translator;

    /**
     * Менеджер сущностей.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Конструктор.
     *
     * @param cpuRepository          репозиторий процессоров
     * @param manufacturerRepository репозиторий производителей
     * @param ramTypeRepository      репозиторий типов оперативной памяти
     * @param socketRepository       репозиторий сокетов
     * @param translator             сервис предоставления сообщений
     */
    @Autowired
    public CpuCrudService(
            final ICpuRepository cpuRepository,
            final IManufacturerRepository manufacturerRepository,
            final IRamTypeRepository ramTypeRepository,
            final ISocketRepository socketRepository,
            final Translator translator
    ) {
        this.cpuRepository = cpuRepository;
        this.manufacturerRepository = manufacturerRepository;
        this.ramTypeRepository = ramTypeRepository;
        this.socketRepository = socketRepository;
        this.translator = translator;
    }

    @Override
    public Cpu getById(final UUID id) {
        return findCpuById(id);
    }

    @Override
    public List<Cpu> getAll() {
        return cpuRepository.findAll();
    }

    @Override
    public Slice<Cpu> getAll(final Pageable pageable) {
        return cpuRepository.findAllBy(pageable);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Cpu create(final Cpu newCpu) {
        final Manufacturer manufacturer = newCpu.getManufacturer();
        if (manufacturer == null || manufacturer.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_MANUFACTURER
            );
        }

        final Socket socket = newCpu.getSocket();
        if (socket == null || socket.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_SOCKET
            );
        }

        final Set<CpuToRamType> supportedRamTypes = newCpu.getSupportedRamTypes();
        validateRamTypes(supportedRamTypes);

        final Manufacturer foundManufacturer = findManufacturerById(manufacturer.getId());
        newCpu.setManufacturer(foundManufacturer);

        final Socket foundSocket = findSocketById(socket.getId());
        newCpu.setSocket(foundSocket);

        supportedRamTypes.forEach(cpuToRamType -> {
            final UUID typeId = cpuToRamType.getRamType().getId();
            final RamType ramType = findRamTypeById(typeId);
            cpuToRamType.setRamType(ramType);
            cpuToRamType.setCpu(newCpu);
        });
        newCpu.setSupportedRamTypes(supportedRamTypes);

        cpuRepository.findByName(newCpu.getName())
                .ifPresent(cpu -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_CPU_UNIQUE,
                                    cpu.getName()
                            ),
                            FIELD_NAME
                    );
                });

        return cpuRepository.save(newCpu);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        cpuRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Cpu update(final UUID id, final Cpu changedCpu) {
        final Cpu toBeUpdated = findCpuById(id);

        final Manufacturer foundManufacturer = Optional.ofNullable(changedCpu.getManufacturer())
                .map(Manufacturer::getId)
                .map(this::findManufacturerById)
                .orElse(toBeUpdated.getManufacturer());

        final Socket foundSocket = Optional.ofNullable(changedCpu.getSocket())
                .map(Socket::getId)
                .map(this::findSocketById)
                .orElse(toBeUpdated.getSocket());

        final String name = Objects.requireNonNullElse(
                changedCpu.getName(),
                toBeUpdated.getName()
        );

        final Integer coreCount = Objects.requireNonNullElse(
                changedCpu.getCoreCount(),
                toBeUpdated.getCoreCount()
        );

        final Integer threadCount = Objects.requireNonNullElse(
                changedCpu.getThreadCount(),
                toBeUpdated.getThreadCount()
        );

        final Integer baseClock = Objects.requireNonNullElse(
                changedCpu.getBaseClock(),
                toBeUpdated.getBaseClock()
        );

        final Integer maxClock = Objects.requireNonNullElse(
                changedCpu.getMaxClock(),
                toBeUpdated.getMaxClock()
        );

        final Integer l3CacheSize = Objects.requireNonNullElse(
                changedCpu.getL3CacheSize(),
                toBeUpdated.getL3CacheSize()
        );

        final Integer maxTdp = Objects.requireNonNullElse(
                changedCpu.getMaxTdp(),
                toBeUpdated.getMaxTdp()
        );

        final Integer maxMemorySize = Objects.requireNonNullElse(
                changedCpu.getMaxMemorySize(),
                toBeUpdated.getMaxMemorySize()
        );

        final Set<CpuToRamType> supportedRamTypes = changedCpu.getSupportedRamTypes();
        if (supportedRamTypes != null) {
            validateRamTypes(supportedRamTypes);

            toBeUpdated.getSupportedRamTypes().forEach(crm -> entityManager.remove(crm));
            toBeUpdated.getSupportedRamTypes().clear();
            entityManager.flush();

            supportedRamTypes.forEach(cpuToRamType -> {
                final UUID typeId = cpuToRamType.getRamType().getId();
                final RamType ramType = findRamTypeById(typeId);
                toBeUpdated.addRamType(ramType, cpuToRamType.getMaxMemoryClock());
            });
        }

        cpuRepository.findByNameAndIdNot(name, id)
                .ifPresent(cpu -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_CPU_UNIQUE,
                                    cpu.getName()
                            ),
                            FIELD_NAME
                    );
                });

        toBeUpdated.setName(name);
        toBeUpdated.setCoreCount(coreCount);
        toBeUpdated.setThreadCount(threadCount);
        toBeUpdated.setBaseClock(baseClock);
        toBeUpdated.setMaxClock(maxClock);
        toBeUpdated.setL3CacheSize(l3CacheSize);
        toBeUpdated.setMaxTdp(maxTdp);
        toBeUpdated.setMaxMemorySize(maxMemorySize);
        toBeUpdated.setManufacturer(foundManufacturer);
        toBeUpdated.setSocket(foundSocket);

        return cpuRepository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Cpu replace(final UUID id, final Cpu newCpu) {
        final Manufacturer manufacturer = newCpu.getManufacturer();
        if (manufacturer == null || manufacturer.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_MANUFACTURER
            );
        }

        final Socket socket = newCpu.getSocket();
        if (socket == null || socket.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_SOCKET
            );
        }

        final Set<CpuToRamType> supportedRamTypes = newCpu.getSupportedRamTypes();
        validateRamTypes(supportedRamTypes);

        final Cpu existent = findCpuById(id);

        final Manufacturer foundManufacturer = findManufacturerById(manufacturer.getId());
        final Socket foundSocket = findSocketById(socket.getId());

        existent.getSupportedRamTypes().forEach(crm -> entityManager.remove(crm));
        existent.getSupportedRamTypes().clear();
        entityManager.flush();

        supportedRamTypes.forEach(cpuToRamType -> {
            final UUID typeId = cpuToRamType.getRamType().getId();
            final RamType ramType = findRamTypeById(typeId);
            existent.addRamType(ramType, cpuToRamType.getMaxMemoryClock());
        });

        cpuRepository.findByNameAndIdNot(
                newCpu.getName(),
                id
        ).ifPresent(cpu -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_CPU_UNIQUE,
                            cpu.getName()
                    ),
                    FIELD_NAME
            );
        });

        existent.setName(newCpu.getName());
        existent.setCoreCount(newCpu.getCoreCount());
        existent.setThreadCount(newCpu.getThreadCount());
        existent.setBaseClock(newCpu.getBaseClock());
        existent.setMaxClock(newCpu.getMaxClock());
        existent.setL3CacheSize(newCpu.getL3CacheSize());
        existent.setMaxTdp(newCpu.getMaxTdp());
        existent.setMaxMemorySize(newCpu.getMaxMemorySize());

        existent.setManufacturer(foundManufacturer);
        existent.setSocket(foundSocket);

        return cpuRepository.save(existent);
    }

    /**
     * Возвращает процессор с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return процессор с указанным ID, если он существует
     */
    private Cpu findCpuById(final UUID id) {
        return cpuRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_CPU_NOT_FOUND,
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
     * Возвращает тип оперативной памяти с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return тип оперативной памяти с указанным ID, если он существует
     */
    private RamType findRamTypeById(final UUID id) {
        return ramTypeRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_RAM_TYPE_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает сокет с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return сокет с указанным ID, если он существует
     */
    private Socket findSocketById(final UUID id) {
        return socketRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_SOCKET_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Проверяет переданное множество поддерживаемых процессором
     * типов оперативной памяти на валидность.
     *
     * @param ramTypes множество поддерживаемых процессором типов оперативной
     */
    private void validateRamTypes(final Set<CpuToRamType> ramTypes) {
        if (ramTypes == null
                || ramTypes.isEmpty()
                || ramTypes.stream().anyMatch(Objects::isNull)
                || ramTypes.stream().anyMatch(cpuToRamType -> cpuToRamType.getRamType() == null)
                || ramTypes.stream().anyMatch(cpuToRamType -> cpuToRamType.getRamType().getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_SUPPORTED_RAM_TYPES
            );
        }
    }
}
