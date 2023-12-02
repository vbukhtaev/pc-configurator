package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Psu;
import ru.bukhtaev.model.cross.ComputerBuildToHdd;
import ru.bukhtaev.model.cross.ComputerBuildToSsd;
import ru.bukhtaev.model.cross.PsuToStoragePowerConnector;
import ru.bukhtaev.model.dictionary.StoragePowerConnector;
import ru.bukhtaev.validation.Translator;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_NOT_ENOUGH_STORAGE_POWER_CONNECTORS;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_TEMPLATE_TWO_PART_NAME;

/**
 * Сервис проверки блока питания.
 * Проверяет, что блок питания располагает всеми необходимыми
 * коннекторами для питания жестких дисков и SSD-накопителей.
 */
@Order(1050)
@Component
public class PsuHasEnoughStoragePowerConnectorsChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected PsuHasEnoughStoragePowerConnectorsChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final Psu psu = computer.getPsu();
        final var computerToHdds = computer.getHdds();
        final var computerToSsds = computer.getSsds();

        if (psu == null
                || (computerToHdds == null
                || computerToHdds.isEmpty()
                && computerToSsds == null
                || computerToSsds.isEmpty()
        )) {
            return Optional.empty();
        }

        final var availableConnectors = psu.getStoragePowerConnectors()
                .stream()
                .collect(Collectors.toMap(
                        PsuToStoragePowerConnector::getStoragePowerConnector,
                        PsuToStoragePowerConnector::getCount,
                        Integer::sum
                ));

        final var hddsConnectors = computerToHdds
                .stream()
                .collect(Collectors.toMap(
                        cth -> cth.getHdd().getPowerConnector(),
                        ComputerBuildToHdd::getCount,
                        Integer::sum
                ));

        final var ssdsConnectors = computerToSsds
                .stream()
                .filter(cts -> cts.getSsd().getPowerConnector() != null)
                .collect(Collectors.toMap(
                        mtc -> mtc.getSsd().getPowerConnector(),
                        ComputerBuildToSsd::getCount,
                        Integer::sum
                ));

        final var neededConnectors = new HashMap<>(hddsConnectors);

        for (final var entry : ssdsConnectors.entrySet()) {
            final StoragePowerConnector connector = entry.getKey();
            final Integer count = entry.getValue();

            neededConnectors.merge(
                    connector,
                    count,
                    Integer::sum
            );
        }

        bookMatchingConnectors(neededConnectors, availableConnectors);

        if (!neededConnectors.isEmpty()) {
            final String psuName = MessageFormat.format(
                    MESSAGE_TEMPLATE_TWO_PART_NAME,
                    psu.getVendor().getName(),
                    psu.getName()
            );

            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_NOT_ENOUGH_STORAGE_POWER_CONNECTORS,
                    psuName
            ));
        }

        return Optional.empty();
    }

    /**
     * Бронирует те из доступных коннекторов подключения накопителей,
     * что полностью совпадают с необходимыми.
     *
     * @param neededConnectors    необходимые коннекторы подключения накопителей
     * @param availableConnectors доступные коннекторы подключения накопителей
     */
    private void bookMatchingConnectors(
            final Map<StoragePowerConnector, Integer> neededConnectors,
            final Map<StoragePowerConnector, Integer> availableConnectors
    ) {
        Set.copyOf(neededConnectors.entrySet()).forEach(entry -> {
            final StoragePowerConnector neededConnector = entry.getKey();
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