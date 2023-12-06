package ru.bukhtaev.service.crud.dictionary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.dictionary.FanSize;
import ru.bukhtaev.repository.dictionary.IFanSizeRepository;
import ru.bukhtaev.service.crud.ICrudService;
import ru.bukhtaev.i18n.Translator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.dictionary.FanSize.*;
import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_FAN_SIZE_NOT_FOUND;
import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_FAN_SIZE_UNIQUE;

/**
 * Реализация сервиса CRUD операций над размерами вентилятора.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class FanSizeCrudService implements ICrudService<FanSize, UUID> {

    /**
     * Репозиторий.
     */
    private final IFanSizeRepository repository;

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
    public FanSizeCrudService(
            final IFanSizeRepository repository,
            final Translator translator
    ) {
        this.repository = repository;
        this.translator = translator;
    }

    @Override
    public FanSize getById(final UUID id) {
        return findById(id);
    }

    @Override
    public List<FanSize> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public FanSize create(final FanSize newSize) {
        repository.findByLengthAndWidthAndHeight(
                newSize.getLength(),
                newSize.getWidth(),
                newSize.getHeight()
        ).ifPresent(fanSize -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_FAN_SIZE_UNIQUE,
                            fanSize.getLength(),
                            fanSize.getWidth(),
                            fanSize.getHeight()
                    ),
                    FIELD_LENGTH,
                    FIELD_WIDTH,
                    FIELD_HEIGHT
            );
        });

        return repository.save(newSize);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public FanSize update(final UUID id, final FanSize changedSize) {
        repository.findByLengthAndWidthAndHeightAndIdNot(
                changedSize.getLength(),
                changedSize.getWidth(),
                changedSize.getHeight(),
                id
        ).ifPresent(fanSize -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_FAN_SIZE_UNIQUE,
                            fanSize.getLength(),
                            fanSize.getWidth(),
                            fanSize.getHeight()
                    ),
                    FIELD_LENGTH,
                    FIELD_WIDTH,
                    FIELD_HEIGHT
            );
        });

        final FanSize toBeUpdated = findById(id);
        Optional.ofNullable(changedSize.getLength())
                .ifPresent(toBeUpdated::setLength);
        Optional.ofNullable(changedSize.getWidth())
                .ifPresent(toBeUpdated::setWidth);
        Optional.ofNullable(changedSize.getHeight())
                .ifPresent(toBeUpdated::setHeight);

        return repository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public FanSize replace(final UUID id, final FanSize newSize) {
        repository.findByLengthAndWidthAndHeightAndIdNot(
                newSize.getLength(),
                newSize.getWidth(),
                newSize.getHeight(),
                id
        ).ifPresent(fanSize -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_FAN_SIZE_UNIQUE,
                            fanSize.getLength(),
                            fanSize.getWidth(),
                            fanSize.getHeight()
                    ),
                    FIELD_LENGTH,
                    FIELD_WIDTH,
                    FIELD_HEIGHT
            );
        });

        final FanSize existent = findById(id);
        existent.setLength(newSize.getLength());
        existent.setWidth(newSize.getWidth());
        existent.setHeight(newSize.getHeight());

        return repository.save(existent);
    }

    /**
     * Возвращает размер вентилятора с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return размер вентилятора с указанным ID, если он существует
     */
    private FanSize findById(final UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_FAN_SIZE_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
