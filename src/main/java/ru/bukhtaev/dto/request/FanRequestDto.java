package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Fan;

import java.util.UUID;

/**
 * DTO для модели {@link Fan}, используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Вентилятор")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class FanRequestDto extends NameableRequestDto {

    /**
     * ID вендора.
     */
    @Schema(description = "ID вендора")
    @NotBlank
    protected UUID vendorId;

    /**
     * ID размера.
     */
    @Schema(description = "ID размера")
    @NotBlank
    protected UUID sizeId;

    /**
     * ID коннектора питания.
     */
    @Schema(description = "ID коннектора питания")
    @NotBlank
    protected UUID powerConnectorId;
}
