package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Motherboard;
import ru.bukhtaev.model.cross.ComputerBuildToFan;
import ru.bukhtaev.model.cross.MotherboardToFanPowerConnector;
import ru.bukhtaev.model.dictionary.FanPowerConnector;
import ru.bukhtaev.validation.Translator;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_NOT_ENOUGH_FAN_POWER_CONNECTORS;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_TEMPLATE_THREE_PART_NAME;

/**
 * Сервис проверки материнской платы.
 * Проверяет, что материнская плата располагает коннекторами питания
 * для всех включенных в сборку вентиляторов.
 */
@Order(650)
@Component
public class MotherboardHasEnoughFanPowerConnectorsChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected MotherboardHasEnoughFanPowerConnectorsChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final Motherboard motherboard = computer.getMotherboard();
        final var computerToFans = computer.getFans();

        if (motherboard == null
                || computerToFans == null
                || computerToFans.isEmpty()
        ) {
            return Optional.empty();
        }

        final var availableConnectors = motherboard.getFanPowerConnectors()
                .stream()
                .collect(Collectors.toMap(
                        MotherboardToFanPowerConnector::getFanPowerConnector,
                        MotherboardToFanPowerConnector::getCount,
                        Integer::sum
                ));

        final var neededConnectors = computerToFans
                .stream()
                .collect(Collectors.toMap(
                        ctf -> ctf.getFan().getPowerConnector(),
                        ComputerBuildToFan::getCount,
                        Integer::sum
                ));

        bookMatchingConnectors(neededConnectors, availableConnectors);
        bookUpperConnectors(neededConnectors, availableConnectors);

        if (!neededConnectors.isEmpty()) {
            final String motherboardName = MessageFormat.format(
                    MESSAGE_TEMPLATE_THREE_PART_NAME,
                    motherboard.getDesign().getVendor().getName(),
                    motherboard.getDesign().getName(),
                    motherboard.getName()
            );

            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_NOT_ENOUGH_FAN_POWER_CONNECTORS,
                    motherboardName
            ));
        }

        return Optional.empty();
    }

    /**
     * Бронирует те из доступных коннекторов питания вентиляторов,
     * что не совпадают с необходимыми, но совместимы без потерь функциональности.
     *
     * @param neededConnectors    необходимые коннекторы питания вентиляторов
     * @param availableConnectors доступные коннекторы питания вентиляторов
     */
    private void bookUpperConnectors(
            final Map<FanPowerConnector, Integer> neededConnectors,
            final Map<FanPowerConnector, Integer> availableConnectors
    ) {
        Set.copyOf(neededConnectors.entrySet()).forEach(entry -> {
            final FanPowerConnector neededConnector = entry.getKey();
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
                final FanPowerConnector upperConnector = upper.getKey();
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
     * Бронирует те из доступных коннекторов питания вентиляторов,
     * что полностью совпадают с необходимыми.
     *
     * @param neededConnectors    необходимые коннекторы питания вентиляторов
     * @param availableConnectors доступные коннекторы питания вентиляторов
     */
    private void bookMatchingConnectors(
            final Map<FanPowerConnector, Integer> neededConnectors,
            final Map<FanPowerConnector, Integer> availableConnectors
    ) {
        Set.copyOf(neededConnectors.entrySet()).forEach(entry -> {
            final FanPowerConnector neededConnector = entry.getKey();
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
