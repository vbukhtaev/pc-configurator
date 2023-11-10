package ru.bukhtaev.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ru.bukhtaev.model.FanSize;

import java.util.List;
import java.util.UUID;

/**
 * Сервис CRUD операций над размерами вентиляторов.
 */
@Validated
public interface IFanSizeCrudService {

    /**
     * Возвращает размер вентилятора с указанным ID.
     *
     * @param id ID
     * @return размер вентилятора с указанным ID
     */
    FanSize getById(final UUID id);

    /**
     * Возвращает все размеры вентиляторов.
     *
     * @return все размеры вентиляторов.
     */
    List<FanSize> getAll();

    /**
     * Сохраняет новый размер вентилятора в базу данных.
     *
     * @param newSize новый размер вентилятора
     * @return сохраненный размер вентилятора
     */
    FanSize create(@Valid final FanSize newSize);

    /**
     * Удаляет размер вентилятора с указанным ID из базы данных.
     *
     * @param id ID
     */
    void delete(final UUID id);

    /**
     * Обновляет размер вентилятора с указанным ID.
     *
     * @param id          ID
     * @param changedSize данные для обновления
     * @return обновленный размер вентилятора
     */
    FanSize update(final UUID id, final FanSize changedSize);

    /**
     * Заменяет размер вентилятора с указанными ID.
     *
     * @param id      ID
     * @param newSize новый размер вентилятора
     * @return замененный размер вентилятора
     */
    FanSize replace(final UUID id, @Valid final FanSize newSize);
}
