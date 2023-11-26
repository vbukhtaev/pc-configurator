package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Motherboard;

import java.util.Set;

/**
 * DTO для модели {@link Motherboard}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Материнская плата")
@Getter
@SuperBuilder
public class MotherboardResponseDto extends NameableResponseDto {

    /**
     * Максимальная частота оперативной памяти (МГц).
     */
    @Schema(description = "Максимальная частота оперативной памяти (МГц)")
    protected Integer maxMemoryClock;

    /**
     * Максимальная частота оперативной памяти с разгоном (МГц).
     */
    @Schema(description = "Максимальная частота оперативной памяти с разгоном (МГц)")
    protected Integer maxMemoryOverClock;

    /**
     * Максимальный объем оперативной памяти (Мб).
     */
    @Schema(description = "Максимальный объем оперативной памяти (Мб)")
    protected Integer maxMemorySize;

    /**
     * Количество слотов.
     */
    @Schema(description = "Количество слотов")
    protected Integer slotsCount;

    /**
     * Вариант исполнения.
     */
    @Schema(description = "Вариант исполнения")
    protected DesignResponseDto design;

    /**
     * Чипсет.
     */
    @Schema(description = "Чипсет")
    protected ChipsetResponseDto chipset;

    /**
     * Тип оперативной памяти.
     */
    @Schema(description = "Тип оперативной памяти")
    protected NameableResponseDto ramType;

    /**
     * Форм-фактор.
     */
    @Schema(description = "Форм-фактор")
    protected NameableResponseDto formFactor;

    /**
     * Коннектор питания процессора.
     */
    @Schema(description = "Коннектор питания процессора")
    protected NameableResponseDto cpuPowerConnector;

    /**
     * Основной коннектор питания.
     */
    @Schema(description = "Основной коннектор питания")
    protected NameableResponseDto mainPowerConnector;

    /**
     * Коннектор питания процессорного кулера.
     */
    @Schema(description = "Коннектор питания процессорного кулера")
    protected NameableResponseDto coolerPowerConnector;

    /**
     * Версия коннектора PCI-Express.
     */
    @Schema(description = "Версия коннектора PCI-Express")
    protected NameableResponseDto pciExpressConnectorVersion;

    /**
     * Коннекторы питания вентиляторов.
     */
    @Schema(description = "Коннекторы питания вентиляторов")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<MotherboardToFanPowerConnectorResponseDto> fanPowerConnectors;

    /**
     * Коннекторы подключения накопителей.
     */
    @Schema(description = "Коннекторы подключения накопителей")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<MotherboardToStorageConnectorResponseDto> storageConnectors;
}
