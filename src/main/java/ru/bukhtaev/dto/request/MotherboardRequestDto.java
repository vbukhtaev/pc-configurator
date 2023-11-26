package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Motherboard;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO для модели {@link Motherboard}, используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Материнская плата")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class MotherboardRequestDto extends NameableRequestDto {

    /**
     * Максимальная частота оперативной памяти (МГц).
     */
    @Schema(description = "Максимальная частота оперативной памяти (МГц)")
    @Min(800)
    @NotNull
    protected Integer maxMemoryClock;

    /**
     * Максимальная частота оперативной памяти с разгоном (МГц).
     */
    @Schema(description = "Максимальная частота оперативной памяти с разгоном (МГц)")
    @Min(800)
    @NotNull
    protected Integer maxMemoryOverClock;

    /**
     * Максимальный объем оперативной памяти (Мб).
     */
    @Schema(description = "Максимальный объем оперативной памяти (Мб)")
    @Min(32768)
    @NotNull
    protected Integer maxMemorySize;

    /**
     * Количество слотов.
     */
    @Schema(description = "Количество слотов")
    @Min(2)
    @NotNull
    protected Integer slotsCount;

    /**
     * ID варианта исполнения.
     */
    @Schema(description = "ID варианта исполнения")
    @NotBlank
    protected UUID designId;

    /**
     * ID чипсета.
     */
    @Schema(description = "ID чипсета")
    @NotBlank
    protected UUID chipsetId;

    /**
     * ID типа оперативной памяти.
     */
    @Schema(description = "ID типа оперативной памяти")
    @NotBlank
    protected UUID ramTypeId;

    /**
     * ID форм-фактора.
     */
    @Schema(description = "ID форм-фактора")
    @NotBlank
    protected UUID formFactorId;

    /**
     * ID коннектора питания процессора.
     */
    @Schema(description = "ID коннектора питания процессора")
    @NotBlank
    protected UUID cpuPowerConnectorId;

    /**
     * ID основного коннектора питания.
     */
    @Schema(description = "ID основного коннектора питания")
    @NotBlank
    protected UUID mainPowerConnectorId;

    /**
     * ID коннектора питания процессорного кулера.
     */
    @Schema(description = "ID коннектора питания процессорного кулера")
    @NotBlank
    protected UUID coolerPowerConnectorId;

    /**
     * ID версии коннектора PCI-Express.
     */
    @Schema(description = "ID версии коннектора PCI-Express")
    @NotBlank
    protected UUID pciExpressConnectorVersionId;

    /**
     * Коннекторы питания вентиляторов.
     */
    @Schema(description = "Коннекторы питания вентиляторов")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<MotherboardToFanPowerConnectorRequestDto> fanPowerConnectors;

    /**
     * Коннекторы подключения накопителей.
     */
    @Schema(description = "Коннекторы подключения накопителей")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<MotherboardToStorageConnectorRequestDto> storageConnectors;

    /**
     * Добавляет коннектор питания вентилятора в указанном количестве.
     *
     * @param connectorId ID коннектора питания вентилятора
     * @param count       количество
     */
    public void addFanPowerConnector(final UUID connectorId, final Integer count) {
        if (this.fanPowerConnectors == null) {
            this.fanPowerConnectors = new HashSet<>();
        }

        final var motherboardToConnector = new MotherboardToFanPowerConnectorRequestDto();

        motherboardToConnector.setFanPowerConnectorId(connectorId);
        motherboardToConnector.setCount(count);

        this.fanPowerConnectors.add(motherboardToConnector);
    }

    /**
     * Добавляет коннектор подключения накопителя в указанном количестве.
     *
     * @param connectorId ID коннектора подключения накопителя
     * @param count       количество
     */
    public void addStorageConnector(final UUID connectorId, final Integer count) {
        if (this.storageConnectors == null) {
            this.storageConnectors = new HashSet<>();
        }

        final var motherboardToConnector = new MotherboardToStorageConnectorRequestDto();

        motherboardToConnector.setStorageConnectorId(connectorId);
        motherboardToConnector.setCount(count);

        this.storageConnectors.add(motherboardToConnector);
    }
}
