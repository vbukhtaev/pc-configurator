package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Hdd;

/**
 * DTO для модели {@link Hdd}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Жесткий диск")
@Getter
@SuperBuilder
public class HddResponseDto extends StorageDeviceResponseDto {

    /**
     * Скорость вращения шпинделя (об/мин).
     */
    @Schema(description = "Скорость вращения шпинделя (об/мин)")
    protected Integer spindleSpeed;

    /**
     * Объем кэш-памяти (Мб).
     */
    @Schema(description = "Объем кэш-памяти (Мб)")
    protected Integer cacheSize;
}
