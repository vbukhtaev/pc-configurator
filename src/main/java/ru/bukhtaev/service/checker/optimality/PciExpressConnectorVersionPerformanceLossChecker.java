package ru.bukhtaev.service.checker.optimality;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.model.GraphicsCard;
import ru.bukhtaev.model.Motherboard;
import ru.bukhtaev.i18n.Translator;

import java.util.Optional;

import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_PCI_EXPRESS_CONNECTOR_VERSION;

/**
 * Сервис проверки версии коннектора PCI-Express.
 * Проверяет, что версия коннектора PCI-Express на видеокарте
 * ниже или равна версии этого коннектора на материнской плате.
 */
@Order(50)
@Component
public class PciExpressConnectorVersionPerformanceLossChecker extends IOptimalityChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    protected PciExpressConnectorVersionPerformanceLossChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        final GraphicsCard graphicsCard = computer.getGraphicsCard();
        final Motherboard motherboard = computer.getMotherboard();

        if (graphicsCard == null || motherboard == null) {
            return Optional.empty();
        }

        final var graphicsCardPciVersion = graphicsCard.getPciExpressConnectorVersion();
        final var motherboardPciVersion = motherboard.getPciExpressConnectorVersion();

        if (!motherboardPciVersion.equals(graphicsCardPciVersion)
                && (!motherboardPciVersion.getLowerVersions().contains(graphicsCardPciVersion))
        ) {
            return Optional.of(translator.getMessage(
                    MESSAGE_CODE_PCI_EXPRESS_CONNECTOR_VERSION,
                    motherboardPciVersion.getName(),
                    graphicsCardPciVersion.getName()
            ));
        }

        return Optional.empty();
    }
}
