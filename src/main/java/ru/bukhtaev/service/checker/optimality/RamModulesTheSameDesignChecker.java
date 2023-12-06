package ru.bukhtaev.service.checker.optimality;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Design;
import ru.bukhtaev.i18n.Translator;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_RAM_MODULES_WITH_DIFFERENT_DESIGN;
import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_TEMPLATE_TWO_PART_NAME;

/**
 * Сервис проверки модулей оперативной памяти.
 * Проверяет, что все модули оперативной памяти
 * имеют одинаковый вариант исполнения.
 */
@Order(100)
@Component
public class RamModulesTheSameDesignChecker extends IOptimalityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected RamModulesTheSameDesignChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final var computerToModules = computer.getRamModules();

        if (computerToModules == null || computerToModules.isEmpty()) {
            return Optional.empty();
        }

        final Set<Design> designSet = computerToModules.stream()
                .map(ctm -> ctm.getRamModule().getDesign())
                .collect(Collectors.toSet());

        if (designSet.size() > 1) {
            final Set<String> designNames = designSet.stream()
                    .map(design -> MessageFormat.format(
                            MESSAGE_TEMPLATE_TWO_PART_NAME,
                            design.getVendor().getName(),
                            design.getName()
                    )).collect(Collectors.toSet());

            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_RAM_MODULES_WITH_DIFFERENT_DESIGN,
                    designNames
            ));
        }

        return Optional.empty();
    }
}
