package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

/**
 * Варианты сортировки для модулей оперативной памяти.
 */
@Getter
@RequiredArgsConstructor
public enum RamModuleSort {

    /**
     * По ID по возрастанию.
     */
    ID_ASC(Sort.by(Sort.Direction.ASC, "id")),

    /**
     * По ID по убыванию.
     */
    ID_DESC(Sort.by(Sort.Direction.DESC, "id")),

    /**
     * По частоте по возрастанию.
     */
    CLOCK_ASC(Sort.by(Sort.Direction.ASC, "clock")),

    /**
     * По частоте по убыванию.
     */
    CLOCK_DESC(Sort.by(Sort.Direction.DESC, "clock")),

    /**
     * По объему по возрастанию.
     */
    CAPACITY_ASC(Sort.by(Sort.Direction.ASC, "capacity")),

    /**
     * По объему по убыванию.
     */
    CAPACITY_DESC(Sort.by(Sort.Direction.DESC, "capacity")),

    /**
     * По названию типа по возрастанию.
     */
    TYPE_NAME_ASC(Sort.by(Sort.Direction.ASC, "type.name")),

    /**
     * По названию типа по убыванию.
     */
    TYPE_NAME_DESC(Sort.by(Sort.Direction.DESC, "type.name")),

    /**
     * По названию варианта исполнения по возрастанию.
     */
    DESIGN_NAME_ASC(Sort.by(Sort.Direction.ASC, "design.name")),

    /**
     * По названию варианта исполнения по убыванию.
     */
    DESIGN_NAME_DESC(Sort.by(Sort.Direction.DESC, "design.name"));

    private final Sort sortValue;
}
