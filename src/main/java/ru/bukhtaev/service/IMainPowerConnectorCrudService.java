package ru.bukhtaev.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ru.bukhtaev.model.MainPowerConnector;

import java.util.List;
import java.util.UUID;

/**
 * Сервис CRUD операций над основными коннекторами питания.
 */
@Validated
public interface IMainPowerConnectorCrudService {

    /**
     * Возвращает основной коннектор питания с указанным ID.
     *
     * @param id ID
     * @return основной коннектор питания с указанным ID
     */
    MainPowerConnector getById(final UUID id);

    /**
     * Возвращает все основные коннекторы питания.
     *
     * @return все основные коннекторы питания.
     */
    List<MainPowerConnector> getAll();

    /**
     * Сохраняет новый основной коннектор питания в базу данных.
     *
     * @param newConnector новый основной коннектор питания
     * @return сохраненный основной коннектор питания
     */
    MainPowerConnector create(@Valid final MainPowerConnector newConnector);

    /**
     * Удаляет основной коннектор питания с указанным ID из базы данных.
     *
     * @param id ID
     */
    void delete(final UUID id);

    /**
     * Обновляет основной коннектор питания с указанным ID.
     *
     * @param id               ID
     * @param changedConnector данные для обновления
     * @return обновленный основной коннектор питания
     */
    MainPowerConnector update(final UUID id, final MainPowerConnector changedConnector);

    /**
     * Заменяет основной коннектор питания с указанными ID.
     *
     * @param id           ID
     * @param newConnector новый основной коннектор питания
     * @return замененный основной коннектор питания
     */
    MainPowerConnector replace(final UUID id, @Valid final MainPowerConnector newConnector);
}
