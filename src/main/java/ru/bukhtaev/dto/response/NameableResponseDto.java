package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.NameableEntity;

/**
 * DTO для модели {@link NameableEntity}, используемый в качестве тела HTTP-ответа.
 */
@Getter
@SuperBuilder
@NoArgsConstructor
public class NameableResponseDto extends BaseResponseDto {

    /**
     * Название.
     */
    @Schema(description = "Название")
    protected String name;
}
