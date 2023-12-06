package ru.bukhtaev.service.crud;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

/**
 * Сервис CRUD операций над сущностями с поддержкой пагинации.
 *
 * @param <T>  тип сущности
 * @param <ID> тип ID
 */
public interface IPagingCrudService<T, ID> extends ICrudService<T, ID> {

    /**
     * Возвращает сущности типа {@link T} постранично.
     *
     * @param pageable информация о номере страницы, ее размере и сортировке
     * @return сущности типа {@link T} постранично
     */
    Slice<T> getAll(final Pageable pageable);
}
