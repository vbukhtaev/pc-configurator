package ru.bukhtaev.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Модель SSD накопителя.
 */
@Getter
@Setter
@Entity
@Table(
        name = "ssd",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "capacity"})
)
@SuperBuilder
@NoArgsConstructor
public class Ssd extends StorageDevice {
}
