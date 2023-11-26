package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.MotherboardToStorageConnector;

/**
 * DTO для модели {@link MotherboardToStorageConnector},
 * используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Имеющиеся у материнской платы коннекторы подключения накопителей")
@Getter
@SuperBuilder
public class MotherboardToStorageConnectorResponseDto extends BaseResponseDto {

    /**
     * Коннектор подключения накопителя.
     */
    @Schema(description = "Коннектор подключения накопителя")
    protected NameableResponseDto storageConnector;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    protected Integer count;
}
