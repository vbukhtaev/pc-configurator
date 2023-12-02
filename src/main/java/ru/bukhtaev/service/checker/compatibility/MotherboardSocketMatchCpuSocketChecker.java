package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Cpu;
import ru.bukhtaev.model.Motherboard;
import ru.bukhtaev.validation.Translator;

import java.util.Optional;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_INCOMPATIBLE_MOTHERBOARD_SOCKET;

/**
 * Сервис проверки материнской платы.
 * Проверяет, что сокет материнской платы соответствует сокету процессора.
 */
@Order(100)
@Component
public class MotherboardSocketMatchCpuSocketChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected MotherboardSocketMatchCpuSocketChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final Cpu cpu = computer.getCpu();
        final Motherboard motherboard = computer.getMotherboard();

        if (cpu == null
                || motherboard == null
        ) {
            return Optional.empty();
        }

        final var cpuSocket = cpu.getSocket();
        final var motherboardSocket = motherboard.getChipset().getSocket();

        if (!cpuSocket.equals(motherboardSocket)) {
            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_INCOMPATIBLE_MOTHERBOARD_SOCKET,
                    motherboardSocket.getName(),
                    cpuSocket.getName()
            ));
        }

        return Optional.empty();
    }
}
