package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Hdd;

/**
 * DTO для модели {@link Hdd}, используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Жесткий диск")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class HddRequestDto extends StorageDeviceRequestDto {

    /**
     * Скорость вращения шпинделя (об/мин).
     */
    @Min(5400)
    @NotNull
    @Schema(description = "Скорость вращения шпинделя (об/мин)")
    protected Integer spindleSpeed;

    /**
     * Объем кэш-памяти (Мб).
     */
    @Min(32)
    @NotNull
    @Schema(description = "Объем кэш-памяти (Мб)")
    protected Integer cacheSize;
}
