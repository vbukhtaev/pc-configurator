package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.ComputerBuild;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO для модели {@link ComputerBuild},
 * используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "Сборка ПК")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ComputerBuildRequestDto extends NameableRequestDto {

    /**
     * ID процессора.
     */
    @Schema(description = "ID процессора")
    protected UUID cpuId;

    /**
     * ID блока питания.
     */
    @Schema(description = "ID блока питания")
    protected UUID psuId;

    /**
     * ID процессорного кулера.
     */
    @Schema(description = "ID процессорного кулера")
    protected UUID coolerId;

    /**
     * ID материнской платы.
     */
    @Schema(description = "ID материнской платы")
    protected UUID motherboardId;

    /**
     * ID видеокарты.
     */
    @Schema(description = "ID видеокарты")
    protected UUID graphicsCardId;

    /**
     * ID корпуса.
     */
    @Schema(description = "ID корпуса")
    protected UUID computerCaseId;

    /**
     * Включенные в сборку ПК вентиляторы.
     */
    @Schema(description = "Включенные в сборку ПК вентиляторы")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<ComputerBuildToFanRequestDto> fans;

    /**
     * Включенные в сборку ПК модули оперативной памяти.
     */
    @Schema(description = "Включенные в сборку ПК модули оперативной памяти")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<ComputerBuildToRamModuleRequestDto> ramModules;

    /**
     * Включенные в сборку ПК жесткие диски.
     */
    @Schema(description = "Включенные в сборку ПК жесткие диски")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<ComputerBuildToHddRequestDto> hdds;

    /**
     * Включенные в сборку ПК SSD-накопители.
     */
    @Schema(description = "Включенные в сборку ПК SSD-накопители")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<ComputerBuildToSsdRequestDto> ssds;

    /**
     * Добавляет вентилятор в указанном количестве.
     *
     * @param fanId ID вентилятора
     * @param count количество
     */
    public void addFan(final UUID fanId, final Integer count) {
        if (this.fans == null) {
            this.fans = new HashSet<>();
        }

        final var buildToFan = new ComputerBuildToFanRequestDto();

        buildToFan.setFanId(fanId);
        buildToFan.setCount(count);

        this.fans.add(buildToFan);
    }

    /**
     * Добавляет модуль оперативной памяти в указанном количестве.
     *
     * @param moduleId ID модуля оперативной памяти
     * @param count    количество
     */
    public void addRamModule(final UUID moduleId, final Integer count) {
        if (this.ramModules == null) {
            this.ramModules = new HashSet<>();
        }

        final var buildToModule = new ComputerBuildToRamModuleRequestDto();

        buildToModule.setRamModuleId(moduleId);
        buildToModule.setCount(count);

        this.ramModules.add(buildToModule);
    }

    /**
     * Добавляет жесткий диск в указанном количестве.
     *
     * @param hddId ID жесткого диска
     * @param count количество
     */
    public void addHdd(final UUID hddId, final Integer count) {
        if (this.hdds == null) {
            this.hdds = new HashSet<>();
        }

        final var buildToHdd = new ComputerBuildToHddRequestDto();

        buildToHdd.setHddId(hddId);
        buildToHdd.setCount(count);

        this.hdds.add(buildToHdd);
    }

    /**
     * Добавляет SSD-накопитель в указанном количестве.
     *
     * @param ssdId ID SSD-накопителя
     * @param count количество
     */
    public void addSsd(final UUID ssdId, final Integer count) {
        if (this.ssds == null) {
            this.ssds = new HashSet<>();
        }

        final var buildToSsd = new ComputerBuildToSsdRequestDto();

        buildToSsd.setSsdId(ssdId);
        buildToSsd.setCount(count);

        this.ssds.add(buildToSsd);
    }
}
