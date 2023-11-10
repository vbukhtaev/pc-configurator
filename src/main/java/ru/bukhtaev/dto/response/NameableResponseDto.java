package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import ru.bukhtaev.model.NameableEntity;

import java.util.UUID;

/**
 * DTO для модели {@link NameableEntity}, используемый в качестве тела HTTP-ответа.
 */
@Getter
public class NameableResponseDto extends BaseResponseDto {

    /**
     * Название.
     */
    @Schema(description = "Название")
    protected String name;

    /**
     * Конструктор.
     *
     * @param id   ID
     * @param name название
     */
    public NameableResponseDto(
            final UUID id,
            final String name
    ) {
        super(id);
        this.name = name;
    }
}
