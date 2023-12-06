package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Chipset;

import java.util.UUID;

/**
 * DTO для модели {@link Chipset}, используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Чипсет")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ChipsetRequestDto extends NameableRequestDto {

    /**
     * ID сокета.
     */
    @Schema(description = "ID сокета")
    @NotBlank
    protected UUID socketId;
}
