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
import ru.bukhtaev.model.dictionary.ExpansionBayFormat;

import java.util.UUID;

/**
 * Модель поддерживаемого корпусом формата отсека расширения.
 */
@Getter
@Setter
@Entity
@Table(
        name = "computer_case_to_expansion_bay_format",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "computer_case_id",
                "expansion_bay_format_id"
        })
)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ComputerCaseToExpansionBayFormat {

    /**
     * Название поля, хранящего корпус.
     */
    public static final String FIELD_COMPUTER_CASE = "computerCase";

    /**
     * Название поля, хранящего формат отсека расширения.
     */
    public static final String FIELD_EXPANSION_BAY_FORMAT = "expansionBayFormat";

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
     * Формат отсека расширения.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "expansion_bay_format_id", referencedColumnName = "id", nullable = false)
    protected ExpansionBayFormat expansionBayFormat;

    /**
     * Количество.
     */
    @Min(1)
    @NotNull
    @Column(name = "count", nullable = false)
    protected Integer count;
}
