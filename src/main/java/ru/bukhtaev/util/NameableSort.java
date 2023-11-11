package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

/**
 * Варианты сортировки для сущностей, имеющих название.
 */
@Getter
@RequiredArgsConstructor
public enum NameableSort {

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
    NAME_DESC(Sort.by(Sort.Direction.DESC, "name"));

    private final Sort sortValue;
}
