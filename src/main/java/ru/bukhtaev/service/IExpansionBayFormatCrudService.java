package ru.bukhtaev.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ru.bukhtaev.model.ExpansionBayFormat;

import java.util.List;
import java.util.UUID;

/**
 * Сервис CRUD операций над форматами отсеков расширения.
 */
@Validated
public interface IExpansionBayFormatCrudService {

    /**
     * Возвращает формат отсека расширения с указанным ID.
     *
     * @param id ID
     * @return формат отсека расширения с указанным ID
     */
    ExpansionBayFormat getById(final UUID id);

    /**
     * Возвращает все форматы отсеков расширения.
     *
     * @return все форматы отсеков расширения.
     */
    List<ExpansionBayFormat> getAll();

    /**
     * Сохраняет новый формат отсека расширения в базу данных.
     *
     * @param newFormat новый формат отсека расширения
     * @return сохраненный формат отсека расширения
     */
    ExpansionBayFormat create(@Valid final ExpansionBayFormat newFormat);

    /**
     * Удаляет формат отсека расширения с указанным ID из базы данных.
     *
     * @param id ID
     */
    void delete(final UUID id);

    /**
     * Обновляет формат отсека расширения с указанным ID.
     *
     * @param id            ID
     * @param changedFormat данные для обновления
     * @return обновленный формат отсека расширения
     */
    ExpansionBayFormat update(final UUID id, final ExpansionBayFormat changedFormat);

    /**
     * Заменяет формат отсека расширения с указанными ID.
     *
     * @param id        ID
     * @param newFormat новый формат отсека расширения
     * @return замененный формат отсека расширения
     */
    ExpansionBayFormat replace(final UUID id, @Valid final ExpansionBayFormat newFormat);
}
