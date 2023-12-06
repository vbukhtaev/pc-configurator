package ru.bukhtaev.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Общее исключение для ситуации, когда в приложении возникла ошибка.
 */
@Getter
public abstract class CommonException extends RuntimeException {

    /**
     * HTTP статус.
     */
    private final HttpStatus targetStatus;

    /**
     * Сообщение об ошибке.
     */
    private final String errorMessage;

    /**
     * Конструктор.
     *
     * @param targetStatus HTTP статус
     * @param errorMessage сообщение об ошибке
     */
    protected CommonException(HttpStatus targetStatus, String errorMessage) {
        this.targetStatus = targetStatus;
        this.errorMessage = errorMessage;
    }
}
