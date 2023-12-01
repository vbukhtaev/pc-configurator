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
import ru.bukhtaev.model.*;
import ru.bukhtaev.model.cross.ComputerBuildToFan;
import ru.bukhtaev.model.cross.ComputerBuildToHdd;
import ru.bukhtaev.model.cross.ComputerBuildToRamModule;
import ru.bukhtaev.model.cross.ComputerBuildToSsd;
import ru.bukhtaev.repository.*;
import ru.bukhtaev.validation.Translator;

import java.util.*;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.ComputerBuild.*;
import static ru.bukhtaev.validation.MessageUtils.*;

/**
 * Реализация сервиса CRUD операций над сборками ПК.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class ComputerBuildCrudService implements IPagingCrudService<ComputerBuild, UUID> {

    /**
     * Репозиторий сборок ПК.
     */
    private final IComputerBuildRepository computerBuildRepository;

    /**
     * Репозиторий процессоров.
     */
    private final ICpuRepository cpuRepository;

    /**
     * Репозиторий блоков питания.
     */
    private final IPsuRepository psuRepository;

    /**
     * Репозиторий процессорных кулеров.
     */
    private final ICoolerRepository coolerRepository;

    /**
     * Репозиторий материнских плат.
     */
    private final IMotherboardRepository motherboardRepository;

    /**
     * Репозиторий видеокарт.
     */
    private final IGraphicsCardRepository graphicsCardRepository;

    /**
     * Репозиторий корпусов.
     */
    private final IComputerCaseRepository computerCaseRepository;

    /**
     * Репозиторий вентиляторов.
     */
    private final IFanRepository fanRepository;

    /**
     * Репозиторий модулей оперативной памяти.
     */
    private final IRamModuleRepository ramModuleRepository;

    /**
     * Репозиторий жестких дисков.
     */
    private final IHddRepository hddRepository;

    /**
     * Репозиторий SSD-накопителей.
     */
    private final ISsdRepository ssdRepository;

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
     * @param computerBuildRepository репозиторий сборок ПК
     * @param graphicsCardRepository  репозиторий видеокарт
     * @param computerCaseRepository  репозиторий корпусов
     * @param motherboardRepository   репозиторий материнских плат
     * @param ramModuleRepository     репозиторий модулей оперативной памяти
     * @param coolerRepository        репозиторий процессорных кулеров
     * @param cpuRepository           репозиторий процессоров
     * @param psuRepository           репозиторий блоков питания
     * @param fanRepository           репозиторий вентиляторов
     * @param hddRepository           репозиторий жестких дисков
     * @param ssdRepository           репозиторий SSD-накопителей
     * @param translator              сервис предоставления сообщений
     */
    @Autowired
    public ComputerBuildCrudService(
            final IComputerBuildRepository computerBuildRepository,
            final IGraphicsCardRepository graphicsCardRepository,
            final IComputerCaseRepository computerCaseRepository,
            final IMotherboardRepository motherboardRepository,
            final IRamModuleRepository ramModuleRepository,
            final ICoolerRepository coolerRepository,
            final ICpuRepository cpuRepository,
            final IPsuRepository psuRepository,
            final IFanRepository fanRepository,
            final IHddRepository hddRepository,
            final ISsdRepository ssdRepository,
            final Translator translator
    ) {
        this.computerBuildRepository = computerBuildRepository;
        this.graphicsCardRepository = graphicsCardRepository;
        this.computerCaseRepository = computerCaseRepository;
        this.motherboardRepository = motherboardRepository;
        this.ramModuleRepository = ramModuleRepository;
        this.coolerRepository = coolerRepository;
        this.cpuRepository = cpuRepository;
        this.psuRepository = psuRepository;
        this.fanRepository = fanRepository;
        this.hddRepository = hddRepository;
        this.ssdRepository = ssdRepository;
        this.translator = translator;
    }

    @Override
    public ComputerBuild getById(final UUID id) {
        return findComputerBuildById(id);
    }

    @Override
    public List<ComputerBuild> getAll() {
        return computerBuildRepository.findAll();
    }

    @Override
    public Slice<ComputerBuild> getAll(final Pageable pageable) {
        return computerBuildRepository.findAllBy(pageable);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public ComputerBuild create(final ComputerBuild newBuild) {
        final Cpu cpu = newBuild.getCpu();
        if (cpu != null && cpu.getId() != null) {
            final Cpu foundCpu = findCpuById(cpu.getId());
            newBuild.setCpu(foundCpu);
        } else {
            newBuild.setCpu(null);
        }

        final Psu psu = newBuild.getPsu();
        if (psu != null && psu.getId() != null) {
            final Psu foundPsu = findPsuById(psu.getId());
            newBuild.setPsu(foundPsu);
        } else {
            newBuild.setPsu(null);
        }

        final Cooler cooler = newBuild.getCooler();
        if (cooler != null && cooler.getId() != null) {
            final Cooler foundCooler = findCoolerById(cooler.getId());
            newBuild.setCooler(foundCooler);
        } else {
            newBuild.setCooler(null);
        }

        final Motherboard motherboard = newBuild.getMotherboard();
        if (motherboard != null && motherboard.getId() != null) {
            final Motherboard foundMotherboard = findMotherboardById(motherboard.getId());
            newBuild.setMotherboard(foundMotherboard);
        } else {
            newBuild.setMotherboard(null);
        }

        final GraphicsCard graphicsCard = newBuild.getGraphicsCard();
        if (graphicsCard != null && graphicsCard.getId() != null) {
            final GraphicsCard foundGraphicsCard = findGraphicsCardById(graphicsCard.getId());
            newBuild.setGraphicsCard(foundGraphicsCard);
        } else {
            newBuild.setGraphicsCard(null);
        }

        final ComputerCase computerCase = newBuild.getComputerCase();
        if (computerCase != null && computerCase.getId() != null) {
            final ComputerCase foundComputerCase = findComputerCaseById(computerCase.getId());
            newBuild.setComputerCase(foundComputerCase);
        } else {
            newBuild.setComputerCase(null);
        }

        final Set<ComputerBuildToFan> fans = newBuild.getFans();
        if (fans != null && !fans.isEmpty()) {
            validateFans(fans);

            fans.forEach(buildToFan -> {
                final UUID fanId = buildToFan.getFan().getId();
                final var fan = findFanById(fanId);
                buildToFan.setFan(fan);
                buildToFan.setComputerBuild(newBuild);
            });
            newBuild.setFans(fans);
        } else {
            newBuild.setFans(new HashSet<>());
        }

        final Set<ComputerBuildToRamModule> ramModules = newBuild.getRamModules();
        if (ramModules != null && !ramModules.isEmpty()) {
            validateRamModules(ramModules);

            ramModules.forEach(buildToModule -> {
                final UUID moduleId = buildToModule.getRamModule().getId();
                final var module = findRamModuleById(moduleId);
                buildToModule.setRamModule(module);
                buildToModule.setComputerBuild(newBuild);
            });
            newBuild.setRamModules(ramModules);
        } else {
            newBuild.setRamModules(new HashSet<>());
        }

        final Set<ComputerBuildToHdd> hdds = newBuild.getHdds();
        if (hdds != null && !hdds.isEmpty()) {
            validateHdds(hdds);

            hdds.forEach(buildToHdd -> {
                final UUID hddId = buildToHdd.getHdd().getId();
                final var hdd = findHddById(hddId);
                buildToHdd.setHdd(hdd);
                buildToHdd.setComputerBuild(newBuild);
            });
            newBuild.setHdds(hdds);
        } else {
            newBuild.setHdds(new HashSet<>());
        }

        final Set<ComputerBuildToSsd> ssds = newBuild.getSsds();
        if (ssds != null && !ssds.isEmpty()) {
            validateSsds(ssds);

            ssds.forEach(buildToSsd -> {
                final UUID ssdId = buildToSsd.getSsd().getId();
                final var ssd = findSsdById(ssdId);
                buildToSsd.setSsd(ssd);
                buildToSsd.setComputerBuild(newBuild);
            });
            newBuild.setSsds(ssds);
        } else {
            newBuild.setSsds(new HashSet<>());
        }

        computerBuildRepository.findByName(newBuild.getName())
                .ifPresent(computerBuild -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_COMPUTER_BUILD_UNIQUE,
                                    computerBuild.getName()
                            ),
                            FIELD_NAME
                    );
                });

        return computerBuildRepository.save(newBuild);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        computerBuildRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public ComputerBuild update(final UUID id, final ComputerBuild changedBuild) {
        final ComputerBuild toBeUpdated = findComputerBuildById(id);

        final Cpu foundCpu = Optional.ofNullable(changedBuild.getCpu())
                .map(Cpu::getId)
                .map(this::findCpuById)
                .orElse(toBeUpdated.getCpu());

        final Psu foundPsu = Optional.ofNullable(changedBuild.getPsu())
                .map(Psu::getId)
                .map(this::findPsuById)
                .orElse(toBeUpdated.getPsu());

        final Cooler foundCooler = Optional.ofNullable(changedBuild.getCooler())
                .map(Cooler::getId)
                .map(this::findCoolerById)
                .orElse(toBeUpdated.getCooler());

        final Motherboard foundMotherboard = Optional.ofNullable(changedBuild.getMotherboard())
                .map(Motherboard::getId)
                .map(this::findMotherboardById)
                .orElse(toBeUpdated.getMotherboard());

        final GraphicsCard foundGraphicsCard = Optional.ofNullable(changedBuild.getGraphicsCard())
                .map(GraphicsCard::getId)
                .map(this::findGraphicsCardById)
                .orElse(toBeUpdated.getGraphicsCard());

        final ComputerCase foundComputerCase = Optional.ofNullable(changedBuild.getComputerCase())
                .map(ComputerCase::getId)
                .map(this::findComputerCaseById)
                .orElse(toBeUpdated.getComputerCase());

        final String name = Objects.requireNonNullElse(
                changedBuild.getName(),
                toBeUpdated.getName()
        );

        final var fans = changedBuild.getFans();
        if (fans != null) {
            validateFans(fans);

            toBeUpdated.getFans().forEach(fan -> entityManager.remove(fan));
            toBeUpdated.getFans().clear();
            entityManager.flush();

            fans.forEach(buildToFan -> {
                final UUID fanId = buildToFan.getFan().getId();
                final var fan = findFanById(fanId);
                toBeUpdated.addFan(fan, buildToFan.getCount());
            });
        }

        final var ramModules = changedBuild.getRamModules();
        if (ramModules != null) {
            validateRamModules(ramModules);

            toBeUpdated.getRamModules().forEach(module -> entityManager.remove(module));
            toBeUpdated.getRamModules().clear();
            entityManager.flush();

            ramModules.forEach(buildToModule -> {
                final UUID moduleId = buildToModule.getRamModule().getId();
                final var module = findRamModuleById(moduleId);
                toBeUpdated.addRamModule(module, buildToModule.getCount());
            });
        }

        final var hdds = changedBuild.getHdds();
        if (hdds != null) {
            validateHdds(hdds);

            toBeUpdated.getHdds().forEach(hdd -> entityManager.remove(hdd));
            toBeUpdated.getHdds().clear();
            entityManager.flush();

            hdds.forEach(buildToHdd -> {
                final UUID hddId = buildToHdd.getHdd().getId();
                final var hdd = findHddById(hddId);
                toBeUpdated.addHdd(hdd, buildToHdd.getCount());
            });
        }

        final var ssds = changedBuild.getSsds();
        if (ssds != null) {
            validateSsds(ssds);

            toBeUpdated.getSsds().forEach(ssd -> entityManager.remove(ssd));
            toBeUpdated.getSsds().clear();
            entityManager.flush();

            ssds.forEach(buildToSsd -> {
                final UUID ssdId = buildToSsd.getSsd().getId();
                final var ssd = findSsdById(ssdId);
                toBeUpdated.addSsd(ssd, buildToSsd.getCount());
            });
        }

        computerBuildRepository.findByNameAndIdNot(
                changedBuild.getName(),
                id
        ).ifPresent(computerBuild -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_COMPUTER_BUILD_UNIQUE,
                            computerBuild.getName()
                    ),
                    FIELD_NAME
            );
        });

        toBeUpdated.setName(name);

        toBeUpdated.setCpu(foundCpu);
        toBeUpdated.setPsu(foundPsu);
        toBeUpdated.setCooler(foundCooler);
        toBeUpdated.setMotherboard(foundMotherboard);
        toBeUpdated.setGraphicsCard(foundGraphicsCard);
        toBeUpdated.setComputerCase(foundComputerCase);

        return computerBuildRepository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public ComputerBuild replace(final UUID id, final ComputerBuild newBuild) {
        Cpu foundCpu = null;
        final Cpu cpu = newBuild.getCpu();
        if (cpu != null && cpu.getId() != null) {
            foundCpu = findCpuById(cpu.getId());
        }

        Psu foundPsu = null;
        final Psu psu = newBuild.getPsu();
        if (psu != null && psu.getId() != null) {
            foundPsu = findPsuById(psu.getId());
        }

        Cooler foundCooler = null;
        final Cooler cooler = newBuild.getCooler();
        if (cooler != null && cooler.getId() != null) {
            foundCooler = findCoolerById(cooler.getId());
        }

        Motherboard foundMotherboard = null;
        final Motherboard motherboard = newBuild.getMotherboard();
        if (motherboard != null && motherboard.getId() != null) {
            foundMotherboard = findMotherboardById(motherboard.getId());
        }

        GraphicsCard foundGraphicsCard = null;
        final GraphicsCard graphicsCard = newBuild.getGraphicsCard();
        if (graphicsCard != null && graphicsCard.getId() != null) {
            foundGraphicsCard = findGraphicsCardById(graphicsCard.getId());
        }

        ComputerCase foundComputerCase = null;
        final ComputerCase computerCase = newBuild.getComputerCase();
        if (computerCase != null && computerCase.getId() != null) {
            foundComputerCase = findComputerCaseById(computerCase.getId());
        }

        final ComputerBuild existent = findComputerBuildById(id);

        existent.getRamModules().forEach(module -> entityManager.remove(module));
        existent.getFans().forEach(fan -> entityManager.remove(fan));
        existent.getHdds().forEach(hdd -> entityManager.remove(hdd));
        existent.getSsds().forEach(ssd -> entityManager.remove(ssd));
        existent.getRamModules().clear();
        existent.getFans().clear();
        existent.getHdds().clear();
        existent.getSsds().clear();
        entityManager.flush();

        final Set<ComputerBuildToFan> fans = newBuild.getFans();
        if (fans != null && !fans.isEmpty()) {
            validateFans(fans);

            fans.forEach(buildToFan -> {
                final UUID fanId = buildToFan.getFan().getId();
                final Fan fan = findFanById(fanId);
                existent.addFan(fan, buildToFan.getCount());
            });
        }

        final Set<ComputerBuildToRamModule> ramModules = newBuild.getRamModules();
        if (ramModules != null && !ramModules.isEmpty()) {
            validateRamModules(ramModules);

            ramModules.forEach(buildToRamModule -> {
                final UUID moduleId = buildToRamModule.getRamModule().getId();
                final RamModule module = findRamModuleById(moduleId);
                existent.addRamModule(module, buildToRamModule.getCount());
            });
        }

        final Set<ComputerBuildToHdd> hdds = newBuild.getHdds();
        if (hdds != null && !hdds.isEmpty()) {
            validateHdds(hdds);

            hdds.forEach(buildToHdd -> {
                final UUID hddId = buildToHdd.getHdd().getId();
                final Hdd hdd = findHddById(hddId);
                existent.addHdd(hdd, buildToHdd.getCount());
            });
        }

        final Set<ComputerBuildToSsd> ssds = newBuild.getSsds();
        if (ssds != null && !ssds.isEmpty()) {
            validateSsds(ssds);

            ssds.forEach(buildToSsd -> {
                final UUID ssdId = buildToSsd.getSsd().getId();
                final Ssd ssd = findSsdById(ssdId);
                existent.addSsd(ssd, buildToSsd.getCount());
            });
        }

        computerBuildRepository.findByNameAndIdNot(
                newBuild.getName(),
                id
        ).ifPresent(computerBuild -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_COMPUTER_BUILD_UNIQUE,
                            computerBuild.getName()
                    ),
                    FIELD_NAME
            );
        });

        existent.setName(newBuild.getName());

        existent.setCpu(foundCpu);
        existent.setPsu(foundPsu);
        existent.setCooler(foundCooler);
        existent.setMotherboard(foundMotherboard);
        existent.setGraphicsCard(foundGraphicsCard);
        existent.setComputerCase(foundComputerCase);

        return computerBuildRepository.save(existent);
    }

    /**
     * Возвращает сборку ПК с указанным ID, если она существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return сборку ПК с указанным ID, если она существует
     */
    private ComputerBuild findComputerBuildById(final UUID id) {
        return computerBuildRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_COMPUTER_BUILD_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
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
     * Возвращает блок питания с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return блок питания с указанным ID, если он существует
     */
    private Psu findPsuById(final UUID id) {
        return psuRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_PSU_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает процессорный кулер с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return процессорный кулер с указанным ID, если он существует
     */
    private Cooler findCoolerById(final UUID id) {
        return coolerRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_COOLER_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает материнскую с указанным ID, если она существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return материнскую с указанным ID, если она существует
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
     * Возвращает видеокарту с указанным ID, если она существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return видеокарту с указанным ID, если она существует
     */
    private GraphicsCard findGraphicsCardById(final UUID id) {
        return graphicsCardRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_GRAPHICS_CARD_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает корпус с указанным ID, если она существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return корпус с указанным ID, если она существует
     */
    private ComputerCase findComputerCaseById(final UUID id) {
        return computerCaseRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_COMPUTER_CASE_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает вентилятор с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return вентилятор с указанным ID, если он существует
     */
    private Fan findFanById(final UUID id) {
        return fanRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_FAN_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает модуль оперативной памяти с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return модуль оперативной памяти с указанным ID, если он существует
     */
    private RamModule findRamModuleById(final UUID id) {
        return ramModuleRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_RAM_MODULE_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает жесткий диск с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return жесткий диск с указанным ID, если он существует
     */
    private Hdd findHddById(final UUID id) {
        return hddRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_HDD_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает SSD-накопитель с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return SSD-накопитель с указанным ID, если он существует
     */
    private Ssd findSsdById(final UUID id) {
        return ssdRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_SSD_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Проверяет переданное множество включенных в сборку ПК вентиляторов.
     *
     * @param fans множество включенных в сборку ПК вентиляторов
     */
    private void validateFans(final Set<ComputerBuildToFan> fans) {
        if (fans == null
                || fans.isEmpty()
                || fans.stream().anyMatch(Objects::isNull)
                || fans.stream().anyMatch(computerBuildToConnector ->
                computerBuildToConnector.getFan() == null)
                || fans.stream().anyMatch(computerBuildToConnector ->
                computerBuildToConnector.getFan().getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_FANS
            );
        }
    }

    /**
     * Проверяет переданное множество включенных в сборку ПК модулей оперативной памяти.
     *
     * @param modules множество включенных в сборку ПК модулей оперативной памяти
     */
    private void validateRamModules(final Set<ComputerBuildToRamModule> modules) {
        if (modules == null
                || modules.isEmpty()
                || modules.stream().anyMatch(Objects::isNull)
                || modules.stream().anyMatch(computerBuildToConnector ->
                computerBuildToConnector.getRamModule() == null)
                || modules.stream().anyMatch(computerBuildToConnector ->
                computerBuildToConnector.getRamModule().getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_RAM_MODULES
            );
        }
    }

    /**
     * Проверяет переданное множество включенных в сборку ПК жестких дисков.
     *
     * @param hdds множество включенных в сборку ПК жестких дисков
     */
    private void validateHdds(final Set<ComputerBuildToHdd> hdds) {
        if (hdds == null
                || hdds.isEmpty()
                || hdds.stream().anyMatch(Objects::isNull)
                || hdds.stream().anyMatch(computerBuildToConnector ->
                computerBuildToConnector.getHdd() == null)
                || hdds.stream().anyMatch(computerBuildToConnector ->
                computerBuildToConnector.getHdd().getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_HDDS
            );
        }
    }

    /**
     * Проверяет переданное множество включенных в сборку ПК SSD-накопителей.
     *
     * @param ssds множество включенных в сборку ПК SSD-накопителей
     */
    private void validateSsds(final Set<ComputerBuildToSsd> ssds) {
        if (ssds == null
                || ssds.isEmpty()
                || ssds.stream().anyMatch(Objects::isNull)
                || ssds.stream().anyMatch(computerBuildToConnector ->
                computerBuildToConnector.getSsd() == null)
                || ssds.stream().anyMatch(computerBuildToConnector ->
                computerBuildToConnector.getSsd().getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_SSDS
            );
        }
    }
}
