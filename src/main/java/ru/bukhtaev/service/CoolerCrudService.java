package ru.bukhtaev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.InvalidParamException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.*;
import ru.bukhtaev.model.dictionary.FanPowerConnector;
import ru.bukhtaev.model.dictionary.Socket;
import ru.bukhtaev.model.dictionary.Vendor;
import ru.bukhtaev.repository.*;
import ru.bukhtaev.validation.Translator;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.Cooler.*;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.validation.MessageUtils.*;

/**
 * Реализация сервиса CRUD операций над процессорными кулерами.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class CoolerCrudService implements IPagingCrudService<Cooler, UUID> {

    /**
     * Репозиторий процессорных кулеров.
     */
    private final ICoolerRepository coolerRepository;

    /**
     * Репозиторий вендоров.
     */
    private final IVendorRepository vendorRepository;

    /**
     * Репозиторий размеров вентиляторов.
     */
    private final IFanSizeRepository fanSizeRepository;

    /**
     * Репозиторий коннекторов питания вентиляторов.
     */
    private final IFanPowerConnectorRepository powerConnectorRepository;

    /**
     * Репозиторий сокетов.
     */
    private final ISocketRepository socketRepository;

    /**
     * Сервис предоставления сообщений.
     */
    private final Translator translator;

    /**
     * Конструктор.
     *
     * @param coolerRepository         репозиторий процессорных кулеров
     * @param vendorRepository         репозиторий вендоров
     * @param fanSizeRepository        репозиторий размеров вентиляторов
     * @param powerConnectorRepository репозиторий коннекторов питания вентиляторов
     * @param socketRepository         репозиторий сокетов
     * @param translator               сервис предоставления сообщений
     */
    @Autowired
    public CoolerCrudService(
            final ICoolerRepository coolerRepository,
            final IVendorRepository vendorRepository,
            final IFanSizeRepository fanSizeRepository,
            final IFanPowerConnectorRepository powerConnectorRepository,
            final ISocketRepository socketRepository,
            final Translator translator
    ) {
        this.coolerRepository = coolerRepository;
        this.vendorRepository = vendorRepository;
        this.fanSizeRepository = fanSizeRepository;
        this.powerConnectorRepository = powerConnectorRepository;
        this.socketRepository = socketRepository;
        this.translator = translator;
    }

    @Override
    public Cooler getById(final UUID id) {
        return findCoolerById(id);
    }

    @Override
    public List<Cooler> getAll() {
        return coolerRepository.findAll();
    }

    @Override
    public Slice<Cooler> getAll(final Pageable pageable) {
        return coolerRepository.findAllBy(pageable);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Cooler create(final Cooler newCooler) {
        final Vendor vendor = newCooler.getVendor();
        if (vendor == null || vendor.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_VENDOR
            );
        }

        final FanSize fanSize = newCooler.getFanSize();
        if (fanSize == null || fanSize.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_FAN_SIZE
            );
        }

        final FanPowerConnector powerConnector = newCooler.getPowerConnector();
        if (powerConnector == null || powerConnector.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_POWER_CONNECTOR
            );
        }

        final Set<Socket> supportedSockets = newCooler.getSupportedSockets();
        if (supportedSockets == null
                || supportedSockets.isEmpty()
                || supportedSockets.stream().anyMatch(Objects::isNull)
                || supportedSockets.stream().anyMatch(socket -> socket.getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_SUPPORTED_SOCKETS
            );
        }

        final Vendor foundVendor = findVendorById(vendor.getId());
        newCooler.setVendor(foundVendor);

        final FanSize foundFanSize = findFanSizeById(fanSize.getId());
        newCooler.setFanSize(foundFanSize);

        final FanPowerConnector foundPowerConnector = findPowerConnectorById(powerConnector.getId());
        newCooler.setPowerConnector(foundPowerConnector);

        final Set<Socket> foundSupportedSockets = supportedSockets.stream()
                .map(socket -> findSocketById(socket.getId()))
                .collect(Collectors.toSet());
        newCooler.setSupportedSockets(foundSupportedSockets);

        coolerRepository.findByName(newCooler.getName())
                .ifPresent(cooler -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_COOLER_UNIQUE,
                                    cooler.getName()
                            ),
                            FIELD_NAME
                    );
                });

        return coolerRepository.save(newCooler);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        coolerRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Cooler update(final UUID id, final Cooler changedCooler) {
        final Cooler toBeUpdated = findCoolerById(id);

        final Vendor foundVendor = Optional.ofNullable(changedCooler.getVendor())
                .map(Vendor::getId)
                .map(this::findVendorById)
                .orElse(toBeUpdated.getVendor());

        final FanSize foundFanSize = Optional.ofNullable(changedCooler.getFanSize())
                .map(FanSize::getId)
                .map(this::findFanSizeById)
                .orElse(toBeUpdated.getFanSize());

        final FanPowerConnector foundPowerConnector = Optional.ofNullable(changedCooler.getPowerConnector())
                .map(FanPowerConnector::getId)
                .map(this::findPowerConnectorById)
                .orElse(toBeUpdated.getPowerConnector());

        final String name = Objects.requireNonNullElse(
                changedCooler.getName(),
                toBeUpdated.getName()
        );

        final Integer height = Objects.requireNonNullElse(
                changedCooler.getHeight(),
                toBeUpdated.getHeight()
        );

        final Integer powerDissipation = Objects.requireNonNullElse(
                changedCooler.getPowerDissipation(),
                toBeUpdated.getPowerDissipation()
        );

        Optional.ofNullable(changedCooler.getSupportedSockets())
                .ifPresent(supportedSockets -> {
                    final Set<Socket> foundSupportedSockets = supportedSockets.stream()
                            .map(socket -> findSocketById(socket.getId()))
                            .collect(Collectors.toSet());
                    toBeUpdated.setSupportedSockets(foundSupportedSockets);
                });

        coolerRepository.findByNameAndIdNot(name, id)
                .ifPresent(cooler -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_COOLER_UNIQUE,
                                    cooler.getName()
                            ),
                            FIELD_NAME
                    );
                });

        toBeUpdated.setName(name);
        toBeUpdated.setHeight(height);
        toBeUpdated.setPowerDissipation(powerDissipation);
        toBeUpdated.setVendor(foundVendor);
        toBeUpdated.setFanSize(foundFanSize);
        toBeUpdated.setPowerConnector(foundPowerConnector);

        return coolerRepository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Cooler replace(final UUID id, final Cooler newCooler) {
        final Vendor vendor = newCooler.getVendor();
        if (vendor == null || vendor.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_VENDOR
            );
        }

        final FanSize fanSize = newCooler.getFanSize();
        if (fanSize == null || fanSize.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_FAN_SIZE
            );
        }

        final FanPowerConnector powerConnector = newCooler.getPowerConnector();
        if (powerConnector == null || powerConnector.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_POWER_CONNECTOR
            );
        }

        final Set<Socket> supportedSockets = newCooler.getSupportedSockets();
        if (supportedSockets == null
                || supportedSockets.isEmpty()
                || supportedSockets.stream().anyMatch(Objects::isNull)
                || supportedSockets.stream().anyMatch(socket -> socket.getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_SUPPORTED_SOCKETS
            );
        }

        final Cooler existent = findCoolerById(id);

        final Vendor foundVendor = findVendorById(vendor.getId());
        final FanSize foundFanSize = findFanSizeById(fanSize.getId());
        final FanPowerConnector foundPowerConnector = findPowerConnectorById(powerConnector.getId());
        final Set<Socket> foundSupportedSockets = supportedSockets.stream()
                .map(socket -> findSocketById(socket.getId()))
                .collect(Collectors.toSet());

        coolerRepository.findByNameAndIdNot(
                newCooler.getName(),
                id
        ).ifPresent(cooler -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_COOLER_UNIQUE,
                            cooler.getName()
                    ),
                    FIELD_NAME
            );
        });

        existent.setName(newCooler.getName());
        existent.setHeight(newCooler.getHeight());
        existent.setPowerDissipation(newCooler.getPowerDissipation());
        existent.setVendor(foundVendor);
        existent.setFanSize(foundFanSize);
        existent.setPowerConnector(foundPowerConnector);
        existent.setSupportedSockets(foundSupportedSockets);

        return coolerRepository.save(existent);
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
}
