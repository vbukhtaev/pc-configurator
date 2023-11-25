package ru.bukhtaev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.dictionary.PciExpressConnectorVersion;
import ru.bukhtaev.repository.IPciExpressConnectorVersionRepository;
import ru.bukhtaev.validation.Translator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_PCI_EXPRESS_CONNECTOR_VERSION_NOT_FOUND;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_PCI_EXPRESS_CONNECTOR_VERSION_UNIQUE;

/**
 * Реализация сервиса CRUD операций над версиями коннектора PCI-Express.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class PciExpressConnectorVersionCrudService implements ICrudService<PciExpressConnectorVersion, UUID> {

    /**
     * Репозиторий.
     */
    private final IPciExpressConnectorVersionRepository repository;

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
    public PciExpressConnectorVersionCrudService(
            final IPciExpressConnectorVersionRepository repository,
            final Translator translator
    ) {
        this.repository = repository;
        this.translator = translator;
    }

    @Override
    public PciExpressConnectorVersion getById(final UUID id) {
        return findById(id);
    }

    @Override
    public List<PciExpressConnectorVersion> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public PciExpressConnectorVersion create(final PciExpressConnectorVersion newVersion) {
        repository.findByName(newVersion.getName())
                .ifPresent(version -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_PCI_EXPRESS_CONNECTOR_VERSION_UNIQUE,
                                    version.getName()
                            ),
                            FIELD_NAME
                    );
                });

        return repository.save(newVersion);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public PciExpressConnectorVersion update(final UUID id, final PciExpressConnectorVersion changedVersion) {
        repository.findByNameAndIdNot(
                changedVersion.getName(),
                id
        ).ifPresent(version -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_PCI_EXPRESS_CONNECTOR_VERSION_UNIQUE,
                            version.getName()
                    ),
                    FIELD_NAME
            );
        });

        final PciExpressConnectorVersion toBeUpdated = findById(id);
        Optional.ofNullable(changedVersion.getName())
                .ifPresent(toBeUpdated::setName);

        return repository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public PciExpressConnectorVersion replace(final UUID id, final PciExpressConnectorVersion newVersion) {
        repository.findByNameAndIdNot(
                newVersion.getName(),
                id
        ).ifPresent(version -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_PCI_EXPRESS_CONNECTOR_VERSION_UNIQUE,
                            version.getName()
                    ),
                    FIELD_NAME
            );
        });

        final PciExpressConnectorVersion existent = findById(id);
        existent.setName(newVersion.getName());

        return repository.save(existent);
    }

    /**
     * Возвращает версию коннектора PCI-Express с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return версию коннектора PCI-Express с указанным ID, если он существует
     */
    private PciExpressConnectorVersion findById(final UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_PCI_EXPRESS_CONNECTOR_VERSION_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
