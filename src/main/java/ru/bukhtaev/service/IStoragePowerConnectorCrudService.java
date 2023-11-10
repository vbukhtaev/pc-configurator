package ru.bukhtaev.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ru.bukhtaev.model.StoragePowerConnector;

import java.util.List;
import java.util.UUID;

/**
 * Сервис CRUD операций над коннекторами питания накопителя.
 */
@Validated
public interface IStoragePowerConnectorCrudService {

    /**
     * Возвращает коннектор питания накопителя с указанным ID.
     *
     * @param id ID
     * @return коннектор питания накопителя с указанным ID
     */
    StoragePowerConnector getById(final UUID id);

    /**
     * Возвращает все коннекторы питания накопителя.
     *
     * @return все коннекторы питания накопителя.
     */
    List<StoragePowerConnector> getAll();

    /**
     * Сохраняет новый коннектор питания накопителя в базу данных.
     *
     * @param newConnector новый коннектор питания накопителя
     * @return сохраненный коннектор питания накопителя
     */
    StoragePowerConnector create(@Valid final StoragePowerConnector newConnector);

    /**
     * Удаляет коннектор питания накопителя с указанным ID из базы данных.
     *
     * @param id ID
     */
    void delete(final UUID id);

    /**
     * Обновляет коннектор питания накопителя с указанным ID.
     *
     * @param id               ID
     * @param changedConnector данные для обновления
     * @return обновленный коннектор питания накопителя
     */
    StoragePowerConnector update(final UUID id, final StoragePowerConnector changedConnector);

    /**
     * Заменяет коннектор питания накопителя с указанными ID.
     *
     * @param id           ID
     * @param newConnector новый коннектор питания накопителя
     * @return замененный коннектор питания накопителя
     */
    StoragePowerConnector replace(final UUID id, @Valid final StoragePowerConnector newConnector);
}
