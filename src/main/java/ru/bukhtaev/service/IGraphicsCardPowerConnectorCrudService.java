package ru.bukhtaev.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ru.bukhtaev.model.GraphicsCardPowerConnector;

import java.util.List;
import java.util.UUID;

/**
 * Сервис CRUD операций над коннекторами питания видеокарты.
 */
@Validated
public interface IGraphicsCardPowerConnectorCrudService {

    /**
     * Возвращает коннектор питания видеокарты с указанным ID.
     *
     * @param id ID
     * @return коннектор питания видеокарты с указанным ID
     */
    GraphicsCardPowerConnector getById(final UUID id);

    /**
     * Возвращает все коннекторы питания видеокарты.
     *
     * @return все коннекторы питания видеокарты.
     */
    List<GraphicsCardPowerConnector> getAll();

    /**
     * Сохраняет новый коннектор питания видеокарты в базу данных.
     *
     * @param newConnector новый коннектор питания видеокарты
     * @return сохраненный коннектор питания видеокарты
     */
    GraphicsCardPowerConnector create(@Valid final GraphicsCardPowerConnector newConnector);

    /**
     * Удаляет коннектор питания видеокарты с указанным ID из базы данных.
     *
     * @param id ID
     */
    void delete(final UUID id);

    /**
     * Обновляет коннектор питания видеокарты с указанным ID.
     *
     * @param id               ID
     * @param changedConnector данные для обновления
     * @return обновленный коннектор питания видеокарты
     */
    GraphicsCardPowerConnector update(final UUID id, final GraphicsCardPowerConnector changedConnector);

    /**
     * Заменяет коннектор питания видеокарты с указанными ID.
     *
     * @param id           ID
     * @param newConnector новый коннектор питания видеокарты
     * @return замененный коннектор питания видеокарты
     */
    GraphicsCardPowerConnector replace(final UUID id, @Valid final GraphicsCardPowerConnector newConnector);
}
