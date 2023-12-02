package ru.bukhtaev.service.checker;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

/**
 * DTO для результата проверки сборки ПК на законченность,
 * совместимость комплектующих и оптимальность.
 */
@Schema(description = "Результат проверки на законченность, совместимость комплектующих и оптимальность")
@Getter
@Builder
public class ComputerVerifyResult {

    /**
     * Нарушения законченности сборки ПК.
     */
    private final Set<String> completenessViolations;

    /**
     * Нарушения совместимости комплектующих.
     */
    private final Set<String> compatibilityViolations;

    /**
     * Предупреждения о не оптимальности сборки ПК.
     */
    private final Set<String> optimalityWarnings;
}
