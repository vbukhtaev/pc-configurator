package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Motherboard;
import ru.bukhtaev.validation.Translator;

import java.util.Optional;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_MOTHERBOARD_MAX_RAM_SIZE_EXCEEDING;

/**
 * Сервис проверки модулей оперативной памяти.
 * Проверяет, что суммарный объем всех модулей оперативной памяти не превышает
 * максимальный объем оперативной памяти материнской платы.
 */
@Order(400)
@Component
public class MotherboardMaxRamSizeChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected MotherboardMaxRamSizeChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final var computerToModules = computer.getRamModules();
        final Motherboard motherboard = computer.getMotherboard();

        if (motherboard == null
                || computerToModules == null
                || computerToModules.isEmpty()) {
            return Optional.empty();
        }

        final int modulesSize = computerToModules.stream()
                .mapToInt(ctm -> ctm.getRamModule().getCapacity() * ctm.getCount())
                .sum();

        final int maxMemorySize = motherboard.getMaxMemorySize();

        if (modulesSize > maxMemorySize) {
            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_MOTHERBOARD_MAX_RAM_SIZE_EXCEEDING,
                    modulesSize / 1024,
                    maxMemorySize / 1024
            ));
        }

        return Optional.empty();
    }
}
