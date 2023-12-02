package ru.bukhtaev.model.dictionary;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.NameableEntity;

import java.util.HashSet;
import java.util.Set;

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
@SuperBuilder
@NoArgsConstructor
public class PciExpressConnectorVersion extends NameableEntity {

    /**
     * Название поля, хранящего более старые версии по отношению к текущей.
     */
    public static final String FIELD_LOWER_VERSIONS = "lowerVersions";

    /**
     * Более старые версии по отношению к текущей.
     */
    @ManyToMany
    @JoinTable(
            name = "pci_express_connector_version_to_pci_express_connector_version",
            joinColumns = @JoinColumn(name = "version_id"),
            inverseJoinColumns = @JoinColumn(name = "lower_version_id")
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<PciExpressConnectorVersion> lowerVersions = new HashSet<>();
}
