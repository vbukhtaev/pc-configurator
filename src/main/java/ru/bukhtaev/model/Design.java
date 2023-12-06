package ru.bukhtaev.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.dictionary.Vendor;

/**
 * Модель варианта исполнения.
 */
@Getter
@Setter
@Entity
@Table(
        name = "design",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@SuperBuilder
@NoArgsConstructor
public class Design extends NameableEntity {

    /**
     * Название поля, хранящего вендора.
     */
    public static final String FIELD_VENDOR = "vendor";

    /**
     * Вендор.
     */
    @ManyToOne
    @JoinColumn(name = "vendor_id", referencedColumnName = "id", nullable = false)
    protected Vendor vendor;
}
