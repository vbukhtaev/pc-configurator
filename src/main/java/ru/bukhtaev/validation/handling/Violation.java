package ru.bukhtaev.validation.handling;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Нарушение валидации.
 */
@Schema(description = "Нарушение валидации")
@Getter
public final class Violation {

    /**
     * Сообщение.
     */
    @Schema(description = "Сообщение")
    private final String message;

    /**
     * Названия параметров, значения которых нарушают правила валидации.
     */
    @Schema(description = "Названия параметров, значения которых нарушают правила валидации")
    private final String[] paramNames;

    /**
     * Конструктор.
     *
     * @param message    сообщение
     * @param paramNames названия параметров, значения которых нарушают правила валидации
     */
    public Violation(final String message, final String... paramNames) {
        this.message = message;
        this.paramNames = paramNames;
    }
}
