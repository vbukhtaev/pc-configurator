package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.ComputerCaseToFanSize;

/**
 * DTO для модели {@link ComputerCaseToFanSize},
 * используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Поддерживаемые корпусом размеры вентиляторов")
@Getter
@SuperBuilder
public class ComputerCaseToFanSizeResponseDto extends BaseResponseDto {

    /**
     * Размер вентилятора.
     */
    @Schema(description = "Размер вентилятора")
    protected FanSizeResponseDto fanSize;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    protected Integer count;
}
