package ru.bukhtaev.model;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.dictionary.ExpansionBayFormat;
import ru.bukhtaev.model.dictionary.StorageConnector;
import ru.bukhtaev.model.dictionary.StoragePowerConnector;
import ru.bukhtaev.model.dictionary.Vendor;

import java.util.Objects;

/**
 * Модель устройства хранения данных.
 */
@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
public abstract class StorageDevice extends NameableEntity {

    /**
     * Название поля, хранящего объем памяти.
     */
    public static final String FIELD_CAPACITY = "capacity";

    /**
     * Название поля, хранящего скорость чтения.
     */
    public static final String FIELD_READING_SPEED = "readingSpeed";

    /**
     * Название поля, хранящего скорость записи.
     */
    public static final String FIELD_WRITING_SPEED = "writingSpeed";

    /**
     * Название поля, хранящего вендора.
     */
    public static final String FIELD_VENDOR = "vendor";

    /**
     * Название поля, хранящего коннектор подключения.
     */
    public static final String FIELD_CONNECTOR = "connector";

    /**
     * Название поля, хранящего коннектор питания.
     */
    public static final String FIELD_POWER_CONNECTOR = "powerConnector";

    /**
     * Название поля, хранящего формат слота расширения.
     */
    public static final String FIELD_EXPANSION_BAY_FORMAT = "expansionBayFormat";

    /**
     * Объем памяти.
     */
    @Min(120)
    @NotNull
    @Column(name = "capacity", nullable = false)
    protected Integer capacity;

    /**
     * Скорость чтения (Мб/с).
     */
    @Min(1)
    @NotNull
    @Column(name = "reading_speed", nullable = false)
    protected Integer readingSpeed;

    /**
     * Скорость записи (Мб/с).
     */
    @Min(1)
    @NotNull
    @Column(name = "writing_speed", nullable = false)
    protected Integer writingSpeed;

    /**
     * Вендор.
     */
    @ManyToOne
    @JoinColumn(name = "vendor_id", referencedColumnName = "id", nullable = false)
    protected Vendor vendor;

    /**
     * Коннектор подключения.
     */
    @ManyToOne
    @JoinColumn(name = "connector_id", referencedColumnName = "id", nullable = false)
    protected StorageConnector connector;

    /**
     * Коннектор питания.
     */
    @ManyToOne
    @JoinColumn(name = "power_connector_id", referencedColumnName = "id")
    protected StoragePowerConnector powerConnector;

    /**
     * Формат слота расширения.
     */
    @ManyToOne
    @JoinColumn(name = "expansion_bay_format_id", referencedColumnName = "id")
    protected ExpansionBayFormat expansionBayFormat;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        StorageDevice that = (StorageDevice) o;

        return Objects.equals(capacity, that.capacity);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (capacity != null ? capacity.hashCode() : 0);
        return result;
    }
}
