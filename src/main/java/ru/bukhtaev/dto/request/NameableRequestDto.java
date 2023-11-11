package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.NameableEntity;

/**
 * DTO для модели {@link NameableEntity}, используемый в качестве тела HTTP-запроса.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class NameableRequestDto {

    /**
     * Название.
     */
    @Schema(description = "Название")
    protected String name;
}
