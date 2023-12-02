package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.ComputerCase;
import ru.bukhtaev.model.GraphicsCard;
import ru.bukhtaev.validation.Translator;

import java.util.Optional;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_CASE_MAX_GRAPHICS_CARD_LENGTH_EXCEEDING;

/**
 * Сервис проверки корпуса.
 * Проверяет, что длина включенной в сборку видеокарты
 * не превышает максимальной длины устанавливаемой видеокарты корпуса.
 */
@Order(950)
@Component
public class CaseMaxGraphicsCardLengthChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected CaseMaxGraphicsCardLengthChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final GraphicsCard graphicsCard = computer.getGraphicsCard();
        final ComputerCase computerCase = computer.getComputerCase();

        if (graphicsCard == null
                || computerCase == null) {
            return Optional.empty();
        }

        final int graphicsCardLength = graphicsCard.getLength();
        final int caseMaxGraphicsCardLength = computerCase.getMaxGraphicsCardLength();

        if (graphicsCardLength > caseMaxGraphicsCardLength) {
            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_CASE_MAX_GRAPHICS_CARD_LENGTH_EXCEEDING,
                    graphicsCardLength,
                    caseMaxGraphicsCardLength
            ));
        }

        return Optional.empty();
    }
}
