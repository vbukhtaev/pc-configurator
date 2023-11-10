package ru.bukhtaev.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Исключение для ситуации, когда не удалось найти запрашиваемые данные.
 */
public class DataNotFoundException extends CommonClientSideException {

    /**
     * Конструктор.
     *
     * @param errorMessage сообщение об ошибке
     * @param paramNames   названия параметров, значения которых привели к исключению
     */
    public DataNotFoundException(final String errorMessage, final String... paramNames) {
        super(NOT_FOUND, errorMessage, paramNames);
    }
}
