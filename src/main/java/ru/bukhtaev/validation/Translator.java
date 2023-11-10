package ru.bukhtaev.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Сервис предоставления сообщений.
 */
@Component
public class Translator {

    /**
     * Источник сообщений.
     */
    private final MessageSource messageSource;

    /**
     * Конструктор.
     *
     * @param messageSource источник сообщений
     */
    @Autowired
    public Translator(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Предоставляет сообщение с заданным кодом и параметрами.
     *
     * @param key  код сообщения
     * @param args параметры сообщения
     * @return сообщение с заданным кодом и параметрами
     */
    public String getMessage(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, locale);
    }
}
