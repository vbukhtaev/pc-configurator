package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.model.Psu.*;

/**
 * Варианты сортировки для блоков питания.
 */
@Getter
@RequiredArgsConstructor
public enum PsuSort {

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
     * По мощности по возрастанию.
     */
    POWER_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_POWER
    )),

    /**
     * По мощности по убыванию.
     */
    POWER_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_POWER
    )),

    /**
     * По мощности по линии 12V по возрастанию.
     */
    POWER_12V_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_POWER_12V
    )),

    /**
     * По мощности по линии 12V по убыванию.
     */
    POWER_12V_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_POWER_12V
    )),

    /**
     * По длине по возрастанию.
     */
    LENGTH_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_LENGTH
    )),

    /**
     * По длине по убыванию.
     */
    LENGTH_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_LENGTH
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
     * По названию форм-фактора по возрастанию.
     */
    FORM_FACTOR_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_FORM_FACTOR + "." + FIELD_NAME
    )),

    /**
     * По названию форм-фактора по убыванию.
     */
    FORM_FACTOR_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_FORM_FACTOR + "." + FIELD_NAME
    )),

    /**
     * По названию сертификата по возрастанию.
     */
    CERTIFICATE_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_CERTIFICATE + "." + FIELD_NAME
    )),

    /**
     * По названию сертификата по убыванию.
     */
    CERTIFICATE_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_CERTIFICATE + "." + FIELD_NAME
    )),

    /**
     * По названию основного коннектора питания по возрастанию.
     */
    MAIN_POWER_CONNECTOR_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_MAIN_POWER_CONNECTOR + "." + FIELD_NAME
    )),

    /**
     * По названию основного коннектора питания по убыванию.
     */
    MAIN_POWER_CONNECTOR_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_MAIN_POWER_CONNECTOR + "." + FIELD_NAME
    ));

    private final Sort sortValue;
}
