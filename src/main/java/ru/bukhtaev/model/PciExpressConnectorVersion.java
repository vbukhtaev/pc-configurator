package ru.bukhtaev.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Модель версии коннектора PCI-Express.
 */
@Getter
@Setter
@Entity
@Table(
        name = "pci_express_connector_version",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@NoArgsConstructor
public class PciExpressConnectorVersion extends NameableEntity {

    /**
     * Конструктор.
     *
     * @param id   ID
     * @param name название
     */
    @Builder
    public PciExpressConnectorVersion(final UUID id, final String name) {
        super(id, name);
    }
}
