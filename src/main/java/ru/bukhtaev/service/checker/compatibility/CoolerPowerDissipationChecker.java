package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Cooler;
import ru.bukhtaev.model.Cpu;
import ru.bukhtaev.validation.Translator;

import java.util.Optional;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_CPU_OVERHEAT;

/**
 * Сервис проверки процессорного кулера.
 * Проверяет, что процессорный кулер способен охладить процессор.
 */
@Order(500)
@Component
public class CoolerPowerDissipationChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected CoolerPowerDissipationChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final Cpu cpu = computer.getCpu();
        final Cooler cooler = computer.getCooler();

        if (cpu == null || cooler == null) {
            return Optional.empty();
        }

        final int cpuMaxTdp = cpu.getMaxTdp();
        final int coolerPowerDissipation = cooler.getPowerDissipation();

        if (cpuMaxTdp > coolerPowerDissipation) {
            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_CPU_OVERHEAT,
                    coolerPowerDissipation,
                    cpuMaxTdp
            ));
        }

        return Optional.empty();
    }
}
