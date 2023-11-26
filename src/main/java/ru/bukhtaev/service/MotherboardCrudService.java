package ru.bukhtaev.service;

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
import ru.bukhtaev.model.Chipset;
import ru.bukhtaev.model.Design;
import ru.bukhtaev.model.Motherboard;
import ru.bukhtaev.model.cross.MotherboardToFanPowerConnector;
import ru.bukhtaev.model.cross.MotherboardToStorageConnector;
import ru.bukhtaev.model.dictionary.*;
import ru.bukhtaev.repository.IChipsetRepository;
import ru.bukhtaev.repository.IDesignRepository;
import ru.bukhtaev.repository.IMotherboardRepository;
import ru.bukhtaev.repository.dictionary.*;
import ru.bukhtaev.validation.Translator;

import java.util.*;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.Motherboard.*;
import static ru.bukhtaev.validation.MessageUtils.*;

/**
 * Реализация сервиса CRUD операций над материнскими платами.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class MotherboardCrudService implements IPagingCrudService<Motherboard, UUID> {

    /**
     * Репозиторий материнских плат.
     */
    private final IMotherboardRepository motherboardRepository;

    /**
     * Репозиторий вариантов исполнения.
     */
    private final IDesignRepository designRepository;

    /**
     * Репозиторий чипсетов.
     */
    private final IChipsetRepository chipsetRepository;

    /**
     * Репозиторий типов оперативной памяти.
     */
    private final IRamTypeRepository ramTypeRepository;

    /**
     * Репозиторий форм-факторов материнских плат.
     */
    private final IMotherboardFormFactorRepository formFactorRepository;

    /**
     * Репозиторий коннекторов питания процессора.
     */
    private final ICpuPowerConnectorRepository cpuPowerConnectorRepository;

    /**
     * Репозиторий основных коннекторов питания.
     */
    private final IMainPowerConnectorRepository mainPowerConnectorRepository;

    /**
     * Репозиторий коннекторов питания вентиляторов.
     */
    private final IFanPowerConnectorRepository fanPowerConnectorRepository;

    /**
     * Репозиторий версий коннектора PCI_Express.
     */
    private final IPciExpressConnectorVersionRepository pciExpressConnectorVersionRepository;

    /**
     * Репозиторий коннекторов подключения накопителей.
     */
    private final IStorageConnectorRepository storageConnectorRepository;

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
     * @param motherboardRepository                репозиторий материнских плат
     * @param designRepository                     репозиторий вариантов исполнения
     * @param chipsetRepository                    репозиторий чипсетов
     * @param ramTypeRepository                    репозиторий типов оперативной памяти
     * @param formFactorRepository                 репозиторий форм-факторов материнских плат
     * @param cpuPowerConnectorRepository          репозиторий коннекторов питания процессора
     * @param mainPowerConnectorRepository         репозиторий основных коннекторов питания
     * @param fanPowerConnectorRepository          репозиторий коннекторов питания вентиляторов
     * @param pciExpressConnectorVersionRepository репозиторий версий коннектора PCI_Express
     * @param storageConnectorRepository           репозиторий коннекторов подключения накопителей
     * @param translator                           сервис предоставления сообщений
     */
    @Autowired
    public MotherboardCrudService(
            final IDesignRepository designRepository,
            final IChipsetRepository chipsetRepository,
            final IRamTypeRepository ramTypeRepository,
            final IMotherboardRepository motherboardRepository,
            final IMotherboardFormFactorRepository formFactorRepository,
            final IStorageConnectorRepository storageConnectorRepository,
            final IFanPowerConnectorRepository fanPowerConnectorRepository,
            final ICpuPowerConnectorRepository cpuPowerConnectorRepository,
            final IMainPowerConnectorRepository mainPowerConnectorRepository,
            final IPciExpressConnectorVersionRepository pciExpressConnectorVersionRepository,
            final Translator translator
    ) {
        this.designRepository = designRepository;
        this.chipsetRepository = chipsetRepository;
        this.ramTypeRepository = ramTypeRepository;
        this.formFactorRepository = formFactorRepository;
        this.motherboardRepository = motherboardRepository;
        this.storageConnectorRepository = storageConnectorRepository;
        this.fanPowerConnectorRepository = fanPowerConnectorRepository;
        this.cpuPowerConnectorRepository = cpuPowerConnectorRepository;
        this.mainPowerConnectorRepository = mainPowerConnectorRepository;
        this.pciExpressConnectorVersionRepository = pciExpressConnectorVersionRepository;
        this.translator = translator;
    }

    @Override
    public Motherboard getById(final UUID id) {
        return findMotherboardById(id);
    }

    @Override
    public List<Motherboard> getAll() {
        return motherboardRepository.findAll();
    }

    @Override
    public Slice<Motherboard> getAll(final Pageable pageable) {
        return motherboardRepository.findAllBy(pageable);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Motherboard create(final Motherboard newMotherboard) {
        final Design design = newMotherboard.getDesign();
        if (design == null || design.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_DESIGN
            );
        }

        final Chipset chipset = newMotherboard.getChipset();
        if (chipset == null || chipset.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_CHIPSET
            );
        }

        final RamType ramType = newMotherboard.getRamType();
        if (ramType == null || ramType.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_RAM_TYPE
            );
        }

        final MotherboardFormFactor formFactor = newMotherboard.getFormFactor();
        if (formFactor == null || formFactor.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_FORM_FACTOR
            );
        }

        final CpuPowerConnector cpuPowerConnector = newMotherboard.getCpuPowerConnector();
        if (cpuPowerConnector == null || cpuPowerConnector.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_CPU_POWER_CONNECTOR
            );
        }

        final MainPowerConnector mainPowerConnector = newMotherboard.getMainPowerConnector();
        if (mainPowerConnector == null || mainPowerConnector.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_MAIN_POWER_CONNECTOR
            );
        }

        final FanPowerConnector coolerPowerConnector = newMotherboard.getCoolerPowerConnector();
        if (coolerPowerConnector == null || coolerPowerConnector.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_COOLER_POWER_CONNECTOR
            );
        }

        final PciExpressConnectorVersion version = newMotherboard.getPciExpressConnectorVersion();
        if (version == null || version.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_PCI_EXPRESS_CONNECTOR_VERSION
            );
        }

        final Set<MotherboardToFanPowerConnector> fanPowerConnectors = newMotherboard.getFanPowerConnectors();
        validateFanPowerConnectors(fanPowerConnectors);

        final Set<MotherboardToStorageConnector> storageConnectors = newMotherboard.getStorageConnectors();
        validateStorageConnectors(storageConnectors);

        final Design foundDesign = findDesignById(design.getId());
        newMotherboard.setDesign(foundDesign);

        final Chipset foundChipset = findChipsetById(chipset.getId());
        newMotherboard.setChipset(foundChipset);

        final RamType foundRamType = findRamTypeById(ramType.getId());
        newMotherboard.setRamType(foundRamType);

        final var foundMotherboardFormFactor = findFormFactorById(formFactor.getId());
        newMotherboard.setFormFactor(foundMotherboardFormFactor);

        final var foundCpuPowerConnector = findCpuPowerConnectorById(cpuPowerConnector.getId());
        newMotherboard.setCpuPowerConnector(foundCpuPowerConnector);

        final var foundMainPowerConnector = findMainPowerConnectorById(mainPowerConnector.getId());
        newMotherboard.setMainPowerConnector(foundMainPowerConnector);

        final var foundCoolerPowerConnector = findFanPowerConnectorById(coolerPowerConnector.getId());
        newMotherboard.setCoolerPowerConnector(foundCoolerPowerConnector);

        final var foundVersion = findPciExpressConnectorVersionById(version.getId());
        newMotherboard.setPciExpressConnectorVersion(foundVersion);

        fanPowerConnectors.forEach(motherboardToConnector -> {
            final UUID connectorId = motherboardToConnector.getFanPowerConnector().getId();
            final var connector = findFanPowerConnectorById(connectorId);
            motherboardToConnector.setFanPowerConnector(connector);
            motherboardToConnector.setMotherboard(newMotherboard);
        });
        newMotherboard.setFanPowerConnectors(fanPowerConnectors);

        storageConnectors.forEach(motherboardToConnector -> {
            final UUID connectorId = motherboardToConnector.getStorageConnector().getId();
            final var connector = findStorageConnectorById(connectorId);
            motherboardToConnector.setStorageConnector(connector);
            motherboardToConnector.setMotherboard(newMotherboard);
        });
        newMotherboard.setStorageConnectors(storageConnectors);

        motherboardRepository.findTheSame(
                newMotherboard.getName(),
                foundDesign,
                foundChipset,
                foundRamType
        ).ifPresent(motherboard -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_MOTHERBOARD_UNIQUE,
                            motherboard.getName(),
                            motherboard.getDesign().getName(),
                            motherboard.getChipset().getName(),
                            motherboard.getRamType().getName()
                    ),
                    FIELD_NAME,
                    FIELD_DESIGN,
                    FIELD_CHIPSET,
                    FIELD_RAM_TYPE
            );
        });

        return motherboardRepository.save(newMotherboard);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        motherboardRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Motherboard update(final UUID id, final Motherboard changedMotherboard) {
        final Motherboard toBeUpdated = findMotherboardById(id);

        final Design foundDesign = Optional.ofNullable(changedMotherboard.getDesign())
                .map(Design::getId)
                .map(this::findDesignById)
                .orElse(toBeUpdated.getDesign());

        final Chipset foundChipset = Optional.ofNullable(changedMotherboard.getChipset())
                .map(Chipset::getId)
                .map(this::findChipsetById)
                .orElse(toBeUpdated.getChipset());

        final RamType foundRamType = Optional.ofNullable(changedMotherboard.getRamType())
                .map(RamType::getId)
                .map(this::findRamTypeById)
                .orElse(toBeUpdated.getRamType());

        final MotherboardFormFactor foundFormFactor = Optional.ofNullable(changedMotherboard.getFormFactor())
                .map(MotherboardFormFactor::getId)
                .map(this::findFormFactorById)
                .orElse(toBeUpdated.getFormFactor());

        final CpuPowerConnector foundCpuPowerConnector = Optional.ofNullable(changedMotherboard.getCpuPowerConnector())
                .map(CpuPowerConnector::getId)
                .map(this::findCpuPowerConnectorById)
                .orElse(toBeUpdated.getCpuPowerConnector());

        final MainPowerConnector foundMainPowerConnector = Optional.ofNullable(changedMotherboard.getMainPowerConnector())
                .map(MainPowerConnector::getId)
                .map(this::findMainPowerConnectorById)
                .orElse(toBeUpdated.getMainPowerConnector());

        final FanPowerConnector foundCoolerPowerConnector = Optional.ofNullable(changedMotherboard.getCoolerPowerConnector())
                .map(FanPowerConnector::getId)
                .map(this::findFanPowerConnectorById)
                .orElse(toBeUpdated.getCoolerPowerConnector());

        final var foundVersion = Optional.ofNullable(changedMotherboard.getPciExpressConnectorVersion())
                .map(PciExpressConnectorVersion::getId)
                .map(this::findPciExpressConnectorVersionById)
                .orElse(toBeUpdated.getPciExpressConnectorVersion());

        final String name = Objects.requireNonNullElse(
                changedMotherboard.getName(),
                toBeUpdated.getName()
        );

        final Integer maxMemoryClock = Objects.requireNonNullElse(
                changedMotherboard.getMaxMemoryClock(),
                toBeUpdated.getMaxMemoryClock()
        );

        final Integer maxMemoryOverClock = Objects.requireNonNullElse(
                changedMotherboard.getMaxMemoryOverClock(),
                toBeUpdated.getMaxMemoryOverClock()
        );

        final Integer maxMemorySize = Objects.requireNonNullElse(
                changedMotherboard.getMaxMemorySize(),
                toBeUpdated.getMaxMemorySize()
        );

        final Integer slotsCount = Objects.requireNonNullElse(
                changedMotherboard.getSlotsCount(),
                toBeUpdated.getSlotsCount()
        );

        final var fanPowerConnectors = changedMotherboard.getFanPowerConnectors();
        if (fanPowerConnectors != null) {
            validateFanPowerConnectors(fanPowerConnectors);

            toBeUpdated.getFanPowerConnectors().forEach(pc -> entityManager.remove(pc));
            toBeUpdated.getFanPowerConnectors().clear();
            entityManager.flush();

            fanPowerConnectors.forEach(motherboardToConnector -> {
                final UUID connectorId = motherboardToConnector.getFanPowerConnector().getId();
                final var connector = findFanPowerConnectorById(connectorId);
                toBeUpdated.addFanPowerConnector(connector, motherboardToConnector.getCount());
            });
        }

        final var storageConnectors = changedMotherboard.getStorageConnectors();
        if (storageConnectors != null) {
            validateStorageConnectors(storageConnectors);

            toBeUpdated.getStorageConnectors().forEach(sc -> entityManager.remove(sc));
            toBeUpdated.getStorageConnectors().clear();
            entityManager.flush();

            storageConnectors.forEach(motherboardToConnector -> {
                final UUID connectorId = motherboardToConnector.getStorageConnector().getId();
                final var connector = findStorageConnectorById(connectorId);
                toBeUpdated.addStorageConnector(connector, motherboardToConnector.getCount());
            });
        }

        motherboardRepository.findTheSameWithAnotherId(
                changedMotherboard.getName(),
                foundDesign,
                foundChipset,
                foundRamType,
                id
        ).ifPresent(motherboard -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_MOTHERBOARD_UNIQUE,
                            motherboard.getName(),
                            motherboard.getDesign().getName(),
                            motherboard.getChipset().getName(),
                            motherboard.getRamType().getName()
                    ),
                    FIELD_NAME,
                    FIELD_DESIGN,
                    FIELD_CHIPSET,
                    FIELD_RAM_TYPE
            );
        });

        toBeUpdated.setName(name);
        toBeUpdated.setMaxMemoryClock(maxMemoryClock);
        toBeUpdated.setMaxMemoryOverClock(maxMemoryOverClock);
        toBeUpdated.setMaxMemorySize(maxMemorySize);
        toBeUpdated.setSlotsCount(slotsCount);

        toBeUpdated.setDesign(foundDesign);
        toBeUpdated.setChipset(foundChipset);
        toBeUpdated.setRamType(foundRamType);
        toBeUpdated.setFormFactor(foundFormFactor);
        toBeUpdated.setCpuPowerConnector(foundCpuPowerConnector);
        toBeUpdated.setMainPowerConnector(foundMainPowerConnector);
        toBeUpdated.setCoolerPowerConnector(foundCoolerPowerConnector);
        toBeUpdated.setPciExpressConnectorVersion(foundVersion);

        return motherboardRepository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Motherboard replace(final UUID id, final Motherboard newMotherboard) {
        final Design design = newMotherboard.getDesign();
        if (design == null || design.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_DESIGN
            );
        }

        final Chipset chipset = newMotherboard.getChipset();
        if (chipset == null || chipset.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_CHIPSET
            );
        }

        final RamType ramType = newMotherboard.getRamType();
        if (ramType == null || ramType.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_RAM_TYPE
            );
        }

        final MotherboardFormFactor formFactor = newMotherboard.getFormFactor();
        if (formFactor == null || formFactor.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_FORM_FACTOR
            );
        }

        final CpuPowerConnector cpuPowerConnector = newMotherboard.getCpuPowerConnector();
        if (cpuPowerConnector == null || cpuPowerConnector.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_CPU_POWER_CONNECTOR
            );
        }

        final MainPowerConnector mainPowerConnector = newMotherboard.getMainPowerConnector();
        if (mainPowerConnector == null || mainPowerConnector.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_MAIN_POWER_CONNECTOR
            );
        }

        final FanPowerConnector coolerPowerConnector = newMotherboard.getCoolerPowerConnector();
        if (coolerPowerConnector == null || coolerPowerConnector.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_COOLER_POWER_CONNECTOR
            );
        }

        final PciExpressConnectorVersion version = newMotherboard.getPciExpressConnectorVersion();
        if (version == null || version.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_PCI_EXPRESS_CONNECTOR_VERSION
            );
        }

        final Set<MotherboardToFanPowerConnector> fanPowerConnectors = newMotherboard.getFanPowerConnectors();
        validateFanPowerConnectors(fanPowerConnectors);

        final Set<MotherboardToStorageConnector> storageConnectors = newMotherboard.getStorageConnectors();
        validateStorageConnectors(storageConnectors);

        final Motherboard existent = findMotherboardById(id);

        final Design foundDesign = findDesignById(design.getId());
        final Chipset foundChipset = findChipsetById(chipset.getId());
        final RamType foundRamType = findRamTypeById(ramType.getId());
        final var foundFormFactor = findFormFactorById(formFactor.getId());
        final var foundCpuPowerConnector = findCpuPowerConnectorById(cpuPowerConnector.getId());
        final var foundMainPowerConnector = findMainPowerConnectorById(mainPowerConnector.getId());
        final var foundCoolerPowerConnector = findFanPowerConnectorById(coolerPowerConnector.getId());
        final var foundVersion = findPciExpressConnectorVersionById(version.getId());

        existent.getFanPowerConnectors().forEach(pc -> entityManager.remove(pc));
        existent.getStorageConnectors().forEach(sc -> entityManager.remove(sc));
        existent.getFanPowerConnectors().clear();
        existent.getStorageConnectors().clear();
        entityManager.flush();

        fanPowerConnectors.forEach(motherboardToConnector -> {
            final UUID connectorId = motherboardToConnector.getFanPowerConnector().getId();
            final var connector = findFanPowerConnectorById(connectorId);
            existent.addFanPowerConnector(connector, motherboardToConnector.getCount());
        });

        storageConnectors.forEach(motherboardToConnector -> {
            final UUID connectorId = motherboardToConnector.getStorageConnector().getId();
            final var connector = findStorageConnectorById(connectorId);
            existent.addStorageConnector(connector, motherboardToConnector.getCount());
        });

        motherboardRepository.findTheSameWithAnotherId(
                newMotherboard.getName(),
                foundDesign,
                foundChipset,
                foundRamType,
                id
        ).ifPresent(motherboard -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_MOTHERBOARD_UNIQUE,
                            motherboard.getName(),
                            motherboard.getDesign().getName(),
                            motherboard.getChipset().getName(),
                            motherboard.getRamType().getName()
                    ),
                    FIELD_NAME,
                    FIELD_DESIGN,
                    FIELD_CHIPSET,
                    FIELD_RAM_TYPE
            );
        });

        existent.setName(newMotherboard.getName());
        existent.setMaxMemoryClock(newMotherboard.getMaxMemoryClock());
        existent.setMaxMemoryOverClock(newMotherboard.getMaxMemoryOverClock());
        existent.setMaxMemorySize(newMotherboard.getMaxMemorySize());
        existent.setSlotsCount(newMotherboard.getSlotsCount());

        existent.setDesign(foundDesign);
        existent.setChipset(foundChipset);
        existent.setRamType(foundRamType);
        existent.setFormFactor(foundFormFactor);
        existent.setCpuPowerConnector(foundCpuPowerConnector);
        existent.setMainPowerConnector(foundMainPowerConnector);
        existent.setCoolerPowerConnector(foundCoolerPowerConnector);
        existent.setPciExpressConnectorVersion(foundVersion);

        return motherboardRepository.save(existent);
    }

    /**
     * Возвращает материнскую плату с указанным ID, если она существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return материнскую плату с указанным ID, если она существует
     */
    private Motherboard findMotherboardById(final UUID id) {
        return motherboardRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_MOTHERBOARD_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает вариант исполнения с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return вариант исполнения с указанным ID, если он существует
     */
    private Design findDesignById(final UUID id) {
        return designRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_DESIGN_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает чипсет с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return чипсет с указанным ID, если он существует
     */
    private Chipset findChipsetById(final UUID id) {
        return chipsetRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_CHIPSET_NOT_FOUND,
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
     * Возвращает форм-фактор материнской платы с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return форм-фактор материнской платы с указанным ID, если он существует
     */
    private MotherboardFormFactor findFormFactorById(final UUID id) {
        return formFactorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_MOTHERBOARD_FORM_FACTOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает коннектор питания процессора с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return коннектор питания процессора с указанным ID, если он существует
     */
    private CpuPowerConnector findCpuPowerConnectorById(final UUID id) {
        return cpuPowerConnectorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_CPU_POWER_CONNECTOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает основной коннектор питания с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return основной коннектор питания с указанным ID, если он существует
     */
    private MainPowerConnector findMainPowerConnectorById(final UUID id) {
        return mainPowerConnectorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_MAIN_POWER_CONNECTOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает коннектор питания вентилятора с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return коннектор питания вентилятора с указанным ID, если он существует
     */
    private FanPowerConnector findFanPowerConnectorById(final UUID id) {
        return fanPowerConnectorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_FAN_POWER_CONNECTOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает версию коннектора PCI_Express с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return версию коннектора PCI_Express с указанным ID, если он существует
     */
    private PciExpressConnectorVersion findPciExpressConnectorVersionById(final UUID id) {
        return pciExpressConnectorVersionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_PCI_EXPRESS_CONNECTOR_VERSION_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает коннектор подключения накопителя с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return коннектор подключения накопителя с указанным ID, если он существует
     */
    private StorageConnector findStorageConnectorById(final UUID id) {
        return storageConnectorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_STORAGE_CONNECTOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Проверяет переданное множество имеющихся
     * у материнской платы коннекторов питания вентиляторов на валидность.
     *
     * @param connectors множество имеющихся у материнской платы коннекторов питания вентиляторов
     */
    private void validateFanPowerConnectors(final Set<MotherboardToFanPowerConnector> connectors) {
        if (connectors == null
                || connectors.isEmpty()
                || connectors.stream().anyMatch(Objects::isNull)
                || connectors.stream().anyMatch(motherboardToConnector ->
                motherboardToConnector.getFanPowerConnector() == null)
                || connectors.stream().anyMatch(motherboardToConnector ->
                motherboardToConnector.getFanPowerConnector().getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_FAN_POWER_CONNECTORS
            );
        }
    }

    /**
     * Проверяет переданное множество имеющихся
     * у материнской платы коннекторов подключения накопителей на валидность.
     *
     * @param connectors множество имеющихся у материнской платы коннекторов подключения накопителей
     */
    private void validateStorageConnectors(final Set<MotherboardToStorageConnector> connectors) {
        if (connectors == null
                || connectors.isEmpty()
                || connectors.stream().anyMatch(Objects::isNull)
                || connectors.stream().anyMatch(motherboardToConnector ->
                motherboardToConnector.getStorageConnector() == null)
                || connectors.stream().anyMatch(motherboardToConnector ->
                motherboardToConnector.getStorageConnector().getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_STORAGE_CONNECTORS
            );
        }
    }
}
