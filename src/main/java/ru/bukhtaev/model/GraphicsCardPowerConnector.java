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
 * Модель коннектора питания видеокарты.
 */
@Getter
@Setter
@Entity
@Table(
        name = "graphics_card_power_connector",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@NoArgsConstructor
public class GraphicsCardPowerConnector extends NameableEntity {

    /**
     * Конструктор.
     *
     * @param id   ID
     * @param name название
     */
    @Builder
    public GraphicsCardPowerConnector(final UUID id, final String name) {
        super(id, name);
    }
}
