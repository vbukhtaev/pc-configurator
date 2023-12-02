package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Motherboard;
import ru.bukhtaev.model.NameableEntity;
import ru.bukhtaev.model.Psu;
import ru.bukhtaev.model.cross.PsuToCpuPowerConnector;
import ru.bukhtaev.validation.Translator;

import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.containsAny;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_INCOMPATIBLE_CPU_POWER_CONNECTOR;

/**
 * Сервис проверки блока питания.
 * Проверяет, что коннектор питания процессора на материнской плате соответствует
 * одному из поддерживаемых блоком питания коннекторов питания процессора.
 */
@Order(750)
@Component
public class CpuPowerConnectorChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected CpuPowerConnectorChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final Motherboard motherboard = computer.getMotherboard();
        final Psu psu = computer.getPsu();

        if (motherboard == null || psu == null) {
            return Optional.empty();
        }

        final var motherboardConnector = motherboard.getCpuPowerConnector();

        final var psuConnectors = psu.getCpuPowerConnectors()
                .stream()
                .map(PsuToCpuPowerConnector::getCpuPowerConnector)
                .collect(Collectors.toSet());

        final var compatibleConnectors = motherboardConnector.getCompatibleConnectors();
        if (!psuConnectors.contains(motherboardConnector)
                && !containsAny(compatibleConnectors, psuConnectors)
        ) {
            final var motherboardConnectors = new HashSet<>(compatibleConnectors)
                    .stream()
                    .map(NameableEntity::getName)
                    .collect(Collectors.toSet());
            motherboardConnectors.add(motherboardConnector.getName());

            final var psuConnectorNames = psuConnectors.stream()
                    .map(NameableEntity::getName)
                    .collect(Collectors.toSet());

            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_INCOMPATIBLE_CPU_POWER_CONNECTOR,
                    psuConnectorNames.toString(),
                    motherboardConnectors.toString()
            ));
        }

        return Optional.empty();
    }
}
