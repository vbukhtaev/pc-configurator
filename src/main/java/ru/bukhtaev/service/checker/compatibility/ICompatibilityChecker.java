package ru.bukhtaev.service.checker.compatibility;

import ru.bukhtaev.service.checker.IComputerBuildChecker;
import ru.bukhtaev.i18n.Translator;

/**
 * Абстрактный сервис проверки сборки ПК
 * на совместимость комплектующих.
 */
public abstract class ICompatibilityChecker extends IComputerBuildChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected ICompatibilityChecker(final Translator translator) {
        super(translator);
    }
}
