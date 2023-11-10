package ru.bukhtaev.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ru.bukhtaev.model.PciExpressConnectorVersion;

import java.util.List;
import java.util.UUID;

/**
 * Сервис CRUD операций над версиями коннектора PCI-Express.
 */
@Validated
public interface IPciExpressConnectorVersionCrudService {

    /**
     * Возвращает версию коннектора PCI-Express с указанным ID.
     *
     * @param id ID
     * @return версию коннектора PCI-Express с указанным ID
     */
    PciExpressConnectorVersion getById(final UUID id);

    /**
     * Возвращает все версии коннектора PCI-Express.
     *
     * @return все версии коннектора PCI-Express.
     */
    List<PciExpressConnectorVersion> getAll();

    /**
     * Сохраняет новую версию коннектора PCI-Express в базу данных.
     *
     * @param newVersion новая версия коннектора PCI-Express
     * @return сохраненную версию коннектора PCI-Express
     */
    PciExpressConnectorVersion create(@Valid final PciExpressConnectorVersion newVersion);

    /**
     * Удаляет версию коннектора PCI-Express с указанным ID из базы данных.
     *
     * @param id ID
     */
    void delete(final UUID id);

    /**
     * Обновляет версию коннектора PCI-Express с указанным ID.
     *
     * @param id             ID
     * @param changedVersion данные для обновления
     * @return обновленную версию коннектора PCI-Express
     */
    PciExpressConnectorVersion update(final UUID id, final PciExpressConnectorVersion changedVersion);

    /**
     * Заменяет версию коннектора PCI-Express с указанными ID.
     *
     * @param id         ID
     * @param newVersion новая версия коннектора PCI-Express
     * @return замененную версию коннектора PCI-Express
     */
    PciExpressConnectorVersion replace(final UUID id, @Valid final PciExpressConnectorVersion newVersion);
}
