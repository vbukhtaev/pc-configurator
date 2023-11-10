package ru.bukhtaev.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ru.bukhtaev.model.VideoMemoryType;

import java.util.List;
import java.util.UUID;

/**
 * Сервис CRUD операций над типами видеопамяти.
 */
@Validated
public interface IVideoMemoryTypeCrudService {

    /**
     * Возвращает тип видеопамяти с указанным ID.
     *
     * @param id ID
     * @return тип видеопамяти с указанным ID
     */
    VideoMemoryType getById(final UUID id);

    /**
     * Возвращает все типы видеопамяти.
     *
     * @return все типы видеопамяти.
     */
    List<VideoMemoryType> getAll();

    /**
     * Сохраняет новый тип видеопамяти в базу данных.
     *
     * @param newType новый тип видеопамяти
     * @return сохраненный тип видеопамяти
     */
    VideoMemoryType create(@Valid final VideoMemoryType newType);

    /**
     * Удаляет тип видеопамяти с указанным ID из базы данных.
     *
     * @param id ID
     */
    void delete(final UUID id);

    /**
     * Обновляет тип видеопамяти с указанным ID.
     *
     * @param id          ID
     * @param changedType данные для обновления
     * @return обновленный тип видеопамяти
     */
    VideoMemoryType update(final UUID id, final VideoMemoryType changedType);

    /**
     * Заменяет тип видеопамяти с указанными ID.
     *
     * @param id      ID
     * @param newType новый тип видеопамяти
     * @return замененный тип видеопамяти
     */
    VideoMemoryType replace(final UUID id, @Valid final VideoMemoryType newType);
}
