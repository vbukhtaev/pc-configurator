package ru.bukhtaev.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

/**
 * Модель сущности, имеющей название.
 */
@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
public abstract class NameableEntity extends BaseEntity {

    /**
     * Название поля, хранящего название.
     */
    public static final String FIELD_NAME = "name";

    /**
     * Название.
     */
    @NotBlank
    @Column(name = "name", length = 64, nullable = false, unique = true)
    protected String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        NameableEntity that = (NameableEntity) o;

        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + (name != null ? name.hashCode() : 0);
    }
}
