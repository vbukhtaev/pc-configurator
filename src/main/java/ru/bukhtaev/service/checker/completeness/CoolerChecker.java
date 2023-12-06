package ru.bukhtaev.service.checker.completeness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.i18n.Translator;

import java.util.Optional;

import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_COMPUTER_BUILD_NO_CPU_COOLER;

/**
 * Сервис проверки сборки ПК
 * на наличие процессорного кулера.
 */
@Order(150)
@Component
public class CoolerChecker extends ICompletenessChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    @Autowired
    protected CoolerChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        if (computer.getCooler() == null) {
            return Optional.of(
                    translator.getMessage(MESSAGE_CODE_COMPUTER_BUILD_NO_CPU_COOLER)
            );
        }

        return Optional.empty();
    }
}
