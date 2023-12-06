package ru.bukhtaev.i18n;

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
     * Шаблон сообщения для сущностей с названием,
     * состоящим из 2 частей.
     */
    public static final String MESSAGE_TEMPLATE_TWO_PART_NAME = "{0} {1}";

    /**
     * Шаблон сообщения для сущностей с составным названием,
     * состоящим из 3 частей.
     */
    public static final String MESSAGE_TEMPLATE_THREE_PART_NAME = "{0} {1} {2}";

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
     * Код сообщения о том, что материнская плата с указанным названием,
     * вариантом исполнения, чипсетом и типом оперативной памяти уже существует.
     */
    public static final String MESSAGE_CODE_MOTHERBOARD_UNIQUE
            = "validation.motherboard.unique-name-and-design-and-chipset-and-ram-type";

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

    /**
     * Код сообщения о том, что в сборке ПК отсутствует процессор.
     */
    public static final String MESSAGE_CODE_COMPUTER_BUILD_NO_CPU = "completeness.no-cpu";

    /**
     * Код сообщения о том, что в сборке ПК отсутствует блок питания.
     */
    public static final String MESSAGE_CODE_COMPUTER_BUILD_NO_PSU = "completeness.no-psu";

    /**
     * Код сообщения о том, что в сборке ПК отсутствует процессорный кулер.
     */
    public static final String MESSAGE_CODE_COMPUTER_BUILD_NO_CPU_COOLER = "completeness.no-cpu-cooler";

    /**
     * Код сообщения о том, что в сборке ПК отсутствует материнская плата.
     */
    public static final String MESSAGE_CODE_COMPUTER_BUILD_NO_MOTHERBOARD = "completeness.no-motherboard";

    /**
     * Код сообщения о том, что в сборке ПК отсутствует видеокарта.
     */
    public static final String MESSAGE_CODE_NO_GRAPHICS_CARD = "completeness.no-graphics-card";

    /**
     * Код сообщения о том, что в сборке ПК отсутствует корпус.
     */
    public static final String MESSAGE_CODE_NO_COMPUTER_CASE = "completeness.no-computer-case";

    /**
     * Код сообщения о том, что в сборке ПК отсутствуют модули оперативной памяти.
     */
    public static final String MESSAGE_CODE_NO_RAM_MODULES = "completeness.no-ram-modules";

    /**
     * Код сообщения о том, что в сборке ПК отсутствуют устройства хранения данных.
     */
    public static final String MESSAGE_CODE_NO_STORAGE_DEVICES = "completeness.no-storage-devices";

    /**
     * Код сообщения о том, что в сборке ПК меньше двух вентиляторов.
     */
    public static final String MESSAGE_CODE_THERE_ARE_AT_LEAST_TWO_FANS = "completeness.there-are-at-least-two-fans";

    /**
     * Код сообщения о том, что в сборке ПК
     * версия коннектора PCI-Express на материнской плате ниже, чем на видеокарте.
     */
    public static final String MESSAGE_CODE_PCI_EXPRESS_CONNECTOR_VERSION = "optimality.pci-express-connector-version";

    /**
     * Код сообщения о том, что в сборке ПК
     * модули оперативной памяти имеют разные варианты исполнения.
     */
    public static final String MESSAGE_CODE_RAM_MODULES_WITH_DIFFERENT_DESIGN
            = "optimality.ram-modules-with-different-design";

    /**
     * Код сообщения о том, что в сборке ПК
     * модули оперативной памяти имеют разную частоту.
     */
    public static final String MESSAGE_CODE_RAM_MODULES_DIFFERENT_CLOCK
            = "optimality.ram-modules-different-clock";

    /**
     * Код сообщения о том, что в сборке ПК
     * есть модули оперативной памяти с частотой выше,
     * чем максимальна частота оперативной памяти процессора.
     */
    public static final String MESSAGE_CODE_CPU_MAX_RAM_CLOCK_EXCEEDING
            = "optimality.cpu-max-ram-clock-exceeding";

    /**
     * Код сообщения о том, что в сборке ПК
     * есть модули оперативной памяти с частотой выше,
     * чем максимальна частота оперативной памяти материнской платы.
     */
    public static final String MESSAGE_CODE_MOTHERBOARD_MAX_RAM_CLOCK_EXCEEDING
            = "optimality.motherboard-max-ram-clock-exceeding";

    /**
     * Код сообщения о том, что в сборке ПК
     * материнская плата не располагает количеством коннекторов питания,
     * позволяющим подключить все включенные в сборку накопители без потерь скорости.
     */
    public static final String MESSAGE_CODE_STORAGE_DEVICE_SPEED_LOSSES = "optimality.storage-connectors-speed-losses";

    /**
     * Код сообщения о том, что в сборке ПК
     * материнская плата не располагает количеством коннекторов подключения,
     * позволяющим подключить все включенные в сборку накопители.
     */
    public static final String MESSAGE_CODE_NOT_ENOUGH_STORAGE_CONNECTORS
            = "compatibility.not-enough-storage-connectors";

    /**
     * Код сообщения о том, что в сборке ПК
     * корпус не поддерживает достаточное количество размеров вентиляторов
     * для всех включенных в сборку вентиляторов.
     */
    public static final String MESSAGE_CODE_NOT_ENOUGH_FAN_SIZES
            = "compatibility.not-enough-fan-sizes";

    /**
     * Код сообщения о том, что в сборке ПК
     * корпус не поддерживает достаточное количество форматов отсеков расширения
     * для всех включенных в сборку жестких дисков и SSD-накопителей.
     */
    public static final String MESSAGE_CODE_NOT_ENOUGH_EXPANSION_BAY_FORMATS
            = "compatibility.not-enough-expansion-bay-formats";

    /**
     * Код сообщения о том, что в сборке ПК
     * блок питания не располагает количеством коннекторов питания накопителей,
     * позволяющим подключить все включенные в сборку накопители.
     */
    public static final String MESSAGE_CODE_NOT_ENOUGH_STORAGE_POWER_CONNECTORS
            = "compatibility.not-enough-storage-power-connectors";

    /**
     * Код сообщения о том, что в сборке ПК
     * материнская плата не располагает количеством коннекторов питания,
     * позволяющим подключить все включенные в сборку вентиляторы.
     */
    public static final String MESSAGE_CODE_NOT_ENOUGH_FAN_POWER_CONNECTORS
            = "compatibility.not-enough-fan-power-connectors";

    /**
     * Код сообщения о том, что в сборке ПК
     * блок питания не располагает всеми необходимыми
     * для питания видеокарты коннекторами.
     */
    public static final String MESSAGE_CODE_NOT_ENOUGH_GRAPHICS_CARD_POWER_CONNECTORS
            = "compatibility.not-enough-graphics-card-power-connectors";

    /**
     * Код сообщения о том, что в сборке ПК
     * материнская плата не располагает количеством слотов оперативной памяти,
     * позволяющим подключить все включенные в сборку модули оперативной памяти.
     */
    public static final String MESSAGE_CODE_NOT_ENOUGH_RAM_SLOTS
            = "compatibility.not-enough-ram-slots";

    /**
     * Код сообщения о том, что в сборке ПК
     * сокет материнской платы не соответствует сокету процессора.
     */
    public static final String MESSAGE_CODE_INCOMPATIBLE_MOTHERBOARD_SOCKET
            = "compatibility.incompatible-motherboard-socket";

    /**
     * Код сообщения о том, что в сборке ПК
     * форм-фактор материнской платы не соответствует ни одному
     * из поддерживаемых корпусом форм-факторов материнских плат.
     */
    public static final String MESSAGE_CODE_INCOMPATIBLE_MOTHERBOARD_FORM_FACTOR
            = "compatibility.incompatible-motherboard-form-factor";

    /**
     * Код сообщения о том, что в сборке ПК
     * форм-фактор блока питания не соответствует ни одному
     * из поддерживаемых корпусом форм-факторов блоков питания.
     */
    public static final String MESSAGE_CODE_INCOMPATIBLE_PSU_FORM_FACTOR
            = "compatibility.incompatible-psu-form-factor";

    /**
     * Код сообщения о том, что в сборке ПК
     * тип поддерживаемый материнской платой тип оперативной памяти не соответствует
     * ни одному поддерживаемому процессором типу оперативной памяти.
     */
    public static final String MESSAGE_CODE_INCOMPATIBLE_MOTHERBOARD_RAM_TYPE
            = "compatibility.incompatible-motherboard-ram-type";

    /**
     * Код сообщения о том, что в сборке ПК
     * модули оперативной памяти имеют разный тип памяти.
     */
    public static final String MESSAGE_CODE_INCOMPATIBLE_RAM_MODULES
            = "compatibility.incompatible-ram-modules";

    /**
     * Код сообщения о том, что в сборке ПК
     * ни один из поддерживаемых процессорным кулером сокетов
     * не соответствует сокету процессора.
     */
    public static final String MESSAGE_CODE_INCOMPATIBLE_COOLER_SOCKETS
            = "compatibility.incompatible-cooler-sockets";

    /**
     * Код сообщения о том, что в сборке ПК
     * коннектор питания процессорного кулера не соответствует
     * коннектору питания процессорного кулера на материнской плате.
     */
    public static final String MESSAGE_CODE_INCOMPATIBLE_COOLER_POWER_CONNECTOR
            = "compatibility.incompatible-cooler-power-connector";

    /**
     * Код сообщения о том, что в сборке ПК
     * основной коннектор питания на материнской плате не соответствует
     * основному коннектору питания в блоке питания.
     */
    public static final String MESSAGE_CODE_INCOMPATIBLE_MAIN_POWER_CONNECTOR
            = "compatibility.incompatible-main-power-connector";

    /**
     * Код сообщения о том, что в сборке ПК
     * ни один из поддерживаемых блоком питания коннекторов питания процессора сокетов
     * не соответствует коннектору питания процессора на материнской плате.
     */
    public static final String MESSAGE_CODE_INCOMPATIBLE_CPU_POWER_CONNECTOR
            = "compatibility.incompatible-cpu-power-connector";

    /**
     * Код сообщения о том, что в сборке ПК
     * процессор не поддерживает тип модулей оперативной памяти.
     */
    public static final String MESSAGE_CODE_CPU_DOES_NOT_SUPPORT_RAM_MODULES_RAM_TYPE
            = "compatibility.cpu-does-not-support-ram-modules-ram-type";

    /**
     * Код сообщения о том, что в сборке ПК
     * материнская плата не поддерживает тип модулей оперативной памяти.
     */
    public static final String MESSAGE_CODE_MOTHERBOARD_DOES_NOT_SUPPORT_RAM_MODULES_RAM_TYPE
            = "compatibility.motherboard-does-not-support-ram-modules-ram-type";

    /**
     * Код сообщения о том, что в сборке ПК
     * суммарный объем модулей оперативной памяти превышает
     * максимальный объем оперативной памяти процессора.
     */
    public static final String MESSAGE_CODE_CPU_MAX_RAM_SIZE_EXCEEDING
            = "compatibility.cpu-max-ram-size-exceeding";

    /**
     * Код сообщения о том, что в сборке ПК
     * мощности блока питания не достаточно.
     */
    public static final String MESSAGE_CODE_PSU_POWER_EXCEEDING
            = "compatibility.psu-power-exceeding";

    /**
     * Код сообщения о том, что в сборке ПК
     * суммарный объем модулей оперативной памяти превышает
     * максимальный объем оперативной памяти материнской платы.
     */
    public static final String MESSAGE_CODE_MOTHERBOARD_MAX_RAM_SIZE_EXCEEDING
            = "compatibility.motherboard-max-ram-size-exceeding";

    /**
     * Код сообщения о том, что в сборке ПК
     * видеокарта не сможет поместиться в корпус.
     */
    public static final String MESSAGE_CODE_CASE_MAX_GRAPHICS_CARD_LENGTH_EXCEEDING
            = "compatibility.case-max-graphics-card-length-exceeding";

    /**
     * Код сообщения о том, что в сборке ПК
     * блок питания не сможет поместиться в корпус.
     */
    public static final String MESSAGE_CODE_CASE_MAX_PSU_LENGTH_EXCEEDING
            = "compatibility.case-max-psu-length-exceeding";

    /**
     * Код сообщения о том, что в сборке ПК
     * процессорный кулер не сможет поместиться в корпус.
     */
    public static final String MESSAGE_CODE_CASE_MAX_COOLER_HEIGHT_EXCEEDING
            = "compatibility.case-max-cooler-height-exceeding";

    /**
     * Код сообщения о том, что в сборке ПК
     * рассеиваемой мощности процессорного кулера не достаточно для охлаждения процессора.
     */
    public static final String MESSAGE_CODE_CPU_OVERHEAT
            = "compatibility.cpu-overheat";
}
