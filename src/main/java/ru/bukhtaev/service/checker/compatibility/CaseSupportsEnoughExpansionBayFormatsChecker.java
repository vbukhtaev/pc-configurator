package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.ComputerCase;
import ru.bukhtaev.model.cross.ComputerBuildToHdd;
import ru.bukhtaev.model.cross.ComputerBuildToSsd;
import ru.bukhtaev.model.cross.ComputerCaseToExpansionBayFormat;
import ru.bukhtaev.model.dictionary.ExpansionBayFormat;
import ru.bukhtaev.validation.Translator;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_NOT_ENOUGH_EXPANSION_BAY_FORMATS;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_TEMPLATE_TWO_PART_NAME;

/**
 * Сервис проверки корпуса.
 * Проверяет, что корпус поддерживает достаточное количество форматов отсеков расширения
 * для всех включенных в сборку жестких дисков и SSD-накопителей.
 */
@Order(1250)
@Component
public class CaseSupportsEnoughExpansionBayFormatsChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected CaseSupportsEnoughExpansionBayFormatsChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final ComputerCase computerCase = computer.getComputerCase();
        final var computerToHdds = computer.getHdds();
        final var computerToSsds = computer.getSsds();

        if (computerCase == null
                || (computerToHdds == null
                || computerToHdds.isEmpty()
                && computerToSsds == null
                || computerToSsds.isEmpty()
        )) {
            return Optional.empty();
        }

        final var availableFormats = computerCase.getExpansionBayFormats()
                .stream()
                .collect(Collectors.toMap(
                        ComputerCaseToExpansionBayFormat::getExpansionBayFormat,
                        ComputerCaseToExpansionBayFormat::getCount,
                        Integer::sum
                ));

        final var hddFormats = computerToHdds
                .stream()
                .collect(Collectors.toMap(
                        mtc -> mtc.getHdd().getExpansionBayFormat(),
                        ComputerBuildToHdd::getCount,
                        Integer::sum
                ));

        final var ssdFormats = computerToSsds
                .stream()
                .filter(cts -> cts.getSsd().getExpansionBayFormat() != null)
                .collect(Collectors.toMap(
                        mtc -> mtc.getSsd().getExpansionBayFormat(),
                        ComputerBuildToSsd::getCount,
                        Integer::sum
                ));

        final Map<ExpansionBayFormat, Integer> neededFormats = new HashMap<>(hddFormats);

        for (final var entry : ssdFormats.entrySet()) {
            final ExpansionBayFormat format = entry.getKey();
            final Integer count = entry.getValue();

            neededFormats.merge(
                    format,
                    count,
                    Integer::sum
            );
        }

        bookMatchingFormats(neededFormats, availableFormats);

        if (!neededFormats.isEmpty()) {
            final String caseName = MessageFormat.format(
                    MESSAGE_TEMPLATE_TWO_PART_NAME,
                    computerCase.getVendor().getName(),
                    computerCase.getName()
            );

            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_NOT_ENOUGH_EXPANSION_BAY_FORMATS,
                    caseName
            ));
        }

        return Optional.empty();
    }

    /**
     * Бронирует доступные форматы отсеков расширения.
     *
     * @param neededFormats    необходимые форматы отсеков расширения
     * @param availableFormats доступные форматы отсеков расширения
     */
    private void bookMatchingFormats(
            final Map<ExpansionBayFormat, Integer> neededFormats,
            final Map<ExpansionBayFormat, Integer> availableFormats
    ) {
        Set.copyOf(neededFormats.entrySet()).forEach(entry -> {
            final ExpansionBayFormat neededFormat = entry.getKey();
            final int neededCount = entry.getValue();

            final int availableCount = availableFormats.getOrDefault(neededFormat, 0);

            if (neededCount == availableCount) {
                neededFormats.remove(neededFormat);
                availableFormats.remove(neededFormat);

            } else if (neededCount > availableCount) {
                availableFormats.remove(neededFormat);
                neededFormats.put(
                        neededFormat,
                        neededCount - availableCount
                );

            } else {
                availableFormats.put(
                        neededFormat,
                        availableCount - neededCount
                );
                neededFormats.remove(neededFormat);
            }
        });
    }
}