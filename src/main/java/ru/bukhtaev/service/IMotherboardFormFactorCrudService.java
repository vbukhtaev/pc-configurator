package ru.bukhtaev.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ru.bukhtaev.model.MotherboardFormFactor;

import java.util.List;
import java.util.UUID;

/**
 * Сервис CRUD операций над форм-факторами материнских плат.
 */
@Validated
public interface IMotherboardFormFactorCrudService {

    /**
     * Возвращает форм-фактор материнской платы с указанным ID.
     *
     * @param id ID
     * @return форм-фактор материнской платы с указанным ID
     */
    MotherboardFormFactor getById(final UUID id);

    /**
     * Возвращает все форм-факторы материнских плат.
     *
     * @return все форм-факторы материнских плат.
     */
    List<MotherboardFormFactor> getAll();

    /**
     * Сохраняет новый форм-фактор материнской платы в базу данных.
     *
     * @param newFormFactor новый форм-фактор материнской платы
     * @return сохраненный форм-фактор материнской платы
     */
    MotherboardFormFactor create(@Valid final MotherboardFormFactor newFormFactor);

    /**
     * Удаляет форм-фактор материнской платы с указанным ID из базы данных.
     *
     * @param id ID
     */
    void delete(final UUID id);

    /**
     * Обновляет форм-фактор материнской платы с указанным ID.
     *
     * @param id                ID
     * @param changedFormFactor данные для обновления
     * @return обновленный форм-фактор материнской платы
     */
    MotherboardFormFactor update(final UUID id, final MotherboardFormFactor changedFormFactor);

    /**
     * Заменяет форм-фактор материнской платы с указанными ID.
     *
     * @param id            ID
     * @param newFormFactor новый форм-фактор материнской платы
     * @return замененный форм-фактор материнской платы
     */
    MotherboardFormFactor replace(final UUID id, @Valid final MotherboardFormFactor newFormFactor);
}
