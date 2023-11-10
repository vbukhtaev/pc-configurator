package ru.bukhtaev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.PsuCertificate;
import ru.bukhtaev.repository.IPsuCertificateRepository;
import ru.bukhtaev.validation.Translator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_PSU_CERTIFICATE_NOT_FOUND;
import static ru.bukhtaev.validation.MessageUtils.MESSAGE_CODE_PSU_CERTIFICATE_UNIQUE;

/**
 * Реализация сервиса CRUD операций над сертификатами блоков питания.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class PsuCertificateCrudService implements ICrudService<PsuCertificate, UUID> {

    /**
     * Репозиторий.
     */
    private final IPsuCertificateRepository repository;

    /**
     * Сервис предоставления сообщений.
     */
    private final Translator translator;

    /**
     * Конструктор.
     *
     * @param repository репозиторий
     * @param translator сервис предоставления сообщений
     */
    @Autowired
    public PsuCertificateCrudService(
            final IPsuCertificateRepository repository,
            final Translator translator
    ) {
        this.repository = repository;
        this.translator = translator;
    }

    @Override
    public PsuCertificate getById(final UUID id) {
        return findById(id);
    }

    @Override
    public List<PsuCertificate> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public PsuCertificate create(final PsuCertificate newCertificate) {
        repository.findByName(newCertificate.getName())
                .ifPresent(certificate -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_PSU_CERTIFICATE_UNIQUE,
                                    certificate.getName()
                            ),
                            FIELD_NAME
                    );
                });

        return repository.save(newCertificate);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public PsuCertificate update(final UUID id, final PsuCertificate changedCertificate) {
        repository.findByNameAndIdNot(
                changedCertificate.getName(),
                id
        ).ifPresent(certificate -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_PSU_CERTIFICATE_UNIQUE,
                            certificate.getName()
                    ),
                    FIELD_NAME
            );
        });

        final PsuCertificate toBeUpdated = findById(id);
        Optional.ofNullable(changedCertificate.getName())
                .ifPresent(toBeUpdated::setName);

        return repository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public PsuCertificate replace(final UUID id, final PsuCertificate newCertificate) {
        repository.findByNameAndIdNot(
                newCertificate.getName(),
                id
        ).ifPresent(certificate -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_PSU_CERTIFICATE_UNIQUE,
                            certificate.getName()
                    ),
                    FIELD_NAME
            );
        });

        final PsuCertificate existent = findById(id);
        existent.setName(newCertificate.getName());

        return repository.save(existent);
    }

    /**
     * Возвращает сертификат блока питания с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return сертификат блока питания с указанным ID, если он существует
     */
    private PsuCertificate findById(final UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_PSU_CERTIFICATE_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
