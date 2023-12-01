package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.ComputerBuildToSsd;

import java.util.UUID;

/**
 * DTO для модели {@link ComputerBuildToSsd},
 * используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Включенные в сборку ПК SSD-накопители")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ComputerBuildToSsdRequestDto {

    /**
     * ID SSD-накопителя.
     */
    @Schema(description = "ID SSD-накопителя")
    @NotBlank
    protected UUID ssdId;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    @Min(1)
    @NotNull
    protected Integer count;
}
