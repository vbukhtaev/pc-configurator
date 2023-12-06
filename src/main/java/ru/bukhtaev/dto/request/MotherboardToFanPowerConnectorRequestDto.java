package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.MotherboardToFanPowerConnector;

import java.util.UUID;

/**
 * DTO для модели {@link MotherboardToFanPowerConnector},
 * используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Имеющиеся у материнской платы коннекторы питания вентиляторов")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class MotherboardToFanPowerConnectorRequestDto {

    /**
     * ID коннектора питания вентилятора.
     */
    @Schema(description = "ID коннектора питания вентилятора")
    @NotBlank
    protected UUID fanPowerConnectorId;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    @Min(1)
    @NotNull
    protected Integer count;
}
