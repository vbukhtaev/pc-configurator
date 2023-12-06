package ru.bukhtaev.service.checker.completeness;

import ru.bukhtaev.service.checker.IComputerBuildChecker;
import ru.bukhtaev.i18n.Translator;

/**
 * Абстрактный сервис проверки сборки ПК
 * на наличие необходимых комплектующих.
 */
public abstract class ICompletenessChecker extends IComputerBuildChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected ICompletenessChecker(final Translator translator) {
        super(translator);
    }
}
