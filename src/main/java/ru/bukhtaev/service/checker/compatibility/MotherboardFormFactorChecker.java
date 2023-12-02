package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.ComputerCase;
import ru.bukhtaev.model.Motherboard;
import ru.bukhtaev.model.NameableEntity;
import ru.bukhtaev.validation.Translator;

import java.util.Optional;
import java.util.stream.Collectors;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_INCOMPATIBLE_MOTHERBOARD_FORM_FACTOR;

/**
 * Сервис проверки материнской платы.
 * Проверяет, что форм-фактор материнской платы соответствует
 * одному из поддерживаемых корпусом форм-факторов материнских плат.
 */
@Order(550)
@Component
public class MotherboardFormFactorChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected MotherboardFormFactorChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final ComputerCase computerCase = computer.getComputerCase();
        final Motherboard motherboard = computer.getMotherboard();

        if (computerCase == null
                || motherboard == null
        ) {
            return Optional.empty();
        }

        final var caseFormFactors = computerCase.getMotherboardFormFactors();
        final var motherboardFormFactor = motherboard.getFormFactor();

        if (!caseFormFactors.contains(motherboardFormFactor)) {
            final var caseFormFactorNames = caseFormFactors.stream()
                    .map(NameableEntity::getName)
                    .collect(Collectors.toSet());

            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_INCOMPATIBLE_MOTHERBOARD_FORM_FACTOR,
                    caseFormFactorNames,
                    motherboardFormFactor.getName()
            ));
        }

        return Optional.empty();
    }
}
