package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.NameableEntity;
import ru.bukhtaev.model.dictionary.RamType;
import ru.bukhtaev.validation.Translator;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_INCOMPATIBLE_RAM_MODULES;

/**
 * Сервис проверки модулей оперативной памяти.
 * Проверяет, что все включенные в сборку модули оперативной памяти
 * имеют один и тот же тип памяти.
 */
@Order(200)
@Component
public class RamModulesTheSameRamTypeChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected RamModulesTheSameRamTypeChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final var computerToModules = computer.getRamModules();

        if (computerToModules == null
                || computerToModules.isEmpty()
        ) {
            return Optional.empty();
        }

        final Set<RamType> typeSet = computerToModules.stream()
                .map(ctm -> ctm.getRamModule().getType())
                .collect(Collectors.toSet());

        if (typeSet.size() > 1) {
            final var typeNameSet = typeSet.stream()
                    .map(NameableEntity::getName)
                    .collect(Collectors.toSet());

            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_INCOMPATIBLE_RAM_MODULES,
                    typeNameSet.toString()
            ));
        }

        return Optional.empty();
    }
}
