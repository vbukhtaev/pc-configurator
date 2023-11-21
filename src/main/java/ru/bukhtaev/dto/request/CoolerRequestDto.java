package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Cooler;

import java.util.Set;
import java.util.UUID;

/**
 * DTO для модели {@link Cooler}, используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Процессорный кулер")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class CoolerRequestDto extends NameableRequestDto {

    /**
     * Рассеиваемая мощность (Вт).
     */
    @Schema(description = "Рассеиваемая мощность (Вт)")
    @Min(1)
    @NotNull
    protected Integer powerDissipation;

    /**
     * Высота (мм).
     */
    @Schema(description = "Высота (мм)")
    @Min(1)
    @NotNull
    protected Integer height;

    /**
     * ID вендора.
     */
    @Schema(description = "ID вендора")
    @NotBlank
    protected UUID vendorId;

    /**
     * ID размера вентилятора.
     */
    @Schema(description = "ID размера вентилятора")
    @NotBlank
    protected UUID fanSizeId;

    /**
     * ID коннектора питания вентилятора.
     */
    @Schema(description = "ID коннектора питания вентилятора")
    @NotBlank
    protected UUID powerConnectorId;

    /**
     * ID поддерживаемых сокетов
     */
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<UUID> supportedSocketIds;
}
