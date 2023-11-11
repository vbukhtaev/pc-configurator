package ru.bukhtaev.exception;

/**
 * Исключение для ситуации, когда значение параметра некорректно.
 */
public class InvalidParamException extends CommonClientSideException {

    /**
     * Конструктор.
     *
     * @param errorMessage сообщение об ошибке
     * @param paramNames   названия параметров, значения которых привели к исключению
     */
    public InvalidParamException(final String errorMessage, final String... paramNames) {
        super(errorMessage, paramNames);
    }
}
