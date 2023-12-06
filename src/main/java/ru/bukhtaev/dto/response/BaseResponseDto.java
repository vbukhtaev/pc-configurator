package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * Базовый DTO, используемый в качестве тела HTTP-ответа.
 */
@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class BaseResponseDto {

    /**
     * ID.
     */
    @Schema(description = "ID")
    protected UUID id;
}
