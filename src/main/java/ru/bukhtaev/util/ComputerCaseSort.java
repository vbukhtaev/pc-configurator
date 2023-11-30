package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.ComputerCase.*;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;

/**
 * Варианты сортировки для корпусов.
 */
@Getter
@RequiredArgsConstructor
public enum ComputerCaseSort {

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
     * По максимальной длине блока питания по возрастанию.
     */
    MAX_PSU_LENGTH_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_MAX_PSU_LENGTH
    )),

    /**
     * По максимальной длине блока питания по убыванию.
     */
    MAX_PSU_LENGTH_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_MAX_PSU_LENGTH
    )),

    /**
     * По максимальной длине видеокарты по возрастанию.
     */
    MAX_GRAPHICS_CARD_LENGTH_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_MAX_GRAPHICS_CARD_LENGTH
    )),

    /**
     * По максимальной длине видеокарты по убыванию.
     */
    MAX_GRAPHICS_CARD_LENGTH_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_MAX_GRAPHICS_CARD_LENGTH
    )),

    /**
     * По максимальной высоте кулера по возрастанию.
     */
    MAX_COOLER_HEIGHT_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_MAX_COOLER_HEIGHT
    )),

    /**
     * По максимальной высоте кулера по убыванию.
     */
    MAX_COOLER_HEIGHT_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_MAX_COOLER_HEIGHT
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
    ));

    private final Sort sortValue;
}
