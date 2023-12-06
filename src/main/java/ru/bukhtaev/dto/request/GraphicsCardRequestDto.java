package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.GraphicsCard;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO для модели {@link GraphicsCard}, используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Видеокарта")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class GraphicsCardRequestDto {

    /**
     * Длина (мм).
     */
    @Schema(description = "Длина (мм)")
    @Min(40)
    @NotNull
    protected Integer length;

    /**
     * ID графического процессора.
     */
    @Schema(description = "ID графического процессора")
    @NotBlank
    protected UUID gpuId;

    /**
     * ID варианта исполнения.
     */
    @Schema(description = "ID варианта исполнения")
    @NotBlank
    protected UUID designId;

    /**
     * ID версии коннектора PCI-Express.
     */
    @Schema(description = "ID версии коннектора PCI-Express")
    @NotBlank
    protected UUID pciExpressConnectorVersionId;

    /**
     * Коннекторы питания.
     */
    @Schema(description = "Коннекторы питания")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<GraphicsCardToPowerConnectorRequestDto> powerConnectors;

    /**
     * Добавляет коннектор питания видеокарты в указанном количестве.
     *
     * @param powerConnectorId ID коннектора питания видеокарты
     * @param count            количество
     */
    public void addPowerConnector(final UUID powerConnectorId, final Integer count) {
        if (this.powerConnectors == null) {
            this.powerConnectors = new HashSet<>();
        }

        final var cardToConnector = new GraphicsCardToPowerConnectorRequestDto();

        cardToConnector.setPowerConnectorId(powerConnectorId);
        cardToConnector.setCount(count);

        this.powerConnectors.add(cardToConnector);
    }
}
