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
import ru.bukhtaev.model.RamModule;

import java.util.UUID;

/**
 * Модель включенных в сборку ПК модулей оперативной памяти.
 */
@Getter
@Setter
@Entity
@Table(
        name = "computer_build_to_ram_module",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "computer_build_id",
                "ram_module_id"
        })
)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ComputerBuildToRamModule {

    /**
     * Название поля, хранящего сборку ПК.
     */
    public static final String FIELD_COMPUTER_BUILD = "computerBuild";

    /**
     * Название поля, хранящего модуль оперативной памяти.
     */
    public static final String FIELD_FAN = "ramModule";

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
     * Модуль оперативной памяти.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "ram_module_id", referencedColumnName = "id", nullable = false)
    protected RamModule ramModule;

    /**
     * Количество.
     */
    @Min(1)
    @NotNull
    @Column(name = "count", nullable = false)
    protected Integer count;
}
