package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.Cpu.*;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;

/**
 * Варианты сортировки для процессоров.
 */
@Getter
@RequiredArgsConstructor
public enum CpuSort {

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
     * По количеству ядер по возрастанию.
     */
    CORE_COUNT_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_CORE_COUNT
    )),

    /**
     * По количеству ядер по убыванию.
     */
    CORE_COUNT_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_CORE_COUNT
    )),

    /**
     * По количеству потоков по возрастанию.
     */
    THREAD_COUNT_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_THREAD_COUNT
    )),

    /**
     * По количеству потоков по убыванию.
     */
    THREAD_COUNT_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_THREAD_COUNT
    )),

    /**
     * По базовой частоте по возрастанию.
     */
    BASE_CLOCK_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_BASE_CLOCK
    )),

    /**
     * По базовой частоте по убыванию.
     */
    BASE_CLOCK_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_BASE_CLOCK
    )),

    /**
     * По максимальной частоте по возрастанию.
     */
    MAX_CLOCK_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_MAX_CLOCK
    )),

    /**
     * По максимальной частоте по убыванию.
     */
    MAX_CLOCK_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_MAX_CLOCK
    )),

    /**
     * По объему L3 кэша по возрастанию.
     */
    L3_CACHE_SIZE_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_L3CACHE_SIZE
    )),

    /**
     * По объему L3 кэша по убыванию.
     */
    L3_CACHE_SIZE_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_L3CACHE_SIZE
    )),

    /**
     * По максимальному тепловыделению по возрастанию.
     */
    MAX_TDP_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_MAX_TDP
    )),

    /**
     * По максимальному тепловыделению по убыванию.
     */
    MAX_TDP_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_MAX_TDP
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
     * По названию сокета по возрастанию.
     */
    SOCKET_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_SOCKET + "." + FIELD_NAME
    )),

    /**
     * По названию сокета по убыванию.
     */
    SOCKET_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_SOCKET + "." + FIELD_NAME
    ));

    private final Sort sortValue;
}
