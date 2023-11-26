package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.model.RamModule.*;

/**
 * Варианты сортировки для модулей оперативной памяти.
 */
@Getter
@RequiredArgsConstructor
public enum RamModuleSort {

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
     * По частоте по возрастанию.
     */
    CLOCK_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_CLOCK
    )),

    /**
     * По частоте по убыванию.
     */
    CLOCK_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_CLOCK
    )),

    /**
     * По объему по возрастанию.
     */
    CAPACITY_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_CAPACITY
    )),

    /**
     * По объему по убыванию.
     */
    CAPACITY_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_CAPACITY
    )),

    /**
     * По названию типа по возрастанию.
     */
    TYPE_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_TYPE + "." + FIELD_NAME
    )),

    /**
     * По названию типа по убыванию.
     */
    TYPE_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_TYPE + "." + FIELD_NAME
    )),

    /**
     * По названию варианта исполнения по возрастанию.
     */
    DESIGN_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_DESIGN + "." + FIELD_NAME
    )),

    /**
     * По названию варианта исполнения по убыванию.
     */
    DESIGN_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_DESIGN + "." + FIELD_NAME
    ));

    private final Sort sortValue;
}
