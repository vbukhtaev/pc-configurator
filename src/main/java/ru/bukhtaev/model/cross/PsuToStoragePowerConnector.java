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
import ru.bukhtaev.model.dictionary.StoragePowerConnector;

import java.util.UUID;

/**
 * Модель имеющихся у блока питания коннекторов питания накопителей.
 */
@Getter
@Setter
@Entity
@Table(
        name = "psu_to_storage_power_connector",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "psu_id",
                "storage_power_connector_id"
        })
)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PsuToStoragePowerConnector {

    /**
     * Название поля, хранящего блок питания.
     */
    public static final String FIELD_PSU = "psu";

    /**
     * Название поля, хранящего коннектор питания накопителя.
     */
    public static final String FIELD_STORAGE_POWER_CONNECTOR = "storagePowerConnector";

    /**
     * Название поля, хранящего количество коннекторов питания накопителей.
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
     * Коннектор питания накопителя.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "storage_power_connector_id", referencedColumnName = "id", nullable = false)
    protected StoragePowerConnector storagePowerConnector;

    /**
     * Количество.
     */
    @Min(1)
    @NotNull
    @Column(name = "count", nullable = false)
    protected Integer count;
}
