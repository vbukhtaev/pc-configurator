package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

/**
 * Варианты сортировки для графических процессоров.
 */
@Getter
@RequiredArgsConstructor
public enum GpuSort {

    /**
     * По ID по возрастанию.
     */
    ID_ASC(Sort.by(Sort.Direction.ASC, "id")),

    /**
     * По ID по убыванию.
     */
    ID_DESC(Sort.by(Sort.Direction.DESC, "id")),

    /**
     * По названию по возрастанию.
     */
    NAME_ASC(Sort.by(Sort.Direction.ASC, "name")),

    /**
     * По названию по убыванию.
     */
    NAME_DESC(Sort.by(Sort.Direction.DESC, "name")),

    /**
     * По объему видеопамяти по возрастанию.
     */
    MEMORY_SIZE_ASC(Sort.by(Sort.Direction.ASC, "memorySize")),

    /**
     * По объему видеопамяти по убыванию.
     */
    MEMORY_SIZE_DESC(Sort.by(Sort.Direction.DESC, "memorySize")),

    /**
     * По энергопотреблению по возрастанию.
     */
    POWER_CONSUMPTION_ASC(Sort.by(Sort.Direction.ASC, "powerConsumption")),

    /**
     * По энергопотреблению по убыванию.
     */
    POWER_CONSUMPTION_DESC(Sort.by(Sort.Direction.DESC, "powerConsumption")),

    /**
     * По названию производителя по возрастанию.
     */
    MANUFACTURER_NAME_ASC(Sort.by(Sort.Direction.ASC, "manufacturer.name")),

    /**
     * По названию производителя по убыванию.
     */
    MANUFACTURER_NAME_DESC(Sort.by(Sort.Direction.DESC, "manufacturer.name")),

    /**
     * По названию типа видеопамяти по возрастанию.
     */
    MEMORY_TYPE_NAME_ASC(Sort.by(Sort.Direction.ASC, "memoryType.name")),

    /**
     * По названию типа видеопамяти по убыванию.
     */
    MEMORY_TYPE_NAME_DESC(Sort.by(Sort.Direction.DESC, "memoryType.name"));

    private final Sort sortValue;
}
