package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Motherboard;
import ru.bukhtaev.model.dictionary.RamType;
import ru.bukhtaev.validation.Translator;

import java.util.Optional;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_MOTHERBOARD_DOES_NOT_SUPPORT_RAM_MODULES_RAM_TYPE;

/**
 * Сервис проверки модулей оперативной памяти.
 * Проверяет, что тип всех включенных в сборку модулей оперативной памяти
 * совпадает с типом оперативной памяти материнской платы.
 */
@Order(450)
@Component
public class RamModulesRamTypeMatchMotherboardRamTypeChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected RamModulesRamTypeMatchMotherboardRamTypeChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final Motherboard motherboard = computer.getMotherboard();
        final var computerToModules = computer.getRamModules();

        if (motherboard == null
                || computerToModules == null
                || computerToModules.isEmpty()
        ) {
            return Optional.empty();
        }

        final Optional<RamType> optModulesRamType = computerToModules.stream()
                .map(ctm -> ctm.getRamModule().getType())
                .distinct()
                .findFirst();

        if (optModulesRamType.isPresent()) {

            final RamType motherboardRamType = motherboard.getRamType();
            final RamType modulesRamType = optModulesRamType.get();

            if (!motherboardRamType.equals(modulesRamType)) {
                return Optional.of(translator.getMessage(
                        MESSAGE_CODE_MOTHERBOARD_DOES_NOT_SUPPORT_RAM_MODULES_RAM_TYPE,
                        motherboardRamType.getName(),
                        modulesRamType.getName()
                ));
            }
        }

        return Optional.empty();
    }
}
