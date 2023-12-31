package ru.bukhtaev.service.crud.dictionary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.InvalidParamException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.dictionary.MainPowerConnector;
import ru.bukhtaev.repository.dictionary.IMainPowerConnectorRepository;
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
import static ru.bukhtaev.model.dictionary.MainPowerConnector.FIELD_COMPATIBLE_CONNECTORS;
import static ru.bukhtaev.i18n.MessageUtils.*;

/**
 * Реализация сервиса CRUD операций над основными коннекторами питания.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class MainPowerConnectorCrudService implements ICrudService<MainPowerConnector, UUID> {

    /**
     * Репозиторий.
     */
    private final IMainPowerConnectorRepository repository;

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
    public MainPowerConnectorCrudService(
            final IMainPowerConnectorRepository repository,
            final Translator translator
    ) {
        this.repository = repository;
        this.translator = translator;
    }

    @Override
    public MainPowerConnector getById(final UUID id) {
        return findById(id);
    }

    @Override
    public List<MainPowerConnector> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public MainPowerConnector create(final MainPowerConnector newConnector) {
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
                                    MESSAGE_CODE_MAIN_POWER_CONNECTOR_UNIQUE,
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
    public MainPowerConnector update(final UUID id, final MainPowerConnector changedConnector) {
        repository.findByNameAndIdNot(
                changedConnector.getName(),
                id
        ).ifPresent(connector -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_MAIN_POWER_CONNECTOR_UNIQUE,
                            connector.getName()
                    ),
                    FIELD_NAME
            );
        });

        final MainPowerConnector toBeUpdated = findById(id);

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
    public MainPowerConnector replace(final UUID id, final MainPowerConnector newConnector) {
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
                            MESSAGE_CODE_MAIN_POWER_CONNECTOR_UNIQUE,
                            connector.getName()
                    ),
                    FIELD_NAME
            );
        });

        final MainPowerConnector existent = findById(id);
        existent.setName(newConnector.getName());
        existent.setCompatibleConnectors(foundCompatibleConnectors);

        return repository.save(existent);
    }

    /**
     * Возвращает основной коннектор питания с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return основной коннектор питания с указанным ID, если он существует
     */
    private MainPowerConnector findById(final UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_MAIN_POWER_CONNECTOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
