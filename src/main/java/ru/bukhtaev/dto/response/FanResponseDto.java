package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Fan;

/**
 * DTO для модели {@link Fan}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Вентилятор")
@Getter
@SuperBuilder
public class FanResponseDto extends NameableResponseDto {

    /**
     * Вендор.
     */
    @Schema(description = "Вендор")
    protected NameableResponseDto vendor;

    /**
     * Размер.
     */
    @Schema(description = "Размер")
    protected FanSizeResponseDto size;

    /**
     * Коннектор питания.
     */
    @Schema(description = "Коннектор питания")
    protected NameableResponseDto powerConnector;
}
