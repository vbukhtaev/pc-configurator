package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import ru.bukhtaev.model.FanSize;

/**
 * DTO для модели {@link FanSize}, используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Размер вентилятора")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FanSizeRequestDto {

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
