package ru.bukhtaev.dto.request.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.model.dictionary.MainPowerConnector;

import java.util.Set;
import java.util.UUID;

/**
 * DTO для модели {@link MainPowerConnector}, используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Основной коннектор питания")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class MainPowerConnectorRequestDto extends NameableRequestDto {

    /**
     * ID совместимых коннекторов.
     */
    @Schema(description = "ID совместимых коннекторов")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<UUID> compatibleConnectorIds;
}
