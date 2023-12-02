package ru.bukhtaev.model.dictionary;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.NameableEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * Модель коннектора подключения накопителя.
 */
@Getter
@Setter
@Entity
@Table(
        name = "storage_connector",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@SuperBuilder
@NoArgsConstructor
public class StorageConnector extends NameableEntity {

    /**
     * Название поля, хранящего совместимые коннекторы.
     */
    public static final String FIELD_COMPATIBLE_CONNECTORS = "compatibleConnectors";

    /**
     * Совместимые коннекторы.
     */
    @ManyToMany
    @JoinTable(
            name = "storage_connector_to_storage_connector",
            joinColumns = @JoinColumn(name = "connector_id"),
            inverseJoinColumns = @JoinColumn(name = "compatible_connector_id")
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<StorageConnector> compatibleConnectors = new HashSet<>();
}
