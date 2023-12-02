package ru.bukhtaev.service.crud.dictionary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.dictionary.RamType;
import ru.bukhtaev.repository.dictionary.IRamTypeRepository;
import ru.bukhtaev.service.crud.ICrudService;
import ru.bukhtaev.validation.Translator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_RAM_TYPE_NOT_FOUND;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_RAM_TYPE_UNIQUE;

/**
 * Реализация сервиса CRUD операций над типами оперативной памяти.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class RamTypeCrudService implements ICrudService<RamType, UUID> {

    /**
     * Репозиторий.
     */
    private final IRamTypeRepository repository;

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
    public RamTypeCrudService(
            final IRamTypeRepository repository,
            final Translator translator
    ) {
        this.repository = repository;
        this.translator = translator;
    }

    @Override
    public RamType getById(final UUID id) {
        return findById(id);
    }

    @Override
    public List<RamType> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public RamType create(final RamType newType) {
        repository.findByName(newType.getName())
                .ifPresent(type -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_RAM_TYPE_UNIQUE,
                                    type.getName()
                            ),
                            FIELD_NAME
                    );
                });

        return repository.save(newType);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public RamType update(final UUID id, final RamType changedType) {
        repository.findByNameAndIdNot(
                changedType.getName(),
                id
        ).ifPresent(type -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_RAM_TYPE_UNIQUE,
                            type.getName()
                    ),
                    FIELD_NAME
            );
        });

        final RamType toBeUpdated = findById(id);
        Optional.ofNullable(changedType.getName())
                .ifPresent(toBeUpdated::setName);

        return repository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public RamType replace(final UUID id, final RamType newType) {
        repository.findByNameAndIdNot(
                newType.getName(),
                id
        ).ifPresent(type -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_RAM_TYPE_UNIQUE,
                            type.getName()
                    ),
                    FIELD_NAME
            );
        });

        final RamType existent = findById(id);
        existent.setName(newType.getName());

        return repository.save(existent);
    }

    /**
     * Возвращает тип оперативной памяти с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return тип оперативной памяти с указанным ID, если он существует
     */
    private RamType findById(final UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_RAM_TYPE_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
