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
 * Модель жесткого диска.
 */
@Getter
@Setter
@Entity
@Table(
        name = "hdd",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "name",
                "capacity",
                "spindle_speed",
                "cache_size"
        })
)
@SuperBuilder
@NoArgsConstructor
public class Hdd extends StorageDevice {

    /**
     * Название поля, хранящего скорость вращения шпинделя.
     */
    public static final String FIELD_SPINDLE_SPEED = "spindleSpeed";

    /**
     * Название поля, хранящего объем кэш-памяти.
     */
    public static final String FIELD_CACHE_SIZE = "cacheSize";

    /**
     * Скорость вращения шпинделя (об/мин).
     */
    @Min(5400)
    @NotNull
    @Column(name = "spindle_speed", nullable = false)
    protected Integer spindleSpeed;

    /**
     * Объем кэш-памяти (Мб).
     */
    @Min(32)
    @NotNull
    @Column(name = "cache_size", nullable = false)
    protected Integer cacheSize;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Hdd hdd = (Hdd) o;

        if (!Objects.equals(spindleSpeed, hdd.spindleSpeed)) return false;
        return Objects.equals(cacheSize, hdd.cacheSize);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (spindleSpeed != null ? spindleSpeed.hashCode() : 0);
        result = 31 * result + (cacheSize != null ? cacheSize.hashCode() : 0);
        return result;
    }
}
