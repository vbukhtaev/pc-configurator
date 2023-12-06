package ru.bukhtaev.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import ru.bukhtaev.i18n.CustomLocaleResolver;

import java.util.Locale;

/**
 * Конфигурация интернационализации.
 */
@Configuration
public class InternationalizationConfig {

    @Bean
    public LocaleResolver localeResolver() {
        return new CustomLocaleResolver();
    }

    @Bean
    public MessageSource messageSource() {
        final ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(
                "classpath:messages"
        );
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.ENGLISH);
        return messageSource;
    }
}
