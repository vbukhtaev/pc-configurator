package ru.bukhtaev.model.cross;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.ComputerCase;
import ru.bukhtaev.model.dictionary.FanSize;

import java.util.UUID;

/**
 * Модель поддерживаемого корпусом размера вентилятора.
 */
@Getter
@Setter
@Entity
@Table(
        name = "computer_case_to_fan_size",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "computer_case_id",
                "fan_size_id"
        })
)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ComputerCaseToFanSize {

    /**
     * Название поля, хранящего корпус.
     */
    public static final String FIELD_COMPUTER_CASE = "computerCase";

    /**
     * Название поля, хранящего размер вентилятора.
     */
    public static final String FIELD_FAN_SIZE = "fanSize";

    /**
     * Название поля, хранящего количество.
     */
    public static final String FIELD_COUNT = "count";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;

    /**
     * Корпус.
     */
    @ManyToOne
    @JoinColumn(name = "computer_case_id", referencedColumnName = "id", nullable = false)
    protected ComputerCase computerCase;

    /**
     * Размер вентилятора.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "fan_size_id", referencedColumnName = "id", nullable = false)
    protected FanSize fanSize;

    /**
     * Количество.
     */
    @Min(1)
    @NotNull
    @Column(name = "count", nullable = false)
    protected Integer count;
}
