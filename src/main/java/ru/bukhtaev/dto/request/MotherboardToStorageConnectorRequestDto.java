package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.MotherboardToStorageConnector;

import java.util.UUID;

/**
 * DTO для модели {@link MotherboardToStorageConnector},
 * используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Имеющиеся у материнской платы коннекторы подключения накопителей")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class MotherboardToStorageConnectorRequestDto {

    /**
     * ID коннектора подключения накопителя.
     */
    @Schema(description = "ID коннектора подключения накопителя")
    @NotBlank
    protected UUID storageConnectorId;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    @Min(1)
    @NotNull
    protected Integer count;
}
