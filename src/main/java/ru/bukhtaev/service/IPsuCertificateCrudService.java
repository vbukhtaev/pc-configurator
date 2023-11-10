package ru.bukhtaev.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ru.bukhtaev.model.PsuCertificate;

import java.util.List;
import java.util.UUID;

/**
 * Сервис CRUD операций над сертификатами блоков питания.
 */
@Validated
public interface IPsuCertificateCrudService {

    /**
     * Возвращает сертификат блока питания с указанным ID.
     *
     * @param id ID
     * @return сертификат блока питания с указанным ID
     */
    PsuCertificate getById(final UUID id);

    /**
     * Возвращает все сертификаты блоков питания.
     *
     * @return все сертификаты блоков питания.
     */
    List<PsuCertificate> getAll();

    /**
     * Сохраняет новый сертификат блока питания в базу данных.
     *
     * @param newCertificate новый сертификат блока питания
     * @return сохраненный сертификат блока питания
     */
    PsuCertificate create(@Valid final PsuCertificate newCertificate);

    /**
     * Удаляет сертификат блока питания с указанным ID из базы данных.
     *
     * @param id ID
     */
    void delete(final UUID id);

    /**
     * Обновляет сертификат блока питания с указанным ID.
     *
     * @param id                 ID
     * @param changedCertificate данные для обновления
     * @return обновленный сертификат блока питания
     */
    PsuCertificate update(final UUID id, final PsuCertificate changedCertificate);

    /**
     * Заменяет сертификат блока питания с указанными ID.
     *
     * @param id             ID
     * @param newCertificate новый сертификат блока питания
     * @return замененный сертификат блока питания
     */
    PsuCertificate replace(final UUID id, @Valid final PsuCertificate newCertificate);
}
