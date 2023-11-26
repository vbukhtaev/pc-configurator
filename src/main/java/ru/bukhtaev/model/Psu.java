package ru.bukhtaev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.PsuToCpuPowerConnector;
import ru.bukhtaev.model.cross.PsuToGraphicsCardPowerConnector;
import ru.bukhtaev.model.cross.PsuToStoragePowerConnector;
import ru.bukhtaev.model.dictionary.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Модель блока питания.
 */
@Getter
@Setter
@Entity
@Table(
        name = "psu",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@SuperBuilder
@NoArgsConstructor
public class Psu extends NameableEntity {

    /**
     * Название поля, хранящего мощность.
     */
    public static final String FIELD_POWER = "power";

    /**
     * Название поля, хранящего мощность по линии 12V.
     */
    public static final String FIELD_POWER_12V = "power12V";

    /**
     * Название поля, хранящего длину.
     */
    public static final String FIELD_LENGTH = "length";

    /**
     * Название поля, хранящего вендора.
     */
    public static final String FIELD_VENDOR = "vendor";

    /**
     * Название поля, хранящего форм-фактор.
     */
    public static final String FIELD_FORM_FACTOR = "formFactor";

    /**
     * Название поля, хранящего сертификат.
     */
    public static final String FIELD_CERTIFICATE = "certificate";

    /**
     * Название поля, хранящего основной коннектор питания.
     */
    public static final String FIELD_MAIN_POWER_CONNECTOR = "mainPowerConnector";

    /**
     * Название поля, хранящего коннекторы питания процессоров.
     */
    public static final String FIELD_CPU_POWER_CONNECTORS = "cpuPowerConnectors";

    /**
     * Название поля, хранящего коннекторы питания накопителей.
     */
    public static final String FIELD_STORAGE_POWER_CONNECTORS = "storagePowerConnectors";

    /**
     * Название поля, хранящего коннекторы питания видеокарт.
     */
    public static final String FIELD_GRAPHICS_CARD_POWER_CONNECTORS = "graphicsCardPowerConnectors";

    /**
     * Мощность (Вт).
     */
    @Min(1)
    @NotNull
    @Column(name = "power", nullable = false)
    protected Integer power;

    /**
     * Мощность по линии 12V (Вт).
     */
    @Min(1)
    @NotNull
    @Column(name = "power_12v", nullable = false)
    protected Integer power12V;

    /**
     * Длина (мм).
     */
    @Min(1)
    @NotNull
    @Column(name = "length", nullable = false)
    protected Integer length;

    /**
     * Вендор.
     */
    @ManyToOne
    @JoinColumn(name = "vendor_id", referencedColumnName = "id", nullable = false)
    protected Vendor vendor;

    /**
     * Форм-фактор.
     */
    @ManyToOne
    @JoinColumn(name = "form_factor_id", referencedColumnName = "id", nullable = false)
    protected PsuFormFactor formFactor;

    /**
     * Сертификат.
     */
    @ManyToOne
    @JoinColumn(name = "certificate_id", referencedColumnName = "id", nullable = false)
    protected PsuCertificate certificate;

    /**
     * Основной коннектор питания.
     */
    @ManyToOne
    @JoinColumn(name = "main_power_connector_id", referencedColumnName = "id", nullable = false)
    protected MainPowerConnector mainPowerConnector;

    /**
     * Коннекторы питания процессоров.
     */
    @NotNull
    @Size(min = 1)
    @OneToMany(
            mappedBy = "psu",
            cascade = CascadeType.ALL
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<PsuToCpuPowerConnector> cpuPowerConnectors = new HashSet<>();

    /**
     * Коннекторы питания накопителей.
     */
    @NotNull
    @Size(min = 1)
    @OneToMany(
            mappedBy = "psu",
            cascade = CascadeType.ALL
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<PsuToStoragePowerConnector> storagePowerConnectors = new HashSet<>();

    /**
     * Коннекторы питания видеокарт.
     */
    @NotNull
    @Size(min = 1)
    @OneToMany(
            mappedBy = "psu",
            cascade = CascadeType.ALL
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<PsuToGraphicsCardPowerConnector> graphicsCardPowerConnectors = new HashSet<>();

    /**
     * Добавляет коннектор питания процессора в указанном количестве.
     *
     * @param connector коннектор питания процессора
     * @param count     количество
     */
    public void addCpuPowerConnector(final CpuPowerConnector connector, final Integer count) {
        final var psuToConnector = new PsuToCpuPowerConnector();

        psuToConnector.setPsu(this);
        psuToConnector.setCpuPowerConnector(connector);
        psuToConnector.setCount(count);

        this.cpuPowerConnectors.add(psuToConnector);
    }

    /**
     * Добавляет коннектор питания накопителя в указанном количестве.
     *
     * @param connector коннектор питания накопителя
     * @param count     количество
     */
    public void addStoragePowerConnector(final StoragePowerConnector connector, final Integer count) {
        final var psuToConnector = new PsuToStoragePowerConnector();

        psuToConnector.setPsu(this);
        psuToConnector.setStoragePowerConnector(connector);
        psuToConnector.setCount(count);

        this.storagePowerConnectors.add(psuToConnector);
    }

    /**
     * Добавляет коннектор питания видеокарты в указанном количестве.
     *
     * @param connector коннектор питания видеокарты
     * @param count     количество
     */
    public void addGraphicsCardPowerConnector(final GraphicsCardPowerConnector connector, final Integer count) {
        final var psuToConnector = new PsuToGraphicsCardPowerConnector();

        psuToConnector.setPsu(this);
        psuToConnector.setGraphicsCardPowerConnector(connector);
        psuToConnector.setCount(count);

        this.graphicsCardPowerConnectors.add(psuToConnector);
    }
}
