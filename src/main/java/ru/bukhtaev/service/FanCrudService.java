package ru.bukhtaev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.InvalidParamException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.Fan;
import ru.bukhtaev.model.dictionary.FanPowerConnector;
import ru.bukhtaev.model.FanSize;
import ru.bukhtaev.model.dictionary.Vendor;
import ru.bukhtaev.repository.IFanPowerConnectorRepository;
import ru.bukhtaev.repository.IFanRepository;
import ru.bukhtaev.repository.IFanSizeRepository;
import ru.bukhtaev.repository.IVendorRepository;
import ru.bukhtaev.validation.Translator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.Fan.*;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.validation.MessageUtils.*;

/**
 * Реализация сервиса CRUD операций над вентиляторами.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class FanCrudService implements IPagingCrudService<Fan, UUID> {

    /**
     * Репозиторий вентиляторов.
     */
    private final IFanRepository fanRepository;

    /**
     * Репозиторий размеров вентиляторов.
     */
    private final IFanSizeRepository sizeRepository;

    /**
     * Репозиторий вендоров.
     */
    private final IVendorRepository vendorRepository;

    /**
     * Репозиторий коннекторов питания вентиляторов.
     */
    private final IFanPowerConnectorRepository powerConnectorRepository;

    /**
     * Сервис предоставления сообщений.
     */
    private final Translator translator;

    /**
     * Конструктор.
     *
     * @param fanRepository            репозиторий вентиляторов
     * @param sizeRepository           репозиторий размеров вентиляторов
     * @param vendorRepository         репозиторий вендоров
     * @param powerConnectorRepository репозиторий коннекторов питания вентиляторов
     * @param translator               сервис предоставления сообщений
     */
    @Autowired
    public FanCrudService(
            final IFanRepository fanRepository,
            final IFanSizeRepository sizeRepository,
            final IVendorRepository vendorRepository,
            final IFanPowerConnectorRepository powerConnectorRepository,
            final Translator translator
    ) {
        this.fanRepository = fanRepository;
        this.sizeRepository = sizeRepository;
        this.vendorRepository = vendorRepository;
        this.powerConnectorRepository = powerConnectorRepository;
        this.translator = translator;
    }

    @Override
    public Fan getById(final UUID id) {
        return findFanById(id);
    }

    @Override
    public List<Fan> getAll() {
        return fanRepository.findAll();
    }

    @Override
    public Slice<Fan> getAll(final Pageable pageable) {
        return fanRepository.findAllBy(pageable);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Fan create(final Fan newFan) {
        final Vendor vendor = newFan.getVendor();
        if (vendor == null || vendor.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_VENDOR
            );
        }

        final FanSize size = newFan.getSize();
        if (size == null || size.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_SIZE
            );
        }

        final FanPowerConnector powerConnector = newFan.getPowerConnector();
        if (powerConnector == null || powerConnector.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_POWER_CONNECTOR
            );
        }

        final Vendor foundVendor = findVendorById(vendor.getId());
        newFan.setVendor(foundVendor);

        final FanSize foundSize = findSizeById(size.getId());
        newFan.setSize(foundSize);

        final FanPowerConnector foundPowerConnector = findPowerConnectorById(powerConnector.getId());
        newFan.setPowerConnector(foundPowerConnector);

        fanRepository.findTheSame(
                newFan.getName(),
                newFan.getSize()
        ).ifPresent(fan -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_FAN_UNIQUE,
                            fan.getName(),
                            fan.getSize().getLength(),
                            fan.getSize().getWidth(),
                            fan.getSize().getHeight()
                    ),
                    FIELD_NAME,
                    FIELD_SIZE
            );
        });

        return fanRepository.save(newFan);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        fanRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Fan update(final UUID id, final Fan changedFan) {
        final Fan toBeUpdated = findFanById(id);

        final Vendor foundVendor = Optional.ofNullable(changedFan.getVendor())
                .map(Vendor::getId)
                .map(this::findVendorById)
                .orElse(toBeUpdated.getVendor());

        final FanSize foundSize = Optional.ofNullable(changedFan.getSize())
                .map(FanSize::getId)
                .map(this::findSizeById)
                .orElse(toBeUpdated.getSize());

        final FanPowerConnector foundPowerConnector = Optional.ofNullable(changedFan.getPowerConnector())
                .map(FanPowerConnector::getId)
                .map(this::findPowerConnectorById)
                .orElse(toBeUpdated.getPowerConnector());

        final String name = Objects.requireNonNullElse(
                changedFan.getName(),
                toBeUpdated.getName()
        );

        fanRepository.findTheSameWithAnotherId(
                name,
                foundSize,
                id
        ).ifPresent(fan -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_FAN_UNIQUE,
                            fan.getName(),
                            fan.getSize().getLength(),
                            fan.getSize().getWidth(),
                            fan.getSize().getHeight()
                    ),
                    FIELD_NAME,
                    FIELD_SIZE
            );
        });

            toBeUpdated.setName(name);
            toBeUpdated.setSize(foundSize);
            toBeUpdated.setVendor(foundVendor);
            toBeUpdated.setPowerConnector(foundPowerConnector);

        return fanRepository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Fan replace(final UUID id, final Fan newFan) {
        final Vendor vendor = newFan.getVendor();
        if (vendor == null || vendor.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_VENDOR
            );
        }

        final FanSize size = newFan.getSize();
        if (size == null || size.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_SIZE
            );
        }

        final FanPowerConnector powerConnector = newFan.getPowerConnector();
        if (powerConnector == null || powerConnector.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_POWER_CONNECTOR
            );
        }

        final Fan existent = findFanById(id);

        final Vendor foundVendor = findVendorById(vendor.getId());
        final FanSize foundSize = findSizeById(size.getId());
        final FanPowerConnector foundPowerConnector = findPowerConnectorById(powerConnector.getId());

        fanRepository.findTheSameWithAnotherId(
                newFan.getName(),
                foundSize,
                id
        ).ifPresent(fan -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_FAN_UNIQUE,
                            fan.getName(),
                            fan.getSize().getLength(),
                            fan.getSize().getWidth(),
                            fan.getSize().getHeight()
                    ),
                    FIELD_NAME,
                    FIELD_SIZE
            );
        });

        existent.setName(newFan.getName());
        existent.setVendor(foundVendor);
        existent.setSize(foundSize);
        existent.setPowerConnector(foundPowerConnector);

        return fanRepository.save(existent);
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
     * Возвращает размер вентилятора с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return размер вентилятора с указанным ID, если он существует
     */
    private FanSize findSizeById(final UUID id) {
        return sizeRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_FAN_SIZE_NOT_FOUND,
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
    private FanPowerConnector findPowerConnectorById(final UUID id) {
        return powerConnectorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_FAN_POWER_CONNECTOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
