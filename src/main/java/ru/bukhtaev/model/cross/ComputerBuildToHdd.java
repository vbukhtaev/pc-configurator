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
import ru.bukhtaev.model.Hdd;

import java.util.UUID;

/**
 * Модель включенных в сборку ПК жестких дисков.
 */
@Getter
@Setter
@Entity
@Table(
        name = "computer_build_to_hdd",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "computer_build_id",
                "hdd_id"
        })
)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ComputerBuildToHdd {

    /**
     * Название поля, хранящего сборку ПК.
     */
    public static final String FIELD_COMPUTER_BUILD = "computerBuild";

    /**
     * Название поля, хранящего жесткий диск.
     */
    public static final String FIELD_HDD = "hdd";

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
     * Жесткий диск.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "hdd_id", referencedColumnName = "id", nullable = false)
    protected Hdd hdd;

    /**
     * Количество.
     */
    @Min(1)
    @NotNull
    @Column(name = "count", nullable = false)
    protected Integer count;
}
