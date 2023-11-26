package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.Fan.*;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.model.dictionary.FanSize.*;

/**
 * Варианты сортировки для вентиляторов.
 */
@Getter
@RequiredArgsConstructor
public enum FanSort {

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
     * По длине по возрастанию.
     */
    SIZE_LENGTH_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_SIZE + "." + FIELD_LENGTH
    )),

    /**
     * По длине по убыванию.
     */
    SIZE_LENGTH_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_SIZE + "." + FIELD_LENGTH
    )),

    /**
     * По ширине по возрастанию.
     */
    SIZE_WIDTH_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_SIZE + "." + FIELD_WIDTH
    )),

    /**
     * По ширине по убыванию.
     */
    SIZE_WIDTH_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_SIZE + "." + FIELD_WIDTH
    )),

    /**
     * По высоте по возрастанию.
     */
    SIZE_HEIGHT_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_SIZE + "." + FIELD_HEIGHT
    )),

    /**
     * По высоте по убыванию.
     */
    SIZE_HEIGHT_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_SIZE + "." + FIELD_HEIGHT
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
    ));

    private final Sort sortValue;
}
