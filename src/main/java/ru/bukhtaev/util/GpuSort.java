package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.Gpu.*;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;

/**
 * Варианты сортировки для графических процессоров.
 */
@Getter
@RequiredArgsConstructor
public enum GpuSort {

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
     * По объему видеопамяти по возрастанию.
     */
    MEMORY_SIZE_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_MEMORY_SIZE
    )),

    /**
     * По объему видеопамяти по убыванию.
     */
    MEMORY_SIZE_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_MEMORY_SIZE
    )),

    /**
     * По энергопотреблению по возрастанию.
     */
    POWER_CONSUMPTION_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_POWER_CONSUMPTION
    )),

    /**
     * По энергопотреблению по убыванию.
     */
    POWER_CONSUMPTION_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_POWER_CONSUMPTION
    )),

    /**
     * По названию производителя по возрастанию.
     */
    MANUFACTURER_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_MANUFACTURER + "." + FIELD_NAME
    )),

    /**
     * По названию производителя по убыванию.
     */
    MANUFACTURER_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_MANUFACTURER + "." + FIELD_NAME
    )),

    /**
     * По названию типа видеопамяти по возрастанию.
     */
    MEMORY_TYPE_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_MEMORY_TYPE + "." + FIELD_NAME
    )),

    /**
     * По названию типа видеопамяти по убыванию.
     */
    MEMORY_TYPE_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_MEMORY_TYPE + "." + FIELD_NAME
    ));

    private final Sort sortValue;
}
