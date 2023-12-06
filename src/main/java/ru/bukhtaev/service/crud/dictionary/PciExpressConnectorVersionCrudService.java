package ru.bukhtaev.service.crud.dictionary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.InvalidParamException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.dictionary.PciExpressConnectorVersion;
import ru.bukhtaev.repository.dictionary.IPciExpressConnectorVersionRepository;
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
import static ru.bukhtaev.model.dictionary.PciExpressConnectorVersion.FIELD_LOWER_VERSIONS;
import static ru.bukhtaev.i18n.MessageUtils.*;

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
        final var lowerVersions = newVersion.getLowerVersions();
        if (lowerVersions == null
                || lowerVersions.stream().anyMatch(Objects::isNull)
                || lowerVersions.stream().anyMatch(version -> version.getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_LOWER_VERSIONS
            );
        }

        final var foundLowerVersions = lowerVersions.stream()
                .map(version -> findById(version.getId()))
                .collect(Collectors.toSet());
        newVersion.setLowerVersions(foundLowerVersions);

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

        Optional.ofNullable(changedVersion.getLowerVersions())
                .ifPresent(versions -> {
                    final var foundLowerVersions = versions.stream()
                            .map(version -> findById(version.getId()))
                            .collect(Collectors.toSet());
                    toBeUpdated.setLowerVersions(foundLowerVersions);
                });

        Optional.ofNullable(changedVersion.getName())
                .ifPresent(toBeUpdated::setName);

        return repository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public PciExpressConnectorVersion replace(final UUID id, final PciExpressConnectorVersion newVersion) {
        final var lowerVersions = newVersion.getLowerVersions();
        if (lowerVersions == null
                || lowerVersions.stream().anyMatch(Objects::isNull)
                || lowerVersions.stream().anyMatch(socket -> socket.getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_LOWER_VERSIONS
            );
        }

        final var foundLowerVersions = lowerVersions.stream()
                .map(version -> findById(version.getId()))
                .collect(Collectors.toSet());

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
        existent.setLowerVersions(foundLowerVersions);

        return repository.save(existent);
    }

    /**
     * Возвращает версию коннектора PCI-Express с указанным ID, если она существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return версию коннектора PCI-Express с указанным ID, если она существует
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
