package ru.bukhtaev.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ru.bukhtaev.model.Manufacturer;

import java.util.List;
import java.util.UUID;

/**
 * Сервис CRUD операций над производителями.
 */
@Validated
public interface IManufacturerCrudService {

    /**
     * Возвращает производителя с указанным ID.
     *
     * @param id ID
     * @return производителя с указанным ID
     */
    Manufacturer getById(final UUID id);

    /**
     * Возвращает всех производителей.
     *
     * @return всех производителей.
     */
    List<Manufacturer> getAll();

    /**
     * Сохраняет нового производителя в базу данных.
     *
     * @param newManufacturer новый производитель
     * @return сохраненного производителя
     */
    Manufacturer create(@Valid final Manufacturer newManufacturer);

    /**
     * Удаляет производителя с указанным ID из базы данных.
     *
     * @param id ID
     */
    void delete(final UUID id);

    /**
     * Обновляет производителя с указанным ID.
     *
     * @param id                  ID
     * @param changedManufacturer данные для обновления
     * @return обновленного производителя
     */
    Manufacturer update(final UUID id, final Manufacturer changedManufacturer);

    /**
     * Заменяет производителя с указанными ID.
     *
     * @param id              ID
     * @param newManufacturer новый производитель
     * @return замененного производителя
     */
    Manufacturer replace(final UUID id, @Valid final Manufacturer newManufacturer);
}
