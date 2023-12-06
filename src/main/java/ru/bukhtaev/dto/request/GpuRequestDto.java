package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Gpu;

import java.util.UUID;

/**
 * DTO для модели {@link Gpu}, используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Графический процессор")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class GpuRequestDto extends NameableRequestDto {

    /**
     * Объем видеопамяти (Мб).
     */
    @Schema(description = "Объем видеопамяти (Мб)")
    @Min(64)
    @NotNull
    protected Integer memorySize;

    /**
     * Энергопотребление (Вт).
     */
    @Schema(description = "Энергопотребление (Вт)")
    @Min(1)
    @NotNull
    protected Integer powerConsumption;

    /**
     * ID производителя.
     */
    @Schema(description = "ID производителя")
    @NotBlank
    protected UUID manufacturerId;

    /**
     * ID типа видеопамяти.
     */
    @Schema(description = "ID типа видеопамяти")
    @NotBlank
    protected UUID memoryTypeId;
}
