package ru.bukhtaev.service.checker.completeness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.validation.Translator;

import java.util.Optional;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_COMPUTER_BUILD_NO_MOTHERBOARD;

/**
 * Сервис проверки сборки ПК
 * на наличие материнской платы.
 */
@Order(200)
@Component
public class MotherboardChecker extends ICompletenessChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    @Autowired
    protected MotherboardChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        if (computer.getMotherboard() == null) {
            return Optional.of(
                    translator.getMessage(MESSAGE_CODE_COMPUTER_BUILD_NO_MOTHERBOARD)
            );
        }

        return Optional.empty();
    }
}
