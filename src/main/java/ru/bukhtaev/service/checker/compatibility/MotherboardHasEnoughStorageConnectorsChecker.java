package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Motherboard;
import ru.bukhtaev.model.cross.ComputerBuildToHdd;
import ru.bukhtaev.model.cross.ComputerBuildToSsd;
import ru.bukhtaev.model.cross.MotherboardToStorageConnector;
import ru.bukhtaev.model.dictionary.StorageConnector;
import ru.bukhtaev.i18n.Translator;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_NOT_ENOUGH_STORAGE_CONNECTORS;
import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_TEMPLATE_THREE_PART_NAME;

/**
 * Сервис проверки материнской платы.
 * Проверяет, что материнская плата располагает коннекторами подключения
 * для всех включенных в сборку жестких дисков и SSD-накопителей.
 */
@Order(50)
@Component
public class MotherboardHasEnoughStorageConnectorsChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected MotherboardHasEnoughStorageConnectorsChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final Motherboard motherboard = computer.getMotherboard();
        final var computerToHdds = computer.getHdds();
        final var computerToSsds = computer.getSsds();

        if (motherboard == null
                || (computerToHdds == null
                || computerToHdds.isEmpty()
                && computerToSsds == null
                || computerToSsds.isEmpty()
        )) {
            return Optional.empty();
        }

        final var availableConnectors = motherboard.getStorageConnectors()
                .stream()
                .collect(Collectors.toMap(
                        MotherboardToStorageConnector::getStorageConnector,
                        MotherboardToStorageConnector::getCount,
                        Integer::sum
                ));

        final var hddsConnectors = computerToHdds
                .stream()
                .collect(Collectors.toMap(
                        mtc -> mtc.getHdd().getConnector(),
                        ComputerBuildToHdd::getCount,
                        Integer::sum
                ));

        final var ssdsConnectors = computerToSsds
                .stream()
                .collect(Collectors.toMap(
                        mtc -> mtc.getSsd().getConnector(),
                        ComputerBuildToSsd::getCount,
                        Integer::sum
                ));

        final Map<StorageConnector, Integer> neededConnectors = new HashMap<>(hddsConnectors);

        for (final var entry : ssdsConnectors.entrySet()) {
            final StorageConnector connector = entry.getKey();
            final Integer count = entry.getValue();

            neededConnectors.merge(
                    connector,
                    count,
                    Integer::sum
            );
        }

        bookMatchingConnectors(neededConnectors, availableConnectors);
        bookUpperConnectors(neededConnectors, availableConnectors);
        bookLowerConnectors(neededConnectors, availableConnectors);

        if (!neededConnectors.isEmpty()) {
            final String motherboardName = MessageFormat.format(
                    MESSAGE_TEMPLATE_THREE_PART_NAME,
                    motherboard.getDesign().getVendor().getName(),
                    motherboard.getDesign().getName(),
                    motherboard.getName()
            );

            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_NOT_ENOUGH_STORAGE_CONNECTORS,
                    motherboardName
            ));
        }

        return Optional.empty();
    }

    /**
     * Бронирует те из доступных коннекторов подключения накопителей,
     * что не совпадают с необходимыми, но совместимы с потерями в скорости.
     *
     * @param neededConnectors    необходимые коннекторы подключения накопителей
     * @param availableConnectors доступные коннекторы подключения накопителей
     */
    private void bookLowerConnectors(
            final Map<StorageConnector, Integer> neededConnectors,
            final Map<StorageConnector, Integer> availableConnectors
    ) {
        Set.copyOf(neededConnectors.entrySet()).forEach(entry -> {
            final StorageConnector neededConnector = entry.getKey();
            final int neededCount = entry.getValue();

            final var lowerConnectors = availableConnectors.entrySet()
                    .stream()
                    .filter(e -> e.getValue() > 0)
                    .filter(e -> neededConnector.getCompatibleConnectors().contains(e.getKey()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));

            for (final var lower : Map.copyOf(lowerConnectors).entrySet()) {
                final StorageConnector lowerConnector = lower.getKey();
                final int lowerCount = lower.getValue();

                if (neededCount == lowerCount) {
                    lowerConnectors.remove(lowerConnector);
                    neededConnectors.remove(neededConnector);
                } else if (neededCount > lowerCount) {
                    lowerConnectors.remove(lowerConnector);
                    neededConnectors.put(neededConnector, neededCount - lowerCount);
                } else {
                    lowerConnectors.put(lowerConnector, lowerCount - neededCount);
                    neededConnectors.remove(neededConnector);
                }
            }
        });
    }

    /**
     * Бронирует те из доступных коннекторов подключения накопителей,
     * что не совпадают с необходимыми, но совместимы без потерь в скорости.
     *
     * @param neededConnectors    необходимые коннекторы подключения накопителей
     * @param availableConnectors доступные коннекторы подключения накопителей
     */
    private void bookUpperConnectors(
            final Map<StorageConnector, Integer> neededConnectors,
            final Map<StorageConnector, Integer> availableConnectors
    ) {
        Set.copyOf(neededConnectors.entrySet()).forEach(entry -> {
            final StorageConnector neededConnector = entry.getKey();
            final int neededCount = entry.getValue();

            // без потерь скорости
            final var upperConnectors = availableConnectors.entrySet()
                    .stream()
                    .filter(e -> e.getValue() > 0)
                    .filter(e -> e.getKey().getCompatibleConnectors().contains(neededConnector))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            Integer::sum
                    ));

            for (final var upper : Map.copyOf(upperConnectors).entrySet()) {
                final StorageConnector upperConnector = upper.getKey();
                final int upperCount = upper.getValue();

                if (neededCount == upperCount) {
                    upperConnectors.remove(upperConnector);
                    neededConnectors.remove(neededConnector);
                } else if (neededCount > upperCount) {
                    upperConnectors.remove(upperConnector);
                    neededConnectors.put(neededConnector, neededCount - upperCount);
                } else {
                    upperConnectors.put(upperConnector, upperCount - neededCount);
                    neededConnectors.remove(neededConnector);
                }
            }
        });
    }

    /**
     * Бронирует те из доступных коннекторов подключения накопителей,
     * что полностью совпадают с необходимыми.
     *
     * @param neededConnectors    необходимые коннекторы подключения накопителей
     * @param availableConnectors доступные коннекторы подключения накопителей
     */
    private void bookMatchingConnectors(
            final Map<StorageConnector, Integer> neededConnectors,
            final Map<StorageConnector, Integer> availableConnectors
    ) {
        Set.copyOf(neededConnectors.entrySet()).forEach(entry -> {
            final StorageConnector neededConnector = entry.getKey();
            final int neededCount = entry.getValue();

            final int availableCount = availableConnectors.getOrDefault(neededConnector, 0);

            if (neededCount == availableCount) {
                neededConnectors.remove(neededConnector);
                availableConnectors.remove(neededConnector);

            } else if (neededCount > availableCount) {
                availableConnectors.remove(neededConnector);
                neededConnectors.put(
                        neededConnector,
                        neededCount - availableCount
                );

            } else {
                availableConnectors.put(
                        neededConnector,
                        availableCount - neededCount
                );
                neededConnectors.remove(neededConnector);
            }
        });
    }
}
