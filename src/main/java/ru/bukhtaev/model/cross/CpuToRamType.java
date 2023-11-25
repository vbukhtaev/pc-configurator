package ru.bukhtaev.model.cross;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Cpu;
import ru.bukhtaev.model.dictionary.RamType;

import java.util.Objects;
import java.util.UUID;

/**
 * Модель поддерживаемого процессором типа оперативной памяти.
 */
@Getter
@Setter
@Entity
@Table(
        name = "cpu_to_ram_type",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "cpu_id",
                "ram_type_id"
        })
)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CpuToRamType {

    /**
     * Название поля, хранящего процессор.
     */
    public static final String FIELD_CPU = "cpu";

    /**
     * Название поля, хранящего тип оперативной памяти.
     */
    public static final String FIELD_RAM_TYPE = "ramType";

    /**
     * Название поля, хранящего частоту оперативной памяти.
     */
    public static final String FIELD_MAX_MEMORY_CLOCK = "maxMemoryClock";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;

    /**
     * Процессор.
     */
    @ManyToOne
    @JoinColumn(name = "cpu_id", referencedColumnName = "id", nullable = false)
    protected Cpu cpu;

    /**
     * Тип оперативной памяти.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "ram_type_id", referencedColumnName = "id", nullable = false)
    protected RamType ramType;

    /**
     * Частота оперативной памяти.
     */
    @Min(800)
    @NotNull
    @Column(name = "max_memory_clock", nullable = false)
    protected Integer maxMemoryClock;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CpuToRamType that = (CpuToRamType) o;

        if (!Objects.equals(ramType, that.ramType)) return false;
        return Objects.equals(maxMemoryClock, that.maxMemoryClock);
    }

    @Override
    public int hashCode() {
        int result = ramType != null ? ramType.hashCode() : 0;
        result = 31 * result + (maxMemoryClock != null ? maxMemoryClock.hashCode() : 0);
        return result;
    }
}
