package ru.bukhtaev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.ComputerCaseToExpansionBayFormat;
import ru.bukhtaev.model.cross.ComputerCaseToFanSize;
import ru.bukhtaev.model.dictionary.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Модель корпуса.
 */
@Getter
@Setter
@Entity
@Table(
        name = "computer_case",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@SuperBuilder
@NoArgsConstructor
public class ComputerCase extends NameableEntity {

    /**
     * Название поля, хранящего максимальную длину блока питания.
     */
    public static final String FIELD_MAX_PSU_LENGTH = "maxPsuLength";

    /**
     * Название поля, хранящего максимальную длину видеокарты.
     */
    public static final String FIELD_MAX_GRAPHICS_CARD_LENGTH = "maxGraphicsCardLength";

    /**
     * Название поля, хранящего максимальную высоту кулера.
     */
    public static final String FIELD_MAX_COOLER_HEIGHT = "maxCoolerHeight";

    /**
     * Название поля, хранящего вендора.
     */
    public static final String FIELD_VENDOR = "vendor";

    /**
     * Название поля, хранящего поддерживаемые форм-факторы материнских плат.
     */
    public static final String FIELD_SUPPORTED_MOTHERBOARD_FORM_FACTORS = "motherboardFormFactors";

    /**
     * Название поля, хранящего поддерживаемые форм-факторы блоков питания.
     */
    public static final String FIELD_SUPPORTED_PSU_FORM_FACTORS = "psuFormFactors";

    /**
     * Название поля, хранящего поддерживаемые форматы отсеков расширения.
     */
    public static final String FIELD_SUPPORTED_EXPANSION_BAY_FORMATS = "expansionBayFormats";

    /**
     * Название поля, хранящего поддерживаемые размеры вентиляторов.
     */
    public static final String FIELD_SUPPORTED_FAN_SIZES = "fanSizes";

    /**
     * Максимальная длина блока питания (мм).
     */
    @Min(1)
    @NotNull
    @Column(name = "max_psu_length", nullable = false)
    protected Integer maxPsuLength;

    /**
     * Максимальная длина видеокарты (мм).
     */
    @Min(1)
    @NotNull
    @Column(name = "max_graphics_card_length", nullable = false)
    protected Integer maxGraphicsCardLength;

    /**
     * Максимальная высота кулера (мм).
     */
    @Min(1)
    @NotNull
    @Column(name = "max_cooler_height", nullable = false)
    protected Integer maxCoolerHeight;

    /**
     * Вендор.
     */
    @ManyToOne
    @JoinColumn(name = "vendor_id", referencedColumnName = "id", nullable = false)
    protected Vendor vendor;

    /**
     * Поддерживаемые форм-факторы материнских плат.
     */
    @Size(min = 1)
    @ManyToMany
    @JoinTable(
            name = "computer_case_to_motherboard_form_factor",
            joinColumns = @JoinColumn(name = "computer_case_id"),
            inverseJoinColumns = @JoinColumn(name = "motherboard_form_factor_id")
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<MotherboardFormFactor> motherboardFormFactors = new HashSet<>();

    /**
     * Поддерживаемые форм-факторы блоков питания.
     */
    @Size(min = 1)
    @ManyToMany
    @JoinTable(
            name = "computer_case_to_psu_form_factor",
            joinColumns = @JoinColumn(name = "computer_case_id"),
            inverseJoinColumns = @JoinColumn(name = "psu_form_factor_id")
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<PsuFormFactor> psuFormFactors = new HashSet<>();

    /**
     * Поддерживаемые форматы отсеков расширения.
     */
    @NotNull
    @Size(min = 1)
    @OneToMany(
            mappedBy = "computerCase",
            cascade = CascadeType.ALL
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<ComputerCaseToExpansionBayFormat> expansionBayFormats = new HashSet<>();

    /**
     * Поддерживаемые размеры вентиляторов.
     */
    @NotNull
    @Size(min = 1)
    @OneToMany(
            mappedBy = "computerCase",
            cascade = CascadeType.ALL
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<ComputerCaseToFanSize> fanSizes = new HashSet<>();

    /**
     * Добавляет формат отсека расширения в указанном количестве.
     *
     * @param format формат отсека расширения
     * @param count  количество
     */
    public void addExpansionBayFormat(final ExpansionBayFormat format, final Integer count) {
        final var caseToFormat = new ComputerCaseToExpansionBayFormat();

        caseToFormat.setComputerCase(this);
        caseToFormat.setExpansionBayFormat(format);
        caseToFormat.setCount(count);

        this.expansionBayFormats.add(caseToFormat);
    }

    /**
     * Добавляет размер вентилятора в указанном количестве.
     *
     * @param size  размер вентилятора
     * @param count количество
     */
    public void addFanSize(final FanSize size, final Integer count) {
        final var caseToSize = new ComputerCaseToFanSize();

        caseToSize.setComputerCase(this);
        caseToSize.setFanSize(size);
        caseToSize.setCount(count);

        this.fanSizes.add(caseToSize);
    }
}
