package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.ComputerBuildToRamModule;

import java.util.UUID;

/**
 * DTO для модели {@link ComputerBuildToRamModule},
 * используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Включенные в сборку ПК модули оперативной памяти")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ComputerBuildToRamModuleRequestDto {

    /**
     * ID модуля оперативной памяти.
     */
    @Schema(description = "ID модуля оперативной памяти")
    @NotBlank
    protected UUID ramModuleId;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    @Min(1)
    @NotNull
    protected Integer count;
}
