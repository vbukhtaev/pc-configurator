package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.StorageDevice;

/**
 * Абстрактный DTO для модели {@link StorageDevice},
 * используемый в качестве тела HTTP-ответа.
 */
@Getter
@SuperBuilder
public abstract class StorageDeviceResponseDto extends NameableResponseDto {

    /**
     * Объем памяти (Гб).
     */
    @Schema(description = "Объем памяти (Гб)")
    protected Integer capacity;

    /**
     * Скорость чтения (Мб/с).
     */
    @Schema(description = "Скорость чтения (Мб/с)")
    protected Integer readingSpeed;

    /**
     * Скорость записи (Мб/с).
     */
    @Schema(description = "Скорость записи (Мб/с)")
    protected Integer writingSpeed;

    /**
     * Вендор.
     */
    @Schema(description = "Вендор")
    protected NameableResponseDto vendor;

    /**
     * Коннектор подключения.
     */
    @Schema(description = "Коннектор подключения")
    protected NameableResponseDto connector;

    /**
     * Коннектор питания.
     */
    @Schema(description = "Коннектор питания")
    protected NameableResponseDto powerConnector;

    /**
     * Формат слота расширения.
     */
    @Schema(description = "Формат слота расширения")
    protected NameableResponseDto expansionBayFormat;
}
