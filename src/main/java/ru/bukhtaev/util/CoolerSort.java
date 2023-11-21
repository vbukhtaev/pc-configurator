package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

/**
 * Варианты сортировки для процессорных кулеров.
 */
@Getter
@RequiredArgsConstructor
public enum CoolerSort {

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
     * По рассеиваемой мощности по возрастанию.
     */
    POWER_DISSIPATION_ASC(Sort.by(Sort.Direction.ASC, "powerDissipation")),

    /**
     * По рассеиваемой мощности по убыванию.
     */
    POWER_DISSIPATION_DESC(Sort.by(Sort.Direction.DESC, "powerDissipation")),

    /**
     * По высоте по возрастанию.
     */
    HEIGHT_ASC(Sort.by(Sort.Direction.ASC, "height")),

    /**
     * По высоте по убыванию.
     */
    HEIGHT_DESC(Sort.by(Sort.Direction.DESC, "height")),

    /**
     * По названию вендора по возрастанию.
     */
    VENDOR_NAME_ASC(Sort.by(Sort.Direction.ASC, "vendor.name")),

    /**
     * По названию вендора по убыванию.
     */
    VENDOR_NAME_DESC(Sort.by(Sort.Direction.DESC, "vendor.name")),

    /**
     * По названию коннектора питания вентилятора по возрастанию.
     */
    POWER_CONNECTOR_NAME_ASC(Sort.by(Sort.Direction.ASC, "powerConnector.name")),

    /**
     * По названию коннектора питания вентилятора по убыванию.
     */
    POWER_CONNECTOR_NAME_DESC(Sort.by(Sort.Direction.DESC, "powerConnector.name")),

    /**
     * По длине вентилятора по возрастанию.
     */
    FAN_SIZE_LENGTH_ASC(Sort.by(Sort.Direction.ASC, "fanSize.length")),

    /**
     * По длине вентилятора по убыванию.
     */
    FAN_SIZE_LENGTH_DESC(Sort.by(Sort.Direction.DESC, "fanSize.length")),

    /**
     * По ширине вентилятора по возрастанию.
     */
    FAN_SIZE_WIDTH_ASC(Sort.by(Sort.Direction.ASC, "fanSize.width")),

    /**
     * По ширине вентилятора по убыванию.
     */
    FAN_SIZE_WIDTH_DESC(Sort.by(Sort.Direction.DESC, "fanSize.width")),

    /**
     * По высоте вентилятора по возрастанию.
     */
    FAN_SIZE_HEIGHT_ASC(Sort.by(Sort.Direction.ASC, "fanSize.height")),

    /**
     * По высоте вентилятора по убыванию.
     */
    FAN_SIZE_HEIGHT_DESC(Sort.by(Sort.Direction.DESC, "fanSize.height"));

    private final Sort sortValue;
}
