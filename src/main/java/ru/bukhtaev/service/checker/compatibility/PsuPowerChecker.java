package ru.bukhtaev.service.checker.compatibility;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.Cpu;
import ru.bukhtaev.model.GraphicsCard;
import ru.bukhtaev.model.Psu;
import ru.bukhtaev.i18n.Translator;

import java.util.Optional;

import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_PSU_POWER_EXCEEDING;

/**
 * Сервис проверки блока питания.
 * Проверяет, что мощности блока питания
 * достаточно для питания компьютера
 */
@Order(900)
@Component
public class PsuPowerChecker extends ICompatibilityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected PsuPowerChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final Psu psu = computer.getPsu();
        final Cpu cpu = computer.getCpu();
        final GraphicsCard graphicsCard = computer.getGraphicsCard();

        if (psu == null
                || cpu == null
                || graphicsCard == null) {
            return Optional.empty();
        }

        final int psuPower12V = psu.getPower12V();
        final int cpuMaxTdp = cpu.getMaxTdp();
        final int cardPowerConsumption = graphicsCard.getGpu().getPowerConsumption();

        final int neededPower = Math.toIntExact(Math.round(
                (cpuMaxTdp + cardPowerConsumption) * 1.5
        ));

        if (neededPower > psuPower12V) {
            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_PSU_POWER_EXCEEDING,
                    Integer.toString(psuPower12V),
                    Integer.toString(neededPower)
            ));
        }

        return Optional.empty();
    }
}
