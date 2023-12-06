package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.PsuToStoragePowerConnector;

import java.util.UUID;

/**
 * DTO для модели {@link PsuToStoragePowerConnector},
 * используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Имеющиеся у блока питания коннекторы питания накопителей")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class PsuToStoragePowerConnectorRequestDto {

    /**
     * ID коннектора питания накопителя.
     */
    @Schema(description = "ID коннектора питания накопителя")
    @NotBlank
    protected UUID storagePowerConnectorId;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    @Min(1)
    @NotNull
    protected Integer count;
}
