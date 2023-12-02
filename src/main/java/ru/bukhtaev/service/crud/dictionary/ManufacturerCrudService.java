package ru.bukhtaev.service.crud.dictionary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.dictionary.Manufacturer;
import ru.bukhtaev.repository.dictionary.IManufacturerRepository;
import ru.bukhtaev.service.crud.ICrudService;
import ru.bukhtaev.validation.Translator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_MANUFACTURER_NOT_FOUND;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_MANUFACTURER_UNIQUE;

/**
 * Реализация сервиса CRUD операций над производителями.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class ManufacturerCrudService implements ICrudService<Manufacturer, UUID> {

    /**
     * Репозиторий.
     */
    private final IManufacturerRepository repository;

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
    public ManufacturerCrudService(
            final IManufacturerRepository repository,
            final Translator translator
    ) {
        this.repository = repository;
        this.translator = translator;
    }

    @Override
    public Manufacturer getById(final UUID id) {
        return findById(id);
    }

    @Override
    public List<Manufacturer> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Manufacturer create(final Manufacturer newManufacturer) {
        repository.findByName(newManufacturer.getName())
                .ifPresent(manufacturer -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_MANUFACTURER_UNIQUE,
                                    manufacturer.getName()
                            ),
                            FIELD_NAME
                    );
                });

        return repository.save(newManufacturer);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Manufacturer update(final UUID id, final Manufacturer changedManufacturer) {
        repository.findByNameAndIdNot(
                changedManufacturer.getName(),
                id
        ).ifPresent(manufacturer -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_MANUFACTURER_UNIQUE,
                            manufacturer.getName()
                    ),
                    FIELD_NAME
            );
        });

        final Manufacturer toBeUpdated = findById(id);
        Optional.ofNullable(changedManufacturer.getName())
                .ifPresent(toBeUpdated::setName);

        return repository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Manufacturer replace(final UUID id, final Manufacturer newManufacturer) {
        repository.findByNameAndIdNot(
                newManufacturer.getName(),
                id
        ).ifPresent(manufacturer -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_MANUFACTURER_UNIQUE,
                            manufacturer.getName()
                    ),
                    FIELD_NAME
            );
        });

        final Manufacturer existent = findById(id);
        existent.setName(newManufacturer.getName());

        return repository.save(existent);
    }

    /**
     * Возвращает производителя с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return производителя с указанным ID, если он существует
     */
    private Manufacturer findById(final UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_MANUFACTURER_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
