package ru.bukhtaev.service.checker.completeness;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.cross.ComputerBuildToFan;
import ru.bukhtaev.i18n.Translator;

import java.util.Optional;

import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_THERE_ARE_AT_LEAST_TWO_FANS;

/**
 * Сервис проверки сборки ПК
 * на наличие как минимум 2 вентиляторов.
 */
@Order(450)
@Component
public class FansChecker extends ICompletenessChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected FansChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final var fans = computer.getFans();

        final Optional<String> message = Optional.of(translator.getMessage(
                MESSAGE_CODE_THERE_ARE_AT_LEAST_TWO_FANS
        ));

        if (fans == null || fans.isEmpty()) {
            return message;
        }

        final int fanCount = fans.stream()
                .mapToInt(ComputerBuildToFan::getCount)
                .sum();

        if (fanCount < 2) {
            return message;
        }

        return Optional.empty();
    }
}
