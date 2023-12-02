package ru.bukhtaev.dto.response.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.dictionary.CpuPowerConnector;

import java.util.Set;

/**
 * DTO для модели {@link CpuPowerConnector}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Коннектор питания процессора")
@Getter
@SuperBuilder
public class CpuPowerConnectorResponseDto extends NameableResponseDto {

    /**
     * Совместимые коннекторы.
     */
    @Schema(description = "Совместимые коннекторы")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<CpuPowerConnectorResponseDto> compatibleConnectors;
}
