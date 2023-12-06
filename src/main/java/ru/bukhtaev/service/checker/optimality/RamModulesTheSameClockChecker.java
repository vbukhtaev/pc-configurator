package ru.bukhtaev.service.checker.optimality;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.RamModule;
import ru.bukhtaev.model.cross.ComputerBuildToRamModule;
import ru.bukhtaev.model.dictionary.RamType;
import ru.bukhtaev.i18n.Translator;

import java.util.*;
import java.util.stream.Collectors;

import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_RAM_MODULES_DIFFERENT_CLOCK;

/**
 * Сервис проверки модулей оперативной памяти.
 * Проверяет, что все модули оперативной памяти
 * имеют одинаковую частоту.
 */
@Order(150)
@Component
public class RamModulesTheSameClockChecker extends IOptimalityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected RamModulesTheSameClockChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final var computerToModules = computer.getRamModules();

        if (computerToModules != null && !computerToModules.isEmpty()) {

            final Map<RamType, List<RamModule>> ramTypeMap = computerToModules.stream()
                    .map(ComputerBuildToRamModule::getRamModule)
                    .collect(Collectors.groupingBy(RamModule::getType));

            for (final var entry : ramTypeMap.entrySet()) {
                final List<RamModule> modules = entry.getValue();

                final Set<Integer> clockSet = modules.stream()
                        .map(RamModule::getClock)
                        .collect(Collectors.toSet());

                if (clockSet.size() > 1) {
                    final var minClock = clockSet.stream()
                            .min(Comparator.comparingInt(clock -> clock))
                            .get();

                    return Optional.of(translator.getMessage(
                            MESSAGE_CODE_RAM_MODULES_DIFFERENT_CLOCK,
                            clockSet,
                            minClock.toString()
                    ));
                }
            }
        }

        return Optional.empty();
    }
}
