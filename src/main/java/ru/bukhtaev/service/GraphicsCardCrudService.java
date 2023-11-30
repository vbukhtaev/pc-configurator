package ru.bukhtaev.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bukhtaev.exception.DataNotFoundException;
import ru.bukhtaev.exception.InvalidParamException;
import ru.bukhtaev.exception.UniqueNameException;
import ru.bukhtaev.model.Design;
import ru.bukhtaev.model.Gpu;
import ru.bukhtaev.model.GraphicsCard;
import ru.bukhtaev.model.cross.GraphicsCardToPowerConnector;
import ru.bukhtaev.model.dictionary.GraphicsCardPowerConnector;
import ru.bukhtaev.model.dictionary.PciExpressConnectorVersion;
import ru.bukhtaev.repository.*;
import ru.bukhtaev.repository.dictionary.IGraphicsCardPowerConnectorRepository;
import ru.bukhtaev.repository.dictionary.IPciExpressConnectorVersionRepository;
import ru.bukhtaev.validation.Translator;

import java.util.*;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.GraphicsCard.*;
import static ru.bukhtaev.validation.MessageUtils.*;

/**
 * Реализация сервиса CRUD операций над видеокартами.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class GraphicsCardCrudService implements IPagingCrudService<GraphicsCard, UUID> {

    /**
     * Репозиторий видеокарт.
     */
    private final IGraphicsCardRepository cardRepository;

    /**
     * Репозиторий графических процессоров.
     */
    private final IGpuRepository gpuRepository;

    /**
     * Репозиторий вариантов исполнения.
     */
    private final IDesignRepository designRepository;

    /**
     * Репозиторий версий коннектора PCI-Express.
     */
    private final IPciExpressConnectorVersionRepository pciExpressConnectorVersionRepository;

    /**
     * Репозиторий коннекторов питания видеокарт.
     */
    private final IGraphicsCardPowerConnectorRepository powerConnectorRepository;

    /**
     * Сервис предоставления сообщений.
     */
    private final Translator translator;

    /**
     * Менеджер сущностей.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Конструктор.
     *
     * @param cardRepository                       репозиторий видеокарт
     * @param gpuRepository                        репозиторий графических процессоров
     * @param designRepository                     репозиторий вариантов исполнения
     * @param pciExpressConnectorVersionRepository репозиторий версий коннектора PCI-Express
     * @param powerConnectorRepository             репозиторий коннекторов питания видеокарт
     * @param translator                           сервис предоставления сообщений
     */
    @Autowired
    public GraphicsCardCrudService(
            final IGraphicsCardRepository cardRepository,
            final IGpuRepository gpuRepository,
            final IDesignRepository designRepository,
            final IPciExpressConnectorVersionRepository pciExpressConnectorVersionRepository,
            final IGraphicsCardPowerConnectorRepository powerConnectorRepository,
            final Translator translator
    ) {
        this.cardRepository = cardRepository;
        this.gpuRepository = gpuRepository;
        this.designRepository = designRepository;
        this.pciExpressConnectorVersionRepository = pciExpressConnectorVersionRepository;
        this.powerConnectorRepository = powerConnectorRepository;
        this.translator = translator;
    }

    @Override
    public GraphicsCard getById(final UUID id) {
        return findGraphicsCardById(id);
    }

    @Override
    public List<GraphicsCard> getAll() {
        return cardRepository.findAll();
    }

    @Override
    public Slice<GraphicsCard> getAll(final Pageable pageable) {
        return cardRepository.findAllBy(pageable);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public GraphicsCard create(final GraphicsCard newCard) {
        final Gpu gpu = newCard.getGpu();
        if (gpu == null || gpu.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_GPU
            );
        }

        final Design design = newCard.getDesign();
        if (design == null || design.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_DESIGN
            );
        }

        final PciExpressConnectorVersion version = newCard.getPciExpressConnectorVersion();
        if (version == null || version.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_PCI_EXPRESS_CONNECTOR_VERSION
            );
        }

        final Set<GraphicsCardToPowerConnector> powerConnectors = newCard.getPowerConnectors();
        validatePowerConnectors(powerConnectors);

        final Gpu foundGpu = findGpuById(gpu.getId());
        newCard.setGpu(foundGpu);

        final Design foundDesign = findDesignById(design.getId());
        newCard.setDesign(foundDesign);

        final PciExpressConnectorVersion foundVersion = findPciExpressConnectorVersionById(version.getId());
        newCard.setPciExpressConnectorVersion(foundVersion);

        powerConnectors.forEach(cardToConnector -> {
            final UUID connectorId = cardToConnector.getPowerConnector().getId();
            final var connector = findPowerConnectorById(connectorId);
            cardToConnector.setPowerConnector(connector);
            cardToConnector.setGraphicsCard(newCard);
        });
        newCard.setPowerConnectors(powerConnectors);

        cardRepository.findTheSame(
                foundGpu,
                foundDesign
        ).ifPresent(card -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_GRAPHICS_CARD_UNIQUE,
                            card.getGpu().getName(),
                            card.getDesign().getName()
                    ),
                    FIELD_GPU,
                    FIELD_DESIGN
            );
        });

        return cardRepository.save(newCard);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        cardRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public GraphicsCard update(final UUID id, final GraphicsCard changedCard) {
        final GraphicsCard toBeUpdated = findGraphicsCardById(id);

        final Gpu foundGpu = Optional.ofNullable(changedCard.getGpu())
                .map(Gpu::getId)
                .map(this::findGpuById)
                .orElse(toBeUpdated.getGpu());

        final Design foundDesign = Optional.ofNullable(changedCard.getDesign())
                .map(Design::getId)
                .map(this::findDesignById)
                .orElse(toBeUpdated.getDesign());

        final var foundVersion = Optional.ofNullable(changedCard.getPciExpressConnectorVersion())
                .map(PciExpressConnectorVersion::getId)
                .map(this::findPciExpressConnectorVersionById)
                .orElse(toBeUpdated.getPciExpressConnectorVersion());

        final Integer length = Objects.requireNonNullElse(
                changedCard.getLength(),
                toBeUpdated.getLength()
        );

        final Set<GraphicsCardToPowerConnector> powerConnectors = changedCard.getPowerConnectors();
        if (powerConnectors != null) {
            validatePowerConnectors(powerConnectors);

            toBeUpdated.getPowerConnectors().forEach(pc -> entityManager.remove(pc));
            toBeUpdated.getPowerConnectors().clear();
            entityManager.flush();

            powerConnectors.forEach(cardToConnector -> {
                final UUID connectorId = cardToConnector.getPowerConnector().getId();
                final var connector = findPowerConnectorById(connectorId);
                toBeUpdated.addPowerConnector(connector, cardToConnector.getCount());
            });
        }

        cardRepository.findTheSameWithAnotherId(
                foundGpu,
                foundDesign,
                id
        ).ifPresent(card -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_GRAPHICS_CARD_UNIQUE,
                            card.getGpu().getName(),
                            card.getDesign().getName()
                    ),
                    FIELD_GPU,
                    FIELD_DESIGN
            );
        });

        toBeUpdated.setLength(length);
        toBeUpdated.setGpu(foundGpu);
        toBeUpdated.setDesign(foundDesign);
        toBeUpdated.setPciExpressConnectorVersion(foundVersion);

        return cardRepository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public GraphicsCard replace(final UUID id, final GraphicsCard newCard) {
        final Gpu gpu = newCard.getGpu();
        if (gpu == null || gpu.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_GPU
            );
        }

        final Design design = newCard.getDesign();
        if (design == null || design.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_DESIGN
            );
        }

        final PciExpressConnectorVersion version = newCard.getPciExpressConnectorVersion();
        if (version == null || version.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_PCI_EXPRESS_CONNECTOR_VERSION
            );
        }

        final Set<GraphicsCardToPowerConnector> powerConnectors = newCard.getPowerConnectors();
        validatePowerConnectors(powerConnectors);

        final GraphicsCard existent = findGraphicsCardById(id);

        final Gpu foundGpu = findGpuById(gpu.getId());
        final Design foundDesign = findDesignById(design.getId());
        final var foundVersion = findPciExpressConnectorVersionById(version.getId());

        existent.getPowerConnectors().forEach(pc -> entityManager.remove(pc));
        existent.getPowerConnectors().clear();
        entityManager.flush();

        powerConnectors.forEach(cardToConnector -> {
            final UUID connectorId = cardToConnector.getPowerConnector().getId();
            final var connector = findPowerConnectorById(connectorId);
            existent.addPowerConnector(connector, cardToConnector.getCount());
        });

        cardRepository.findTheSameWithAnotherId(
                foundGpu,
                foundDesign,
                id
        ).ifPresent(card -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_GRAPHICS_CARD_UNIQUE,
                            card.getGpu().getName(),
                            card.getDesign().getName()
                    ),
                    FIELD_GPU,
                    FIELD_DESIGN
            );
        });

        existent.setLength(newCard.getLength());
        existent.setGpu(foundGpu);
        existent.setDesign(foundDesign);
        existent.setPciExpressConnectorVersion(foundVersion);

        return cardRepository.save(existent);
    }

    /**
     * Возвращает видеокарту с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return видеокарту с указанным ID, если он существует
     */
    private GraphicsCard findGraphicsCardById(final UUID id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_GRAPHICS_CARD_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает графический процессор с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return графический процессор с указанным ID, если он существует
     */
    private Gpu findGpuById(final UUID id) {
        return gpuRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_GPU_NOT_FOUND,
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
     * Возвращает версию коннектора PCI_Express с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return версию коннектора PCI_Express с указанным ID, если он существует
     */
    private PciExpressConnectorVersion findPciExpressConnectorVersionById(final UUID id) {
        return pciExpressConnectorVersionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_PCI_EXPRESS_CONNECTOR_VERSION_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает коннектор питания видеокарты с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return коннектор питания видеокарты с указанным ID, если он существует
     */
    private GraphicsCardPowerConnector findPowerConnectorById(final UUID id) {
        return powerConnectorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_GRAPHICS_CARD_POWER_CONNECTOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Проверяет переданное множество имеющихся
     * у видеокарты коннекторов питания на валидность.
     *
     * @param powerConnectors множество имеющихся у видеокарты коннекторов питания
     */
    private void validatePowerConnectors(final Set<GraphicsCardToPowerConnector> powerConnectors) {
        if (powerConnectors == null
                || powerConnectors.isEmpty()
                || powerConnectors.stream().anyMatch(Objects::isNull)
                || powerConnectors.stream().anyMatch(cardToConnector ->
                cardToConnector.getPowerConnector() == null)
                || powerConnectors.stream().anyMatch(cardToConnector ->
                cardToConnector.getPowerConnector().getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_POWER_CONNECTORS
            );
        }
    }
}
