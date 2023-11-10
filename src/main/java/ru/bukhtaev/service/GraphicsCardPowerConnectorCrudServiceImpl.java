package ru.bukhtaev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.GraphicsCardPowerConnector;
import ru.bukhtaev.repository.IGraphicsCardPowerConnectorRepository;
import ru.bukhtaev.validation.Translator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.*;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_GRAPHICS_CARD_POWER_CONNECTOR_NOT_FOUND;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_GRAPHICS_CARD_POWER_CONNECTOR_UNIQUE;

/**
 * Реализация сервиса CRUD операций над коннекторами питания видеокарт.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class GraphicsCardPowerConnectorCrudServiceImpl implements IGraphicsCardPowerConnectorCrudService {

    /**
     * Репозиторий.
     */
    private final IGraphicsCardPowerConnectorRepository repository;

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
    public GraphicsCardPowerConnectorCrudServiceImpl(
            final IGraphicsCardPowerConnectorRepository repository,
            final Translator translator
    ) {
        this.repository = repository;
        this.translator = translator;
    }

    @Override
    public GraphicsCardPowerConnector getById(final UUID id) {
        return findById(id);
    }

    @Override
    public List<GraphicsCardPowerConnector> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public GraphicsCardPowerConnector create(final GraphicsCardPowerConnector newConnector) {
        repository.findByName(newConnector.getName())
                .ifPresent(connector -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_GRAPHICS_CARD_POWER_CONNECTOR_UNIQUE,
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
    public GraphicsCardPowerConnector update(final UUID id, final GraphicsCardPowerConnector changedConnector) {
        repository.findByNameAndIdNot(
                changedConnector.getName(),
                id
        ).ifPresent(connector -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_GRAPHICS_CARD_POWER_CONNECTOR_UNIQUE,
                            connector.getName()
                    ),
                    FIELD_NAME
            );
        });

        final GraphicsCardPowerConnector toBeUpdated = findById(id);
        Optional.ofNullable(changedConnector.getName())
                .ifPresent(toBeUpdated::setName);

        return repository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public GraphicsCardPowerConnector replace(final UUID id, final GraphicsCardPowerConnector newConnector) {
        repository.findByNameAndIdNot(
                newConnector.getName(),
                id
        ).ifPresent(connector -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_GRAPHICS_CARD_POWER_CONNECTOR_UNIQUE,
                            connector.getName()
                    ),
                    FIELD_NAME
            );
        });

        final GraphicsCardPowerConnector existent = findById(id);
        existent.setName(newConnector.getName());

        return repository.save(existent);
    }

    /**
     * Возвращает коннектор питания видеокарты с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return коннектор питания видеокарты с указанным ID, если он существует
     */
    private GraphicsCardPowerConnector findById(final UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_GRAPHICS_CARD_POWER_CONNECTOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
