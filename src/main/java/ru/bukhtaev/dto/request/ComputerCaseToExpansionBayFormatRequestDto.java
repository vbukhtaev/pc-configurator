package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.ComputerCaseToExpansionBayFormat;

import java.util.UUID;

/**
 * DTO для модели {@link ComputerCaseToExpansionBayFormat},
 * используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Поддерживаемые корпусом форматы отсеков расширения")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ComputerCaseToExpansionBayFormatRequestDto {

    /**
     * ID формата отсека расширения.
     */
    @Schema(description = "ID формата отсека расширения")
    @NotBlank
    protected UUID expansionBayFormatId;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    @Min(1)
    @NotNull
    protected Integer count;
}
