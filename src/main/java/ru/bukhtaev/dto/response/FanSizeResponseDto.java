package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import ru.bukhtaev.model.FanSize;

import java.util.UUID;

/**
 * DTO для модели {@link FanSize}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Размер вентилятора")
@Getter
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

    /**
     * Конструктор.
     *
     * @param id     ID
     * @param length длина
     * @param width  ширина
     * @param height толщина
     */
    @Builder
    public FanSizeResponseDto(
            final UUID id,
            final Integer length,
            final Integer width,
            final Integer height
    ) {
        super(id);
        this.length = length;
        this.width = width;
        this.height = height;
    }
}
