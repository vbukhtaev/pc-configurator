package ru.bukhtaev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.Socket;
import ru.bukhtaev.repository.ISocketRepository;
import ru.bukhtaev.validation.Translator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_SOCKET_NOT_FOUND;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_SOCKET_UNIQUE;

/**
 * Реализация сервиса CRUD операций над сокетами.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class SocketCrudService implements IPagingCrudService<Socket, UUID> {

    /**
     * Репозиторий.
     */
    private final ISocketRepository repository;

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
    public SocketCrudService(
            final ISocketRepository repository,
            final Translator translator
    ) {
        this.repository = repository;
        this.translator = translator;
    }

    @Override
    public Socket getById(final UUID id) {
        return findById(id);
    }

    @Override
    public List<Socket> getAll() {
        return repository.findAll();
    }

    @Override
    public Slice<Socket> getAll(final Pageable pageable) {
        return repository.findAllBy(pageable);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Socket create(final Socket newSocket) {
        repository.findByName(newSocket.getName())
                .ifPresent(socket -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_SOCKET_UNIQUE,
                                    socket.getName()
                            ),
                            FIELD_NAME
                    );
                });

        return repository.save(newSocket);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Socket update(final UUID id, final Socket changedSocket) {
        repository.findByNameAndIdNot(
                changedSocket.getName(),
                id
        ).ifPresent(socket -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_SOCKET_UNIQUE,
                            socket.getName()
                    ),
                    FIELD_NAME
            );
        });

        final Socket toBeUpdated = findById(id);
        Optional.ofNullable(changedSocket.getName())
                .ifPresent(toBeUpdated::setName);

        return repository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Socket replace(final UUID id, final Socket newSocket) {
        repository.findByNameAndIdNot(
                newSocket.getName(),
                id
        ).ifPresent(socket -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_SOCKET_UNIQUE,
                            socket.getName()
                    ),
                    FIELD_NAME
            );
        });

        final Socket existent = findById(id);
        existent.setName(newSocket.getName());

        return repository.save(existent);
    }

    /**
     * Возвращает сокет с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return сокет с указанным ID, если он существует
     */
    private Socket findById(final UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_SOCKET_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
