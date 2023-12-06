package ru.bukhtaev.dto.response.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.dictionary.StorageConnector;

import java.util.Set;

/**
 * DTO для модели {@link StorageConnector}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Коннектор подключения накопителя")
@Getter
@SuperBuilder
public class StorageConnectorResponseDto extends NameableResponseDto {

    /**
     * Совместимые коннекторы.
     */
    @Schema(description = "Совместимые коннекторы")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<StorageConnectorResponseDto> compatibleConnectors;
}
