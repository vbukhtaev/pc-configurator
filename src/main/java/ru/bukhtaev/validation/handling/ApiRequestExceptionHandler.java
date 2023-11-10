package ru.bukhtaev.validation.handling;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.bukhtaev.exception.CommonClientSideException;
import ru.bukhtaev.exception.CommonException;
import ru.bukhtaev.exception.CommonServerSideException;

import java.time.ZonedDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Обработчик ошибок запросов к API.
 */
@Slf4j
@RestControllerAdvice
public class ApiRequestExceptionHandler {

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handle(final CommonException exception) {
        log.error(exception.getErrorMessage(), exception);

        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        new Violation("Unknown error"),
                        ZonedDateTime.now()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final MethodArgumentNotValidException exception) {
        log.error(exception.getMessage(), exception);

        final List<Violation> violations = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new Violation(
                        error.getDefaultMessage(),
                        error.getField()
                ))
                .toList();

        return new ErrorResponse(
                violations,
                ZonedDateTime.now()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final ConstraintViolationException exception) {
        log.error(exception.getMessage(), exception);

        final List<Violation> violations = exception.getConstraintViolations()
                .stream()
                .map(violation -> new Violation(
                        violation.getMessage(),
                        resolveParamName(violation)
                ))
                .toList();

        return new ErrorResponse(
                violations,
                ZonedDateTime.now()
        );
    }

    @ExceptionHandler(CommonClientSideException.class)
    public ResponseEntity<ErrorResponse> handle(final CommonClientSideException exception) {
        log.error(exception.getErrorMessage(), exception);

        return ResponseEntity.status(exception.getTargetStatus())
                .body(new ErrorResponse(
                        new Violation(
                                exception.getErrorMessage(),
                                exception.getParamNames()
                        ),
                        ZonedDateTime.now()
                ));
    }

    @ExceptionHandler(CommonServerSideException.class)
    public ResponseEntity<ErrorResponse> handle(final CommonServerSideException exception) {
        log.error(exception.getErrorMessage(), exception);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        new Violation(INTERNAL_SERVER_ERROR.getReasonPhrase()),
                        ZonedDateTime.now()
                ));
    }

    /**
     * Распознает название параметра, значение которого нарушает правила валидации.
     *
     * @param violation нарушение
     * @return название параметра, значение которого нарушает правила валидации
     */
    private String resolveParamName(final ConstraintViolation<?> violation) {
        final String proprtyPathString = violation.getPropertyPath().toString();
        return proprtyPathString.substring(
                proprtyPathString.lastIndexOf(".") + 1
        );
    }
}
