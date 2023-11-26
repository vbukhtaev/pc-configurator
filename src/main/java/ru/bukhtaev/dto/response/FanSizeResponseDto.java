package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.dictionary.FanSize;

/**
 * DTO для модели {@link FanSize}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Размер вентилятора")
@Getter
@SuperBuilder
public class FanSizeResponseDto extends BaseResponseDto {

    /**
     * Длина.
     */
    @Schema(description = "Длина (в мм)")
    protected Integer length;

    /**
     * Ширина.
     */
    @Schema(description = "Ширина (в мм)")
    protected Integer width;

    /**
     * Толщина.
     */
    @Schema(description = "Толщина (в мм)")
    protected Integer height;
}
