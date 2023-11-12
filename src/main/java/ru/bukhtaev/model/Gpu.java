package ru.bukhtaev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

/**
 * Модель графического процессора.
 */
@Getter
@Setter
@Entity
@Table(
        name = "gpu",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "name",
                "memory_size",
                "memory_type_id"
        })
)
@SuperBuilder
@NoArgsConstructor
public class Gpu extends NameableEntity {

    /**
     * Название поля, хранящего объем видеопамяти.
     */
    public static final String FIELD_MEMORY_SIZE = "memorySize";

    /**
     * Название поля, хранящего энергопотребление.
     */
    public static final String FIELD_POWER_CONSUMPTION = "powerConsumption";

    /**
     * Название поля, хранящего тип видеопамяти.
     */
    public static final String FIELD_MEMORY_TYPE = "memoryType";

    /**
     * Название поля, хранящего производителя.
     */
    public static final String FIELD_MANUFACTURER = "manufacturer";

    /**
     * Объем видеопамяти (Мб).
     */
    @Min(64)
    @NotNull
    @Column(name = "memory_size", nullable = false)
    protected Integer memorySize;

    /**
     * Энергопотребление (Вт).
     */
    @Min(1)
    @NotNull
    @Column(name = "power_consumption", nullable = false)
    protected Integer powerConsumption;

    /**
     * Тип видеопамяти.
     */
    @ManyToOne
    @JoinColumn(name = "memory_type_id", referencedColumnName = "id", nullable = false)
    protected VideoMemoryType memoryType;

    /**
     * Производитель.
     */
    @ManyToOne
    @JoinColumn(name = "manufacturer_id", referencedColumnName = "id", nullable = false)
    protected Manufacturer manufacturer;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Gpu gpu = (Gpu) o;

        if (!Objects.equals(memorySize, gpu.memorySize)) return false;
        return Objects.equals(memoryType, gpu.memoryType);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (memorySize != null ? memorySize.hashCode() : 0);
        result = 31 * result + (memoryType != null ? memoryType.hashCode() : 0);
        return result;
    }
}
