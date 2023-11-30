package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.ComputerCase;

import java.util.Set;

/**
 * DTO для модели {@link ComputerCase},
 * используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Корпус")
@Getter
@SuperBuilder
public class ComputerCaseResponseDto extends NameableResponseDto {

    /**
     * Максимальная длина блока питания (мм).
     */
    @Schema(description = "Максимальная длина блока питания (мм)")
    protected Integer maxPsuLength;

    /**
     * Максимальная длина видеокарты (мм).
     */
    @Schema(description = "Максимальная длина видеокарты (мм)")
    protected Integer maxGraphicsCardLength;

    /**
     * Максимальная высота кулера (мм).
     */
    @Schema(description = "Максимальная высота кулера (мм)")
    protected Integer maxCoolerHeight;

    /**
     * Вендор.
     */
    @Schema(description = "Вендор")
    protected NameableResponseDto vendor;

    /**
     * Поддерживаемые форм-факторы материнских плат.
     */
    @Schema(description = "Поддерживаемые форм-факторы материнских плат")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<NameableResponseDto> motherboardFormFactors;

    /**
     * Поддерживаемые форм-факторы блоков питания.
     */
    @Schema(description = "Поддерживаемые форм-факторы блоков питания")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<NameableResponseDto> psuFormFactors;

    /**
     * Поддерживаемые форматы отсеков расширения.
     */
    @Schema(description = "Поддерживаемые форматы отсеков расширения")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<ComputerCaseToExpansionBayFormatResponseDto> expansionBayFormats;

    /**
     * Поддерживаемые размеры вентиляторов.
     */
    @Schema(description = "Поддерживаемые размеры вентиляторов")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<ComputerCaseToFanSizeResponseDto> fanSizes;
}
