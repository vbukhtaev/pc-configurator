package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Gpu;

/**
 * DTO для модели {@link Gpu}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Графический процессор")
@Getter
@SuperBuilder
public class GpuResponseDto extends NameableResponseDto {

    /**
     * Объем видеопамяти (Мб).
     */
    @Schema(description = "Объем видеопамяти (Мб)")
    protected Integer memorySize;

    /**
     * Энергопотребление (Вт).
     */
    @Schema(description = "Энергопотребление (Вт)")
    protected Integer powerConsumption;

    /**
     * Производитель.
     */
    @Schema(description = "Производитель")
    protected NameableResponseDto manufacturer;

    /**
     * Тип видеопамяти.
     */
    @Schema(description = "Тип видеопамяти")
    protected NameableResponseDto memoryType;
}
