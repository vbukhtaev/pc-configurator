package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.CpuToRamType;

import java.util.UUID;

/**
 * DTO для модели {@link CpuToRamType}, используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Поддерживаемая процессором частота оперативной памяти")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class CpuToRamTypeRequestDto {

    /**
     * ID типа оперативной памяти.
     */
    @Schema(description = "ID типа оперативной памяти")
    @NotBlank
    protected UUID ramTypeId;

    /**
     * Частота оперативной памяти (МГц).
     */
    @Schema(description = "Частота оперативной памяти (МГц)")
    @Min(800)
    @NotNull
    protected Integer maxMemoryClock;
}
