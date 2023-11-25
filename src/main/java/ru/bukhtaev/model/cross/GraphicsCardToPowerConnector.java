package ru.bukhtaev.model.cross;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.GraphicsCard;
import ru.bukhtaev.model.dictionary.GraphicsCardPowerConnector;

import java.util.Objects;
import java.util.UUID;

/**
 * Модель имеющегося у видеокарты коннектора питания.
 */
@Getter
@Setter
@Entity
@Table(
        name = "graphics_card_to_power_connector",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "graphics_card_id",
                "power_connector_id"
        })
)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GraphicsCardToPowerConnector {

    /**
     * Название поля, хранящего видеокарту.
     */
    public static final String FIELD_GRAPHICS_CARD = "graphicsCard";

    /**
     * Название поля, хранящего коннектор питания видеокарты.
     */
    public static final String FIELD_POWER_CONNECTOR = "powerConnector";

    /**
     * Название поля, хранящего количество коннекторов питания.
     */
    public static final String FIELD_COUNT = "count";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;

    /**
     * Видеокарта.
     */
    @ManyToOne
    @JoinColumn(name = "graphics_card_id", referencedColumnName = "id", nullable = false)
    protected GraphicsCard graphicsCard;

    /**
     * Коннектор питания видеокарты.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "power_connector_id", referencedColumnName = "id", nullable = false)
    protected GraphicsCardPowerConnector powerConnector;

    /**
     * Количество коннекторов питания.
     */
    @Min(1)
    @NotNull
    @Column(name = "count", nullable = false)
    protected Integer count;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GraphicsCardToPowerConnector that = (GraphicsCardToPowerConnector) o;

        if (!Objects.equals(powerConnector, that.powerConnector))
            return false;
        return Objects.equals(count, that.count);
    }

    @Override
    public int hashCode() {
        int result = powerConnector != null ? powerConnector.hashCode() : 0;
        result = 31 * result + (count != null ? count.hashCode() : 0);
        return result;
    }
}
