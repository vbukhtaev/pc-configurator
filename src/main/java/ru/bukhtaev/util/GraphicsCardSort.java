package ru.bukhtaev.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

/**
 * Варианты сортировки для видеокарт.
 */
@Getter
@RequiredArgsConstructor
public enum GraphicsCardSort {

    /**
     * По ID по возрастанию.
     */
    ID_ASC(Sort.by(Sort.Direction.ASC, "id")),

    /**
     * По ID по убыванию.
     */
    ID_DESC(Sort.by(Sort.Direction.DESC, "id")),

    /**
     * По длине по возрастанию.
     */
    NAME_ASC(Sort.by(Sort.Direction.ASC, "length")),

    /**
     * По длине по убыванию.
     */
    NAME_DESC(Sort.by(Sort.Direction.DESC, "length")),

    /**
     * По названию графического процессора по возрастанию.
     */
    GPU_NAME_ASC(Sort.by(Sort.Direction.ASC, "gpu.name")),

    /**
     * По названию графического процессора по убыванию.
     */
    GPU_NAME_DESC(Sort.by(Sort.Direction.DESC, "gpu.name")),

    /**
     * По объему видеопамяти графического процессора по возрастанию.
     */
    GPU_LENGTH_ASC(Sort.by(Sort.Direction.ASC, "gpu.memorySize")),

    /**
     * По объему видеопамяти графического процессора по убыванию.
     */
    GPU_LENGTH_DESC(Sort.by(Sort.Direction.DESC, "gpu.memorySize")),

    /**
     * По названию варианта исполнения по возрастанию.
     */
    DESIGN_NAME_ASC(Sort.by(Sort.Direction.ASC, "design.name")),

    /**
     * По названию варианта исполнения по убыванию.
     */
    DESIGN_NAME_DESC(Sort.by(Sort.Direction.DESC, "design.name")),

    /**
     * По названию версии коннектора PCI-Express по возрастанию.
     */
    PCI_EXPRESS_CONNECTOR_VERSION_NAME_ASC(Sort.by(Sort.Direction.ASC, "pciExpressConnectorVersion.name")),

    /**
     * По названию версии коннектора PCI-Express по убыванию.
     */
    PCI_EXPRESS_CONNECTOR_VERSION_NAME_DESC(Sort.by(Sort.Direction.DESC, "pciExpressConnectorVersion.name"));

    private final Sort sortValue;
}
