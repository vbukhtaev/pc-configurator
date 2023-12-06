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
import ru.bukhtaev.model.dictionary.CpuPowerConnector;

import java.util.UUID;

/**
 * Модель имеющихся у блока питания коннекторов питания процессоров.
 */
@Getter
@Setter
@Entity
@Table(
        name = "psu_to_cpu_power_connector",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "psu_id",
                "cpu_power_connector_id"
        })
)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PsuToCpuPowerConnector {

    /**
     * Название поля, хранящего блок питания.
     */
    public static final String FIELD_PSU = "psu";

    /**
     * Название поля, хранящего коннектор питания процессора.
     */
    public static final String FIELD_CPU_POWER_CONNECTOR = "cpuPowerConnector";

    /**
     * Название поля, хранящего количество коннекторов питания процессоров.
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
     * Коннектор питания процессора.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "cpu_power_connector_id", referencedColumnName = "id", nullable = false)
    protected CpuPowerConnector cpuPowerConnector;

    /**
     * Количество.
     */
    @Min(1)
    @NotNull
    @Column(name = "count", nullable = false)
    protected Integer count;
}
