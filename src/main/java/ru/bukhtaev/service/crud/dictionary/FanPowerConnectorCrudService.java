package ru.bukhtaev.service.crud.dictionary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.InvalidParamException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.dictionary.FanPowerConnector;
import ru.bukhtaev.repository.dictionary.IFanPowerConnectorRepository;
import ru.bukhtaev.service.crud.ICrudService;
import ru.bukhtaev.i18n.Translator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.model.dictionary.FanPowerConnector.FIELD_COMPATIBLE_CONNECTORS;
import static ru.bukhtaev.i18n.MessageUtils.*;

/**
 * Реализация сервиса CRUD операций над коннекторами питания вентиляторов.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class FanPowerConnectorCrudService implements ICrudService<FanPowerConnector, UUID> {

    /**
     * Репозиторий.
     */
    private final IFanPowerConnectorRepository repository;

    /**
     * Сервис предоставления сообщений.
     */
    private final Translator translator;

    /**
     * Конструктор.
     *
     * @param repository репозиторий
     * @param translator сервис предоставления сообщений
     */
    @Autowired
    public FanPowerConnectorCrudService(
            final IFanPowerConnectorRepository repository,
            final Translator translator
    ) {
        this.repository = repository;
        this.translator = translator;
    }

    @Override
    public FanPowerConnector getById(final UUID id) {
        return findById(id);
    }

    @Override
    public List<FanPowerConnector> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public FanPowerConnector create(final FanPowerConnector newConnector) {
        final var compatibleConnectors = newConnector.getCompatibleConnectors();
        if (compatibleConnectors == null
                || compatibleConnectors.stream().anyMatch(Objects::isNull)
                || compatibleConnectors.stream().anyMatch(connector -> connector.getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_COMPATIBLE_CONNECTORS
            );
        }

        final var foundCompatibleConnectors = compatibleConnectors.stream()
                .map(connector -> findById(connector.getId()))
                .collect(Collectors.toSet());
        newConnector.setCompatibleConnectors(foundCompatibleConnectors);

        repository.findByName(newConnector.getName())
                .ifPresent(connector -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_FAN_POWER_CONNECTOR_UNIQUE,
                                    connector.getName()
                            ),
                            FIELD_NAME
                    );
                });

        return repository.save(newConnector);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public FanPowerConnector update(final UUID id, final FanPowerConnector changedConnector) {
        repository.findByNameAndIdNot(
                changedConnector.getName(),
                id
        ).ifPresent(connector -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_FAN_POWER_CONNECTOR_UNIQUE,
                            connector.getName()
                    ),
                    FIELD_NAME
            );
        });

        final FanPowerConnector toBeUpdated = findById(id);

        Optional.ofNullable(changedConnector.getCompatibleConnectors())
                .ifPresent(connectors -> {
                    final var foundCompatibleConnectors = connectors.stream()
                            .map(connector -> findById(connector.getId()))
                            .collect(Collectors.toSet());
                    toBeUpdated.setCompatibleConnectors(foundCompatibleConnectors);
                });

        Optional.ofNullable(changedConnector.getName())
                .ifPresent(toBeUpdated::setName);

        return repository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public FanPowerConnector replace(final UUID id, final FanPowerConnector newConnector) {
        final var compatibleConnectors = newConnector.getCompatibleConnectors();
        if (compatibleConnectors == null
                || compatibleConnectors.stream().anyMatch(Objects::isNull)
                || compatibleConnectors.stream().anyMatch(connector -> connector.getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_COMPATIBLE_CONNECTORS
            );
        }

        final var foundCompatibleConnectors = compatibleConnectors.stream()
                .map(connector -> findById(connector.getId()))
                .collect(Collectors.toSet());

        repository.findByNameAndIdNot(
                newConnector.getName(),
                id
        ).ifPresent(connector -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_FAN_POWER_CONNECTOR_UNIQUE,
                            connector.getName()
                    ),
                    FIELD_NAME
            );
        });

        final FanPowerConnector existent = findById(id);
        existent.setName(newConnector.getName());
        existent.setCompatibleConnectors(foundCompatibleConnectors);

        return repository.save(existent);
    }

    /**
     * Возвращает коннектор питания вентилятора с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return коннектор питания вентилятора с указанным ID, если он существует
     */
    private FanPowerConnector findById(final UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_FAN_POWER_CONNECTOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
