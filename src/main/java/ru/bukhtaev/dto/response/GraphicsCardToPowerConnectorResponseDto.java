package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.GraphicsCardToPowerConnector;

/**
 * DTO для модели {@link GraphicsCardToPowerConnector},
 * используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Имеющиеся у видеокарты коннекторы питания")
@Getter
@SuperBuilder
public class GraphicsCardToPowerConnectorResponseDto extends BaseResponseDto {

    /**
     * Коннектор питания видеокарты.
     */
    @Schema(description = "Коннектор питания видеокарты")
    protected NameableResponseDto powerConnector;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    protected Integer count;
}
