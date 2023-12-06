package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Cpu;
import ru.bukhtaev.i18n.Translator;

import java.util.Optional;

import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_CPU_MAX_RAM_SIZE_EXCEEDING;

/**
 * Сервис проверки модулей оперативной памяти.
 * Проверяет, что суммарный объем всех модулей оперативной памяти не превышает
 * максимальный объем оперативной памяти процессора.
 */
@Order(350)
@Component
public class CpuMaxRamSizeChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected CpuMaxRamSizeChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final var computerToModules = computer.getRamModules();
        final Cpu cpu = computer.getCpu();

        if (cpu == null
                || computerToModules == null
                || computerToModules.isEmpty()) {
            return Optional.empty();
        }

        final int modulesSize = computerToModules.stream()
                .mapToInt(ctm -> ctm.getRamModule().getCapacity() * ctm.getCount())
                .sum();

        final int maxMemorySize = cpu.getMaxMemorySize();

        if (modulesSize > maxMemorySize) {
            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_CPU_MAX_RAM_SIZE_EXCEEDING,
                    modulesSize / 1024,
                    maxMemorySize / 1024
            ));
        }

        return Optional.empty();
    }
}
