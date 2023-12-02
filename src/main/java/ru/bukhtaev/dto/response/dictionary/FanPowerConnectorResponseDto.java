package ru.bukhtaev.dto.response.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.dictionary.FanPowerConnector;

import java.util.Set;

/**
 * DTO для модели {@link FanPowerConnector}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Коннектор питания вентилятора")
@Getter
@SuperBuilder
public class FanPowerConnectorResponseDto extends NameableResponseDto {

    /**
     * Совместимые коннекторы.
     */
    @Schema(description = "Совместимые коннекторы")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<FanPowerConnectorResponseDto> compatibleConnectors;
}
