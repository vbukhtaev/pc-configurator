package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Chipset;

/**
 * DTO для модели {@link Chipset}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Чипсет")
@Getter
@SuperBuilder
public class ChipsetResponseDto extends NameableResponseDto {

    /**
     * Сокет.
     */
    @Schema(description = "Сокет")
    protected NameableResponseDto socket;
}
