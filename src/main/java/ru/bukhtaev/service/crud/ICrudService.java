package ru.bukhtaev.service.crud;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Сервис CRUD операций над сущностями.
 *
 * @param <T>  тип сущности
 * @param <ID> тип ID
 */
@Validated
public interface ICrudService<T, ID> {

    /**
     * Возвращает сущность типа {@link T} с указанным ID.
     *
     * @param id ID
     * @return сущность типа {@link T} с указанным ID
     */
    T getById(final ID id);

    /**
     * Возвращает все сущности типа {@link T}.
     *
     * @return все сущности типа {@link T}.
     */
    List<T> getAll();

    /**
     * Сохраняет новую сущность типа {@link T} в базу данных.
     *
     * @param newEntity новая сущность типа {@link T}
     * @return сохраненную сущность типа {@link T}
     */
    T create(@Valid final T newEntity);

    /**
     * Удаляет сущность типа {@link T} с указанным ID из базы данных.
     *
     * @param id ID
     */
    void delete(final ID id);

    /**
     * Обновляет сущность типа {@link T} с указанным ID.
     *
     * @param id            ID
     * @param changedEntity данные для обновления
     * @return обновленную сущность типа {@link T}
     */
    T update(final ID id, final T changedEntity);

    /**
     * Заменяет сущность типа {@link T} с указанными ID.
     *
     * @param id        ID
     * @param newEntity новая сущность типа {@link T}
     * @return замененную сущность типа {@link T}
     */
    T replace(final ID id, @Valid final T newEntity);
}
