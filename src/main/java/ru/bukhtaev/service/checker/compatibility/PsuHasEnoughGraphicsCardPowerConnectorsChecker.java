package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.GraphicsCard;
import ru.bukhtaev.model.Psu;
import ru.bukhtaev.model.cross.GraphicsCardToPowerConnector;
import ru.bukhtaev.model.cross.PsuToGraphicsCardPowerConnector;
import ru.bukhtaev.model.dictionary.GraphicsCardPowerConnector;
import ru.bukhtaev.i18n.Translator;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_NOT_ENOUGH_GRAPHICS_CARD_POWER_CONNECTORS;
import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_TEMPLATE_TWO_PART_NAME;

/**
 * Сервис проверки блока питания.
 * Проверяет, что блок питания располагает всеми
 * необходимыми для питания видеокарты коннекторами.
 */
@Order(850)
@Component
public class PsuHasEnoughGraphicsCardPowerConnectorsChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected PsuHasEnoughGraphicsCardPowerConnectorsChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final GraphicsCard graphicsCard = computer.getGraphicsCard();
        final Psu psu = computer.getPsu();

        if (graphicsCard == null || psu == null) {
            return Optional.empty();
        }

        final var cardToConnectors = graphicsCard.getPowerConnectors();
        final var psuToConnectors = psu.getGraphicsCardPowerConnectors();

        final var availableConnectors = psuToConnectors.stream()
                .collect(Collectors.toMap(
                        PsuToGraphicsCardPowerConnector::getGraphicsCardPowerConnector,
                        PsuToGraphicsCardPowerConnector::getCount,
                        Integer::sum
                ));

        final var neededConnectors = cardToConnectors.stream()
                .collect(Collectors.toMap(
                        GraphicsCardToPowerConnector::getPowerConnector,
                        GraphicsCardToPowerConnector::getCount,
                        Integer::sum
                ));

        bookMatchingConnectors(neededConnectors, availableConnectors);
        bookCompatibleConnectors(neededConnectors, availableConnectors);

        if (!neededConnectors.isEmpty()) {
            final String psuName = MessageFormat.format(
                    MESSAGE_TEMPLATE_TWO_PART_NAME,
                    psu.getVendor().getName(),
                    psu.getName()
            );

            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_NOT_ENOUGH_GRAPHICS_CARD_POWER_CONNECTORS,
                    psuName
            ));
        }

        return Optional.empty();
    }

    /**
     * Бронирует те из доступных коннекторов питания видеокарты,
     * что не совпадают с необходимыми, но совместимы с ними.
     *
     * @param neededConnectors    необходимые коннекторы питания видеокарты
     * @param availableConnectors доступные коннекторы питания видеокарты
     */
    private void bookCompatibleConnectors(
            final Map<GraphicsCardPowerConnector, Integer> neededConnectors,
            final Map<GraphicsCardPowerConnector, Integer> availableConnectors
    ) {
        Set.copyOf(neededConnectors.entrySet()).forEach(entry -> {
            final var neededConnector = entry.getKey();
            final int neededCount = entry.getValue();

            // без потерь скорости
            final var compatibleConnectors = availableConnectors.entrySet()
                    .stream()
                    .filter(e -> e.getValue() > 0)
                    .filter(e -> neededConnector.getCompatibleConnectors().contains(e.getKey()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            Integer::sum
                    ));

            for (final var compatible : Map.copyOf(compatibleConnectors).entrySet()) {
                final var compatibleConnector = compatible.getKey();
                final int compatibleCount = compatible.getValue();

                if (neededCount == compatibleCount) {
                    compatibleConnectors.remove(compatibleConnector);
                    neededConnectors.remove(neededConnector);
                } else if (neededCount > compatibleCount) {
                    compatibleConnectors.remove(compatibleConnector);
                    neededConnectors.put(neededConnector, neededCount - compatibleCount);
                } else {
                    compatibleConnectors.put(compatibleConnector, compatibleCount - neededCount);
                    neededConnectors.remove(neededConnector);
                }
            }
        });
    }

    /**
     * Бронирует те из доступных коннекторов питания видеокарты,
     * что полностью совпадают с необходимыми.
     *
     * @param neededConnectors    необходимые коннекторы питания видеокарты
     * @param availableConnectors доступные коннекторы питания видеокарты
     */
    private void bookMatchingConnectors(
            final Map<GraphicsCardPowerConnector, Integer> neededConnectors,
            final Map<GraphicsCardPowerConnector, Integer> availableConnectors
    ) {
        Set.copyOf(neededConnectors.entrySet()).forEach(entry -> {
            final var neededConnector = entry.getKey();
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