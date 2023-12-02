package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Cpu;
import ru.bukhtaev.model.NameableEntity;
import ru.bukhtaev.model.cross.CpuToRamType;
import ru.bukhtaev.model.dictionary.RamType;
import ru.bukhtaev.validation.Translator;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_CPU_DOES_NOT_SUPPORT_RAM_MODULES_RAM_TYPE;

/**
 * Сервис проверки модулей оперативной памяти.
 * Проверяет, что тип всех включенных в сборку модулей оперативной памяти
 * совпадает с одним из поддерживаемых процессором типов оперативной памяти.
 */
@Order(300)
@Component
public class RamModulesRamTypeMatchCpuRamTypeChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected RamModulesRamTypeMatchCpuRamTypeChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final Cpu cpu = computer.getCpu();
        final var computerToModules = computer.getRamModules();

        if (cpu == null
                || computerToModules == null
                || computerToModules.isEmpty()
        ) {
            return Optional.empty();
        }

        final Set<RamType> modulesRamTypes = computerToModules.stream()
                .map(ctm -> ctm.getRamModule().getType())
                .collect(Collectors.toSet());

        final Set<RamType> cpuRamTypes = cpu.getSupportedRamTypes()
                .stream()
                .map(CpuToRamType::getRamType)
                .collect(Collectors.toSet());

        if (!cpuRamTypes.containsAll(modulesRamTypes)) {
            final var cpuRamTypeNames = cpuRamTypes.stream()
                    .map(NameableEntity::getName)
                    .collect(Collectors.toSet());

            final var optModulesRamType = modulesRamTypes.stream()
                    .map(NameableEntity::getName)
                    .findFirst();

            if (optModulesRamType.isPresent()) {
                return Optional.of(translator.getMessage(
                        MESSAGE_CODE_CPU_DOES_NOT_SUPPORT_RAM_MODULES_RAM_TYPE,
                        cpuRamTypeNames.toString(),
                        optModulesRamType.get()
                ));
            }
        }

        return Optional.empty();
    }
}
