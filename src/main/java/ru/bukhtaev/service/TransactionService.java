package ru.bukhtaev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Утилитный сервис выполнения кода в транзакции.
 */
@Service
public class TransactionService {

    /**
     * Менеджер транзакций.
     */
    private final PlatformTransactionManager transactionManager;

    /**
     * Конструктор.
     *
     * @param transactionManager менеджер транзакций
     */
    @Autowired
    public TransactionService(final PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * Выполняет код в транзакции.
     *
     * @param readOnly является ли транзакция read only
     * @param runnable код для выполнения
     */
    public void doInTransaction(final boolean readOnly, final Runnable runnable) {
        final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        if (readOnly) {
            transactionTemplate.setReadOnly(true);
        }
        transactionTemplate.executeWithoutResult(status -> runnable.run());
    }

}
