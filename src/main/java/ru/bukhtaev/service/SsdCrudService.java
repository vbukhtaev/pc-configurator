package ru.bukhtaev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.InvalidParamException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.Ssd;
import ru.bukhtaev.model.dictionary.StorageConnector;
import ru.bukhtaev.model.dictionary.StoragePowerConnector;
import ru.bukhtaev.model.dictionary.Vendor;
import ru.bukhtaev.repository.ISsdRepository;
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
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.model.Ssd.FIELD_VENDOR;
import static ru.bukhtaev.model.StorageDevice.FIELD_CAPACITY;
import static ru.bukhtaev.model.StorageDevice.FIELD_CONNECTOR;
import static ru.bukhtaev.validation.MessageUtils.*;

/**
 * Реализация сервиса CRUD операций над SSD накопителями.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class SsdCrudService implements IPagingCrudService<Ssd, UUID> {

    /**
     * Репозиторий SSD накопителей.
     */
    private final ISsdRepository ssdRepository;

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
     * @param ssdRepository            репозиторий SSD накопителей
     * @param vendorRepository         репозиторий вендоров
     * @param connectorRepository      репозиторий коннекторов подключения накопителей
     * @param powerConnectorRepository репозиторий коннекторов питания накопителей
     * @param translator               сервис предоставления сообщений
     */
    @Autowired
    public SsdCrudService(
            final ISsdRepository ssdRepository,
            final IVendorRepository vendorRepository,
            final IStorageConnectorRepository connectorRepository,
            final IStoragePowerConnectorRepository powerConnectorRepository,
            final Translator translator
    ) {
        this.ssdRepository = ssdRepository;
        this.vendorRepository = vendorRepository;
        this.connectorRepository = connectorRepository;
        this.powerConnectorRepository = powerConnectorRepository;
        this.translator = translator;
    }

    @Override
    public Ssd getById(final UUID id) {
        return findSsdById(id);
    }

    @Override
    public List<Ssd> getAll() {
        return ssdRepository.findAll();
    }

    @Override
    public Slice<Ssd> getAll(final Pageable pageable) {
        return ssdRepository.findAllBy(pageable);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Ssd create(final Ssd newSsd) {
        final Vendor vendor = newSsd.getVendor();
        if (vendor == null || vendor.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_VENDOR
            );
        }

        final StorageConnector connector = newSsd.getConnector();
        if (connector == null || connector.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_CONNECTOR
            );
        }

        final StoragePowerConnector powerConnector = newSsd.getPowerConnector();
        if (powerConnector != null && powerConnector.getId() != null) {
            final StoragePowerConnector foundPowerConnector
                    = findPowerConnectorById(powerConnector.getId());
            newSsd.setPowerConnector(foundPowerConnector);
        }

        final Vendor foundVendor = findVendorById(vendor.getId());
        newSsd.setVendor(foundVendor);

        final StorageConnector foundConnector = findConnectorById(connector.getId());
        newSsd.setConnector(foundConnector);

        ssdRepository.findTheSame(
                newSsd.getName(),
                newSsd.getCapacity()
        ).ifPresent(ssd -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_SSD_UNIQUE,
                            ssd.getName(),
                            ssd.getCapacity()
                    ),
                    FIELD_NAME,
                    FIELD_CAPACITY
            );
        });

        return ssdRepository.save(newSsd);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        ssdRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Ssd update(final UUID id, final Ssd changedSsd) {
        final Ssd toBeUpdated = findSsdById(id);

        final Vendor foundVendor = Optional.ofNullable(changedSsd.getVendor())
                .map(Vendor::getId)
                .map(this::findVendorById)
                .orElse(toBeUpdated.getVendor());

        final StorageConnector foundConnector = Optional.ofNullable(changedSsd.getConnector())
                .map(StorageConnector::getId)
                .map(this::findConnectorById)
                .orElse(toBeUpdated.getConnector());

        final StoragePowerConnector foundPowerConnector = Optional.ofNullable(changedSsd.getPowerConnector())
                .map(StoragePowerConnector::getId)
                .map(this::findPowerConnectorById)
                .orElse(toBeUpdated.getPowerConnector());

        final String name = Objects.requireNonNullElse(
                changedSsd.getName(),
                toBeUpdated.getName()
        );

        final Integer capacity = Objects.requireNonNullElse(
                changedSsd.getCapacity(),
                toBeUpdated.getCapacity()
        );

        final Integer readingSpeed = Objects.requireNonNullElse(
                changedSsd.getReadingSpeed(),
                toBeUpdated.getReadingSpeed()
        );

        final Integer writingSpeed = Objects.requireNonNullElse(
                changedSsd.getWritingSpeed(),
                toBeUpdated.getWritingSpeed()
        );

        ssdRepository.findTheSameWithAnotherId(
                name,
                capacity,
                id
        ).ifPresent(ssd -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_SSD_UNIQUE,
                            ssd.getName(),
                            ssd.getCapacity()
                    ),
                    FIELD_NAME,
                    FIELD_CAPACITY
            );
        });

        toBeUpdated.setName(name);
        toBeUpdated.setCapacity(capacity);
        toBeUpdated.setReadingSpeed(readingSpeed);
        toBeUpdated.setWritingSpeed(writingSpeed);
        toBeUpdated.setVendor(foundVendor);
        toBeUpdated.setConnector(foundConnector);
        toBeUpdated.setPowerConnector(foundPowerConnector);

        return ssdRepository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Ssd replace(final UUID id, final Ssd newSsd) {
        final Vendor vendor = newSsd.getVendor();
        if (vendor == null || vendor.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_VENDOR
            );
        }

        final StorageConnector connector = newSsd.getConnector();
        if (connector == null || connector.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_CONNECTOR
            );
        }

        StoragePowerConnector foundPowerConnector = null;
        final StoragePowerConnector powerConnector = newSsd.getPowerConnector();
        if (powerConnector != null && powerConnector.getId() != null) {
            foundPowerConnector = findPowerConnectorById(powerConnector.getId());
        }

        final Ssd existent = findSsdById(id);

        final Vendor foundVendor = findVendorById(vendor.getId());
        final StorageConnector foundConnector = findConnectorById(connector.getId());

        ssdRepository.findTheSameWithAnotherId(
                newSsd.getName(),
                newSsd.getCapacity(),
                id
        ).ifPresent(ssd -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_SSD_UNIQUE,
                            ssd.getName(),
                            ssd.getCapacity()
                    ),
                    FIELD_NAME,
                    FIELD_CAPACITY
            );
        });

        existent.setName(newSsd.getName());
        existent.setCapacity(newSsd.getCapacity());
        existent.setReadingSpeed(newSsd.getReadingSpeed());
        existent.setWritingSpeed(newSsd.getWritingSpeed());
        existent.setVendor(foundVendor);
        existent.setConnector(foundConnector);
        existent.setPowerConnector(foundPowerConnector);

        return ssdRepository.save(existent);
    }

    /**
     * Возвращает SSD накопитель с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return SSD накопитель с указанным ID, если он существует
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
