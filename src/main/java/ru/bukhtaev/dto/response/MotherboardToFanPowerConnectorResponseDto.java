package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.MotherboardToFanPowerConnector;

/**
 * DTO для модели {@link MotherboardToFanPowerConnector},
 * используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Имеющиеся у материнской платы коннекторы питания вентиляторов")
@Getter
@SuperBuilder
public class MotherboardToFanPowerConnectorResponseDto extends BaseResponseDto {

    /**
     * Коннектор питания вентилятора.
     */
    @Schema(description = "Коннектор питания вентилятора")
    protected NameableResponseDto fanPowerConnector;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    protected Integer count;
}
