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
 * Модель процессорного кулера.
 */
@Getter
@Setter
@Entity
@Table(
        name = "cooler",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@SuperBuilder
@NoArgsConstructor
public class Cooler extends NameableEntity {

    /**
     * Название поля, хранящего рассеиваемую мощность.
     */
    public static final String FIELD_POWER_DISSIPATION = "powerDissipation";

    /**
     * Название поля, хранящего высоту.
     */
    public static final String FIELD_HEIGHT = "height";

    /**
     * Название поля, хранящего вендора.
     */
    public static final String FIELD_VENDOR = "vendor";

    /**
     * Название поля, хранящего размер вентилятора.
     */
    public static final String FIELD_FAN_SIZE = "fanSize";

    /**
     * Название поля, хранящего коннектор питания вентилятора.
     */
    public static final String FIELD_POWER_CONNECTOR = "powerConnector";

    /**
     * Название поля, хранящего поддерживаемые сокеты.
     */
    public static final String FIELD_SUPPORTED_SOCKETS = "supportedSockets";

    /**
     * Рассеиваемая мощность (Вт).
     */
    @Min(1)
    @NotNull
    @Column(name = "power_dissipation", nullable = false)
    protected Integer powerDissipation;

    /**
     * Высота (мм).
     */
    @Min(1)
    @NotNull
    @Column(name = "height", nullable = false)
    protected Integer height;

    /**
     * Вендор.
     */
    @ManyToOne
    @JoinColumn(name = "vendor_id", referencedColumnName = "id", nullable = false)
    protected Vendor vendor;

    /**
     * Размер вентилятора.
     */
    @ManyToOne
    @JoinColumn(name = "fan_size_id", referencedColumnName = "id", nullable = false)
    protected FanSize fanSize;

    /**
     * Коннектор питания вентилятора.
     */
    @ManyToOne
    @JoinColumn(name = "power_connector_id", referencedColumnName = "id", nullable = false)
    protected FanPowerConnector powerConnector;

    /**
     * Поддерживаемые сокеты.
     */
    @Size(min = 1)
    @ManyToMany
    @JoinTable(
            name = "cooler_to_socket",
            joinColumns = @JoinColumn(name = "cooler_id"),
            inverseJoinColumns = @JoinColumn(name = "socket_id")
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<Socket> supportedSockets = new HashSet<>();
}
