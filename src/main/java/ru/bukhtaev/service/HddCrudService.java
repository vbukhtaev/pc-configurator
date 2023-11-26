package ru.bukhtaev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.InvalidParamException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.Hdd;
import ru.bukhtaev.model.dictionary.StorageConnector;
import ru.bukhtaev.model.dictionary.StoragePowerConnector;
import ru.bukhtaev.model.dictionary.Vendor;
import ru.bukhtaev.repository.IHddRepository;
import ru.bukhtaev.repository.dictionary.IStorageConnectorRepository;
import ru.bukhtaev.repository.dictionary.IStoragePowerConnectorRepository;
import ru.bukhtaev.repository.dictionary.IVendorRepository;
import ru.bukhtaev.validation.Translator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.Hdd.*;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.model.StorageDevice.FIELD_CAPACITY;
import static ru.bukhtaev.model.StorageDevice.FIELD_CONNECTOR;
import static ru.bukhtaev.validation.MessageUtils.*;

/**
 * Реализация сервиса CRUD операций над жесткими дисками.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class HddCrudService implements IPagingCrudService<Hdd, UUID> {

    /**
     * Репозиторий жестких дисков.
     */
    private final IHddRepository hddRepository;

    /**
     * Репозиторий вендоров.
     */
    private final IVendorRepository vendorRepository;

    /**
     * Репозиторий коннекторов подключения накопителей.
     */
    private final IStorageConnectorRepository connectorRepository;

    /**
     * Репозиторий коннекторов питания накопителей.
     */
    private final IStoragePowerConnectorRepository powerConnectorRepository;

    /**
     * Сервис предоставления сообщений.
     */
    private final Translator translator;

    /**
     * Конструктор.
     *
     * @param hddRepository            репозиторий жестких дисков
     * @param vendorRepository         репозиторий вендоров
     * @param connectorRepository      репозиторий коннекторов подключения накопителей
     * @param powerConnectorRepository репозиторий коннекторов питания накопителей
     * @param translator               сервис предоставления сообщений
     */
    @Autowired
    public HddCrudService(
            final IHddRepository hddRepository,
            final IVendorRepository vendorRepository,
            final IStorageConnectorRepository connectorRepository,
            final IStoragePowerConnectorRepository powerConnectorRepository,
            final Translator translator
    ) {
        this.hddRepository = hddRepository;
        this.vendorRepository = vendorRepository;
        this.connectorRepository = connectorRepository;
        this.powerConnectorRepository = powerConnectorRepository;
        this.translator = translator;
    }

    @Override
    public Hdd getById(final UUID id) {
        return findHddById(id);
    }

    @Override
    public List<Hdd> getAll() {
        return hddRepository.findAll();
    }

    @Override
    public Slice<Hdd> getAll(final Pageable pageable) {
        return hddRepository.findAllBy(pageable);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Hdd create(final Hdd newHdd) {
        final Vendor vendor = newHdd.getVendor();
        if (vendor == null || vendor.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_VENDOR
            );
        }

        final StorageConnector connector = newHdd.getConnector();
        if (connector == null || connector.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_CONNECTOR
            );
        }

        final StoragePowerConnector powerConnector = newHdd.getPowerConnector();
        if (powerConnector == null || powerConnector.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_POWER_CONNECTOR
            );
        }

        final Vendor foundVendor = findVendorById(vendor.getId());
        newHdd.setVendor(foundVendor);

        final StorageConnector foundConnector = findConnectorById(connector.getId());
        newHdd.setConnector(foundConnector);

        final StoragePowerConnector foundPowerConnector = findPowerConnectorById(powerConnector.getId());
        newHdd.setPowerConnector(foundPowerConnector);

        hddRepository.findTheSame(
                newHdd.getName(),
                newHdd.getCapacity(),
                newHdd.getSpindleSpeed(),
                newHdd.getCacheSize()
        ).ifPresent(hdd -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_HDD_UNIQUE,
                            hdd.getName(),
                            hdd.getCapacity(),
                            hdd.getSpindleSpeed(),
                            hdd.getCacheSize()
                    ),
                    FIELD_NAME,
                    FIELD_CAPACITY,
                    FIELD_SPINDLE_SPEED,
                    FIELD_CACHE_SIZE
            );
        });

        return hddRepository.save(newHdd);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        hddRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Hdd update(final UUID id, final Hdd changedHdd) {
        final Hdd toBeUpdated = findHddById(id);

        final Vendor foundVendor = Optional.ofNullable(changedHdd.getVendor())
                .map(Vendor::getId)
                .map(this::findVendorById)
                .orElse(toBeUpdated.getVendor());

        final StorageConnector foundConnector = Optional.ofNullable(changedHdd.getConnector())
                .map(StorageConnector::getId)
                .map(this::findConnectorById)
                .orElse(toBeUpdated.getConnector());

        final StoragePowerConnector foundPowerConnector = Optional.ofNullable(changedHdd.getPowerConnector())
                .map(StoragePowerConnector::getId)
                .map(this::findPowerConnectorById)
                .orElse(toBeUpdated.getPowerConnector());

        final String name = Objects.requireNonNullElse(
                changedHdd.getName(),
                toBeUpdated.getName()
        );

        final Integer capacity = Objects.requireNonNullElse(
                changedHdd.getCapacity(),
                toBeUpdated.getCapacity()
        );

        final Integer readingSpeed = Objects.requireNonNullElse(
                changedHdd.getReadingSpeed(),
                toBeUpdated.getReadingSpeed()
        );

        final Integer writingSpeed = Objects.requireNonNullElse(
                changedHdd.getWritingSpeed(),
                toBeUpdated.getWritingSpeed()
        );

        final Integer spindleSpeed = Objects.requireNonNullElse(
                changedHdd.getSpindleSpeed(),
                toBeUpdated.getSpindleSpeed()
        );

        final Integer cacheSize = Objects.requireNonNullElse(
                changedHdd.getCacheSize(),
                toBeUpdated.getCacheSize()
        );

        hddRepository.findTheSameWithAnotherId(
                name,
                capacity,
                spindleSpeed,
                cacheSize,
                id
        ).ifPresent(hdd -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_HDD_UNIQUE,
                            hdd.getName(),
                            hdd.getCapacity(),
                            hdd.getSpindleSpeed(),
                            hdd.getCacheSize()
                    ),
                    FIELD_NAME,
                    FIELD_CAPACITY,
                    FIELD_SPINDLE_SPEED,
                    FIELD_CACHE_SIZE
            );
        });

        toBeUpdated.setName(name);
        toBeUpdated.setCapacity(capacity);
        toBeUpdated.setReadingSpeed(readingSpeed);
        toBeUpdated.setWritingSpeed(writingSpeed);
        toBeUpdated.setSpindleSpeed(spindleSpeed);
        toBeUpdated.setCacheSize(cacheSize);
        toBeUpdated.setVendor(foundVendor);
        toBeUpdated.setConnector(foundConnector);
        toBeUpdated.setPowerConnector(foundPowerConnector);

        return hddRepository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Hdd replace(final UUID id, final Hdd newHdd) {
        final Vendor vendor = newHdd.getVendor();
        if (vendor == null || vendor.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_VENDOR
            );
        }

        final StorageConnector connector = newHdd.getConnector();
        if (connector == null || connector.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_CONNECTOR
            );
        }

        final StoragePowerConnector powerConnector = newHdd.getPowerConnector();
        if (powerConnector == null || powerConnector.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_POWER_CONNECTOR
            );
        }

        final Hdd existent = findHddById(id);

        final Vendor foundVendor = findVendorById(vendor.getId());
        final StorageConnector foundConnector = findConnectorById(connector.getId());
        final StoragePowerConnector foundPowerConnector = findPowerConnectorById(powerConnector.getId());

        hddRepository.findTheSameWithAnotherId(
                newHdd.getName(),
                newHdd.getCapacity(),
                newHdd.getSpindleSpeed(),
                newHdd.getCacheSize(),
                id
        ).ifPresent(hdd -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_HDD_UNIQUE,
                            hdd.getName(),
                            hdd.getCapacity(),
                            hdd.getSpindleSpeed(),
                            hdd.getCacheSize()
                    ),
                    FIELD_NAME,
                    FIELD_CAPACITY,
                    FIELD_SPINDLE_SPEED,
                    FIELD_CACHE_SIZE
            );
        });

        existent.setName(newHdd.getName());
        existent.setCapacity(newHdd.getCapacity());
        existent.setReadingSpeed(newHdd.getReadingSpeed());
        existent.setWritingSpeed(newHdd.getWritingSpeed());
        existent.setSpindleSpeed(newHdd.getSpindleSpeed());
        existent.setCacheSize(newHdd.getCacheSize());
        existent.setVendor(foundVendor);
        existent.setConnector(foundConnector);
        existent.setPowerConnector(foundPowerConnector);

        return hddRepository.save(existent);
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
     * Возвращает коннектор подключения накопителя с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return коннектор подключения накопителя с указанным ID, если он существует
     */
    private StorageConnector findConnectorById(final UUID id) {
        return connectorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_STORAGE_CONNECTOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает коннектор питания накопителя с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return коннектор питания накопителя с указанным ID, если он существует
     */
    private StoragePowerConnector findPowerConnectorById(final UUID id) {
        return powerConnectorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_STORAGE_POWER_CONNECTOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
