package ru.bukhtaev.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ru.bukhtaev.model.PsuFormFactor;

import java.util.List;
import java.util.UUID;

/**
 * Сервис CRUD операций над форм-факторами блоков питания.
 */
@Validated
public interface IPsuFormFactorCrudService {

    /**
     * Возвращает форм-фактор блока питания с указанным ID.
     *
     * @param id ID
     * @return форм-фактор блока питания с указанным ID
     */
    PsuFormFactor getById(final UUID id);

    /**
     * Возвращает все форм-факторы блоков питания.
     *
     * @return все форм-факторы блоков питания.
     */
    List<PsuFormFactor> getAll();

    /**
     * Сохраняет новый форм-фактор блока питания в базу данных.
     *
     * @param newFormFactor новый форм-фактор блока питания
     * @return сохраненный форм-фактор блока питания
     */
    PsuFormFactor create(@Valid final PsuFormFactor newFormFactor);

    /**
     * Удаляет форм-фактор блока питания с указанным ID из базы данных.
     *
     * @param id ID
     */
    void delete(final UUID id);

    /**
     * Обновляет форм-фактор блока питания с указанным ID.
     *
     * @param id                ID
     * @param changedFormFactor данные для обновления
     * @return обновленный форм-фактор блока питания
     */
    PsuFormFactor update(final UUID id, final PsuFormFactor changedFormFactor);

    /**
     * Заменяет форм-фактор блока питания с указанными ID.
     *
     * @param id            ID
     * @param newFormFactor новый форм-фактор блока питания
     * @return замененный форм-фактор блока питания
     */
    PsuFormFactor replace(final UUID id, @Valid final PsuFormFactor newFormFactor);
}
