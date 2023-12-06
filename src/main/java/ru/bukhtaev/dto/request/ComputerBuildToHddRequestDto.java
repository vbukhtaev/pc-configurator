package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.ComputerBuildToHdd;

import java.util.UUID;

/**
 * DTO для модели {@link ComputerBuildToHdd},
 * используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Включенные в сборку ПК жесткие диски")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ComputerBuildToHddRequestDto {

    /**
     * ID жесткого диска.
     */
    @Schema(description = "ID жесткого диска")
    @NotBlank
    protected UUID hddId;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    @Min(1)
    @NotNull
    protected Integer count;
}
