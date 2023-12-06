package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.ComputerCaseToExpansionBayFormat;

/**
 * DTO для модели {@link ComputerCaseToExpansionBayFormat},
 * используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Поддерживаемые корпусом форматы отсеков расширения")
@Getter
@SuperBuilder
public class ComputerCaseToExpansionBayFormatResponseDto extends BaseResponseDto {

    /**
     * Формат отсека расширения.
     */
    @Schema(description = "Формат отсека расширения")
    protected NameableResponseDto expansionBayFormat;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    protected Integer count;
}
