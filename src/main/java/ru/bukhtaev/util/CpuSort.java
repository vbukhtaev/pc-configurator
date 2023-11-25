package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

/**
 * Варианты сортировки для процессоров.
 */
@Getter
@RequiredArgsConstructor
public enum CpuSort {

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
     * По количеству ядер по возрастанию.
     */
    CORE_COUNT_ASC(Sort.by(Sort.Direction.ASC, "coreCount")),

    /**
     * По количеству ядер по убыванию.
     */
    CORE_COUNT_DESC(Sort.by(Sort.Direction.DESC, "coreCount")),

    /**
     * По количеству потоков по возрастанию.
     */
    THREAD_COUNT_ASC(Sort.by(Sort.Direction.ASC, "threadCount")),

    /**
     * По количеству потоков по убыванию.
     */
    THREAD_COUNT_DESC(Sort.by(Sort.Direction.DESC, "threadCount")),

    /**
     * По базовой частоте по возрастанию.
     */
    BASE_CLOCK_ASC(Sort.by(Sort.Direction.ASC, "baseClock")),

    /**
     * По базовой частоте по убыванию.
     */
    BASE_CLOCK_DESC(Sort.by(Sort.Direction.DESC, "baseClock")),

    /**
     * По максимальной частоте по возрастанию.
     */
    MAX_CLOCK_ASC(Sort.by(Sort.Direction.ASC, "maxClock")),

    /**
     * По максимальной частоте по убыванию.
     */
    MAX_CLOCK_DESC(Sort.by(Sort.Direction.DESC, "maxClock")),

    /**
     * По объему L3 кэша по возрастанию.
     */
    L3_CACHE_SIZE_ASC(Sort.by(Sort.Direction.ASC, "l3CacheSize")),

    /**
     * По объему L3 кэша по убыванию.
     */
    L3_CACHE_SIZE_DESC(Sort.by(Sort.Direction.DESC, "l3CacheSize")),

    /**
     * По максимальному тепловыделению по возрастанию.
     */
    MAX_TDP_ASC(Sort.by(Sort.Direction.ASC, "maxTdp")),

    /**
     * По максимальному тепловыделению по убыванию.
     */
    MAX_TDP_DESC(Sort.by(Sort.Direction.DESC, "maxTdp")),

    /**
     * По названию производителя по возрастанию.
     */
    MANUFACTURER_NAME_ASC(Sort.by(Sort.Direction.ASC, "manufacturer.name")),

    /**
     * По названию производителя по убыванию.
     */
    MANUFACTURER_NAME_DESC(Sort.by(Sort.Direction.DESC, "manufacturer.name")),

    /**
     * По названию сокета по возрастанию.
     */
    SOCKET_NAME_ASC(Sort.by(Sort.Direction.ASC, "socket.name")),

    /**
     * По названию сокета по убыванию.
     */
    SOCKET_NAME_DESC(Sort.by(Sort.Direction.DESC, "socket.name"));

    private final Sort sortValue;
}
