package ru.bukhtaev.service.crud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.InvalidParamException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.Chipset;
import ru.bukhtaev.model.dictionary.Socket;
import ru.bukhtaev.repository.IChipsetRepository;
import ru.bukhtaev.repository.dictionary.ISocketRepository;
import ru.bukhtaev.i18n.Translator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.Chipset.FIELD_SOCKET;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.i18n.MessageUtils.*;

/**
 * Реализация сервиса CRUD операций над чипсетами.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class ChipsetCrudService implements IPagingCrudService<Chipset, UUID> {

    /**
     * Репозиторий чипсетов.
     */
    private final IChipsetRepository chipsetRepository;

    /**
     * Репозиторий сокетов.
     */
    private final ISocketRepository socketRepository;

    /**
     * Сервис предоставления сообщений.
     */
    private final Translator translator;

    /**
     * Конструктор.
     *
     * @param chipsetRepository репозиторий чипсетов
     * @param socketRepository  репозиторий сокетов
     * @param translator        сервис предоставления сообщений
     */
    @Autowired
    public ChipsetCrudService(
            final IChipsetRepository chipsetRepository,
            final ISocketRepository socketRepository,
            final Translator translator
    ) {
        this.chipsetRepository = chipsetRepository;
        this.socketRepository = socketRepository;
        this.translator = translator;
    }

    @Override
    public Chipset getById(final UUID id) {
        return findChipsetById(id);
    }

    @Override
    public List<Chipset> getAll() {
        return chipsetRepository.findAll();
    }

    @Override
    public Slice<Chipset> getAll(final Pageable pageable) {
        return chipsetRepository.findAllBy(pageable);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Chipset create(final Chipset newChipset) {
        final Socket socket = newChipset.getSocket();
        if (socket == null || socket.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_SOCKET
            );
        }

        final Socket foundSocket = findSocketById(socket.getId());
        newChipset.setSocket(foundSocket);

        chipsetRepository.findByName(newChipset.getName())
                .ifPresent(chipset -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_CHIPSET_UNIQUE,
                                    chipset.getName()
                            ),
                            FIELD_NAME
                    );
                });

        return chipsetRepository.save(newChipset);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        chipsetRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Chipset update(final UUID id, final Chipset changedChipset) {
        final Chipset toBeUpdated = findChipsetById(id);

        chipsetRepository.findByNameAndIdNot(
                changedChipset.getName(),
                id
        ).ifPresent(chipset -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_CHIPSET_UNIQUE,
                            chipset.getName()
                    ),
                    FIELD_NAME
            );
        });

        Optional.ofNullable(changedChipset.getName())
                .ifPresent(toBeUpdated::setName);

        final Socket socket = changedChipset.getSocket();
        if (socket != null && socket.getId() != null) {
            final Socket found = findSocketById(socket.getId());
            toBeUpdated.setSocket(found);
        }

        return chipsetRepository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Chipset replace(final UUID id, final Chipset newChipset) {
        final Chipset existent = findChipsetById(id);

        chipsetRepository.findByNameAndIdNot(
                newChipset.getName(),
                id
        ).ifPresent(chipset -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_CHIPSET_UNIQUE,
                            chipset.getName()
                    ),
                    FIELD_NAME
            );
        });

        existent.setName(newChipset.getName());

        final Socket socket = newChipset.getSocket();
        if (socket == null || socket.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_SOCKET
            );
        }

        final Socket foundSocket = findSocketById(socket.getId());
        existent.setSocket(foundSocket);

        return chipsetRepository.save(existent);
    }

    /**
     * Возвращает чипсет с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return чипсет с указанным ID, если он существует
     */
    private Chipset findChipsetById(final UUID id) {
        return chipsetRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_CHIPSET_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает сокет с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return сокет с указанным ID, если он существует
     */
    private Socket findSocketById(final UUID id) {
        return socketRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_SOCKET_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
