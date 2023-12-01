package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.ComputerBuildToHdd;

/**
 * DTO для модели {@link ComputerBuildToHdd},
 * используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Включенные в сборку ПК жесткие диски")
@Getter
@SuperBuilder
public class ComputerBuildToHddResponseDto extends BaseResponseDto {

    /**
     * Жесткий диск.
     */
    @Schema(description = "Жесткий диск")
    protected HddResponseDto hdd;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    protected Integer count;
}
