package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.PsuToStoragePowerConnector;

/**
 * DTO для модели {@link PsuToStoragePowerConnector},
 * используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Имеющиеся у блока питания коннекторы питания накопителей")
@Getter
@SuperBuilder
public class PsuToStoragePowerConnectorResponseDto extends BaseResponseDto {

    /**
     * Коннектор питания накопителя.
     */
    @Schema(description = "Коннектор питания накопителя")
    protected NameableResponseDto storagePowerConnector;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    protected Integer count;
}
