package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Cooler;
import ru.bukhtaev.model.Cpu;
import ru.bukhtaev.model.NameableEntity;
import ru.bukhtaev.i18n.Translator;

import java.util.Optional;
import java.util.stream.Collectors;

import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_INCOMPATIBLE_COOLER_SOCKETS;

/**
 * Сервис проверки процессорного кулера.
 * Проверяет, что поддерживаемые процессорным кулером сокеты
 * содержат сокет процессора.
 */
@Order(250)
@Component
public class CoolerSocketMatchCpuSocketChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected CoolerSocketMatchCpuSocketChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final Cpu cpu = computer.getCpu();
        final Cooler cooler = computer.getCooler();

        if (cpu == null
                || cooler == null
        ) {
            return Optional.empty();
        }

        final var cpuSocket = cpu.getSocket();
        final var coolerSockets = cooler.getSupportedSockets();

        if (!coolerSockets.contains(cpuSocket)) {
            final var coolerSocketNames = coolerSockets.stream()
                    .map(NameableEntity::getName)
                    .collect(Collectors.toSet());

            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_INCOMPATIBLE_COOLER_SOCKETS,
                    coolerSocketNames.toString(),
                    cpuSocket.getName()
            ));
        }

        return Optional.empty();
    }
}
