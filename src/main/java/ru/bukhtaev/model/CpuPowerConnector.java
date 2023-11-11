package ru.bukhtaev.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Модель коннектора питания процессора.
 */
@Getter
@Setter
@Entity
@Table(
        name = "cpu_power_connector",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@SuperBuilder
@NoArgsConstructor
public class CpuPowerConnector extends NameableEntity {
}
