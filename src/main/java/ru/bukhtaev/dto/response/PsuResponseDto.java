package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Psu;

import java.util.Set;

/**
 * DTO для модели {@link Psu}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Блок питания")
@Getter
@SuperBuilder
public class PsuResponseDto extends NameableResponseDto {

    /**
     * Мощность (Вт).
     */
    @Schema(description = "Мощность (Вт)")
    protected Integer power;

    /**
     * Мощность по линии 12V (Вт).
     */
    @Schema(description = "Мощность по линии 12V (Вт)")
    protected Integer power12V;

    /**
     * Длина (мм).
     */
    @Schema(description = "Длина (мм)")
    protected Integer length;

    /**
     * Вендор.
     */
    @Schema(description = "Вендор")
    protected NameableResponseDto vendor;

    /**
     * Форм-фактор.
     */
    @Schema(description = "Форм-фактор")
    protected NameableResponseDto formFactor;

    /**
     * Сертификат.
     */
    @Schema(description = "Сертификат")
    protected NameableResponseDto certificate;

    /**
     * Основной коннектор питания.
     */
    @Schema(description = "Основной коннектор питания")
    protected NameableResponseDto mainPowerConnector;

    /**
     * Коннекторы питания процессоров.
     */
    @Schema(description = "Коннекторы питания процессоров")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<PsuToCpuPowerConnectorResponseDto> cpuPowerConnectors;

    /**
     * Коннекторы питания накопителей.
     */
    @Schema(description = "Коннекторы питания накопителей")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<PsuToStoragePowerConnectorResponseDto> storagePowerConnectors;

    /**
     * Коннекторы питания видеокарт.
     */
    @Schema(description = "Коннекторы питания видеокарт")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<PsuToGraphicsCardPowerConnectorResponseDto> graphicsCardPowerConnectors;
}
