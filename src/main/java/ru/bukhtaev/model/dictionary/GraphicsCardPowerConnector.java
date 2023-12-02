package ru.bukhtaev.model.dictionary;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.NameableEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * Модель коннектора питания видеокарты.
 */
@Getter
@Setter
@Entity
@Table(
        name = "graphics_card_power_connector",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@SuperBuilder
@NoArgsConstructor
public class GraphicsCardPowerConnector extends NameableEntity {

    /**
     * Название поля, хранящего совместимые коннекторы.
     */
    public static final String FIELD_COMPATIBLE_CONNECTORS = "compatibleConnectors";

    /**
     * Совместимые коннекторы.
     */
    @ManyToMany
    @JoinTable(
            name = "graphics_card_power_connector_to_graphics_card_power_connector",
            joinColumns = @JoinColumn(name = "connector_id"),
            inverseJoinColumns = @JoinColumn(name = "compatible_connector_id")
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<GraphicsCardPowerConnector> compatibleConnectors = new HashSet<>();
}
