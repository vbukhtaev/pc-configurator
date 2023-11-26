package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.Gpu.FIELD_MEMORY_SIZE;
import static ru.bukhtaev.model.GraphicsCard.*;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;

/**
 * Варианты сортировки для видеокарт.
 */
@Getter
@RequiredArgsConstructor
public enum GraphicsCardSort {

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
     * По длине по возрастанию.
     */
    NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_LENGTH
    )),

    /**
     * По длине по убыванию.
     */
    NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_LENGTH
    )),

    /**
     * По названию графического процессора по возрастанию.
     */
    GPU_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_GPU + "." + FIELD_NAME
    )),

    /**
     * По названию графического процессора по убыванию.
     */
    GPU_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_GPU + "." + FIELD_NAME
    )),

    /**
     * По объему видеопамяти графического процессора по возрастанию.
     */
    GPU_LENGTH_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_GPU + "." + FIELD_MEMORY_SIZE
    )),

    /**
     * По объему видеопамяти графического процессора по убыванию.
     */
    GPU_LENGTH_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_GPU + "." + FIELD_MEMORY_SIZE
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
    )),

    /**
     * По названию версии коннектора PCI-Express по возрастанию.
     */
    PCI_EXPRESS_CONNECTOR_VERSION_NAME_ASC(Sort.by(
            Sort.Direction.ASC,
            FIELD_PCI_EXPRESS_CONNECTOR_VERSION + "." + FIELD_NAME
    )),

    /**
     * По названию версии коннектора PCI-Express по убыванию.
     */
    PCI_EXPRESS_CONNECTOR_VERSION_NAME_DESC(Sort.by(
            Sort.Direction.DESC,
            FIELD_PCI_EXPRESS_CONNECTOR_VERSION + "." + FIELD_NAME
    ));

    private final Sort sortValue;
}
