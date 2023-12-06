package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.ComputerCaseToFanSize;

import java.util.UUID;

/**
 * DTO для модели {@link ComputerCaseToFanSize},
 * используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Поддерживаемые корпусом размеры вентиляторов")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ComputerCaseToFanSizeRequestDto {

    /**
     * ID размера вентилятора.
     */
    @Schema(description = "ID размера вентилятора")
    @NotBlank
    protected UUID fanSizeId;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    @Min(1)
    @NotNull
    protected Integer count;
}
