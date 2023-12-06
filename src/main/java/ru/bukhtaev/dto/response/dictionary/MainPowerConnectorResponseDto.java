package ru.bukhtaev.dto.response.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.dictionary.MainPowerConnector;

import java.util.Set;

/**
 * DTO для модели {@link MainPowerConnector}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Основной коннектор питания")
@Getter
@SuperBuilder
public class MainPowerConnectorResponseDto extends NameableResponseDto {

    /**
     * Совместимые коннекторы.
     */
    @Schema(description = "Совместимые коннекторы")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<MainPowerConnectorResponseDto> compatibleConnectors;
}
