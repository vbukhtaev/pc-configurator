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
 * Модель формата отсека расширения.
 */
@Getter
@Setter
@Entity
@Table(
        name = "expansion_bay_format",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@NoArgsConstructor
public class ExpansionBayFormat extends NameableEntity {

    /**
     * Конструктор.
     *
     * @param id   ID
     * @param name название
     */
    @Builder
    public ExpansionBayFormat(final UUID id, final String name) {
        super(id, name);
    }
}
