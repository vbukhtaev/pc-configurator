package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import ru.bukhtaev.model.NameableEntity;

/**
 * DTO для модели {@link NameableEntity}, используемый в качестве тела HTTP-запроса.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NameableRequestDto {

    /**
     * Название.
     */
    @Schema(description = "Название")
    protected String name;
}
