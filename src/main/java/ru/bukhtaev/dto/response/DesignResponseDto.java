package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Design;

/**
 * DTO для модели {@link Design}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Вариант исполнения")
@Getter
@SuperBuilder
public class DesignResponseDto extends NameableResponseDto {

    /**
     * Вендор
     */
    @Schema(description = "Вендор")
    protected NameableResponseDto vendor;
}
