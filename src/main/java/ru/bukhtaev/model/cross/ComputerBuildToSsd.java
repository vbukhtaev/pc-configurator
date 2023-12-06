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
import ru.bukhtaev.model.Ssd;

import java.util.UUID;

/**
 * Модель включенных в сборку ПК SSD-накопителей.
 */
@Getter
@Setter
@Entity
@Table(
        name = "computer_build_to_ssd",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "computer_build_id",
                "ssd_id"
        })
)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ComputerBuildToSsd {

    /**
     * Название поля, хранящего сборку ПК.
     */
    public static final String FIELD_COMPUTER_BUILD = "computerBuild";

    /**
     * Название поля, хранящего SSD-накопитель.
     */
    public static final String FIELD_SSD = "ssd";

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
     * SSD-накопитель.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "ssd_id", referencedColumnName = "id", nullable = false)
    protected Ssd ssd;

    /**
     * Количество.
     */
    @Min(1)
    @NotNull
    @Column(name = "count", nullable = false)
    protected Integer count;
}
