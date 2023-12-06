package ru.bukhtaev.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Общее исключение для ситуации, когда возникла ошибка на стороне сервера.
 * В данном случае под сервером подразумевается это приложение.
 */
public class CommonServerSideException extends CommonException {

    /**
     * Конструктор.
     *
     * @param errorMessage сообщение об ошибке
     */
    public CommonServerSideException(final String errorMessage) {
        super(INTERNAL_SERVER_ERROR, errorMessage);
    }
}
