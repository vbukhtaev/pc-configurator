package ru.bukhtaev.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.ComputerBuildToFan;
import ru.bukhtaev.model.cross.ComputerBuildToHdd;
import ru.bukhtaev.model.cross.ComputerBuildToRamModule;
import ru.bukhtaev.model.cross.ComputerBuildToSsd;

import java.util.HashSet;
import java.util.Set;

/**
 * Модель сборки ПК.
 */
@Getter
@Setter
@Entity
@Table(
        name = "computer_build",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@SuperBuilder
@NoArgsConstructor
public class ComputerBuild extends NameableEntity {

    /**
     * Название поля, хранящего процессор.
     */
    public static final String FIELD_CPU = "cpu";

    /**
     * Название поля, хранящего блок питания.
     */
    public static final String FIELD_PSU = "psu";

    /**
     * Название поля, хранящего процессорный кулер.
     */
    public static final String FIELD_COOLER = "cooler";

    /**
     * Название поля, хранящего материнскую плату.
     */
    public static final String FIELD_MOTHERBOARD = "motherboard";

    /**
     * Название поля, хранящего видеокарту.
     */
    public static final String FIELD_GRAPHICS_CARD = "graphicsCard";

    /**
     * Название поля, хранящего корпус.
     */
    public static final String FIELD_COMPUTER_CASE = "computerCase";

    /**
     * Название поля, хранящего включенные в сборку ПК вентиляторы.
     */
    public static final String FIELD_FANS = "fans";

    /**
     * Название поля, хранящего включенные в сборку ПК модули оперативной памяти.
     */
    public static final String FIELD_RAM_MODULES = "ramModules";

    /**
     * Название поля, хранящего включенные в сборку ПК жесткие диски.
     */
    public static final String FIELD_HDDS = "hdds";

    /**
     * Название поля, хранящего включенные в сборку ПК SSD-накопители.
     */
    public static final String FIELD_SSDS = "ssds";

    /**
     * Процессор.
     */
    @ManyToOne
    @JoinColumn(name = "cpu_id", referencedColumnName = "id")
    protected Cpu cpu;

    /**
     * Блок питания.
     */
    @ManyToOne
    @JoinColumn(name = "psu_id", referencedColumnName = "id")
    protected Psu psu;

    /**
     * Процессорный кулер.
     */
    @ManyToOne
    @JoinColumn(name = "cooler_id", referencedColumnName = "id")
    protected Cooler cooler;

    /**
     * Материнская плата.
     */
    @ManyToOne
    @JoinColumn(name = "motherboard_id", referencedColumnName = "id")
    protected Motherboard motherboard;

    /**
     * Видеокарта.
     */
    @ManyToOne
    @JoinColumn(name = "graphics_card_id", referencedColumnName = "id")
    protected GraphicsCard graphicsCard;

    /**
     * Корпус.
     */
    @ManyToOne
    @JoinColumn(name = "computer_case_id", referencedColumnName = "id")
    protected ComputerCase computerCase;

    /**
     * Включенные в сборку ПК вентиляторы.
     */
    @OneToMany(
            mappedBy = "computerBuild",
            cascade = CascadeType.ALL
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<ComputerBuildToFan> fans = new HashSet<>();

    /**
     * Включенные в сборку ПК модули оперативной памяти.
     */
    @OneToMany(
            mappedBy = "computerBuild",
            cascade = CascadeType.ALL
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<ComputerBuildToRamModule> ramModules = new HashSet<>();

    /**
     * Включенные в сборку ПК жесткие диски.
     */
    @OneToMany(
            mappedBy = "computerBuild",
            cascade = CascadeType.ALL
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<ComputerBuildToHdd> hdds = new HashSet<>();

    /**
     * Включенные в сборку ПК SSD-накопители.
     */
    @OneToMany(
            mappedBy = "computerBuild",
            cascade = CascadeType.ALL
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<ComputerBuildToSsd> ssds = new HashSet<>();

    /**
     * Добавляет вентилятор в указанном количестве.
     *
     * @param fan   вентилятор
     * @param count количество
     */
    public void addFan(final Fan fan, final Integer count) {
        final var buildToFan = new ComputerBuildToFan();

        buildToFan.setComputerBuild(this);
        buildToFan.setFan(fan);
        buildToFan.setCount(count);

        this.fans.add(buildToFan);
    }

    /**
     * Добавляет модуль оперативной памяти в указанном количестве.
     *
     * @param module модуль оперативной памяти
     * @param count  количество
     */
    public void addRamModule(final RamModule module, final Integer count) {
        final var buildToRamModule = new ComputerBuildToRamModule();

        buildToRamModule.setComputerBuild(this);
        buildToRamModule.setRamModule(module);
        buildToRamModule.setCount(count);

        this.ramModules.add(buildToRamModule);
    }

    /**
     * Добавляет жесткий диск в указанном количестве.
     *
     * @param hdd   жесткий диск
     * @param count количество
     */
    public void addHdd(final Hdd hdd, final Integer count) {
        final var buildToHdd = new ComputerBuildToHdd();

        buildToHdd.setComputerBuild(this);
        buildToHdd.setHdd(hdd);
        buildToHdd.setCount(count);

        this.hdds.add(buildToHdd);
    }

    /**
     * Добавляет SSD-накопитель в указанном количестве.
     *
     * @param ssd   SSD-накопитель
     * @param count количество
     */
    public void addSsd(final Ssd ssd, final Integer count) {
        final var buildToSsd = new ComputerBuildToSsd();

        buildToSsd.setComputerBuild(this);
        buildToSsd.setSsd(ssd);
        buildToSsd.setCount(count);

        this.ssds.add(buildToSsd);
    }
}
