package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.ComputerCase;

import java.util.Set;

/**
 * DTO для модели {@link ComputerCase}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Сборка ПК")
@Getter
@SuperBuilder
public class ComputerBuildResponseDto extends NameableResponseDto {

    /**
     * Процессор.
     */
    @Schema(description = "Процессор")
    protected CpuResponseDto cpu;

    /**
     * Блок питания.
     */
    @Schema(description = "Блок питания")
    protected PsuResponseDto psu;

    /**
     * Процессорный кулер.
     */
    @Schema(description = "Процессорный кулер")
    protected CoolerResponseDto cooler;

    /**
     * Материнская плата.
     */
    @Schema(description = "Материнская плата")
    protected MotherboardResponseDto motherboard;

    /**
     * Видеокарта.
     */
    @Schema(description = "Видеокарта")
    protected GraphicsCardResponseDto graphicsCard;

    /**
     * Корпус.
     */
    @Schema(description = "Чипсет")
    protected ComputerCaseResponseDto computerCase;

    /**
     * Включенные в сборку ПК вентиляторы.
     */
    @Schema(description = "Включенные в сборку ПК вентиляторы")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<ComputerBuildToFanResponseDto> fans;

    /**
     * Включенные в сборку ПК модули оперативной памяти.
     */
    @Schema(description = "Включенные в сборку ПК модули оперативной памяти")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<ComputerBuildToRamModuleResponseDto> ramModules;

    /**
     * Включенные в сборку ПК жесткие диски.
     */
    @Schema(description = "Включенные в сборку ПК жесткие диски")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<ComputerBuildToHddResponseDto> hdds;

    /**
     * Включенные в сборку ПК SSD-накопители.
     */
    @Schema(description = "Включенные в сборку ПК SSD-накопители")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<ComputerBuildToSsdResponseDto> ssds;
}
