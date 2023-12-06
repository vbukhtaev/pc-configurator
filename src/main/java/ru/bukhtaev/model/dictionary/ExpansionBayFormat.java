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
 * Модель формата отсека расширения.
 */
@Getter
@Setter
@Entity
@Table(
        name = "expansion_bay_format",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@SuperBuilder
@NoArgsConstructor
public class ExpansionBayFormat extends NameableEntity {
}
