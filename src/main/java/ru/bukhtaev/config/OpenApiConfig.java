package ru.bukhtaev.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * Конфигурация Open API.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Weather CRUD API",
                description = "API for virtual PC configuration app",
                version = "0.0.1-SNAPSHOT",
                contact = @Contact(
                        name = "Bukhtaev Vladislav",
                        url = "https://t.me/VBukhtaev"
                )
        )
)
public class OpenApiConfig {
}
