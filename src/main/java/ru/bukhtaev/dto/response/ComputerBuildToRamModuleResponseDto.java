package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.ComputerBuildToRamModule;

/**
 * DTO для модели {@link ComputerBuildToRamModule},
 * используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Включенные в сборку ПК модули оперативной памяти")
@Getter
@SuperBuilder
public class ComputerBuildToRamModuleResponseDto extends BaseResponseDto {

    /**
     * Модуль оперативной памяти.
     */
    @Schema(description = "Модуль оперативной памяти")
    protected RamModuleResponseDto ramModule;

    /**
     * Количество.
     */
    @Schema(description = "Количество")
    protected Integer count;
}
