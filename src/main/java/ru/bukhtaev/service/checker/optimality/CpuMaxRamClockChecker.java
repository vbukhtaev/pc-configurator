package ru.bukhtaev.service.checker.optimality;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Cpu;
import ru.bukhtaev.model.RamModule;
import ru.bukhtaev.model.cross.ComputerBuildToRamModule;
import ru.bukhtaev.model.dictionary.RamType;
import ru.bukhtaev.i18n.Translator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_CPU_MAX_RAM_CLOCK_EXCEEDING;

/**
 * Сервис проверки модулей оперативной памяти.
 * Проверяет, что все модули оперативной памяти не превышают
 * максимальную частоту оперативной памяти процессора.
 */
@Order(200)
@Component
public class CpuMaxRamClockChecker extends IOptimalityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected CpuMaxRamClockChecker(final Translator translator) {
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

        final Map<RamType, List<RamModule>> ramTypeMap = computerToModules.stream()
                .map(ComputerBuildToRamModule::getRamModule)
                .collect(Collectors.groupingBy(RamModule::getType));

        for (final var entry : ramTypeMap.entrySet()) {
            final RamType type = entry.getKey();
            final List<RamModule> modules = entry.getValue();

            final var optCpuToType = cpu.getSupportedRamTypes()
                    .stream()
                    .filter(cpuToRamType -> cpuToRamType.getRamType().equals(type))
                    .findFirst();

            if (optCpuToType.isPresent()) {
                final var cpuToType = optCpuToType.get();

                final Set<Integer> exceedingClockSet = modules.stream()
                        .map(RamModule::getClock)
                        .filter(clock -> clock > cpuToType.getMaxMemoryClock())
                        .collect(Collectors.toSet());

                if (exceedingClockSet.isEmpty()) {
                    return Optional.empty();
                }

                return Optional.of(translator.getMessage(
                        MESSAGE_CODE_CPU_MAX_RAM_CLOCK_EXCEEDING,
                        exceedingClockSet.toString(),
                        cpuToType.getMaxMemoryClock().toString()
                ));
            }
        }

        return Optional.empty();
    }
}
