package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.StorageDevice;

import java.util.UUID;

/**
 * Абстрактный DTO для модели {@link StorageDevice},
 * используемый в качестве тела HTTP-запроса.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class StorageDeviceRequestDto extends NameableRequestDto {

    /**
     * Объем памяти (Гб).
     */
    @Min(120)
    @NotNull
    @Schema(description = "Объем памяти (Гб)")
    protected Integer capacity;

    /**
     * Скорость чтения (Мб/с).
     */
    @Min(1)
    @NotNull
    @Schema(description = "Скорость чтения (Мб/с)")
    protected Integer readingSpeed;

    /**
     * Скорость записи (Мб/с).
     */
    @Min(1)
    @NotNull
    @Schema(description = "Скорость записи (Мб/с)")
    protected Integer writingSpeed;

    /**
     * ID вендора.
     */
    @NotBlank
    @Schema(description = "ID вендора")
    protected UUID vendorId;

    /**
     * ID коннектора подключения.
     */
    @NotBlank
    @Schema(description = "ID коннектора подключения")
    protected UUID connectorId;

    /**
     * ID коннектора питания.
     */
    @Schema(description = "ID коннектора питания")
    protected UUID powerConnectorId;
}
