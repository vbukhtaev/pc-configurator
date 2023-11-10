package ru.bukhtaev.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Общее исключение для ситуации, когда возникла ошибка на стороне клиента.
 * В данном случае под клиентом подразумевается пользователь этого приложения.
 */
@Getter
public abstract class CommonClientSideException extends CommonException {

    /**
     * Названия параметров, значения которых привели к исключению.
     */
    private final String[] paramNames;

    /**
     * Конструктор.
     *
     * @param targetStatus HTTP статус
     * @param errorMessage сообщение об ошибке
     * @param paramNames   названия параметров
     */
    protected CommonClientSideException(
            final HttpStatus targetStatus,
            final String errorMessage,
            final String... paramNames
    ) {
        super(targetStatus, errorMessage);
        this.paramNames = paramNames;
    }

    /**
     * Конструктор.
     *
     * @param errorMessage сообщение об ошибке
     * @param paramNames   названия параметров
     */
    protected CommonClientSideException(final String errorMessage, final String... paramNames) {
        super(BAD_REQUEST, errorMessage);
        this.paramNames = paramNames;
    }
}
