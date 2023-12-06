package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.RamModule;

/**
 * DTO для модели {@link RamModule}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Модуль оперативной памяти")
@Getter
@SuperBuilder
public class RamModuleResponseDto extends BaseResponseDto {

    /**
     * Частота (МГц).
     */
    @Schema(description = "Частота (МГц)")
    protected Integer clock;

    /**
     * Объем (Мб).
     */
    @Schema(description = "Объем (Мб)")
    protected Integer capacity;

    /**
     * Тип.
     */
    @Schema(description = "Тип")
    protected NameableResponseDto type;

    /**
     * Вариант исполнения.
     */
    @Schema(description = "Вариант исполнения")
    protected DesignResponseDto design;
}
