package ru.bukhtaev.dto.response.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.dictionary.PciExpressConnectorVersion;

import java.util.Set;

/**
 * DTO для модели {@link PciExpressConnectorVersion}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Версия коннектора PCI-Express")
@Getter
@SuperBuilder
public class PciExpressConnectorVersionResponseDto extends NameableResponseDto {

    /**
     * Более старые версии по отношению к текущей.
     */
    @Schema(description = "Более старые версии по отношению к текущей")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<PciExpressConnectorVersionResponseDto> lowerVersions;
}
