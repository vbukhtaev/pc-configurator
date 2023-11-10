package ru.bukhtaev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.MotherboardFormFactor;
import ru.bukhtaev.repository.IMotherboardFormFactorRepository;
import ru.bukhtaev.validation.Translator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_MOTHERBOARD_FORM_FACTOR_NOT_FOUND;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_MOTHERBOARD_FORM_FACTOR_UNIQUE;

/**
 * Реализация сервиса CRUD операций над форм-факторами материнских плат.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class MotherboardFormFactorCrudServiceImpl implements IMotherboardFormFactorCrudService {

    /**
     * Репозиторий.
     */
    private final IMotherboardFormFactorRepository repository;

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
    public MotherboardFormFactorCrudServiceImpl(
            final IMotherboardFormFactorRepository repository,
            final Translator translator
    ) {
        this.repository = repository;
        this.translator = translator;
    }

    @Override
    public MotherboardFormFactor getById(final UUID id) {
        return findById(id);
    }

    @Override
    public List<MotherboardFormFactor> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public MotherboardFormFactor create(final MotherboardFormFactor newFormFactor) {
        repository.findByName(newFormFactor.getName())
                .ifPresent(formFactor -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_MOTHERBOARD_FORM_FACTOR_UNIQUE,
                                    formFactor.getName()
                            ),
                            FIELD_NAME
                    );
                });

        return repository.save(newFormFactor);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public MotherboardFormFactor update(final UUID id, final MotherboardFormFactor changedFormFactor) {
        repository.findByNameAndIdNot(
                changedFormFactor.getName(),
                id
        ).ifPresent(formFactor -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_MOTHERBOARD_FORM_FACTOR_UNIQUE,
                            formFactor.getName()
                    ),
                    FIELD_NAME
            );
        });

        final MotherboardFormFactor toBeUpdated = findById(id);
        Optional.ofNullable(changedFormFactor.getName())
                .ifPresent(toBeUpdated::setName);

        return repository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public MotherboardFormFactor replace(final UUID id, final MotherboardFormFactor newFormFactor) {
        repository.findByNameAndIdNot(
                newFormFactor.getName(),
                id
        ).ifPresent(formFactor -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_MOTHERBOARD_FORM_FACTOR_UNIQUE,
                            formFactor.getName()
                    ),
                    FIELD_NAME
            );
        });

        final MotherboardFormFactor existent = findById(id);
        existent.setName(newFormFactor.getName());

        return repository.save(existent);
    }

    /**
     * Возвращает форм-фактор материнской платы с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return форм-фактор материнской платы с указанным ID, если он существует
     */
    private MotherboardFormFactor findById(final UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_MOTHERBOARD_FORM_FACTOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
