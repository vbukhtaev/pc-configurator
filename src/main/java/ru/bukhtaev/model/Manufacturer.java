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
 * Модель производителя.
 */
@Getter
@Setter
@Entity
@Table(
        name = "manufacturer",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@NoArgsConstructor
public class Manufacturer extends NameableEntity {

    /**
     * Конструктор.
     *
     * @param id   ID
     * @param name название
     */
    @Builder
    public Manufacturer(final UUID id, final String name) {
        super(id, name);
    }
}
