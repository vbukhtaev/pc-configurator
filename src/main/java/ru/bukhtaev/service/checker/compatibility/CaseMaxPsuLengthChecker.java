package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.ComputerCase;
import ru.bukhtaev.model.Psu;
import ru.bukhtaev.i18n.Translator;

import java.util.Optional;

import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_CASE_MAX_PSU_LENGTH_EXCEEDING;

/**
 * Сервис проверки корпуса.
 * Проверяет, что длина включенного в сборку блока питания не превышает
 * максимальной длины устанавливаемого блока питания корпуса.
 */
@Order(1100)
@Component
public class CaseMaxPsuLengthChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected CaseMaxPsuLengthChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final Psu psu = computer.getPsu();
        final ComputerCase computerCase = computer.getComputerCase();

        if (psu == null
                || computerCase == null) {
            return Optional.empty();
        }

        final int psuLength = psu.getLength();
        final int caseMaxPsuLength = computerCase.getMaxPsuLength();

        if (psuLength > caseMaxPsuLength) {
            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_CASE_MAX_PSU_LENGTH_EXCEEDING,
                    psuLength,
                    caseMaxPsuLength
            ));
        }

        return Optional.empty();
    }
}
