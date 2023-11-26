package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.Motherboard.*;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;

/**
 * Варианты сортировки для материнских плат.
 */
@Getter
@RequiredArgsConstructor
public enum MotherboardSort {

    /**
     * По ID по возрастанию.
     */
    ID_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_ID
    )),

    /**
     * По ID по убыванию.
     */
    ID_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_ID
    )),

    /**
     * По названию по возрастанию.
     */
    NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_NAME
    )),

    /**
     * По названию по убыванию.
     */
    NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_NAME
    )),

    /**
     * По максимальной частоте оперативной памяти по возрастанию.
     */
    MAX_MEMORY_CLOCK_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_MAX_MEMORY_CLOCK
    )),

    /**
     * По максимальной частоте оперативной памяти по убыванию.
     */
    MAX_MEMORY_CLOCK_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_MAX_MEMORY_CLOCK
    )),

    /**
     * По максимальной частоте оперативной памяти с разгоном по возрастанию.
     */
    MAX_MEMORY_OVER_CLOCK_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_MAX_MEMORY_OVER_CLOCK
    )),

    /**
     * По максимальной частоте оперативной памяти с разгоном по убыванию.
     */
    MAX_MEMORY_OVER_CLOCK_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_MAX_MEMORY_OVER_CLOCK
    )),

    /**
     * По максимальному объему оперативной памяти по возрастанию.
     */
    MAX_MEMORY_SIZE_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_MAX_MEMORY_SIZE
    )),

    /**
     * По максимальному объему оперативной памяти по убыванию.
     */
    MAX_MEMORY_SIZE_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_MAX_MEMORY_SIZE
    )),

    /**
     * По количеству слотов по возрастанию.
     */
    MEMORY_SLOTS_COUNT_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_MEMORY_SLOTS_COUNT
    )),

    /**
     * По количеству слотов по убыванию.
     */
    MEMORY_SLOTS_COUNT_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_MEMORY_SLOTS_COUNT
    )),

    /**
     * По названию варианта исполнения по возрастанию.
     */
    DESIGN_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_DESIGN + "." + FIELD_NAME
    )),

    /**
     * По названию варианта исполнения по убыванию.
     */
    DESIGN_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_DESIGN + "." + FIELD_NAME
    )),

    /**
     * По названию чипсета по возрастанию.
     */
    CHIPSET_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_CHIPSET + "." + FIELD_NAME
    )),

    /**
     * По названию чипсета по убыванию.
     */
    CHIPSET_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_CHIPSET + "." + FIELD_NAME
    )),

    /**
     * По названию типа оперативной памяти по возрастанию.
     */
    RAM_TYPE_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_RAM_TYPE + "." + FIELD_NAME
    )),

    /**
     * По названию типа оперативной памяти по убыванию.
     */
    RAM_TYPE_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_RAM_TYPE + "." + FIELD_NAME
    )),

    /**
     * По названию форм-фактора по возрастанию.
     */
    FORM_FACTOR_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_FORM_FACTOR + "." + FIELD_NAME
    )),

    /**
     * По названию форм-фактора по убыванию.
     */
    FORM_FACTOR_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_FORM_FACTOR + "." + FIELD_NAME
    )),

    /**
     * По названию коннектора питания процессора по возрастанию.
     */
    CPU_POWER_CONNECTOR_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_CPU_POWER_CONNECTOR + "." + FIELD_NAME
    )),

    /**
     * По названию коннектора питания процессора по убыванию.
     */
    CPU_POWER_CONNECTOR_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_CPU_POWER_CONNECTOR + "." + FIELD_NAME
    )),

    /**
     * По названию основного коннектора питания по возрастанию.
     */
    MAIN_POWER_CONNECTOR_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_MAIN_POWER_CONNECTOR + "." + FIELD_NAME
    )),

    /**
     * По названию основного коннектора питания по убыванию.
     */
    MAIN_POWER_CONNECTOR_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_MAIN_POWER_CONNECTOR + "." + FIELD_NAME
    )),

    /**
     * По названию коннектора питания кулера по возрастанию.
     */
    COOLER_POWER_CONNECTOR_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_COOLER_POWER_CONNECTOR + "." + FIELD_NAME
    )),

    /**
     * По названию коннектора питания кулера по убыванию.
     */
    COOLER_POWER_CONNECTOR_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_COOLER_POWER_CONNECTOR + "." + FIELD_NAME
    )),

    /**
     * По названию версии коннектора PCI-Express по возрастанию.
     */
    PCI_EXPRESS_CONNECTOR_VERSION_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_PCI_EXPRESS_CONNECTOR_VERSION + "." + FIELD_NAME
    )),

    /**
     * По названию версии коннектора PCI-Express по убыванию.
     */
    PCI_EXPRESS_CONNECTOR_VERSION_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_PCI_EXPRESS_CONNECTOR_VERSION + "." + FIELD_NAME
    ));

    private final Sort sortValue;
}
