package ru.bukhtaev.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Исключение для ситуации, когда нарушается уникальность имени сущности.
 */
public class UniqueNameException extends CommonClientSideException {

    /**
     * Конструктор.
     *
     * @param errorMessage сообщение об ошибке
     * @param paramNames   названия параметров, значения которых привели к исключению
     */
    public UniqueNameException(final String errorMessage, final String... paramNames) {
        super(BAD_REQUEST, errorMessage, paramNames);
    }
}
