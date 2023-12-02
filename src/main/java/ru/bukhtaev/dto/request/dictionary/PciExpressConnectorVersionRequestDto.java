package ru.bukhtaev.dto.request.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.model.dictionary.PciExpressConnectorVersion;

import java.util.Set;
import java.util.UUID;

/**
 * DTO для модели {@link PciExpressConnectorVersion}, используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Версия коннектора PCI-Express")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class PciExpressConnectorVersionRequestDto extends NameableRequestDto {

    /**
     * ID более старых версий по отношению к текущей.
     */
    @Schema(description = "ID более старых версий по отношению к текущей")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<UUID> lowerVersionIds;
}
