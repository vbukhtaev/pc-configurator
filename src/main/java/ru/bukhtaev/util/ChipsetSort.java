package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.Chipset.FIELD_SOCKET;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;

/**
 * Варианты сортировки для чипсетов.
 */
@Getter
@RequiredArgsConstructor
public enum ChipsetSort {

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
     * По названию сокета по возрастанию.
     */
    SOCKET_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_SOCKET + "." + FIELD_NAME
    )),

    /**
     * По названию сокета по убыванию.
     */
    SOCKET_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_SOCKET + "." + FIELD_NAME
    ));

    private final Sort sortValue;
}
