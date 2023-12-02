package ru.bukhtaev.service.checker.optimality;

import ru.bukhtaev.service.checker.IComputerBuildChecker;
import ru.bukhtaev.validation.Translator;

/**
 * Абстрактный сервис проверки сборки ПК
 * на оптимальность.
 */
public abstract class IOptimalityChecker extends IComputerBuildChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected IOptimalityChecker(final Translator translator) {
        super(translator);
    }
}
