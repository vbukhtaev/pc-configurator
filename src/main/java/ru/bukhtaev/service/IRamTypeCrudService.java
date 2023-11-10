package ru.bukhtaev.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ru.bukhtaev.model.RamType;

import java.util.List;
import java.util.UUID;

/**
 * Сервис CRUD операций над типами оперативной памяти.
 */
@Validated
public interface IRamTypeCrudService {

    /**
     * Возвращает тип оперативной памяти с указанным ID.
     *
     * @param id ID
     * @return тип оперативной памяти с указанным ID
     */
    RamType getById(final UUID id);

    /**
     * Возвращает все типы оперативной памяти.
     *
     * @return все типы оперативной памяти.
     */
    List<RamType> getAll();

    /**
     * Сохраняет новый тип оперативной памяти в базу данных.
     *
     * @param newType новый тип оперативной памяти
     * @return сохраненный тип оперативной памяти
     */
    RamType create(@Valid final RamType newType);

    /**
     * Удаляет тип оперативной памяти с указанным ID из базы данных.
     *
     * @param id ID
     */
    void delete(final UUID id);

    /**
     * Обновляет тип оперативной памяти с указанным ID.
     *
     * @param id          ID
     * @param changedType данные для обновления
     * @return обновленный тип оперативной памяти
     */
    RamType update(final UUID id, final RamType changedType);

    /**
     * Заменяет тип оперативной памяти с указанными ID.
     *
     * @param id      ID
     * @param newType новый тип оперативной памяти
     * @return замененный тип оперативной памяти
     */
    RamType replace(final UUID id, @Valid final RamType newType);
}
