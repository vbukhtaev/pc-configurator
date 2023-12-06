package ru.bukhtaev.service.checker;

import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.i18n.Translator;

import java.util.Optional;

/**
 * Абстрактный сервис проверки сборки ПК.
 */
public abstract class IComputerBuildChecker {

    /**
     * Сервис предоставления сообщений.
     */
    protected final Translator translator;

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected IComputerBuildChecker(final Translator translator) {
        this.translator = translator;
    }

    /**
     * Проверяет сборку ПК.
     * Возвращает объект типа {@link Optional} с сообщением о нарушении, если оно обнаружено.
     * В противном случае возвращает пустой объект типа {@link Optional}.
     *
     * @param computer сборка ПК
     * @return объект типа {@link Optional} с сообщением о нарушении, если оно обнаружено
     */
    public abstract Optional<String> check(final ComputerBuild computer);
}
