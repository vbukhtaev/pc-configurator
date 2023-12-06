package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.ComputerCase;
import ru.bukhtaev.model.NameableEntity;
import ru.bukhtaev.model.Psu;
import ru.bukhtaev.i18n.Translator;

import java.util.Optional;
import java.util.stream.Collectors;

import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_INCOMPATIBLE_PSU_FORM_FACTOR;

/**
 * Сервис проверки блока питания.
 * Проверяет, что форм-фактор блока питания соответствует
 * одному из поддерживаемых корпусом форм-факторов блоков питания.
 */
@Order(1000)
@Component
public class PsuFormFactorChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected PsuFormFactorChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final ComputerCase computerCase = computer.getComputerCase();
        final Psu psu = computer.getPsu();

        if (psu == null || computerCase == null) {
            return Optional.empty();
        }

        final var caseFormFactors = computerCase.getPsuFormFactors();
        final var psuFormFactor = psu.getFormFactor();

        if (!caseFormFactors.contains(psuFormFactor)) {
            final var caseFormFactorNames = caseFormFactors.stream()
                    .map(NameableEntity::getName)
                    .collect(Collectors.toSet());

            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_INCOMPATIBLE_PSU_FORM_FACTOR,
                    caseFormFactorNames,
                    psuFormFactor.getName()
            ));
        }

        return Optional.empty();
    }
}
