package ru.bukhtaev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.MotherboardToFanPowerConnector;
import ru.bukhtaev.model.cross.MotherboardToStorageConnector;
import ru.bukhtaev.model.dictionary.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Модель материнской платы.
 */
@Getter
@Setter
@Entity
@Table(
        name = "motherboard",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "name",
                "design_id",
                "chipset_id",
                "ram_type_id"
        })
)
@SuperBuilder
@NoArgsConstructor
public class Motherboard extends NameableEntity {

    /**
     * Название поля, хранящего максимальную частоту оперативной памяти.
     */
    public static final String FIELD_MAX_MEMORY_CLOCK = "maxMemoryClock";

    /**
     * Название поля, хранящего максимальную частоту оперативной памяти с разгоном.
     */
    public static final String FIELD_MAX_MEMORY_OVER_CLOCK = "maxMemoryOverClock";

    /**
     * Название поля, хранящего максимальный объем оперативной памяти.
     */
    public static final String FIELD_MAX_MEMORY_SIZE = "maxMemorySize";

    /**
     * Название поля, хранящего количество слотов.
     */
    public static final String FIELD_MEMORY_SLOTS_COUNT = "slotsCount";

    /**
     * Название поля, хранящего вариант исполнения.
     */
    public static final String FIELD_DESIGN = "design";

    /**
     * Название поля, хранящего чипсет.
     */
    public static final String FIELD_CHIPSET = "chipset";

    /**
     * Название поля, хранящего тип оперативной памяти.
     */
    public static final String FIELD_RAM_TYPE = "ramType";

    /**
     * Название поля, хранящего форм-фактор.
     */
    public static final String FIELD_FORM_FACTOR = "formFactor";

    /**
     * Название поля, хранящего коннектор питания процессора.
     */
    public static final String FIELD_CPU_POWER_CONNECTOR = "cpuPowerConnector";

    /**
     * Название поля, хранящего основной коннектор питания.
     */
    public static final String FIELD_MAIN_POWER_CONNECTOR = "mainPowerConnector";

    /**
     * Название поля, хранящего коннектор питания процессорного кулера.
     */
    public static final String FIELD_COOLER_POWER_CONNECTOR = "coolerPowerConnector";

    /**
     * Название поля, хранящего версию коннектора PCI-Express.
     */
    public static final String FIELD_PCI_EXPRESS_CONNECTOR_VERSION = "pciExpressConnectorVersion";

    /**
     * Название поля, хранящего коннекторы питания вентиляторов.
     */
    public static final String FIELD_FAN_POWER_CONNECTORS = "fanPowerConnectors";

    /**
     * Название поля, хранящего коннекторы подключения накопителей.
     */
    public static final String FIELD_STORAGE_CONNECTORS = "fanPowerConnectors";

    /**
     * Максимальная частота оперативной памяти (МГц).
     */
    @Min(800)
    @NotNull
    @Column(name = "max_memory_clock", nullable = false)
    protected Integer maxMemoryClock;

    /**
     * Максимальная частота оперативной памяти с разгоном (МГц).
     */
    @Min(800)
    @NotNull
    @Column(name = "max_memory_over_clock", nullable = false)
    protected Integer maxMemoryOverClock;

    /**
     * Максимальный объем оперативной памяти (Мб).
     */
    @Min(32768)
    @NotNull
    @Column(name = "max_memory_size", nullable = false)
    protected Integer maxMemorySize;

    /**
     * Количество слотов.
     */
    @Min(2)
    @NotNull
    @Column(name = "memory_slots_count", nullable = false)
    protected Integer slotsCount;

    /**
     * Вариант исполнения.
     */
    @ManyToOne
    @JoinColumn(name = "design_id", referencedColumnName = "id", nullable = false)
    protected Design design;

    /**
     * Чипсет.
     */
    @ManyToOne
    @JoinColumn(name = "chipset_id", referencedColumnName = "id", nullable = false)
    protected Chipset chipset;

    /**
     * Тип оперативной памяти.
     */
    @ManyToOne
    @JoinColumn(name = "ram_type_id", referencedColumnName = "id", nullable = false)
    protected RamType ramType;

    /**
     * Форм-фактор.
     */
    @ManyToOne
    @JoinColumn(name = "form_factor_id", referencedColumnName = "id", nullable = false)
    protected MotherboardFormFactor formFactor;

    /**
     * Коннектор питания процессора.
     */
    @ManyToOne
    @JoinColumn(name = "cpu_power_connector_id", referencedColumnName = "id", nullable = false)
    protected CpuPowerConnector cpuPowerConnector;

    /**
     * Основной коннектор питания.
     */
    @ManyToOne
    @JoinColumn(name = "main_power_connector_id", referencedColumnName = "id", nullable = false)
    protected MainPowerConnector mainPowerConnector;

    /**
     * Коннектор питания процессорного кулера.
     */
    @ManyToOne
    @JoinColumn(name = "cooler_power_connector_id", referencedColumnName = "id", nullable = false)
    protected FanPowerConnector coolerPowerConnector;

    /**
     * Версия коннектора PCI-Express.
     */
    @ManyToOne
    @JoinColumn(name = "pci_express_connector_version_id", referencedColumnName = "id", nullable = false)
    protected PciExpressConnectorVersion pciExpressConnectorVersion;

    /**
     * Коннекторы питания вентиляторов.
     */
    @NotNull
    @Size(min = 1)
    @OneToMany(
            mappedBy = "motherboard",
            cascade = CascadeType.ALL
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<MotherboardToFanPowerConnector> fanPowerConnectors = new HashSet<>();

    /**
     * Коннекторы подключения накопителей.
     */
    @NotNull
    @Size(min = 1)
    @OneToMany(
            mappedBy = "motherboard",
            cascade = CascadeType.ALL
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<MotherboardToStorageConnector> storageConnectors = new HashSet<>();

    /**
     * Добавляет коннектор питания вентилятора в указанном количестве.
     *
     * @param connector коннектор питания вентилятора
     * @param count     количество
     */
    public void addFanPowerConnector(final FanPowerConnector connector, final Integer count) {
        final var motherboardToConnector = new MotherboardToFanPowerConnector();

        motherboardToConnector.setMotherboard(this);
        motherboardToConnector.setFanPowerConnector(connector);
        motherboardToConnector.setCount(count);

        this.fanPowerConnectors.add(motherboardToConnector);
    }

    /**
     * Добавляет коннектор подключения накопителя в указанном количестве.
     *
     * @param connector коннектор подключения накопителя
     * @param count     количество
     */
    public void addStorageConnector(final StorageConnector connector, final Integer count) {
        final var cardToConnector = new MotherboardToStorageConnector();

        cardToConnector.setMotherboard(this);
        cardToConnector.setStorageConnector(connector);
        cardToConnector.setCount(count);

        this.storageConnectors.add(cardToConnector);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Motherboard that = (Motherboard) o;

        if (!Objects.equals(design, that.design)) return false;
        if (!Objects.equals(chipset, that.chipset)) return false;
        return Objects.equals(ramType, that.ramType);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (design != null ? design.hashCode() : 0);
        result = 31 * result + (chipset != null ? chipset.hashCode() : 0);
        result = 31 * result + (ramType != null ? ramType.hashCode() : 0);
        return result;
    }
}
