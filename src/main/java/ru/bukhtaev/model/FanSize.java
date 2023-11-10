package ru.bukhtaev.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

/**
 * Модель размера вентилятора.
 */
@Getter
@Setter
@Entity
@Table(
        name = "fan_size",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "length",
                "width",
                "height"
        })
)
@NoArgsConstructor
public class FanSize extends BaseEntity {

    /**
     * Название поля, хранящего название.
     */
    public static final String FIELD_LENGTH = "length";

    /**
     * Название поля, хранящего название.
     */
    public static final String FIELD_WIDTH = "width";

    /**
     * Название поля, хранящего название.
     */
    public static final String FIELD_HEIGHT = "height";

    /**
     * Длина.
     */
    @Min(1)
    @NotNull
    @Column(name = "length", nullable = false)
    protected Integer length;

    /**
     * Ширина.
     */
    @Min(1)
    @NotNull
    @Column(name = "width", nullable = false)
    protected Integer width;

    /**
     * Высота.
     */
    @Min(1)
    @NotNull
    @Column(name = "height", nullable = false)
    protected Integer height;

    /**
     * Конструктор
     *
     * @param id     ID
     * @param length длина
     * @param width  ширина
     * @param height высота
     */
    @Builder
    public FanSize(
            final UUID id,
            final @NotNull Integer length,
            final @NotNull Integer width,
            final @NotNull Integer height
    ) {
        super(id);
        this.length = length;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        FanSize fanSize = (FanSize) o;

        if (!Objects.equals(length, fanSize.length)) return false;
        if (!Objects.equals(width, fanSize.width)) return false;
        return Objects.equals(height, fanSize.height);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (length != null ? length.hashCode() : 0);
        result = 31 * result + (width != null ? width.hashCode() : 0);
        result = 31 * result + (height != null ? height.hashCode() : 0);
        return result;
    }
}
