package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.PsuToCpuPowerConnector;

/**
 * DTO для модели {@link PsuToCpuPowerConnector},
 * используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Имеющиеся у блока питания коннекторы питания процессоров")
@Getter
@SuperBuilder
public class PsuToCpuPowerConnectorResponseDto extends BaseResponseDto {

    /**
     * Коннектор питания процессора.
     */
    @Schema(description = "Коннектор питания процессора")
    protected NameableResponseDto cpuPowerConnector;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    protected Integer count;
}
