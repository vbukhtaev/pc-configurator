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
import ru.bukhtaev.model.RamModule;
import ru.bukhtaev.model.dictionary.RamType;
import ru.bukhtaev.repository.IDesignRepository;
import ru.bukhtaev.repository.IRamModuleRepository;
import ru.bukhtaev.repository.dictionary.IRamTypeRepository;
import ru.bukhtaev.i18n.Translator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.RamModule.*;
import static ru.bukhtaev.i18n.MessageUtils.*;

/**
 * Реализация сервиса CRUD операций над модулями оперативной памяти.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class RamModuleCrudService implements IPagingCrudService<RamModule, UUID> {

    /**
     * Репозиторий модулей оперативной памяти.
     */
    private final IRamModuleRepository moduleRepository;

    /**
     * Репозиторий вариантов исполнения.
     */
    private final IDesignRepository designRepository;

    /**
     * Репозиторий типов оперативной памяти.
     */
    private final IRamTypeRepository typeRepository;

    /**
     * Сервис предоставления сообщений.
     */
    private final Translator translator;

    /**
     * Конструктор.
     *
     * @param moduleRepository репозиторий модулей оперативной памяти
     * @param designRepository репозиторий вариантов исполнения
     * @param typeRepository   репозиторий типов оперативной памяти
     * @param translator       сервис предоставления сообщений
     */
    @Autowired
    public RamModuleCrudService(
            final IRamModuleRepository moduleRepository,
            final IDesignRepository designRepository,
            final IRamTypeRepository typeRepository,
            final Translator translator
    ) {
        this.moduleRepository = moduleRepository;
        this.designRepository = designRepository;
        this.typeRepository = typeRepository;
        this.translator = translator;
    }

    @Override
    public RamModule getById(final UUID id) {
        return findRamModuleById(id);
    }

    @Override
    public List<RamModule> getAll() {
        return moduleRepository.findAll();
    }

    @Override
    public Slice<RamModule> getAll(final Pageable pageable) {
        return moduleRepository.findAllBy(pageable);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public RamModule create(final RamModule newModule) {
        final Design design = newModule.getDesign();
        if (design == null || design.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_DESIGN
            );
        }

        final RamType type = newModule.getType();
        if (type == null || type.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_TYPE
            );
        }

        final Design foundDesign = findDesignById(design.getId());
        newModule.setDesign(foundDesign);

        final RamType foundType = findTypeById(type.getId());
        newModule.setType(foundType);

        moduleRepository.findTheSame(
                newModule.getClock(),
                newModule.getCapacity(),
                newModule.getType(),
                newModule.getDesign()
        ).ifPresent(module -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_RAM_MODULE_UNIQUE,
                            module.getClock(),
                            module.getCapacity(),
                            module.getType().getName(),
                            module.getDesign().getName()
                    ),
                    FIELD_CLOCK,
                    FIELD_CAPACITY,
                    FIELD_TYPE,
                    FIELD_DESIGN
            );
        });

        return moduleRepository.save(newModule);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        moduleRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public RamModule update(final UUID id, final RamModule changedModule) {
        final RamModule toBeUpdated = findRamModuleById(id);

        final Design foundDesign = Optional.ofNullable(changedModule.getDesign())
                .map(Design::getId)
                .map(this::findDesignById)
                .orElse(toBeUpdated.getDesign());

        final RamType foundType = Optional.ofNullable(changedModule.getType())
                .map(RamType::getId)
                .map(this::findTypeById)
                .orElse(toBeUpdated.getType());

        final Integer clock = Objects.requireNonNullElse(
                changedModule.getClock(),
                toBeUpdated.getClock()
        );

        final Integer capacity = Objects.requireNonNullElse(
                changedModule.getCapacity(),
                toBeUpdated.getCapacity()
        );

        moduleRepository.findTheSameWithAnotherId(
                clock,
                capacity,
                foundType,
                foundDesign,
                id
        ).ifPresent(module -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_RAM_MODULE_UNIQUE,
                            module.getClock(),
                            module.getCapacity(),
                            module.getType().getName(),
                            module.getDesign().getName()
                    ),
                    FIELD_CLOCK,
                    FIELD_CAPACITY,
                    FIELD_TYPE,
                    FIELD_DESIGN
            );
        });

        toBeUpdated.setClock(clock);
        toBeUpdated.setCapacity(capacity);
        toBeUpdated.setDesign(foundDesign);
        toBeUpdated.setType(foundType);

        return moduleRepository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public RamModule replace(final UUID id, final RamModule newModule) {
        final Design design = newModule.getDesign();
        if (design == null || design.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_DESIGN
            );
        }

        final RamType type = newModule.getType();
        if (type == null || type.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_TYPE
            );
        }

        final RamModule existent = findRamModuleById(id);

        final Design foundDesign = findDesignById(design.getId());
        final RamType foundType = findTypeById(type.getId());

        moduleRepository.findTheSameWithAnotherId(
                newModule.getClock(),
                newModule.getCapacity(),
                foundType,
                foundDesign,
                id
        ).ifPresent(module -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_RAM_MODULE_UNIQUE,
                            module.getClock(),
                            module.getCapacity(),
                            module.getType().getName(),
                            module.getDesign().getName()
                    ),
                    FIELD_CLOCK,
                    FIELD_CAPACITY,
                    FIELD_TYPE,
                    FIELD_DESIGN
            );
        });

        existent.setClock(newModule.getClock());
        existent.setCapacity(newModule.getCapacity());
        existent.setDesign(foundDesign);
        existent.setType(foundType);

        return moduleRepository.save(existent);
    }

    /**
     * Возвращает модуль оперативной памяти с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return модуль оперативной памяти с указанным ID, если он существует
     */
    private RamModule findRamModuleById(final UUID id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_RAM_MODULE_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
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
     * Возвращает тип оперативной памяти с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return тип оперативной памяти с указанным ID, если он существует
     */
    private RamType findTypeById(final UUID id) {
        return typeRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_RAM_TYPE_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }
}
