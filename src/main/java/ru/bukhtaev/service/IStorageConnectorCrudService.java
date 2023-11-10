package ru.bukhtaev.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ru.bukhtaev.model.StorageConnector;

import java.util.List;
import java.util.UUID;

/**
 * Сервис CRUD операций над коннекторами подключения накопителя.
 */
@Validated
public interface IStorageConnectorCrudService {

    /**
     * Возвращает коннектор подключения накопителя с указанным ID.
     *
     * @param id ID
     * @return коннектор подключения накопителя с указанным ID
     */
    StorageConnector getById(final UUID id);

    /**
     * Возвращает все коннекторы подключения накопителя.
     *
     * @return все коннекторы подключения накопителя.
     */
    List<StorageConnector> getAll();

    /**
     * Сохраняет новый коннектор подключения накопителя в базу данных.
     *
     * @param newConnector новый коннектор подключения накопителя
     * @return сохраненный коннектор подключения накопителя
     */
    StorageConnector create(@Valid final StorageConnector newConnector);

    /**
     * Удаляет коннектор подключения накопителя с указанным ID из базы данных.
     *
     * @param id ID
     */
    void delete(final UUID id);

    /**
     * Обновляет коннектор подключения накопителя с указанным ID.
     *
     * @param id               ID
     * @param changedConnector данные для обновления
     * @return обновленный коннектор подключения накопителя
     */
    StorageConnector update(final UUID id, final StorageConnector changedConnector);

    /**
     * Заменяет коннектор подключения накопителя с указанными ID.
     *
     * @param id           ID
     * @param newConnector новый коннектор подключения накопителя
     * @return замененный коннектор подключения накопителя
     */
    StorageConnector replace(final UUID id, @Valid final StorageConnector newConnector);
}
