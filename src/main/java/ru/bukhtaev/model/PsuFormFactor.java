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
 * Модель форм-фактора блока питания.
 */
@Getter
@Setter
@Entity
@Table(
        name = "psu_form_factor",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@NoArgsConstructor
public class PsuFormFactor extends NameableEntity {

    /**
     * Конструктор.
     *
     * @param id   ID
     * @param name название
     */
    @Builder
    public PsuFormFactor(final UUID id, final String name) {
        super(id, name);
    }
}
