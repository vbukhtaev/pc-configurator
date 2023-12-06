package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Motherboard;
import ru.bukhtaev.model.cross.ComputerBuildToRamModule;
import ru.bukhtaev.i18n.Translator;

import java.util.Optional;

import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_NOT_ENOUGH_RAM_SLOTS;

/**
 * Сервис проверки материнской платы.
 * Проверяет, что материнская плата располагает слотами
 * для всех включенных в сборку модулей оперативной памяти.
 */
@Order(800)
@Component
public class MotherboardHasEnoughSlotsChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected MotherboardHasEnoughSlotsChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final var computerToModules = computer.getRamModules();
        final Motherboard motherboard = computer.getMotherboard();

        if (motherboard == null
                || computerToModules == null
                || computerToModules.isEmpty()
        ) {
            return Optional.empty();
        }

        final int modulesCount = computerToModules.stream()
                .mapToInt(ComputerBuildToRamModule::getCount)
                .sum();

        final int slotsCount = motherboard.getSlotsCount();

        if (modulesCount > slotsCount) {
            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_NOT_ENOUGH_RAM_SLOTS,
                    slotsCount,
                    modulesCount
            ));
        }

        return Optional.empty();
    }
}
