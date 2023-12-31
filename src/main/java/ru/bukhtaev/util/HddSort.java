package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.Hdd.FIELD_CACHE_SIZE;
import static ru.bukhtaev.model.Hdd.FIELD_SPINDLE_SPEED;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.model.StorageDevice.*;

/**
 * Варианты сортировки для жестких дисков.
 */
@Getter
@RequiredArgsConstructor
public enum HddSort {

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
     * По объему памяти по возрастанию.
     */
    CAPACITY_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_CAPACITY
    )),

    /**
     * По объему памяти по убыванию.
     */
    CAPACITY_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_CAPACITY
    )),

    /**
     * По скорости чтения по возрастанию.
     */
    READING_SPEED_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_READING_SPEED
    )),

    /**
     * По скорости чтения по убыванию.
     */
    READING_SPEED_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_READING_SPEED
    )),

    /**
     * По скорости записи по возрастанию.
     */
    WRITING_SPEED_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_WRITING_SPEED
    )),

    /**
     * По скорости записи по убыванию.
     */
    WRITING_SPEED_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_WRITING_SPEED
    )),

    /**
     * По скорости вращения шпинделя по возрастанию.
     */
    SPINDLE_SPEED_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_SPINDLE_SPEED
    )),

    /**
     * По скорости вращения шпинделя по убыванию.
     */
    SPINDLE_SPEED_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_SPINDLE_SPEED
    )),

    /**
     * По объему кэш-памяти по возрастанию.
     */
    CACHE_SIZE_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_CACHE_SIZE
    )),

    /**
     * По объему кэш-памяти по убыванию.
     */
    CACHE_SIZE_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_CACHE_SIZE
    )),

    /**
     * По названию вендора по возрастанию.
     */
    VENDOR_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_VENDOR + "." + FIELD_NAME
    )),

    /**
     * По названию вендора по убыванию.
     */
    VENDOR_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_VENDOR + "." + FIELD_NAME
    )),

    /**
     * По названию коннектора подключения по возрастанию.
     */
    CONNECTOR_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_CONNECTOR + "." + FIELD_NAME
    )),

    /**
     * По названию коннектора подключения по убыванию.
     */
    CONNECTOR_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_CONNECTOR + "." + FIELD_NAME
    )),

    /**
     * По названию коннектора питания по возрастанию.
     */
    POWER_CONNECTOR_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_POWER_CONNECTOR + "." + FIELD_NAME
    )),

    /**
     * По названию коннектора питания по убыванию.
     */
    POWER_CONNECTOR_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_POWER_CONNECTOR + "." + FIELD_NAME
    )),

    /**
     * По названию формата отсека расширения по возрастанию.
     */
    EXPANSION_BAY_FORMAT_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_EXPANSION_BAY_FORMAT + "." + FIELD_NAME
    )),

    /**
     * По названию формата отсека расширения по убыванию.
     */
    EXPANSION_BAY_FORMAT_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_EXPANSION_BAY_FORMAT + "." + FIELD_NAME
    ));

    private final Sort sortValue;
}
