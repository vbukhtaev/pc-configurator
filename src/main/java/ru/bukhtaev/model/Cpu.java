package ru.bukhtaev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.CpuToRamType;
import ru.bukhtaev.model.dictionary.Manufacturer;
import ru.bukhtaev.model.dictionary.RamType;
import ru.bukhtaev.model.dictionary.Socket;

import java.util.HashSet;
import java.util.Set;

/**
 * Модель процессора.
 */
@Getter
@Setter
@Entity
@Table(
        name = "cpu",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@SuperBuilder
@NoArgsConstructor
public class Cpu extends NameableEntity {

    /**
     * Название поля, хранящего количество ядер.
     */
    public static final String FIELD_CORE_COUNT = "coreCount";

    /**
     * Название поля, хранящего количество потоков.
     */
    public static final String FIELD_THREAD_COUNT = "threadCount";

    /**
     * Название поля, хранящего базовую частоту.
     */
    public static final String FIELD_BASE_CLOCK = "baseClock";

    /**
     * Название поля, хранящего максимальную частоту.
     */
    public static final String FIELD_MAX_CLOCK = "maxClock";

    /**
     * Название поля, хранящего объем L3 кэша.
     */
    public static final String FIELD_L3CACHE_SIZE = "l3CacheSize";

    /**
     * Название поля, хранящего максимальное тепловыделение.
     */
    public static final String FIELD_MAX_TDP = "maxTdp";

    /**
     * Название поля, хранящего максимальный объем оперативной памяти.
     */
    public static final String FIELD_MAX_MEMORY_SIZE = "maxMemorySize";

    /**
     * Название поля, хранящего производителя.
     */
    public static final String FIELD_MANUFACTURER = "manufacturer";

    /**
     * Название поля, хранящего сокет.
     */
    public static final String FIELD_SOCKET = "socket";

    /**
     * Название поля, хранящего поддерживаемые типы оперативной памяти.
     */
    public static final String FIELD_SUPPORTED_RAM_TYPES = "supportedRamTypes";

    /**
     * Количество ядер.
     */
    @Min(1)
    @NotNull
    @Column(name = "core_count", nullable = false)
    protected Integer coreCount;

    /**
     * Количество потоков.
     */
    @Min(1)
    @NotNull
    @Column(name = "thread_count", nullable = false)
    protected Integer threadCount;

    /**
     * Базовая частота (МГц).
     */
    @Min(100)
    @NotNull
    @Column(name = "base_clock", nullable = false)
    protected Integer baseClock;

    /**
     * Максимальная частота (МГц).
     */
    @Min(100)
    @NotNull
    @Column(name = "max_clock", nullable = false)
    protected Integer maxClock;

    /**
     * Объем L3 кэша (Мб).
     */
    @Min(1)
    @NotNull
    @Column(name = "l3cache_size", nullable = false)
    protected Integer l3CacheSize;

    /**
     * Максимальное тепловыделение (Вт).
     */
    @Min(1)
    @NotNull
    @Column(name = "max_tdp", nullable = false)
    protected Integer maxTdp;

    /**
     * Максимальный объем оперативной памяти (Мб).
     */
    @Min(32768)
    @NotNull
    @Column(name = "max_memory_size", nullable = false)
    protected Integer maxMemorySize;

    /**
     * Производитель.
     */
    @ManyToOne
    @JoinColumn(name = "manufacturer_id", referencedColumnName = "id", nullable = false)
    protected Manufacturer manufacturer;

    /**
     * Сокет.
     */
    @ManyToOne
    @JoinColumn(name = "socket_id", referencedColumnName = "id", nullable = false)
    protected Socket socket;

    /**
     * Поддерживаемые типы оперативной памяти.
     */
    @NotNull
    @Size(min = 1)
    @OneToMany(
            mappedBy = "cpu",
            cascade = CascadeType.ALL
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<CpuToRamType> supportedRamTypes = new HashSet<>();

    /**
     * Добавляет тип оперативной памяти с указанной частотой.
     *
     * @param type  тип оперативной памяти
     * @param clock частота
     */
    public void addRamType(final RamType type, final Integer clock) {
        final CpuToRamType cpuToRamType = new CpuToRamType();

        cpuToRamType.setCpu(this);
        cpuToRamType.setRamType(type);
        cpuToRamType.setMaxMemoryClock(clock);

        this.supportedRamTypes.add(cpuToRamType);
    }
}
