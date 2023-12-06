package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.CpuToRamType;

/**
 * DTO для модели {@link CpuToRamType}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Поддерживаемая процессором частота оперативной памяти")
@Getter
@SuperBuilder
public class CpuToRamTypeResponseDto extends BaseResponseDto {

    /**
     * Тип оперативной памяти.
     */
    @Schema(description = "Тип оперативной памяти")
    protected NameableResponseDto ramType;

    /**
     * Частота оперативной памяти (МГц).
     */
    @Schema(description = "Частота оперативной памяти (МГц)")
    protected Integer maxMemoryClock;
}
