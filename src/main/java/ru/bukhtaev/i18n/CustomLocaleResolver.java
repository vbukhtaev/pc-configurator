package ru.bukhtaev.i18n;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

/**
 * Сервис определения локали по значению заголовка {@link HttpHeaders#ACCEPT_LANGUAGE}.
 */
public class CustomLocaleResolver extends AcceptHeaderLocaleResolver {

    /**
     * Список поддерживаемых локалей.
     */
    private static final List<Locale> LOCALES = List.of(
            new Locale("en"),
            new Locale("ru")
    );

    @Override
    public @NonNull Locale resolveLocale(HttpServletRequest request) {
        final String headerLang = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);

        if (headerLang != null && !headerLang.isEmpty()) {
            final Locale resolved = Locale.lookup(
                    Locale.LanguageRange.parse(headerLang),
                    LOCALES
            );

            if (resolved != null) {
                return resolved;
            }
        }

        return Locale.ENGLISH;
    }
}
