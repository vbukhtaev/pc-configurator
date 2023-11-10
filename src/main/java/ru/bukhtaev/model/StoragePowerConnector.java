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
 * Модель коннектора питания накопителя.
 */
@Getter
@Setter
@Entity
@Table(
        name = "storage_power_connector",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@NoArgsConstructor
public class StoragePowerConnector extends NameableEntity {

    /**
     * Конструктор.
     *
     * @param id   ID
     * @param name название
     */
    @Builder
    public StoragePowerConnector(final UUID id, final String name) {
        super(id, name);
    }
}
