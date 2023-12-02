package ru.bukhtaev.service.checker.completeness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.validation.Translator;

import java.util.Optional;

import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_NO_STORAGE_DEVICES;

/**
 * Сервис проверки сборки ПК
 * на наличие устройств хранения данных.
 */
@Order(400)
@Component
public class StorageDeviceChecker extends ICompletenessChecker {

    /**
     * Конструктор.
     *
     * @param translator сервис предоставления сообщений
     */
    @Autowired
    protected StorageDeviceChecker(final Translator translator) {
        super(translator);
    }

    @Override
    public Optional<String> check(final ComputerBuild computer) {

        if ((computer.getHdds().size() + computer.getSsds().size()) < 1) {
            return Optional.of(
                    translator.getMessage(MESSAGE_CODE_NO_STORAGE_DEVICES)
            );
        }

        return Optional.empty();
    }
}
