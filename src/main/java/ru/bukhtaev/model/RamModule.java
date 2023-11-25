package ru.bukhtaev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.dictionary.RamType;

import java.util.Objects;

/**
 * Модель модуля оперативной памяти.
 */
@Getter
@Setter
@Entity
@Table(
        name = "ram_module",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "clock",
                "capacity",
                "type_id",
                "design_id"
        })
)
@SuperBuilder
@NoArgsConstructor
public class RamModule extends BaseEntity {

    /**
     * Название поля, хранящего частоту.
     */
    public static final String FIELD_CLOCK = "clock";

    /**
     * Название поля, хранящего объем.
     */
    public static final String FIELD_CAPACITY = "capacity";

    /**
     * Название поля, хранящего тип.
     */
    public static final String FIELD_TYPE = "type";

    /**
     * Название поля, хранящего вариант исполнения.
     */
    public static final String FIELD_DESIGN = "design";

    /**
     * Частота (МГц).
     */
    @Min(1333)
    @NotNull
    @Column(name = "clock", nullable = false)
    protected Integer clock;

    /**
     * Объем (Мб).
     */
    @Min(512)
    @NotNull
    @Column(name = "capacity", nullable = false)
    protected Integer capacity;

    /**
     * Тип.
     */
    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id", nullable = false)
    protected RamType type;

    /**
     * Вариант исполнения.
     */
    @ManyToOne
    @JoinColumn(name = "design_id", referencedColumnName = "id", nullable = false)
    protected Design design;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RamModule ramModule = (RamModule) o;

        if (!Objects.equals(clock, ramModule.clock)) return false;
        if (!Objects.equals(capacity, ramModule.capacity)) return false;
        if (!Objects.equals(type, ramModule.type)) return false;
        return Objects.equals(design, ramModule.design);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (clock != null ? clock.hashCode() : 0);
        result = 31 * result + (capacity != null ? capacity.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (design != null ? design.hashCode() : 0);
        return result;
    }
}
