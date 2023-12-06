package ru.bukhtaev.service.checker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.repository.IComputerBuildRepository;
import ru.bukhtaev.service.checker.compatibility.ICompatibilityChecker;
import ru.bukhtaev.service.checker.completeness.ICompletenessChecker;
import ru.bukhtaev.service.checker.optimality.IOptimalityChecker;
import ru.bukhtaev.i18n.Translator;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.bukhtaev.model.ComputerBuild.FIELD_ID;
import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_COMPUTER_BUILD_NOT_FOUND;

/**
 * Сервис проверки сборки ПК на законченность,
 * совместимость комплектующих и оптимальность.
 */
@Service
public class ComputerBuildVerifyingService {

    /**
     * Сервисы проверки сборки ПК на завершенность.
     */
    private final List<ICompletenessChecker> completenessCheckers;

    /**
     * Сервисы проверки сборки ПК на совместимость комплектующих.
     */
    private final List<ICompatibilityChecker> compatibilityCheckers;

    /**
     * Сервисы проверки сборки ПК на оптимальность.
     */
    private final List<IOptimalityChecker> optimalityCheckers;

    /**
     * Репозиторий сборок ПК.
     */
    private final IComputerBuildRepository repository;

    /**
     * Сервис предоставления сообщений.
     */
    private final Translator translator;

    /**
     * Конструктор.
     *
     * @param completenessCheckers  сервисы проверки сборки ПК на завершенность
     * @param compatibilityCheckers сервисы проверки сборки ПК на совместимость комплектующих
     * @param optimalityCheckers    сервисы проверки сборки ПК на оптимальность
     * @param repository            репозиторий сборок ПК
     * @param translator            сервис предоставления сообщений
     */
    @Autowired
    public ComputerBuildVerifyingService(
            final List<ICompletenessChecker> completenessCheckers,
            final List<ICompatibilityChecker> compatibilityCheckers,
            final List<IOptimalityChecker> optimalityCheckers,
            final IComputerBuildRepository repository,
            final Translator translator
    ) {
        this.completenessCheckers = completenessCheckers;
        this.compatibilityCheckers = compatibilityCheckers;
        this.optimalityCheckers = optimalityCheckers;
        this.repository = repository;
        this.translator = translator;
    }

    /**
     * Проверяет сборку ПК на законченность,
     * совместимость комплектующих и оптимальность.
     *
     * @param id ID сборки ПК
     * @return результат проверки
     */
    public ComputerVerifyResult verify(final UUID id) {
        final ComputerBuild computer = findById(id);

        final Set<String> completenessViolations = completenessCheckers.stream()
                .map(checker -> checker.check(computer))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        final Set<String> compatibilityViolations = compatibilityCheckers.stream()
                .map(checker -> checker.check(computer))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        final Set<String> optimalityWarnings = optimalityCheckers.stream()
                .map(checker -> checker.check(computer))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        return ComputerVerifyResult.builder()
                .completenessViolations(completenessViolations)
                .compatibilityViolations(compatibilityViolations)
                .optimalityWarnings(optimalityWarnings)
                .build();
    }

    /**
     * Возвращает сборку ПК с указанным ID, если она существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return сборку ПК с указанным ID, если она существует
     */
    private ComputerBuild findById(final UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_COMPUTER_BUILD_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
