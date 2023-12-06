package ru.bukhtaev.service.checker.completeness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.i18n.Translator;

import java.util.Optional;

import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_NO_COMPUTER_CASE;

/**
 * Сервис проверки сборки ПК
 * на наличие корпуса.
 */
@Order(300)
@Component
public class ComputerCaseChecker extends ICompletenessChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    @Autowired
    protected ComputerCaseChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        if (computer.getComputerCase() == null) {
            return Optional.of(
                    translator.getMessage(MESSAGE_CODE_NO_COMPUTER_CASE)
            );
        }

        return Optional.empty();
    }
}
