package ru.bukhtaev.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.dictionary.FanPowerConnector;
import ru.bukhtaev.model.dictionary.FanSize;
import ru.bukhtaev.model.dictionary.Vendor;

import java.util.Objects;

/**
 * Модель вентилятора.
 */
@Getter
@Setter
@Entity
@Table(
        name = "fan",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "size_id"})
)
@SuperBuilder
@NoArgsConstructor
public class Fan extends NameableEntity {

    /**
     * Название поля, хранящего вендора.
     */
    public static final String FIELD_VENDOR = "vendor";

    /**
     * Название поля, хранящего размер.
     */
    public static final String FIELD_SIZE = "size";

    /**
     * Название поля, хранящего коннектор питания.
     */
    public static final String FIELD_POWER_CONNECTOR = "powerConnector";

    /**
     * Вендор.
     */
    @ManyToOne
    @JoinColumn(name = "vendor_id", referencedColumnName = "id", nullable = false)
    protected Vendor vendor;

    /**
     * Размер.
     */
    @ManyToOne
    @JoinColumn(name = "size_id", referencedColumnName = "id", nullable = false)
    protected FanSize size;

    /**
     * Коннектор питания.
     */
    @ManyToOne
    @JoinColumn(name = "power_connector_id", referencedColumnName = "id", nullable = false)
    protected FanPowerConnector powerConnector;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Fan fan = (Fan) o;

        return Objects.equals(size, fan.size);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (size != null ? size.hashCode() : 0);
        return result;
    }
}
