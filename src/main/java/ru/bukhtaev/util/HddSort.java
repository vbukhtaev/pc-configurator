package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

/**
 * Варианты сортировки для жестких дисков.
 */
@Getter
@RequiredArgsConstructor
public enum HddSort {

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
     * По объему памяти по возрастанию.
     */
    CAPACITY_ASC(Sort.by(Sort.Direction.ASC, "capacity")),

    /**
     * По объему памяти по убыванию.
     */
    CAPACITY_DESC(Sort.by(Sort.Direction.DESC, "capacity")),

    /**
     * По скорости чтения по возрастанию.
     */
    READING_SPEED_ASC(Sort.by(Sort.Direction.ASC, "readingSpeed")),

    /**
     * По скорости чтения по убыванию.
     */
    READING_SPEED_DESC(Sort.by(Sort.Direction.DESC, "readingSpeed")),

    /**
     * По скорости записи по возрастанию.
     */
    WRITING_SPEED_ASC(Sort.by(Sort.Direction.ASC, "writingSpeed")),

    /**
     * По скорости записи по убыванию.
     */
    WRITING_SPEED_DESC(Sort.by(Sort.Direction.DESC, "writingSpeed")),

    /**
     * По скорости вращения шпинделя по возрастанию.
     */
    SPINDLE_SPEED_ASC(Sort.by(Sort.Direction.ASC, "spindleSpeed")),

    /**
     * По скорости вращения шпинделя по убыванию.
     */
    SPINDLE_SPEED_DESC(Sort.by(Sort.Direction.DESC, "spindleSpeed")),

    /**
     * По объему кэш-памяти по возрастанию.
     */
    CACHE_SIZE_ASC(Sort.by(Sort.Direction.ASC, "cacheSize")),

    /**
     * По объему кэш-памяти по убыванию.
     */
    CACHE_SIZE_DESC(Sort.by(Sort.Direction.DESC, "cacheSize")),

    /**
     * По названию вендора по возрастанию.
     */
    VENDOR_NAME_ASC(Sort.by(Sort.Direction.ASC, "vendor.name")),

    /**
     * По названию вендора по убыванию.
     */
    VENDOR_NAME_DESC(Sort.by(Sort.Direction.DESC, "vendor.name")),

    /**
     * По названию коннектора подключения по возрастанию.
     */
    CONNECTOR_NAME_ASC(Sort.by(Sort.Direction.ASC, "connector.name")),

    /**
     * По названию коннектора подключения по убыванию.
     */
    CONNECTOR_NAME_DESC(Sort.by(Sort.Direction.DESC, "connector.name")),

    /**
     * По названию коннектора питания по возрастанию.
     */
    POWER_CONNECTOR_NAME_ASC(Sort.by(Sort.Direction.ASC, "powerConnector.name")),

    /**
     * По названию коннектора питания по убыванию.
     */
    POWER_CONNECTOR_NAME_DESC(Sort.by(Sort.Direction.DESC, "powerConnector.name"));

    private final Sort sortValue;
}
