package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Cooler;
import ru.bukhtaev.model.Motherboard;
import ru.bukhtaev.model.NameableEntity;
import ru.bukhtaev.validation.Translator;

import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_INCOMPATIBLE_COOLER_POWER_CONNECTOR;

/**
 * Сервис проверки материнской платы.
 * Проверяет, что коннектор питания процессорного кулера соответствует
 * коннектору питания процессорного кулера на материнской плате.
 */
@Order(600)
@Component
public class CoolerPowerConnectorChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected CoolerPowerConnectorChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final Motherboard motherboard = computer.getMotherboard();
        final Cooler cooler = computer.getCooler();

        if (motherboard == null || cooler == null) {
            return Optional.empty();
        }

        final var motherboardConnector = motherboard.getCoolerPowerConnector();
        final var coolerConnector = cooler.getPowerConnector();

        final var compatibleConnectors = motherboardConnector.getCompatibleConnectors();
        if (!motherboardConnector.equals(coolerConnector)
                && !compatibleConnectors.contains(coolerConnector)
        ) {
            final var motherboardConnectors = new HashSet<>(compatibleConnectors)
                    .stream()
                    .map(NameableEntity::getName)
                    .collect(Collectors.toSet());
            motherboardConnectors.add(motherboardConnector.getName());

            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_INCOMPATIBLE_COOLER_POWER_CONNECTOR,
                    motherboardConnectors.toString(),
                    coolerConnector.getName()
            ));
        }

        return Optional.empty();
    }
}
