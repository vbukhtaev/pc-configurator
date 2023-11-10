package ru.bukhtaev.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.validation.annotation.Validated;
import ru.bukhtaev.model.Vendor;

import java.util.List;
import java.util.UUID;

/**
 * Сервис CRUD операций над вендорами.
 */
@Validated
public interface IVendorCrudService {

    /**
     * Возвращает вендора с указанным ID.
     *
     * @param id ID
     * @return вендора с указанным ID
     */
    Vendor getById(final UUID id);

    /**
     * Возвращает всех вендоров.
     *
     * @return всех вендоров.
     */
    List<Vendor> getAll();

    /**
     * Возвращает вендоров постранично.
     *
     * @param pageable информация о номере страницы, ее размере и сортировке
     * @return вендоров постранично
     */
    Slice<Vendor> getAll(final Pageable pageable);

    /**
     * Сохраняет нового вендора в базу данных.
     *
     * @param newVendor новый вендор
     * @return сохраненного вендора
     */
    Vendor create(@Valid final Vendor newVendor);

    /**
     * Удаляет вендора с указанным ID из базы данных.
     *
     * @param id ID
     */
    void delete(final UUID id);

    /**
     * Обновляет вендора с указанным ID.
     *
     * @param id            ID
     * @param changedVendor данные для обновления
     * @return обновленного вендора
     */
    Vendor update(final UUID id, final Vendor changedVendor);

    /**
     * Заменяет вендора с указанными ID.
     *
     * @param id        ID
     * @param newVendor новый вендор
     * @return замененного вендора
     */
    Vendor replace(final UUID id, @Valid final Vendor newVendor);
}
