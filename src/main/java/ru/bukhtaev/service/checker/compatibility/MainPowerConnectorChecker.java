package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Motherboard;
import ru.bukhtaev.model.NameableEntity;
import ru.bukhtaev.model.Psu;
import ru.bukhtaev.validation.Translator;

import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_INCOMPATIBLE_MAIN_POWER_CONNECTOR;

/**
 * Сервис проверки блока питания.
 * Проверяет, что основной коннектор питания на материнской плате
 * соответствует основному коннектору питания в блоке питания.
 */
@Order(700)
@Component
public class MainPowerConnectorChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected MainPowerConnectorChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final Motherboard motherboard = computer.getMotherboard();
        final Psu psu = computer.getPsu();

        if (motherboard == null || psu == null) {
            return Optional.empty();
        }

        final var motherboardConnector = motherboard.getMainPowerConnector();
        final var psuConnector = psu.getMainPowerConnector();

        final var compatibleConnectors = motherboardConnector.getCompatibleConnectors();
        if (!motherboardConnector.equals(psuConnector)
                && !compatibleConnectors.contains(psuConnector)
        ) {
            final var motherboardConnectors = new HashSet<>(compatibleConnectors)
                    .stream()
                    .map(NameableEntity::getName)
                    .collect(Collectors.toSet());
            motherboardConnectors.add(motherboardConnector.getName());

            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_INCOMPATIBLE_MAIN_POWER_CONNECTOR,
                    motherboardConnectors.toString(),
                    psuConnector.getName()
            ));
        }

        return Optional.empty();
    }
}
