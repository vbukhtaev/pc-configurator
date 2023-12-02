package ru.bukhtaev.service.checker.completeness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.validation.Translator;

import java.util.Optional;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_NO_GRAPHICS_CARD;

/**
 * Сервис проверки сборки ПК
 * на наличие видеокарты.
 */
@Order(250)
@Component
public class GraphicsCardChecker extends ICompletenessChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    @Autowired
    protected GraphicsCardChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        if (computer.getGraphicsCard() == null) {
            return Optional.of(
                    translator.getMessage(MESSAGE_CODE_NO_GRAPHICS_CARD)
            );
        }

        return Optional.empty();
    }
}
