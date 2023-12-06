package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Psu;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO для модели {@link Psu}, используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Блок питания")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class PsuRequestDto extends NameableRequestDto {

    /**
     * Мощность (Вт).
     */
    @Schema(description = "Мощность (Вт)")
    @Min(1)
    @NotNull
    protected Integer power;

    /**
     * Мощность по линии 12V (Вт).
     */
    @Schema(description = "Мощность по линии 12V (Вт)")
    @Min(1)
    @NotNull
    protected Integer power12V;

    /**
     * Длина (мм).
     */
    @Schema(description = "Длина (мм)")
    @Min(1)
    @NotNull
    protected Integer length;

    /**
     * ID вендора.
     */
    @Schema(description = "ID вендора")
    @NotBlank
    protected UUID vendorId;

    /**
     * ID форм-фактора.
     */
    @Schema(description = "ID форм-фактора")
    @NotBlank
    protected UUID formFactorId;

    /**
     * ID сертификата.
     */
    @Schema(description = "ID сертификата")
    @NotBlank
    protected UUID certificateId;

    /**
     * ID основного коннектора питания.
     */
    @Schema(description = "ID основного коннектора питания")
    @NotBlank
    protected UUID mainPowerConnectorId;

    /**
     * Коннекторы питания процессоров.
     */
    @Schema(description = "Коннекторы питания процессоров")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<PsuToCpuPowerConnectorRequestDto> cpuPowerConnectors;

    /**
     * Коннекторы питания накопителей.
     */
    @Schema(description = "Коннекторы питания накопителей")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<PsuToStoragePowerConnectorRequestDto> storagePowerConnectors;

    /**
     * Коннекторы питания видеокарт.
     */
    @Schema(description = "Коннекторы питания видеокарт")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<PsuToGraphicsCardPowerConnectorRequestDto> graphicsCardPowerConnectors;

    /**
     * Добавляет коннектор питания процессора в указанном количестве.
     *
     * @param connectorId ID коннектора питания процессора
     * @param count       количество
     */
    public void addCpuPowerConnector(final UUID connectorId, final Integer count) {
        if (this.cpuPowerConnectors == null) {
            this.cpuPowerConnectors = new HashSet<>();
        }

        final var psuToConnector = new PsuToCpuPowerConnectorRequestDto();

        psuToConnector.setCpuPowerConnectorId(connectorId);
        psuToConnector.setCount(count);

        this.cpuPowerConnectors.add(psuToConnector);
    }

    /**
     * Добавляет коннектор питания накопителя в указанном количестве.
     *
     * @param connectorId ID коннектора питания накопителя
     * @param count       количество
     */
    public void addStoragePowerConnector(final UUID connectorId, final Integer count) {
        if (this.storagePowerConnectors == null) {
            this.storagePowerConnectors = new HashSet<>();
        }

        final var psuToConnector = new PsuToStoragePowerConnectorRequestDto();

        psuToConnector.setStoragePowerConnectorId(connectorId);
        psuToConnector.setCount(count);

        this.storagePowerConnectors.add(psuToConnector);
    }

    /**
     * Добавляет коннектор питания видеокарты в указанном количестве.
     *
     * @param connectorId ID коннектора питания видеокарты
     * @param count       количество
     */
    public void addGraphicsCardPowerConnector(final UUID connectorId, final Integer count) {
        if (this.graphicsCardPowerConnectors == null) {
            this.graphicsCardPowerConnectors = new HashSet<>();
        }

        final var psuToConnector = new PsuToGraphicsCardPowerConnectorRequestDto();

        psuToConnector.setGraphicsCardPowerConnectorId(connectorId);
        psuToConnector.setCount(count);

        this.graphicsCardPowerConnectors.add(psuToConnector);
    }
}
