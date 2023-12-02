package ru.bukhtaev.service.checker.completeness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.validation.Translator;

import java.util.Optional;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_NO_RAM_MODULES;

/**
 * Сервис проверки сборки ПК
 * на наличие модулей оперативной памяти.
 */
@Order(350)
@Component
public class RamModulesChecker extends ICompletenessChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    @Autowired
    protected RamModulesChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final var computerToModules = computer.getRamModules();
        if (computerToModules == null || computerToModules.isEmpty()) {
            return Optional.of(
                    translator.getMessage(MESSAGE_CODE_NO_RAM_MODULES)
            );
        }

        return Optional.empty();
    }
}
