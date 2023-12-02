package ru.bukhtaev.service.checker.completeness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.validation.Translator;

import java.util.Optional;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_COMPUTER_BUILD_NO_CPU;

/**
 * Сервис проверки сборки ПК
 * на наличие процессора.
 */
@Order(50)
@Component
public class CpuChecker extends ICompletenessChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    @Autowired
    protected CpuChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        if (computer.getCpu() == null) {
            return Optional.of(
                    translator.getMessage(MESSAGE_CODE_COMPUTER_BUILD_NO_CPU)
            );
        }

        return Optional.empty();
    }
}
