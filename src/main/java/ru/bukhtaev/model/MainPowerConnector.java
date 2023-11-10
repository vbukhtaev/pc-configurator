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
 * Модель основного коннектора питания.
 */
@Getter
@Setter
@Entity
@Table(
        name = "main_power_connector",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@NoArgsConstructor
public class MainPowerConnector extends NameableEntity {

    /**
     * Конструктор.
     *
     * @param id   ID
     * @param name название
     */
    @Builder
    public MainPowerConnector(final UUID id, final String name) {
        super(id, name);
    }
}
