package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.RamModule;

import java.util.UUID;

/**
 * DTO для модели {@link RamModule}, используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Модуль оперативной памяти")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class RamModuleRequestDto {

    /**
     * Частота (МГц).
     */
    @Schema(description = "Частота (МГц)")
    @Min(1333)
    @NotNull
    protected Integer clock;

    /**
     * Объем (Мб).
     */
    @Schema(description = "Объем (Мб)")
    @Min(512)
    @NotNull
    protected Integer capacity;

    /**
     * ID типа.
     */
    @Schema(description = "ID типа")
    @NotBlank
    protected UUID typeId;

    /**
     * ID варианта исполнения.
     */
    @Schema(description = "ID варианта исполнения")
    @NotBlank
    protected UUID designId;
}
