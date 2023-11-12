package ru.bukhtaev.validation;

/**
 * Утилитный класс со статическими константами, содержащими коды сообщений и сообщения.
 */
public class MessageUtils {

    /**
     * Только для статического использования.
     */
    private MessageUtils() {
    }

    /**
     * Код сообщения о том, что значение параметра некорректно.
     */
    public static final String MESSAGE_CODE_INVALID_PARAM_VALUE = "validation.common.invalid-param-value";

    /**
     * Код сообщения о том, что производитель с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_MANUFACTURER_NOT_FOUND = "validation.manufacturer.not-found";

    /**
     * Код сообщения о том, что производитель с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_MANUFACTURER_UNIQUE = "validation.manufacturer.unique-name";

    /**
     * Код сообщения о том, что сокет с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_SOCKET_NOT_FOUND = "validation.socket.not-found";

    /**
     * Код сообщения о том, что сокет с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_SOCKET_UNIQUE = "validation.socket.unique-name";

    /**
     * Код сообщения о том, что вендор с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_VENDOR_NOT_FOUND = "validation.vendor.not-found";

    /**
     * Код сообщения о том, что вендор с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_VENDOR_UNIQUE = "validation.vendor.unique-name";

    /**
     * Код сообщения о том, что тип оперативной памяти с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_RAM_TYPE_NOT_FOUND = "validation.ram-type.not-found";

    /**
     * Код сообщения о том, что тип оперативной памяти с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_RAM_TYPE_UNIQUE = "validation.ram-type.unique-name";

    /**
     * Код сообщения о том, что тип видеопамяти с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_VIDEO_MEMORY_TYPE_NOT_FOUND = "validation.video-memory-type.not-found";

    /**
     * Код сообщения о том, что тип видеопамяти с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_VIDEO_MEMORY_TYPE_UNIQUE = "validation.video-memory-type.unique-name";

    /**
     * Код сообщения о том, что коннектор питания вентилятора с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_FAN_POWER_CONNECTOR_NOT_FOUND = "validation.fan-power-connector.not-found";

    /**
     * Код сообщения о том, что коннектор питания вентилятора с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_FAN_POWER_CONNECTOR_UNIQUE = "validation.fan-power-connector.unique-name";

    /**
     * Код сообщения о том, что коннектор питания процессора с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_CPU_POWER_CONNECTOR_NOT_FOUND = "validation.cpu-power-connector.not-found";

    /**
     * Код сообщения о том, что коннектор питания процессора с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_CPU_POWER_CONNECTOR_UNIQUE = "validation.cpu-power-connector.unique-name";

    /**
     * Код сообщения о том, что основной коннектор питания с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_MAIN_POWER_CONNECTOR_NOT_FOUND =
            "validation.main-power-connector.not-found";

    /**
     * Код сообщения о том, что основной коннектор питания с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_MAIN_POWER_CONNECTOR_UNIQUE = "validation.main-power-connector.unique-name";

    /**
     * Код сообщения о том, что форм-фактор материнской платы с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_MOTHERBOARD_FORM_FACTOR_NOT_FOUND =
            "validation.motherboard-form-factor.not-found";

    /**
     * Код сообщения о том, что форм-фактор материнской платы с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_MOTHERBOARD_FORM_FACTOR_UNIQUE =
            "validation.motherboard-form-factor.unique-name";

    /**
     * Код сообщения о том, что коннектор подключения накопителя с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_STORAGE_CONNECTOR_NOT_FOUND = "validation.storage-connector.not-found";

    /**
     * Код сообщения о том, что коннектор подключения накопителя с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_STORAGE_CONNECTOR_UNIQUE = "validation.storage-connector.unique-name";

    /**
     * Код сообщения о том, что коннектор питания накопителя с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_STORAGE_POWER_CONNECTOR_NOT_FOUND =
            "validation.storage-power-connector.not-found";

    /**
     * Код сообщения о том, что коннектор питания накопителя с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_STORAGE_POWER_CONNECTOR_UNIQUE =
            "validation.storage-power-connector.unique-name";

    /**
     * Код сообщения о том, что версия коннектора PCI-Express с указанным ID не найдена.
     */
    public static final String MESSAGE_CODE_PCI_EXPRESS_CONNECTOR_VERSION_NOT_FOUND =
            "validation.pci-express-connector-version.not-found";

    /**
     * Код сообщения о том, что версия коннектора PCI-Express с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_PCI_EXPRESS_CONNECTOR_VERSION_UNIQUE =
            "validation.pci-express-connector-version.unique-name";

    /**
     * Код сообщения о том, что форм-фактор блока питания с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_PSU_FORM_FACTOR_NOT_FOUND = "validation.psu-form-factor.not-found";

    /**
     * Код сообщения о том, что форм-фактор блока питания с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_PSU_FORM_FACTOR_UNIQUE = "validation.psu-form-factor.unique-name";

    /**
     * Код сообщения о том, что сертификат блока питания с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_PSU_CERTIFICATE_NOT_FOUND = "validation.psu-certificate.not-found";

    /**
     * Код сообщения о том, что сертификат блока питания с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_PSU_CERTIFICATE_UNIQUE = "validation.psu-certificate.unique-name";

    /**
     * Код сообщения о том, что коннектор питания видеокарты с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_GRAPHICS_CARD_POWER_CONNECTOR_NOT_FOUND =
            "validation.graphics-card-power-connector.not-found";

    /**
     * Код сообщения о том, что коннектор питания видеокарты с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_GRAPHICS_CARD_POWER_CONNECTOR_UNIQUE =
            "validation.graphics-card-power-connector.unique-name";

    /**
     * Код сообщения о том, что формат отсеков расширения с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_EXPANSION_BAY_FORMAT_NOT_FOUND = "validation.expansion-bay-format.not-found";

    /**
     * Код сообщения о том, что формат отсеков расширения с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_EXPANSION_BAY_FORMAT_UNIQUE = "validation.expansion-bay-format.unique-name";

    /**
     * Код сообщения о том, что размер вентилятора с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_FAN_SIZE_NOT_FOUND = "validation.fan-size.not-found";

    /**
     * Код сообщения о том, что размер вентилятора с указанной длиной, шириной и высотой уже существует.
     */
    public static final String MESSAGE_CODE_FAN_SIZE_UNIQUE = "validation.fan-size.unique-size";

    /**
     * Код сообщения о том, что SSD с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_SSD_NOT_FOUND = "validation.ssd.not-found";

    /**
     * Код сообщения о том, что SSD с указанным названием и вместимостью уже существует.
     */
    public static final String MESSAGE_CODE_SSD_UNIQUE = "validation.ssd.unique-name-and-capacity";

    /**
     * Код сообщения о том, что HDD с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_HDD_NOT_FOUND = "validation.hdd.not-found";

    /**
     * Код сообщения о том, что HDD с указанным названием, вместимостью,
     * скоростью вращения шпинделя и объемом кэш-памяти уже существует.
     */
    public static final String MESSAGE_CODE_HDD_UNIQUE
            = "validation.hdd.unique-name-and-capacity-and-spindle-speed-and-cache-size";

    /**
     * Код сообщения о том, что вентилятор с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_FAN_NOT_FOUND = "validation.fan.not-found";

    /**
     * Код сообщения о том, что вентилятор с указанным названием и размером уже существует.
     */
    public static final String MESSAGE_CODE_FAN_UNIQUE = "validation.fan.unique-name-and-size";

    /**
     * Код сообщения о том, что чипсет с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_CHIPSET_NOT_FOUND = "validation.chipset.not-found";

    /**
     * Код сообщения о том, что чипсет с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_CHIPSET_UNIQUE = "validation.chipset.unique-name";

    /**
     * Код сообщения о том, что процессорный кулер с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_COOLER_NOT_FOUND = "validation.cooler.not-found";

    /**
     * Код сообщения о том, что процессорный кулер с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_COOLER_UNIQUE = "validation.cooler.unique-name";

    /**
     * Код сообщения о том, что вариант исполнения с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_DESIGN_NOT_FOUND = "validation.design.not-found";

    /**
     * Код сообщения о том, что вариант исполнения с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_DESIGN_UNIQUE = "validation.design.unique-name";

    /**
     * Код сообщения о том, что процессор с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_CPU_NOT_FOUND = "validation.cpu.not-found";

    /**
     * Код сообщения о том, что процессор с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_CPU_UNIQUE = "validation.cpu.unique-name";

    /**
     * Код сообщения о том, что графический процессор с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_GPU_NOT_FOUND = "validation.gpu.not-found";

    /**
     * Код сообщения о том, что графический процессор
     * с указанным названием, объемом видеопамяти и типом видеопамяти уже существует.
     */
    public static final String MESSAGE_CODE_GPU_UNIQUE = "validation.gpu.unique-name-and-memory-size-and-memory-type";

    /**
     * Код сообщения о том, что блок питания с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_PSU_NOT_FOUND = "validation.psu.not-found";

    /**
     * Код сообщения о том, что блок питания с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_PSU_UNIQUE = "validation.psu.unique-name";

    /**
     * Код сообщения о том, что корпус с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_COMPUTER_CASE_NOT_FOUND = "validation.computer-case.not-found";

    /**
     * Код сообщения о том, что корпус с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_COMPUTER_CASE_UNIQUE = "validation.computer-case.unique-name";

    /**
     * Код сообщения о том, что видеокарта с указанным ID не найдена.
     */
    public static final String MESSAGE_CODE_GRAPHICS_CARD_NOT_FOUND = "validation.graphics-card.not-found";

    /**
     * Код сообщения о том, что видеокарта
     * с указанным графическим процессором и вариантом исполнения уже существует.
     */
    public static final String MESSAGE_CODE_GRAPHICS_CARD_UNIQUE = "validation.graphics-card.unique-gpu-and-design";

    /**
     * Код сообщения о том, что материнская плата с указанным ID не найдена.
     */
    public static final String MESSAGE_CODE_MOTHERBOARD_NOT_FOUND = "validation.motherboard.not-found";

    /**
     * Код сообщения о том, что материнская плата
     * с указанным вариантом исполнения, чипсетом и типом оперативной памяти уже существует.
     */
    public static final String MESSAGE_CODE_MOTHERBOARD_UNIQUE
            = "validation.motherboard.unique-design-and-chipset-and-ram-type";

    /**
     * Код сообщения о том, что модуль оперативной памяти с указанным ID не найден.
     */
    public static final String MESSAGE_CODE_RAM_MODULE_NOT_FOUND = "validation.ram-module.not-found";

    /**
     * Код сообщения о том, что модуль оперативной памяти
     * с указанной частотой, вместимостью, типом и вариантом исполнения уже существует.
     */
    public static final String MESSAGE_CODE_RAM_MODULE_UNIQUE
            = "validation.ram-module.unique-clock-and-capacity-and-type-and-design";

    /**
     * Код сообщения о том, что конфигурация ПК с указанным ID не найдена.
     */
    public static final String MESSAGE_CODE_COMPUTER_BUILD_NOT_FOUND = "validation.computer-build.not-found";

    /**
     * Код сообщения о том, что конфигурация ПК с указанным названием уже существует.
     */
    public static final String MESSAGE_CODE_COMPUTER_BUILD_UNIQUE = "validation.computer-build.unique-name";
}
