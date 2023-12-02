package ru.bukhtaev.service.crud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.InvalidParamException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.Design;
import ru.bukhtaev.model.dictionary.Vendor;
import ru.bukhtaev.repository.IDesignRepository;
import ru.bukhtaev.repository.dictionary.IVendorRepository;
import ru.bukhtaev.validation.Translator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.Design.FIELD_VENDOR;
import static ru.bukhtaev.model.NameableEntity.FIELD_NAME;
import static ru.bukhtaev.validation.MessageUtils.*;

/**
 * Реализация сервиса CRUD операций над вариантами исполнения.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class DesignCrudService implements IPagingCrudService<Design, UUID> {

    /**
     * Репозиторий вариантов исполнения.
     */
    private final IDesignRepository designRepository;

    /**
     * Репозиторий вендоров.
     */
    private final IVendorRepository vendorRepository;

    /**
     * Сервис предоставления сообщений.
     */
    private final Translator translator;

    /**
     * Конструктор.
     *
     * @param designRepository репозиторий вариантов исполнения
     * @param vendorRepository репозиторий вендоров
     * @param translator       сервис предоставления сообщений
     */
    @Autowired
    public DesignCrudService(
            final IDesignRepository designRepository,
            final IVendorRepository vendorRepository,
            final Translator translator
    ) {
        this.designRepository = designRepository;
        this.vendorRepository = vendorRepository;
        this.translator = translator;
    }

    @Override
    public Design getById(final UUID id) {
        return findDesignById(id);
    }

    @Override
    public List<Design> getAll() {
        return designRepository.findAll();
    }

    @Override
    public Slice<Design> getAll(final Pageable pageable) {
        return designRepository.findAllBy(pageable);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Design create(final Design newDesign) {
        final Vendor vendor = newDesign.getVendor();
        if (vendor == null || vendor.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_VENDOR
            );
        }

        final Vendor foundVendor = findVendorById(vendor.getId());
        newDesign.setVendor(foundVendor);

        designRepository.findByName(newDesign.getName())
                .ifPresent(design -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_DESIGN_UNIQUE,
                                    design.getName()
                            ),
                            FIELD_NAME
                    );
                });

        return designRepository.save(newDesign);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        designRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Design update(final UUID id, final Design changedDesign) {
        final Design toBeUpdated = findDesignById(id);

        designRepository.findByNameAndIdNot(
                changedDesign.getName(),
                id
        ).ifPresent(design -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_DESIGN_UNIQUE,
                            design.getName()
                    ),
                    FIELD_NAME
            );
        });

        Optional.ofNullable(changedDesign.getName())
                .ifPresent(toBeUpdated::setName);

        final Vendor vendor = changedDesign.getVendor();
        if (vendor != null && vendor.getId() != null) {
            final Vendor found = findVendorById(vendor.getId());
            toBeUpdated.setVendor(found);
        }

        return designRepository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Design replace(final UUID id, final Design newDesign) {
        final Design existent = findDesignById(id);

        designRepository.findByNameAndIdNot(
                newDesign.getName(),
                id
        ).ifPresent(design -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_DESIGN_UNIQUE,
                            design.getName()
                    ),
                    FIELD_NAME
            );
        });

        existent.setName(newDesign.getName());

        final Vendor vendor = newDesign.getVendor();
        if (vendor == null || vendor.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_VENDOR
            );
        }

        final Vendor foundVendor = findVendorById(vendor.getId());
        existent.setVendor(foundVendor);

        return designRepository.save(existent);
    }

    /**
     * Возвращает вариант исполнения с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return вариант исполнения с указанным ID, если он существует
     */
    private Design findDesignById(final UUID id) {
        return designRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_DESIGN_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает вендора с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return вендора с указанным ID, если он существует
     */
    private Vendor findVendorById(final UUID id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_VENDOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
