package ru.bukhtaev.model.dictionary;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.NameableEntity;

/**
 * Модель вендора.
 */
@Getter
@Setter
@Entity
@Table(
        name = "vendor",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@SuperBuilder
@NoArgsConstructor
public class Vendor extends NameableEntity {
}
