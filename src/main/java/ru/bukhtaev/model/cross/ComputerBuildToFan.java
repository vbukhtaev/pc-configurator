package ru.bukhtaev.model.cross;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Fan;

import java.util.UUID;

/**
 * Модель включенных в сборку ПК вентиляторов.
 */
@Getter
@Setter
@Entity
@Table(
        name = "computer_build_to_fan",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "computer_build_id",
                "fan_id"
        })
)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ComputerBuildToFan {

    /**
     * Название поля, хранящего сборку ПК.
     */
    public static final String FIELD_COMPUTER_BUILD = "computerBuild";

    /**
     * Название поля, хранящего вентилятор.
     */
    public static final String FIELD_FAN = "fan";

    /**
     * Название поля, хранящего количество.
     */
    public static final String FIELD_COUNT = "count";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;

    /**
     * Сборка ПК.
     */
    @ManyToOne
    @JoinColumn(name = "computer_build_id", referencedColumnName = "id", nullable = false)
    protected ComputerBuild computerBuild;

    /**
     * Вентилятор.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "fan_id", referencedColumnName = "id", nullable = false)
    protected Fan fan;

    /**
     * Количество.
     */
    @Min(1)
    @NotNull
    @Column(name = "count", nullable = false)
    protected Integer count;
}
