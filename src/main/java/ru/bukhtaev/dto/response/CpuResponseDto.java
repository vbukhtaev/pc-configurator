package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Cpu;

import java.util.Set;

/**
 * DTO для модели {@link Cpu}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "Процессор")
@Getter
@SuperBuilder
public class CpuResponseDto extends NameableResponseDto {

    /**
     * Количество ядер.
     */
    @Schema(description = "Количество ядер")
    protected Integer coreCount;

    /**
     * Количество потоков.
     */
    @Schema(description = "Количество потоков")
    protected Integer threadCount;

    /**
     * Базовая частота (МГц).
     */
    @Schema(description = "Базовая частота (МГц)")
    protected Integer baseClock;

    /**
     * Максимальная частота (МГц).
     */
    @Schema(description = "Максимальная частота (МГц)")
    protected Integer maxClock;

    /**
     * Объем L3 кэша (Мб).
     */
    @Schema(description = "Объем L3 кэша (Мб)")
    protected Integer l3CacheSize;

    /**
     * Максимальное тепловыделение (Вт).
     */
    @Schema(description = "Максимальное тепловыделение (Вт)")
    protected Integer maxTdp;

    /**
     * Производитель.
     */
    @Schema(description = "Производитель")
    protected NameableResponseDto manufacturer;

    /**
     * Сокет.
     */
    @Schema(description = "Сокет")
    protected NameableResponseDto socket;

    /**
     * Поддерживаемые типы оперативной памяти.
     */
    @Schema(description = "Поддерживаемые типы оперативной памяти")
    @Size(min = 1)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<CpuToRamTypeResponseDto> supportedRamTypes;
}
