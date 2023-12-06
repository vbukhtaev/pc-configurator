package ru.bukhtaev.model.cross;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Motherboard;
import ru.bukhtaev.model.dictionary.FanPowerConnector;

import java.util.UUID;

/**
 * Модель имеющихся у материнской платы коннекторов питания вентиляторов.
 */
@Getter
@Setter
@Entity
@Table(
        name = "motherboard_to_fan_power_connector",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "motherboard_id",
                "fan_power_connector_id"
        })
)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MotherboardToFanPowerConnector {

    /**
     * Название поля, хранящего материнскую плату.
     */
    public static final String FIELD_MOTHERBOARD = "motherboard";

    /**
     * Название поля, хранящего коннектор питания вентилятора.
     */
    public static final String FIELD_FAN_POWER_CONNECTOR = "fanPowerConnector";

    /**
     * Название поля, хранящего количество коннекторов питания вентиляторов.
     */
    public static final String FIELD_COUNT = "count";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;

    /**
     * Материнская плата.
     */
    @ManyToOne
    @JoinColumn(name = "motherboard_id", referencedColumnName = "id", nullable = false)
    protected Motherboard motherboard;

    /**
     * Коннектор питания вентилятора.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "fan_power_connector_id", referencedColumnName = "id", nullable = false)
    protected FanPowerConnector fanPowerConnector;

    /**
     * Количество.
     */
    @Min(1)
    @NotNull
    @Column(name = "count", nullable = false)
    protected Integer count;
}
