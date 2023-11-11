package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

/**
 * Варианты сортировки для чипсетов.
 */
@Getter
@RequiredArgsConstructor
public enum ChipsetSort {

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
     * По названию сокета по возрастанию.
     */
    SOCKET_NAME_ASC(Sort.by(Sort.Direction.ASC, "socket.name")),

    /**
     * По названию сокета по убыванию.
     */
    SOCKET_NAME_DESC(Sort.by(Sort.Direction.DESC, "socket.name"));

    private final Sort sortValue;
}
