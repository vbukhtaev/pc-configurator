package ru.bukhtaev.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

/**
 * Модель сущности, имеющей название.
 */
@Getter
@Setter
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

    /**
     * Конструктор.
     *
     * @param id   ID
     * @param name название
     */
    protected NameableEntity(final UUID id, final String name) {
        super(id);
        this.name = name;
    }

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
