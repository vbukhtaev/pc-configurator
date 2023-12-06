package ru.bukhtaev.service.crud.dictionary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.dictionary.Vendor;
import ru.bukhtaev.repository.dictionary.IVendorRepository;
import ru.bukhtaev.service.crud.IPagingCrudService;
import ru.bukhtaev.i18n.Translator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_VENDOR_NOT_FOUND;
import static ru.bukhtaev.i18n.MessageUtils.MESSAGE_CODE_VENDOR_UNIQUE;

/**
 * Реализация сервиса CRUD операций над вендорами.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class VendorCrudService implements IPagingCrudService<Vendor, UUID> {

    /**
     * Репозиторий.
     */
    private final IVendorRepository repository;

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
    public VendorCrudService(
            final IVendorRepository repository,
            final Translator translator
    ) {
        this.repository = repository;
        this.translator = translator;
    }

    @Override
    public Vendor getById(final UUID id) {
        return findById(id);
    }

    @Override
    public List<Vendor> getAll() {
        return repository.findAll();
    }

    @Override
    public Slice<Vendor> getAll(final Pageable pageable) {
        return repository.findAllBy(pageable);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Vendor create(final Vendor newVendor) {
        repository.findByName(newVendor.getName())
                .ifPresent(vendor -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_VENDOR_UNIQUE,
                                    vendor.getName()
                            ),
                            FIELD_NAME
                    );
                });

        return repository.save(newVendor);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Vendor update(final UUID id, final Vendor changedVendor) {
        repository.findByNameAndIdNot(
                changedVendor.getName(),
                id
        ).ifPresent(vendor -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_VENDOR_UNIQUE,
                            vendor.getName()
                    ),
                    FIELD_NAME
            );
        });

        final Vendor toBeUpdated = findById(id);
        Optional.ofNullable(changedVendor.getName())
                .ifPresent(toBeUpdated::setName);

        return repository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Vendor replace(final UUID id, final Vendor newVendor) {
        repository.findByNameAndIdNot(
                newVendor.getName(),
                id
        ).ifPresent(vendor -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_VENDOR_UNIQUE,
                            vendor.getName()
                    ),
                    FIELD_NAME
            );
        });

        final Vendor existent = findById(id);
        existent.setName(newVendor.getName());

        return repository.save(existent);
    }

    /**
     * Возвращает вендора с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return вендора с указанным ID, если он существует
     */
    private Vendor findById(final UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_VENDOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
