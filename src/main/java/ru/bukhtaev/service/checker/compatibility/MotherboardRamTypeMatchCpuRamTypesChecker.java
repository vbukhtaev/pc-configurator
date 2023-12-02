package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Cpu;
import ru.bukhtaev.model.Motherboard;
import ru.bukhtaev.model.NameableEntity;
import ru.bukhtaev.model.cross.CpuToRamType;
import ru.bukhtaev.validation.Translator;

import java.util.Optional;
import java.util.stream.Collectors;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_INCOMPATIBLE_MOTHERBOARD_RAM_TYPE;

/**
 * Сервис проверки материнской платы.
 * Проверяет, что поддерживаемый материнской платой тип оперативной памяти
 * соответствует одному из поддерживаемых процессором типов оперативной памяти.
 */
@Order(150)
@Component
public class MotherboardRamTypeMatchCpuRamTypesChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected MotherboardRamTypeMatchCpuRamTypesChecker(final Translator translator) {
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

        final var cpuRamTypes = cpu.getSupportedRamTypes()
                .stream()
                .map(CpuToRamType::getRamType)
                .collect(Collectors.toSet());

        final var motherboardRamType = motherboard.getRamType();

        if (!cpuRamTypes.contains(motherboardRamType)) {
            final var cpuRamTypeNames = cpuRamTypes.stream()
                    .map(NameableEntity::getName)
                    .collect(Collectors.toSet());

            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_INCOMPATIBLE_MOTHERBOARD_RAM_TYPE,
                    motherboardRamType,
                    cpuRamTypeNames.toString()
            ));
        }

        return Optional.empty();
    }
}
