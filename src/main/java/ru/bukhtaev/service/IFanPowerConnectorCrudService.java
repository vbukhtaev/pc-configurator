package ru.bukhtaev.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ru.bukhtaev.model.FanPowerConnector;

import java.util.List;
import java.util.UUID;

/**
 * Сервис CRUD операций над коннекторами питания вентилятора.
 */
@Validated
public interface IFanPowerConnectorCrudService {

    /**
     * Возвращает коннектор питания вентилятора с указанным ID.
     *
     * @param id ID
     * @return коннектор питания вентилятора с указанным ID
     */
    FanPowerConnector getById(final UUID id);

    /**
     * Возвращает все коннекторы питания вентилятора.
     *
     * @return все коннекторы питания вентилятора.
     */
    List<FanPowerConnector> getAll();

    /**
     * Сохраняет новый коннектор питания вентилятора в базу данных.
     *
     * @param newConnector новый коннектор питания вентилятора
     * @return сохраненный коннектор питания вентилятора
     */
    FanPowerConnector create(@Valid final FanPowerConnector newConnector);

    /**
     * Удаляет коннектор питания вентилятора с указанным ID из базы данных.
     *
     * @param id ID
     */
    void delete(final UUID id);

    /**
     * Обновляет коннектор питания вентилятора с указанным ID.
     *
     * @param id               ID
     * @param changedConnector данные для обновления
     * @return обновленный коннектор питания вентилятора
     */
    FanPowerConnector update(final UUID id, final FanPowerConnector changedConnector);

    /**
     * Заменяет коннектор питания вентилятора с указанными ID.
     *
     * @param id           ID
     * @param newConnector новый коннектор питания вентилятора
     * @return замененный коннектор питания вентилятора
     */
    FanPowerConnector replace(final UUID id, @Valid final FanPowerConnector newConnector);
}
