package ru.bukhtaev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.dictionary.ExpansionBayFormat;
import ru.bukhtaev.repository.IExpansionBayFormatRepository;
import ru.bukhtaev.validation.Translator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_EXPANSION_BAY_FORMAT_NOT_FOUND;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_EXPANSION_BAY_FORMAT_UNIQUE;

/**
 * Реализация сервиса CRUD операций над форматами отсеков расширения.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class ExpansionBayFormatCrudService implements ICrudService<ExpansionBayFormat, UUID> {

    /**
     * Репозиторий.
     */
    private final IExpansionBayFormatRepository repository;

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
    public ExpansionBayFormatCrudService(
            final IExpansionBayFormatRepository repository,
            final Translator translator
    ) {
        this.repository = repository;
        this.translator = translator;
    }

    @Override
    public ExpansionBayFormat getById(final UUID id) {
        return findById(id);
    }

    @Override
    public List<ExpansionBayFormat> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public ExpansionBayFormat create(final ExpansionBayFormat newFormat) {
        repository.findByName(newFormat.getName())
                .ifPresent(format -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_EXPANSION_BAY_FORMAT_UNIQUE,
                                    format.getName()
                            ),
                            FIELD_NAME
                    );
                });

        return repository.save(newFormat);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public ExpansionBayFormat update(final UUID id, final ExpansionBayFormat changedFormat) {
        repository.findByNameAndIdNot(
                changedFormat.getName(),
                id
        ).ifPresent(format -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_EXPANSION_BAY_FORMAT_UNIQUE,
                            format.getName()
                    ),
                    FIELD_NAME
            );
        });

        final ExpansionBayFormat toBeUpdated = findById(id);
        Optional.ofNullable(changedFormat.getName())
                .ifPresent(toBeUpdated::setName);

        return repository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public ExpansionBayFormat replace(final UUID id, final ExpansionBayFormat newFormat) {
        repository.findByNameAndIdNot(
                newFormat.getName(),
                id
        ).ifPresent(format -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_EXPANSION_BAY_FORMAT_UNIQUE,
                            format.getName()
                    ),
                    FIELD_NAME
            );
        });

        final ExpansionBayFormat existent = findById(id);
        existent.setName(newFormat.getName());

        return repository.save(existent);
    }

    /**
     * Возвращает формат отсека расширения с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return формат отсека расширения с указанным ID, если он существует
     */
    private ExpansionBayFormat findById(final UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_EXPANSION_BAY_FORMAT_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
