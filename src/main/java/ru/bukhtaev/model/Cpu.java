package ru.bukhtaev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
            cascade = CascadeType.ALL, fetch = FetchType.EAGER
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<CpuRamType> supportedRamTypes = new HashSet<>();

    public void addRamType(final RamType type, final Integer clock) {
        final CpuRamType cpuRamType = new CpuRamType();

        cpuRamType.setCpu(this);
        cpuRamType.setRamType(type);
        cpuRamType.setMaxMemoryClock(clock);

        this.supportedRamTypes.add(cpuRamType);
    }
}