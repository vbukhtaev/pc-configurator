package ru.bukhtaev.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

/**
 * Модель базовой сущности, имеющей ID.
 */
@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
public abstract class BaseEntity {

    /**
     * Название поля, хранящего ID.
     */
    public static final String FIELD_ID = "id";

    /**
     * ID
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;

    /**
     * Конструктор.
     *
     * @param id ID
     */
    protected BaseEntity(final UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseEntity that = (BaseEntity) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
