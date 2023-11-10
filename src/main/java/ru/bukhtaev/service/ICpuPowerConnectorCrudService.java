package ru.bukhtaev.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ru.bukhtaev.model.CpuPowerConnector;

import java.util.List;
import java.util.UUID;

/**
 * Сервис CRUD операций над коннекторами питания процессора.
 */
@Validated
public interface ICpuPowerConnectorCrudService {

    /**
     * Возвращает коннектор питания процессора с указанным ID.
     *
     * @param id ID
     * @return коннектор питания процессора с указанным ID
     */
    CpuPowerConnector getById(final UUID id);

    /**
     * Возвращает все коннекторы питания процессора.
     *
     * @return все коннекторы питания процессора.
     */
    List<CpuPowerConnector> getAll();

    /**
     * Сохраняет новый коннектор питания процессора в базу данных.
     *
     * @param newConnector новый коннектор питания процессора
     * @return сохраненный коннектор питания процессора
     */
    CpuPowerConnector create(@Valid final CpuPowerConnector newConnector);

    /**
     * Удаляет коннектор питания процессора с указанным ID из базы данных.
     *
     * @param id ID
     */
    void delete(final UUID id);

    /**
     * Обновляет коннектор питания процессора с указанным ID.
     *
     * @param id               ID
     * @param changedConnector данные для обновления
     * @return обновленный коннектор питания процессора
     */
    CpuPowerConnector update(final UUID id, final CpuPowerConnector changedConnector);

    /**
     * Заменяет коннектор питания процессора с указанными ID.
     *
     * @param id           ID
     * @param newConnector новый коннектор питания процессора
     * @return замененный коннектор питания процессора
     */
    CpuPowerConnector replace(final UUID id, @Valid final CpuPowerConnector newConnector);
}
