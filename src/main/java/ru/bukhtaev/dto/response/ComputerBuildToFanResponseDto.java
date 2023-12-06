package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.ComputerBuildToFan;

/**
 * DTO для модели {@link ComputerBuildToFan},
 * используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Включенные в сборку ПК вентиляторы")
@Getter
@SuperBuilder
public class ComputerBuildToFanResponseDto extends BaseResponseDto {

    /**
     * Вентилятор.
     */
    @Schema(description = "Вентилятор")
    protected FanResponseDto fan;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    protected Integer count;
}
