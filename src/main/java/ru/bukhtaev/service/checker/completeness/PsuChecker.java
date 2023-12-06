package ru.bukhtaev.service.checker.completeness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.i18n.Translator;

import java.util.Optional;

import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_COMPUTER_BUILD_NO_PSU;

/**
 * Сервис проверки сборки ПК
 * на наличие блока питания.
 */
@Order(100)
@Component
public class PsuChecker extends ICompletenessChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    @Autowired
    protected PsuChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        if (computer.getPsu() == null) {
            return Optional.of(
                    translator.getMessage(MESSAGE_CODE_COMPUTER_BUILD_NO_PSU)
            );
        }

        return Optional.empty();
    }
}
