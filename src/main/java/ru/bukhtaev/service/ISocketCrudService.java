package ru.bukhtaev.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.validation.annotation.Validated;
import ru.bukhtaev.model.Socket;

import java.util.List;
import java.util.UUID;

/**
 * Сервис CRUD операций над сокетами.
 */
@Validated
public interface ISocketCrudService {

    /**
     * Возвращает сокет с указанным ID.
     *
     * @param id ID
     * @return сокет с указанным ID
     */
    Socket getById(final UUID id);

    /**
     * Возвращает все сокеты.
     *
     * @return все сокеты.
     */
    List<Socket> getAll();

    /**
     * Возвращает сокеты постранично.
     *
     * @param pageable информация о номере страницы, ее размере и сортировке
     * @return сокеты постранично
     */
    Slice<Socket> getAll(final Pageable pageable);

    /**
     * Сохраняет новый сокет в базу данных.
     *
     * @param newSocket новый сокет
     * @return сохраненный сокет
     */
    Socket create(@Valid final Socket newSocket);

    /**
     * Удаляет сокет с указанным ID из базы данных.
     *
     * @param id ID
     */
    void delete(final UUID id);

    /**
     * Обновляет сокет с указанным ID.
     *
     * @param id            ID
     * @param changedSocket данные для обновления
     * @return обновленный сокет
     */
    Socket update(final UUID id, final Socket changedSocket);

    /**
     * Заменяет сокет с указанными ID.
     *
     * @param id        ID
     * @param newSocket новый сокет
     * @return замененный сокет
     */
    Socket replace(final UUID id, @Valid final Socket newSocket);
}
