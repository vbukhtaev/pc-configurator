package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.ComputerCase;
import ru.bukhtaev.model.cross.ComputerBuildToFan;
import ru.bukhtaev.model.cross.ComputerCaseToFanSize;
import ru.bukhtaev.model.dictionary.FanSize;
import ru.bukhtaev.validation.Translator;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_NOT_ENOUGH_FAN_SIZES;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_TEMPLATE_TWO_PART_NAME;

/**
 * Сервис проверки корпуса.
 * Проверяет, что корпус поддерживает достаточное количество размеров вентиляторов
 * для всех включенных в сборку вентиляторов.
 */
@Order(1200)
@Component
public class CaseSupportsEnoughFanSizesChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected CaseSupportsEnoughFanSizesChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final ComputerCase computerCase = computer.getComputerCase();
        final var computerToFans = computer.getFans();

        if (computerCase == null
                || computerToFans == null
                || computerToFans.isEmpty()
        ) {
            return Optional.empty();
        }

        final var availableSizes = computerCase.getFanSizes()
                .stream()
                .collect(Collectors.toMap(
                        ComputerCaseToFanSize::getFanSize,
                        ComputerCaseToFanSize::getCount,
                        Integer::sum
                ));

        final var neededSizes = computerToFans
                .stream()
                .collect(Collectors.toMap(
                        ctf -> ctf.getFan().getSize(),
                        ComputerBuildToFan::getCount,
                        Integer::sum
                ));

        bookMatchingSizes(neededSizes, availableSizes);

        if (!neededSizes.isEmpty()) {
            final String caseName = MessageFormat.format(
                    MESSAGE_TEMPLATE_TWO_PART_NAME,
                    computerCase.getVendor().getName(),
                    computerCase.getName()
            );

            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_NOT_ENOUGH_FAN_SIZES,
                    caseName
            ));
        }

        return Optional.empty();
    }

    /**
     * Бронирует доступные размеры вентиляторов.
     *
     * @param neededSizes    необходимые размеры вентиляторов
     * @param availableSizes доступные размеры вентиляторов
     */
    private void bookMatchingSizes(
            final Map<FanSize, Integer> neededSizes,
            final Map<FanSize, Integer> availableSizes
    ) {
        Set.copyOf(neededSizes.entrySet()).forEach(entry -> {
            final FanSize neededSize = entry.getKey();
            final int neededCount = entry.getValue();

            final int availableCount = availableSizes.getOrDefault(neededSize, 0);

            if (neededCount == availableCount) {
                neededSizes.remove(neededSize);
                availableSizes.remove(neededSize);

            } else if (neededCount > availableCount) {
                availableSizes.remove(neededSize);
                neededSizes.put(
                        neededSize,
                        neededCount - availableCount
                );

            } else {
                availableSizes.put(
                        neededSize,
                        availableCount - neededCount
                );
                neededSizes.remove(neededSize);
            }
        });
    }
}
