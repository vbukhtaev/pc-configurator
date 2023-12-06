package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.ComputerCase;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO для модели {@link ComputerCase},
 * используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Корпус")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ComputerCaseRequestDto extends NameableRequestDto {

    /**
     * Максимальная длина блока питания (мм).
     */
    @Schema(description = "Максимальная длина блока питания (мм)")
    @Min(1)
    @NotNull
    protected Integer maxPsuLength;

    /**
     * Максимальная длина видеокарты (мм).
     */
    @Schema(description = "Максимальная длина видеокарты (мм)")
    @Min(1)
    @NotNull
    protected Integer maxGraphicsCardLength;

    /**
     * Максимальная высота кулера (мм).
     */
    @Schema(description = "Максимальная высота кулера (мм)")
    @Min(1)
    @NotNull
    protected Integer maxCoolerHeight;

    /**
     * ID вендора.
     */
    @Schema(description = "ID вендора")
    @NotBlank
    protected UUID vendorId;

    /**
     * ID поддерживаемых форм-факторов материнских плат.
     */
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<UUID> motherboardFormFactorIds;

    /**
     * ID поддерживаемых форм-факторов блоков питания.
     */
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<UUID> psuFormFactorIds;

    /**
     * Поддерживаемые форматы отсеков расширения.
     */
    @Schema(description = "Поддерживаемые форматы отсеков расширения")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<ComputerCaseToExpansionBayFormatRequestDto> expansionBayFormats;

    /**
     * Поддерживаемые размеры вентиляторов.
     */
    @Schema(description = "Поддерживаемые размеры вентиляторов")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<ComputerCaseToFanSizeRequestDto> fanSizes;

    /**
     * Добавляет формат отсека расширения в указанном количестве.
     *
     * @param expansionBayFormatId ID формата отсека расширения
     * @param count                количество
     */
    public void addExpansionBayFormat(final UUID expansionBayFormatId, final Integer count) {
        if (this.expansionBayFormats == null) {
            this.expansionBayFormats = new HashSet<>();
        }

        final var caseToFormat = new ComputerCaseToExpansionBayFormatRequestDto();

        caseToFormat.setExpansionBayFormatId(expansionBayFormatId);
        caseToFormat.setCount(count);

        this.expansionBayFormats.add(caseToFormat);
    }

    /**
     * Добавляет размер вентилятора в указанном количестве.
     *
     * @param fanSizeId ID размера вентилятора
     * @param count     количество
     */
    public void addFanSize(final UUID fanSizeId, final Integer count) {
        if (this.fanSizes == null) {
            this.fanSizes = new HashSet<>();
        }

        final var caseToSize = new ComputerCaseToFanSizeRequestDto();

        caseToSize.setFanSizeId(fanSizeId);
        caseToSize.setCount(count);

        this.fanSizes.add(caseToSize);
    }
}
