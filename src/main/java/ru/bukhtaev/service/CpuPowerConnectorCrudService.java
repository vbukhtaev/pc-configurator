package ru.bukhtaev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.CpuPowerConnector;
import ru.bukhtaev.repository.ICpuPowerConnectorRepository;
import ru.bukhtaev.validation.Translator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.*;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_CPU_POWER_CONNECTOR_NOT_FOUND;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_CPU_POWER_CONNECTOR_UNIQUE;

/**
 * Реализация сервиса CRUD операций над коннекторами питания процессоров.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class CpuPowerConnectorCrudService implements ICrudService<CpuPowerConnector, UUID> {

    /**
     * Репозиторий.
     */
    private final ICpuPowerConnectorRepository repository;

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
    public CpuPowerConnectorCrudService(
            final ICpuPowerConnectorRepository repository,
            final Translator translator
    ) {
        this.repository = repository;
        this.translator = translator;
    }

    @Override
    public CpuPowerConnector getById(final UUID id) {
        return findById(id);
    }

    @Override
    public List<CpuPowerConnector> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public CpuPowerConnector create(final CpuPowerConnector newConnector) {
        repository.findByName(newConnector.getName())
                .ifPresent(connector -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_CPU_POWER_CONNECTOR_UNIQUE,
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
    public CpuPowerConnector update(final UUID id, final CpuPowerConnector changedConnector) {
        repository.findByNameAndIdNot(
                changedConnector.getName(),
                id
        ).ifPresent(connector -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_CPU_POWER_CONNECTOR_UNIQUE,
                            connector.getName()
                    ),
                    FIELD_NAME
            );
        });

        final CpuPowerConnector toBeUpdated = findById(id);
        Optional.ofNullable(changedConnector.getName())
                .ifPresent(toBeUpdated::setName);

        return repository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public CpuPowerConnector replace(final UUID id, final CpuPowerConnector newConnector) {
        repository.findByNameAndIdNot(
                newConnector.getName(),
                id
        ).ifPresent(connector -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_CPU_POWER_CONNECTOR_UNIQUE,
                            connector.getName()
                    ),
                    FIELD_NAME
            );
        });

        final CpuPowerConnector existent = findById(id);
        existent.setName(newConnector.getName());

        return repository.save(existent);
    }

    /**
     * Возвращает коннектор питания процессора с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return коннектор питания процессора с указанным ID, если он существует
     */
    private CpuPowerConnector findById(final UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_CPU_POWER_CONNECTOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
