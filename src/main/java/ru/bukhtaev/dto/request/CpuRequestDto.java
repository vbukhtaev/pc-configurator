package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Cpu;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO для модели {@link Cpu}, используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Процессор")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class CpuRequestDto extends NameableRequestDto {

    /**
     * Количество ядер.
     */
    @Schema(description = "Количество ядер")
    @Min(1)
    @NotNull
    protected Integer coreCount;

    /**
     * Количество потоков.
     */
    @Schema(description = "Количество потоков")
    @Min(1)
    @NotNull
    protected Integer threadCount;

    /**
     * Базовая частота (МГц).
     */
    @Schema(description = "Базовая частота (МГц)")
    @Min(100)
    @NotNull
    protected Integer baseClock;

    /**
     * Максимальная частота (МГц).
     */
    @Schema(description = "Максимальная частота (МГц)")
    @Min(100)
    @NotNull
    protected Integer maxClock;

    /**
     * Объем L3 кэша (Мб).
     */
    @Schema(description = "Объем L3 кэша (Мб)")
    @Min(1)
    @NotNull
    protected Integer l3CacheSize;

    /**
     * Максимальное тепловыделение (Вт).
     */
    @Schema(description = "Максимальное тепловыделение (Вт)")
    @Min(1)
    @NotNull
    protected Integer maxTdp;

    /**
     * Максимальный объем оперативной памяти (Мб).
     */
    @Schema(description = "Максимальный объем оперативной памяти (Мб)")
    @Min(32768)
    @NotNull
    protected Integer maxMemorySize;

    /**
     * ID производителя.
     */
    @Schema(description = "ID производителя")
    @NotBlank
    protected UUID manufacturerId;

    /**
     * ID сокета.
     */
    @Schema(description = "ID сокета")
    @NotBlank
    protected UUID socketId;

    /**
     * Поддерживаемые типы оперативной памяти.
     */
    @Schema(description = "Поддерживаемые типы оперативной памяти")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<CpuToRamTypeRequestDto> supportedRamTypes;

    /**
     * Добавляет тип оперативной памяти с указанной частотой.
     *
     * @param typeId ID типа оперативной памяти
     * @param clock  частота
     */
    public void addRamType(final UUID typeId, final Integer clock) {
        if (this.supportedRamTypes == null) {
            this.supportedRamTypes = new HashSet<>();
        }

        final var cpuToRamType = new CpuToRamTypeRequestDto();

        cpuToRamType.setRamTypeId(typeId);
        cpuToRamType.setMaxMemoryClock(clock);

        this.supportedRamTypes.add(cpuToRamType);
    }
}
