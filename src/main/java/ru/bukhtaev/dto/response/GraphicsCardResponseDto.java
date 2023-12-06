package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.GraphicsCard;

import java.util.Set;

/**
 * DTO для модели {@link GraphicsCard}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Видеокарта")
@Getter
@SuperBuilder
public class GraphicsCardResponseDto extends BaseResponseDto {

    /**
     * Длина (мм).
     */
    @Schema(description = "Длина (мм)")
    protected Integer length;

    /**
     * Графический процессор.
     */
    @Schema(description = "Графический процессор")
    protected GpuResponseDto gpu;

    /**
     * Вариант исполнения.
     */
    @Schema(description = "Вариант исполнения")
    protected DesignResponseDto design;

    /**
     * Версия коннектора PCI-Express.
     */
    @Schema(description = "Версия коннектора PCI-Express")
    protected NameableResponseDto pciExpressConnectorVersion;

    /**
     * Коннекторы питания.
     */
    @Schema(description = "Коннекторы питания")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<GraphicsCardToPowerConnectorResponseDto> powerConnectors;
}
