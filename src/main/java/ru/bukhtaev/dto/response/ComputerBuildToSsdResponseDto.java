package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.ComputerBuildToSsd;

/**
 * DTO для модели {@link ComputerBuildToSsd},
 * используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Включенные в сборку ПК SSD-накопители")
@Getter
@SuperBuilder
public class ComputerBuildToSsdResponseDto extends BaseResponseDto {

    /**
     * SSD-накопитель.
     */
    @Schema(description = "SSD-накопитель")
    protected SsdResponseDto ssd;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    protected Integer count;
}
