package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.GraphicsCardToPowerConnector;

import java.util.UUID;

/**
 * DTO для модели {@link GraphicsCardToPowerConnector},
 * используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Имеющиеся у видеокарты коннекторы питания")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class GraphicsCardToPowerConnectorRequestDto {

    /**
     * ID коннектора питания видеокарты.
     */
    @Schema(description = "ID коннектора питания видеокарты")
    @NotBlank
    protected UUID powerConnectorId;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    @Min(1)
    @NotNull
    protected Integer count;
}
