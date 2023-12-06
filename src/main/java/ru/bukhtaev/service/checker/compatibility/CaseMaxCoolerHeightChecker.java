package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.ComputerCase;
import ru.bukhtaev.model.Cooler;
import ru.bukhtaev.i18n.Translator;

import java.util.Optional;

import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_CASE_MAX_COOLER_HEIGHT_EXCEEDING;

/**
 * Сервис проверки корпуса.
 * Проверяет, что высота включенного в сборку процессорного кулера
 * не превышает максимальной высоты устанавливаемого процессорного кулера корпуса.
 */
@Order(1150)
@Component
public class CaseMaxCoolerHeightChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected CaseMaxCoolerHeightChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final Cooler cooler = computer.getCooler();
        final ComputerCase computerCase = computer.getComputerCase();

        if (cooler == null
                || computerCase == null) {
            return Optional.empty();
        }

        final int coolerHeight = cooler.getHeight();
        final int caseMaxCoolerHeight = computerCase.getMaxCoolerHeight();

        if (coolerHeight > caseMaxCoolerHeight) {
            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_CASE_MAX_COOLER_HEIGHT_EXCEEDING,
                    coolerHeight,
                    caseMaxCoolerHeight
            ));
        }

        return Optional.empty();
    }
}
