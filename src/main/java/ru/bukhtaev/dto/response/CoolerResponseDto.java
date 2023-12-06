package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Cooler;

import java.util.Set;

/**
 * DTO для модели {@link Cooler}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Процессорный кулер")
@Getter
@SuperBuilder
public class CoolerResponseDto extends NameableResponseDto {

    /**
     * Рассеиваемая мощность (Вт).
     */
    @Schema(description = "Рассеиваемая мощность (Вт)")
    protected Integer powerDissipation;

    /**
     * Высота (мм).
     */
    @Schema(description = "Высота (мм)")
    protected Integer height;

    /**
     * Вендор.
     */
    @Schema(description = "Вендор")
    protected NameableResponseDto vendor;

    /**
     * Размер вентилятора.
     */
    @Schema(description = "Размер вентилятора")
    protected FanSizeResponseDto fanSize;

    /**
     * Коннектор питания вентилятора.
     */
    @Schema(description = "Коннектор питания вентилятора")
    protected NameableResponseDto powerConnector;

    /**
     * Поддерживаемые сокеты.
     */
    @Schema(description = "Поддерживаемые сокеты")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<NameableResponseDto> supportedSockets;
}
