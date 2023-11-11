package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

/**
 * Варианты сортировки для вентиляторов.
 */
@Getter
@RequiredArgsConstructor
public enum FanSort {

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
     * По названию вендора по возрастанию.
     */
    VENDOR_NAME_ASC(Sort.by(Sort.Direction.ASC, "vendor.name")),

    /**
     * По названию вендора по убыванию.
     */
    VENDOR_NAME_DESC(Sort.by(Sort.Direction.DESC, "vendor.name")),

    /**
     * По длине по возрастанию.
     */
    SIZE_LENGTH_ASC(Sort.by(Sort.Direction.ASC, "size.length")),

    /**
     * По длине по убыванию.
     */
    SIZE_LENGTH_DESC(Sort.by(Sort.Direction.DESC, "size.length")),

    /**
     * По ширине по возрастанию.
     */
    SIZE_WIDTH_ASC(Sort.by(Sort.Direction.ASC, "size.width")),

    /**
     * По ширине по убыванию.
     */
    SIZE_WIDTH_DESC(Sort.by(Sort.Direction.DESC, "size.width")),

    /**
     * По высоте по возрастанию.
     */
    SIZE_HEIGHT_ASC(Sort.by(Sort.Direction.ASC, "size.height")),

    /**
     * По высоте по убыванию.
     */
    SIZE_HEIGHT_DESC(Sort.by(Sort.Direction.DESC, "size.height")),

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
