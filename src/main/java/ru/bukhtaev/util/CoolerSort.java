package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.Cooler.*;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.model.dictionary.FanSize.FIELD_LENGTH;
import static ru.bukhtaev.model.dictionary.FanSize.FIELD_WIDTH;

/**
 * Варианты сортировки для процессорных кулеров.
 */
@Getter
@RequiredArgsConstructor
public enum CoolerSort {

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
     * По рассеиваемой мощности по возрастанию.
     */
    POWER_DISSIPATION_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_POWER_DISSIPATION
    )),

    /**
     * По рассеиваемой мощности по убыванию.
     */
    POWER_DISSIPATION_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_POWER_DISSIPATION
    )),

    /**
     * По высоте по возрастанию.
     */
    HEIGHT_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_HEIGHT
    )),

    /**
     * По высоте по убыванию.
     */
    HEIGHT_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_HEIGHT
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
     * По названию коннектора питания вентилятора по возрастанию.
     */
    POWER_CONNECTOR_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_POWER_CONNECTOR + "." + FIELD_NAME
    )),

    /**
     * По названию коннектора питания вентилятора по убыванию.
     */
    POWER_CONNECTOR_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_POWER_CONNECTOR + "." + FIELD_NAME
    )),

    /**
     * По длине вентилятора по возрастанию.
     */
    FAN_SIZE_LENGTH_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_FAN_SIZE + "." + FIELD_LENGTH
    )),

    /**
     * По длине вентилятора по убыванию.
     */
    FAN_SIZE_LENGTH_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_FAN_SIZE + "." + FIELD_LENGTH
    )),

    /**
     * По ширине вентилятора по возрастанию.
     */
    FAN_SIZE_WIDTH_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_FAN_SIZE + "." + FIELD_WIDTH
    )),

    /**
     * По ширине вентилятора по убыванию.
     */
    FAN_SIZE_WIDTH_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_FAN_SIZE + "." + FIELD_WIDTH
    )),

    /**
     * По высоте вентилятора по возрастанию.
     */
    FAN_SIZE_HEIGHT_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_FAN_SIZE + "." + FIELD_HEIGHT
    )),

    /**
     * По высоте вентилятора по убыванию.
     */
    FAN_SIZE_HEIGHT_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_FAN_SIZE + "." + FIELD_HEIGHT
    ));

    private final Sort sortValue;
}
