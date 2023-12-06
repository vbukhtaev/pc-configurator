package ru.bukhtaev.dto.request.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.model.dictionary.CpuPowerConnector;

import java.util.Set;
import java.util.UUID;

/**
 * DTO для модели {@link CpuPowerConnector}, используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Коннектор питания процессора")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class CpuPowerConnectorRequestDto extends NameableRequestDto {

    /**
     * ID совместимых коннекторов.
     */
    @Schema(description = "ID совместимых коннекторов")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<UUID> compatibleConnectorIds;
}
