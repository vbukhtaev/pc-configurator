package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Design;

import java.util.UUID;

/**
 * DTO для модели {@link Design}, используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Вариант исполнения")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class DesignRequestDto extends NameableRequestDto {

    /**
     * ID вендора.
     */
    @Schema(description = "ID вендора")
    @NotBlank
    protected UUID vendorId;
}
