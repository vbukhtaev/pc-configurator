package ru.bukhtaev.service.checker.optimality;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Motherboard;
import ru.bukhtaev.model.RamModule;
import ru.bukhtaev.model.cross.ComputerBuildToRamModule;
import ru.bukhtaev.model.dictionary.RamType;
import ru.bukhtaev.i18n.Translator;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_MOTHERBOARD_MAX_RAM_CLOCK_EXCEEDING;

/**
 * Сервис проверки модулей оперативной памяти.
 * Проверяет, что все модули оперативной памяти не превышают
 * максимальную частоту оперативной памяти материнской платы.
 */
@Order(250)
@Component
public class MotherboardMaxRamClockChecker extends IOptimalityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected MotherboardMaxRamClockChecker(final Translator translator) {
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

        final RamType motherboardRamType = motherboard.getRamType();
        final Integer motherboardMaxMemoryClock = motherboard.getMaxMemoryClock();

        final List<RamModule> modules = computerToModules.stream()
                .map(ComputerBuildToRamModule::getRamModule)
                .filter(module -> module.getType().equals(motherboardRamType)
                        && module.getClock() > motherboardMaxMemoryClock
                ).toList();

        if (modules.isEmpty()) {
            return Optional.empty();
        }

        final Set<Integer> exceedingClockSet = modules.stream()
                .map(RamModule::getClock)
                .collect(Collectors.toSet());

        return Optional.of(translator.getMessage(
                MESSAGE_CODE_MOTHERBOARD_MAX_RAM_CLOCK_EXCEEDING,
                exceedingClockSet.toString(),
                motherboardMaxMemoryClock.toString()
        ));
    }
}
