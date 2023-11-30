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
import ru.bukhtaev.model.ComputerCase;
import ru.bukhtaev.model.cross.ComputerCaseToExpansionBayFormat;
import ru.bukhtaev.model.cross.ComputerCaseToFanSize;
import ru.bukhtaev.model.dictionary.*;
import ru.bukhtaev.repository.IComputerCaseRepository;
import ru.bukhtaev.repository.dictionary.*;
import ru.bukhtaev.validation.Translator;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.ComputerCase.*;
import static ru.bukhtaev.validation.MessageUtils.*;

/**
 * Реализация сервиса CRUD операций над корпусами.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class ComputerCaseCrudService implements IPagingCrudService<ComputerCase, UUID> {

    /**
     * Репозиторий корпусов.
     */
    private final IComputerCaseRepository computerCaseRepository;

    /**
     * Репозиторий вендоров.
     */
    private final IVendorRepository vendorRepository;

    /**
     * Репозиторий форм-факторов материнских плат.
     */
    private final IMotherboardFormFactorRepository motherboardFormFactorRepository;

    /**
     * Репозиторий форм-факторов блоков питания.
     */
    private final IPsuFormFactorRepository psuFormFactorRepository;

    /**
     * Репозиторий форматов отсеков расширения.
     */
    private final IExpansionBayFormatRepository expansionBayFormatRepository;

    /**
     * Репозиторий размеров вентиляторов.
     */
    private final IFanSizeRepository fanSizeRepository;

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
     * @param computerCaseRepository          репозиторий корпусов
     * @param vendorRepository                репозиторий вендоров
     * @param motherboardFormFactorRepository репозиторий форм-факторов материнских плат
     * @param psuFormFactorRepository         репозиторий форм-факторов блоков питания
     * @param expansionBayFormatRepository    репозиторий форматов отсеков расширения
     * @param fanSizeRepository               репозиторий размеров вентиляторов
     * @param translator                      сервис предоставления сообщений
     */
    @Autowired
    public ComputerCaseCrudService(
            final IComputerCaseRepository computerCaseRepository,
            final IVendorRepository vendorRepository,
            final IMotherboardFormFactorRepository motherboardFormFactorRepository,
            final IPsuFormFactorRepository psuFormFactorRepository,
            final IExpansionBayFormatRepository expansionBayFormatRepository,
            final IFanSizeRepository fanSizeRepository,
            final Translator translator
    ) {
        this.computerCaseRepository = computerCaseRepository;
        this.vendorRepository = vendorRepository;
        this.motherboardFormFactorRepository = motherboardFormFactorRepository;
        this.psuFormFactorRepository = psuFormFactorRepository;
        this.expansionBayFormatRepository = expansionBayFormatRepository;
        this.fanSizeRepository = fanSizeRepository;
        this.translator = translator;
    }

    @Override
    public ComputerCase getById(final UUID id) {
        return findComputerCaseById(id);
    }

    @Override
    public List<ComputerCase> getAll() {
        return computerCaseRepository.findAll();
    }

    @Override
    public Slice<ComputerCase> getAll(final Pageable pageable) {
        return computerCaseRepository.findAllBy(pageable);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public ComputerCase create(final ComputerCase newCase) {
        final Vendor vendor = newCase.getVendor();
        if (vendor == null || vendor.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_VENDOR
            );
        }

        final var motherboardFormFactors = newCase.getMotherboardFormFactors();
        validateMotherboardFormFactors(motherboardFormFactors);

        final var psuFormFactors = newCase.getPsuFormFactors();
        validatePsuFormFactors(psuFormFactors);

        final var expansionBayFormats = newCase.getExpansionBayFormats();
        validateExpansionBayFormats(expansionBayFormats);

        final var fanSizes = newCase.getFanSizes();
        validateFanSizes(fanSizes);

        final Vendor foundVendor = findVendorById(vendor.getId());
        newCase.setVendor(foundVendor);

        final Set<MotherboardFormFactor> foundMotherboardFormFactors = motherboardFormFactors.stream()
                .map(formFactor -> findMotherboardFormFactorById(formFactor.getId()))
                .collect(Collectors.toSet());
        newCase.setMotherboardFormFactors(foundMotherboardFormFactors);

        final Set<PsuFormFactor> foundPsuFormFactors = psuFormFactors.stream()
                .map(formFactor -> findPsuFormFactorById(formFactor.getId()))
                .collect(Collectors.toSet());
        newCase.setPsuFormFactors(foundPsuFormFactors);

        expansionBayFormats.forEach(caseToFormat -> {
            final UUID formatId = caseToFormat.getExpansionBayFormat().getId();
            final var format = findExpansionBayFormatById(formatId);
            caseToFormat.setExpansionBayFormat(format);
            caseToFormat.setComputerCase(newCase);
        });
        newCase.setExpansionBayFormats(expansionBayFormats);

        fanSizes.forEach(caseToFanSize -> {
            final UUID fanSizeId = caseToFanSize.getFanSize().getId();
            final var fanSize = findFanSizeById(fanSizeId);
            caseToFanSize.setFanSize(fanSize);
            caseToFanSize.setComputerCase(newCase);
        });
        newCase.setFanSizes(fanSizes);

        computerCaseRepository.findByName(newCase.getName())
                .ifPresent(computerCase -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_COMPUTER_CASE_UNIQUE,
                                    computerCase.getName()
                            ),
                            FIELD_NAME
                    );
                });

        return computerCaseRepository.save(newCase);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        computerCaseRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public ComputerCase update(final UUID id, final ComputerCase changedCase) {
        final ComputerCase toBeUpdated = findComputerCaseById(id);

        final Vendor foundVendor = Optional.ofNullable(changedCase.getVendor())
                .map(Vendor::getId)
                .map(this::findVendorById)
                .orElse(toBeUpdated.getVendor());

        final String name = Objects.requireNonNullElse(
                changedCase.getName(),
                toBeUpdated.getName()
        );

        final Integer maxPsuLength = Objects.requireNonNullElse(
                changedCase.getMaxPsuLength(),
                toBeUpdated.getMaxPsuLength()
        );

        final Integer maxGraphicsCardLength = Objects.requireNonNullElse(
                changedCase.getMaxGraphicsCardLength(),
                toBeUpdated.getMaxGraphicsCardLength()
        );

        final Integer maxCoolerHeight = Objects.requireNonNullElse(
                changedCase.getMaxCoolerHeight(),
                toBeUpdated.getMaxCoolerHeight()
        );

        final var expansionBayFormats = changedCase.getExpansionBayFormats();
        if (expansionBayFormats != null) {
            validateExpansionBayFormats(expansionBayFormats);

            toBeUpdated.getExpansionBayFormats().forEach(ebf -> entityManager.remove(ebf));
            toBeUpdated.getExpansionBayFormats().clear();
            entityManager.flush();

            expansionBayFormats.forEach(caseToFormat -> {
                final UUID formatId = caseToFormat.getExpansionBayFormat().getId();
                final var format = findExpansionBayFormatById(formatId);
                toBeUpdated.addExpansionBayFormat(format, caseToFormat.getCount());
            });
        }

        final var fanSizes = changedCase.getFanSizes();
        if (fanSizes != null) {
            validateFanSizes(fanSizes);

            toBeUpdated.getFanSizes().forEach(fs -> entityManager.remove(fs));
            toBeUpdated.getFanSizes().clear();
            entityManager.flush();

            fanSizes.forEach(caseToFanSize -> {
                final UUID fanSizeId = caseToFanSize.getFanSize().getId();
                final var fanSize = findFanSizeById(fanSizeId);
                toBeUpdated.addFanSize(fanSize, caseToFanSize.getCount());
            });
        }

        Optional.ofNullable(changedCase.getMotherboardFormFactors())
                .ifPresent(formFactors -> {
                    validateMotherboardFormFactors(formFactors);

                    final Set<MotherboardFormFactor> foundFormFactors = formFactors.stream()
                            .map(formFactor -> findMotherboardFormFactorById(formFactor.getId()))
                            .collect(Collectors.toSet());
                    toBeUpdated.setMotherboardFormFactors(foundFormFactors);
                });

        Optional.ofNullable(changedCase.getPsuFormFactors())
                .ifPresent(formFactors -> {
                    validatePsuFormFactors(formFactors);

                    final Set<PsuFormFactor> foundFormFactors = formFactors.stream()
                            .map(formFactor -> findPsuFormFactorById(formFactor.getId()))
                            .collect(Collectors.toSet());
                    toBeUpdated.setPsuFormFactors(foundFormFactors);
                });

        computerCaseRepository.findByNameAndIdNot(
                changedCase.getName(),
                id
        ).ifPresent(computerCase -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_COMPUTER_CASE_UNIQUE,
                            computerCase.getName()
                    ),
                    FIELD_NAME
            );
        });

        toBeUpdated.setName(name);
        toBeUpdated.setMaxPsuLength(maxPsuLength);
        toBeUpdated.setMaxGraphicsCardLength(maxGraphicsCardLength);
        toBeUpdated.setMaxCoolerHeight(maxCoolerHeight);

        toBeUpdated.setVendor(foundVendor);

        return computerCaseRepository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public ComputerCase replace(final UUID id, final ComputerCase newCase) {
        final Vendor vendor = newCase.getVendor();
        if (vendor == null || vendor.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_VENDOR
            );
        }

        final var motherboardFormFactors = newCase.getMotherboardFormFactors();
        validateMotherboardFormFactors(motherboardFormFactors);

        final var psuFormFactors = newCase.getPsuFormFactors();
        validatePsuFormFactors(psuFormFactors);

        final var expansionBayFormats = newCase.getExpansionBayFormats();
        validateExpansionBayFormats(expansionBayFormats);

        final var fanSizes = newCase.getFanSizes();
        validateFanSizes(fanSizes);

        final ComputerCase existent = findComputerCaseById(id);

        final Vendor foundVendor = findVendorById(vendor.getId());

        existent.getExpansionBayFormats().forEach(ebf -> entityManager.remove(ebf));
        existent.getFanSizes().forEach(fs -> entityManager.remove(fs));
        existent.getExpansionBayFormats().clear();
        existent.getFanSizes().clear();
        entityManager.flush();

        final Set<MotherboardFormFactor> foundMotherboardFormFactors = motherboardFormFactors.stream()
                .map(formFactor -> findMotherboardFormFactorById(formFactor.getId()))
                .collect(Collectors.toSet());

        final Set<PsuFormFactor> foundPsuFormFactors = psuFormFactors.stream()
                .map(formFactor -> findPsuFormFactorById(formFactor.getId()))
                .collect(Collectors.toSet());

        expansionBayFormats.forEach(caseToFormat -> {
            final UUID formatId = caseToFormat.getExpansionBayFormat().getId();
            final var format = findExpansionBayFormatById(formatId);
            existent.addExpansionBayFormat(format, caseToFormat.getCount());
        });

        fanSizes.forEach(caseToFanSize -> {
            final UUID fanSizeId = caseToFanSize.getFanSize().getId();
            final var fanSize = findFanSizeById(fanSizeId);
            existent.addFanSize(fanSize, caseToFanSize.getCount());
        });

        computerCaseRepository.findByNameAndIdNot(
                newCase.getName(),
                id
        ).ifPresent(computerCase -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_COMPUTER_CASE_UNIQUE,
                            computerCase.getName()
                    ),
                    FIELD_NAME
            );
        });

        existent.setName(newCase.getName());
        existent.setMaxPsuLength(newCase.getMaxPsuLength());
        existent.setMaxGraphicsCardLength(newCase.getMaxGraphicsCardLength());
        existent.setMaxCoolerHeight(newCase.getMaxCoolerHeight());

        existent.setVendor(foundVendor);

        existent.setMotherboardFormFactors(foundMotherboardFormFactors);
        existent.setPsuFormFactors(foundPsuFormFactors);

        return computerCaseRepository.save(existent);
    }

    /**
     * Возвращает корпус с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return корпус с указанным ID, если он существует
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
     * Возвращает вендора с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return вендора с указанным ID, если он существует
     */
    private Vendor findVendorById(final UUID id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_VENDOR_NOT_FOUND,
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
    private MotherboardFormFactor findMotherboardFormFactorById(final UUID id) {
        return motherboardFormFactorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_MOTHERBOARD_FORM_FACTOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает форм-фактор блока питания с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return форм-фактор блока питания с указанным ID, если он существует
     */
    private PsuFormFactor findPsuFormFactorById(final UUID id) {
        return psuFormFactorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_PSU_FORM_FACTOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает формат отсека расширения с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return формат отсека расширения с указанным ID, если он существует
     */
    private ExpansionBayFormat findExpansionBayFormatById(final UUID id) {
        return expansionBayFormatRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_EXPANSION_BAY_FORMAT_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает размер вентилятора с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return размер вентилятора с указанным ID, если он существует
     */
    private FanSize findFanSizeById(final UUID id) {
        return fanSizeRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_FAN_SIZE_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Проверяет переданное множество поддерживаемых блоком питания
     * форм-факторов блоков питания на валидность.
     *
     * @param formFactors множество поддерживаемых блоком питания форм-факторов блоков питания
     */
    private void validatePsuFormFactors(final Set<PsuFormFactor> formFactors) {
        if (formFactors == null
                || formFactors.isEmpty()
                || formFactors.stream().anyMatch(Objects::isNull)
                || formFactors.stream().anyMatch(formFactor -> formFactor.getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_SUPPORTED_PSU_FORM_FACTORS
            );
        }
    }

    /**
     * Проверяет переданное множество поддерживаемых блоком питания
     * форм-факторов материнских плат на валидность.
     *
     * @param formFactors множество поддерживаемых блоком питания форм-факторов материнских плат
     */
    private void validateMotherboardFormFactors(final Set<MotherboardFormFactor> formFactors) {
        if (formFactors == null
                || formFactors.isEmpty()
                || formFactors.stream().anyMatch(Objects::isNull)
                || formFactors.stream().anyMatch(formFactor -> formFactor.getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_SUPPORTED_MOTHERBOARD_FORM_FACTORS
            );
        }
    }

    /**
     * Проверяет переданное множество поддерживаемых блоком питания
     * форматов отсеков расширения на валидность.
     *
     * @param formats множество поддерживаемых блоком питания форматов отсеков расширения
     */
    private void validateExpansionBayFormats(final Set<ComputerCaseToExpansionBayFormat> formats) {
        if (formats == null
                || formats.isEmpty()
                || formats.stream().anyMatch(Objects::isNull)
                || formats.stream().anyMatch(motherboardToConnector ->
                motherboardToConnector.getExpansionBayFormat() == null)
                || formats.stream().anyMatch(motherboardToConnector ->
                motherboardToConnector.getExpansionBayFormat().getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_SUPPORTED_EXPANSION_BAY_FORMATS
            );
        }
    }

    /**
     * Проверяет переданное множество поддерживаемых блоком питания
     * размеров вентиляторов на валидность.
     *
     * @param fanSizes множество поддерживаемых блоком питания размеров вентиляторов
     */
    private void validateFanSizes(final Set<ComputerCaseToFanSize> fanSizes) {
        if (fanSizes == null
                || fanSizes.isEmpty()
                || fanSizes.stream().anyMatch(Objects::isNull)
                || fanSizes.stream().anyMatch(motherboardToConnector ->
                motherboardToConnector.getFanSize() == null)
                || fanSizes.stream().anyMatch(motherboardToConnector ->
                motherboardToConnector.getFanSize().getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_SUPPORTED_FAN_SIZES
            );
        }
    }
}
