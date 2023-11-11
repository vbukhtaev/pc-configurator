package ru.bukhtaev;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.bukhtaev.util.ChipsetSort;
import ru.bukhtaev.util.DesignSort;
import ru.bukhtaev.util.NameableSort;

/**
 * Утилитный класс, содержащий полезные для тестирования константы.
 */
public class TestUtils {

    /**
     * Только для статического использования.
     */
    private TestUtils() {
    }

    /**
     * Объект типа {@code Pageable}, подходящий для всех сущностей, имеющих название.
     */
    public static final Pageable NAMEABLE_PAGEABLE = PageRequest.of(
            0,
            20,
            NameableSort.NAME_ASC.getSortValue()
    );

    /**
     * Объект типа {@code Pageable} для вариантов исполнения.
     */
    public static final Pageable DESIGN_PAGEABLE = PageRequest.of(
            0,
            20,
            DesignSort.VENDOR_NAME_ASC.getSortValue()
    );

    /**
     * Объект типа {@code Pageable} для чипсетов.
     */
    public static final Pageable CHIPSET_PAGEABLE = PageRequest.of(
            0,
            20,
            ChipsetSort.SOCKET_NAME_ASC.getSortValue()
    );
}
