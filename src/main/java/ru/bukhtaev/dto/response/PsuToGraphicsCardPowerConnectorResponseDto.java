package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.PsuToGraphicsCardPowerConnector;

/**
 * DTO для модели {@link PsuToGraphicsCardPowerConnector},
 * используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Имеющиеся у блока питания коннекторы питания видеокарт")
@Getter
@SuperBuilder
public class PsuToGraphicsCardPowerConnectorResponseDto extends BaseResponseDto {

    /**
     * Коннектор питания видеокарты.
     */
    @Schema(description = "Коннектор питания видеокарты")
    protected NameableResponseDto graphicsCardPowerConnector;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    protected Integer count;
}
