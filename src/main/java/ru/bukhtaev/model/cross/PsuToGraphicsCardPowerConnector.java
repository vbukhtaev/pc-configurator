package ru.bukhtaev.model.cross;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Psu;
import ru.bukhtaev.model.dictionary.GraphicsCardPowerConnector;

import java.util.UUID;

/**
 * Модель имеющихся у блока питания коннекторов питания видеокарт.
 */
@Getter
@Setter
@Entity
@Table(
        name = "psu_to_graphics_card_power_connector",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "psu_id",
                "graphics_card_power_connector_id"
        })
)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PsuToGraphicsCardPowerConnector {

    /**
     * Название поля, хранящего блок питания.
     */
    public static final String FIELD_PSU = "psu";

    /**
     * Название поля, хранящего коннектор питания видеокарты.
     */
    public static final String FIELD_GRAPHICS_CARD_POWER_CONNECTOR = "graphicsCardPowerConnector";

    /**
     * Название поля, хранящего количество коннекторов питания видеокарт.
     */
    public static final String FIELD_COUNT = "count";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;

    /**
     * Блок питания.
     */
    @ManyToOne
    @JoinColumn(name = "psu_id", referencedColumnName = "id", nullable = false)
    protected Psu psu;

    /**
     * Коннектор питания видеокарты.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "graphics_card_power_connector_id", referencedColumnName = "id", nullable = false)
    protected GraphicsCardPowerConnector graphicsCardPowerConnector;

    /**
     * Количество.
     */
    @Min(1)
    @NotNull
    @Column(name = "count", nullable = false)
    protected Integer count;
}
