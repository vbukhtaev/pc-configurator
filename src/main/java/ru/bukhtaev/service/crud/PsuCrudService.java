package ru.bukhtaev.service.crud;

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
import ru.bukhtaev.model.Psu;
import ru.bukhtaev.model.cross.PsuToCpuPowerConnector;
import ru.bukhtaev.model.cross.PsuToGraphicsCardPowerConnector;
import ru.bukhtaev.model.cross.PsuToStoragePowerConnector;
import ru.bukhtaev.model.dictionary.*;
import ru.bukhtaev.repository.IPsuRepository;
import ru.bukhtaev.repository.dictionary.*;
import ru.bukhtaev.validation.Translator;

import java.util.*;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static ru.bukhtaev.model.BaseEntity.FIELD_ID;
import static ru.bukhtaev.model.Psu.*;
import static ru.bukhtaev.validation.MessageUtils.*;

/**
 * Реализация сервиса CRUD операций над блоками питания.
 */
@Service
@Transactional(
        isolation = READ_COMMITTED,
        readOnly = true
)
public class PsuCrudService implements IPagingCrudService<Psu, UUID> {

    /**
     * Репозиторий блоков питания.
     */
    private final IPsuRepository psuRepository;

    /**
     * Репозиторий вендоров.
     */
    private final IVendorRepository vendorRepository;

    /**
     * Репозиторий форм-факторов блоков питания.
     */
    private final IPsuFormFactorRepository formFactorRepository;

    /**
     * Репозиторий сертификатов блоков питания.
     */
    private final IPsuCertificateRepository certificateRepository;

    /**
     * Репозиторий основных коннекторов питания.
     */
    private final IMainPowerConnectorRepository mainPowerConnectorRepository;

    /**
     * Репозиторий коннекторов питания процессоров.
     */
    private final ICpuPowerConnectorRepository cpuPowerConnectorRepository;

    /**
     * Репозиторий коннекторов питания накопителей.
     */
    private final IStoragePowerConnectorRepository storagePowerConnectorRepository;

    /**
     * Репозиторий коннекторов питания видеокарт.
     */
    private final IGraphicsCardPowerConnectorRepository graphicsCardPowerConnectorRepository;

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
     * @param psuRepository                        репозиторий блоков питания
     * @param vendorRepository                     репозиторий вендоров
     * @param formFactorRepository                 репозиторий форм-факторов блоков питания
     * @param certificateRepository                репозиторий сертификатов блоков питания
     * @param mainPowerConnectorRepository         репозиторий основных коннекторов питания
     * @param cpuPowerConnectorRepository          репозиторий коннекторов питания процессоров
     * @param storagePowerConnectorRepository      репозиторий коннекторов питания накопителей
     * @param graphicsCardPowerConnectorRepository репозиторий коннекторов питания видеокарт
     * @param translator                           сервис предоставления сообщений
     */
    @Autowired
    public PsuCrudService(
            final IPsuRepository psuRepository,
            final IVendorRepository vendorRepository,
            final IPsuFormFactorRepository formFactorRepository,
            final IPsuCertificateRepository certificateRepository,
            final IMainPowerConnectorRepository mainPowerConnectorRepository,
            final ICpuPowerConnectorRepository cpuPowerConnectorRepository,
            final IStoragePowerConnectorRepository storagePowerConnectorRepository,
            final IGraphicsCardPowerConnectorRepository graphicsCardPowerConnectorRepository,
            final Translator translator
    ) {
        this.psuRepository = psuRepository;
        this.vendorRepository = vendorRepository;
        this.formFactorRepository = formFactorRepository;
        this.certificateRepository = certificateRepository;
        this.mainPowerConnectorRepository = mainPowerConnectorRepository;
        this.cpuPowerConnectorRepository = cpuPowerConnectorRepository;
        this.storagePowerConnectorRepository = storagePowerConnectorRepository;
        this.graphicsCardPowerConnectorRepository = graphicsCardPowerConnectorRepository;
        this.translator = translator;
    }

    @Override
    public Psu getById(final UUID id) {
        return findPsuById(id);
    }

    @Override
    public List<Psu> getAll() {
        return psuRepository.findAll();
    }

    @Override
    public Slice<Psu> getAll(final Pageable pageable) {
        return psuRepository.findAllBy(pageable);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Psu create(final Psu newPsu) {
        final Vendor vendor = newPsu.getVendor();
        if (vendor == null || vendor.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_VENDOR
            );
        }

        final PsuFormFactor formFactor = newPsu.getFormFactor();
        if (formFactor == null || formFactor.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_FORM_FACTOR
            );
        }

        final PsuCertificate certificate = newPsu.getCertificate();
        if (certificate == null || certificate.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_CERTIFICATE
            );
        }

        final MainPowerConnector mainPowerConnector = newPsu.getMainPowerConnector();
        if (mainPowerConnector == null || mainPowerConnector.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_MAIN_POWER_CONNECTOR
            );
        }

        final var cpuPowerConnectors = newPsu.getCpuPowerConnectors();
        validateCpuPowerConnectors(cpuPowerConnectors);

        final var storagePowerConnectors = newPsu.getStoragePowerConnectors();
        validateStoragePowerConnectors(storagePowerConnectors);

        final var graphicsCardPowerConnectors = newPsu.getGraphicsCardPowerConnectors();
        validateGraphicsCardPowerConnectors(graphicsCardPowerConnectors);

        final Vendor foundVendor = findVendorById(vendor.getId());
        newPsu.setVendor(foundVendor);

        final var foundFormFactor = findFormFactorById(formFactor.getId());
        newPsu.setFormFactor(foundFormFactor);

        final var foundCertificate = findCertificateById(certificate.getId());
        newPsu.setCertificate(foundCertificate);

        cpuPowerConnectors.forEach(psuToConnector -> {
            final UUID connectorId = psuToConnector.getCpuPowerConnector().getId();
            final var connector = findCpuPowerConnectorById(connectorId);
            psuToConnector.setCpuPowerConnector(connector);
            psuToConnector.setPsu(newPsu);
        });
        newPsu.setCpuPowerConnectors(cpuPowerConnectors);

        storagePowerConnectors.forEach(psuToConnector -> {
            final UUID connectorId = psuToConnector.getStoragePowerConnector().getId();
            final var connector = findStoragePowerConnectorById(connectorId);
            psuToConnector.setStoragePowerConnector(connector);
            psuToConnector.setPsu(newPsu);
        });
        newPsu.setStoragePowerConnectors(storagePowerConnectors);

        graphicsCardPowerConnectors.forEach(psuToConnector -> {
            final UUID connectorId = psuToConnector.getGraphicsCardPowerConnector().getId();
            final var connector = findGraphicsCardPowerConnectorById(connectorId);
            psuToConnector.setGraphicsCardPowerConnector(connector);
            psuToConnector.setPsu(newPsu);
        });
        newPsu.setGraphicsCardPowerConnectors(graphicsCardPowerConnectors);

        psuRepository.findByName(newPsu.getName())
                .ifPresent(psu -> {
                    throw new UniqueNameException(
                            translator.getMessage(
                                    MESSAGE_CODE_PSU_UNIQUE,
                                    psu.getName()
                            ),
                            FIELD_NAME
                    );
                });

        return psuRepository.save(newPsu);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public void delete(final UUID id) {
        psuRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Psu update(final UUID id, final Psu changedPsu) {
        final Psu toBeUpdated = findPsuById(id);

        final Vendor foundVendor = Optional.ofNullable(changedPsu.getVendor())
                .map(Vendor::getId)
                .map(this::findVendorById)
                .orElse(toBeUpdated.getVendor());

        final PsuFormFactor foundFormFactor = Optional.ofNullable(changedPsu.getFormFactor())
                .map(PsuFormFactor::getId)
                .map(this::findFormFactorById)
                .orElse(toBeUpdated.getFormFactor());

        final PsuCertificate foundCertificate = Optional.ofNullable(changedPsu.getCertificate())
                .map(PsuCertificate::getId)
                .map(this::findCertificateById)
                .orElse(toBeUpdated.getCertificate());

        final MainPowerConnector foundMainPowerConnector = Optional.ofNullable(changedPsu.getMainPowerConnector())
                .map(MainPowerConnector::getId)
                .map(this::findMainPowerConnectorById)
                .orElse(toBeUpdated.getMainPowerConnector());

        final String name = Objects.requireNonNullElse(
                changedPsu.getName(),
                toBeUpdated.getName()
        );

        final Integer power = Objects.requireNonNullElse(
                changedPsu.getPower(),
                toBeUpdated.getPower()
        );

        final Integer power12V = Objects.requireNonNullElse(
                changedPsu.getPower12V(),
                toBeUpdated.getPower12V()
        );

        final Integer length = Objects.requireNonNullElse(
                changedPsu.getLength(),
                toBeUpdated.getLength()
        );

        final var cpuPowerConnectors = changedPsu.getCpuPowerConnectors();
        if (cpuPowerConnectors != null) {
            validateCpuPowerConnectors(cpuPowerConnectors);

            toBeUpdated.getCpuPowerConnectors().forEach(pc -> entityManager.remove(pc));
            toBeUpdated.getCpuPowerConnectors().clear();
            entityManager.flush();

            cpuPowerConnectors.forEach(psuToConnector -> {
                final UUID connectorId = psuToConnector.getCpuPowerConnector().getId();
                final var connector = findCpuPowerConnectorById(connectorId);
                toBeUpdated.addCpuPowerConnector(connector, psuToConnector.getCount());
            });
        }

        final var storagePowerConnectors = changedPsu.getStoragePowerConnectors();
        if (storagePowerConnectors != null) {
            validateStoragePowerConnectors(storagePowerConnectors);

            toBeUpdated.getStoragePowerConnectors().forEach(pc -> entityManager.remove(pc));
            toBeUpdated.getStoragePowerConnectors().clear();
            entityManager.flush();

            storagePowerConnectors.forEach(psuToConnector -> {
                final UUID connectorId = psuToConnector.getStoragePowerConnector().getId();
                final var connector = findStoragePowerConnectorById(connectorId);
                toBeUpdated.addStoragePowerConnector(connector, psuToConnector.getCount());
            });
        }

        final var graphicsCardPowerConnectors = changedPsu.getGraphicsCardPowerConnectors();
        if (graphicsCardPowerConnectors != null) {
            validateGraphicsCardPowerConnectors(graphicsCardPowerConnectors);

            toBeUpdated.getGraphicsCardPowerConnectors().forEach(pc -> entityManager.remove(pc));
            toBeUpdated.getGraphicsCardPowerConnectors().clear();
            entityManager.flush();

            graphicsCardPowerConnectors.forEach(psuToConnector -> {
                final UUID connectorId = psuToConnector.getGraphicsCardPowerConnector().getId();
                final var connector = findGraphicsCardPowerConnectorById(connectorId);
                toBeUpdated.addGraphicsCardPowerConnector(connector, psuToConnector.getCount());
            });
        }

        psuRepository.findByNameAndIdNot(
                changedPsu.getName(),
                id
        ).ifPresent(psu -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_PSU_UNIQUE,
                            psu.getName()
                    ),
                    FIELD_NAME
            );
        });

        toBeUpdated.setName(name);
        toBeUpdated.setPower(power);
        toBeUpdated.setPower12V(power12V);
        toBeUpdated.setLength(length);

        toBeUpdated.setVendor(foundVendor);
        toBeUpdated.setFormFactor(foundFormFactor);
        toBeUpdated.setCertificate(foundCertificate);
        toBeUpdated.setMainPowerConnector(foundMainPowerConnector);

        return psuRepository.save(toBeUpdated);
    }

    @Override
    @Transactional(isolation = READ_COMMITTED)
    public Psu replace(final UUID id, final Psu newPsu) {
        final Vendor vendor = newPsu.getVendor();
        if (vendor == null || vendor.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_VENDOR
            );
        }

        final PsuFormFactor formFactor = newPsu.getFormFactor();
        if (formFactor == null || formFactor.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_FORM_FACTOR
            );
        }

        final PsuCertificate certificate = newPsu.getCertificate();
        if (certificate == null || certificate.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_CERTIFICATE
            );
        }

        final MainPowerConnector mainPowerConnector = newPsu.getMainPowerConnector();
        if (mainPowerConnector == null || mainPowerConnector.getId() == null) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_MAIN_POWER_CONNECTOR
            );
        }

        final var cpuPowerConnectors = newPsu.getCpuPowerConnectors();
        validateCpuPowerConnectors(cpuPowerConnectors);

        final var storagePowerConnectors = newPsu.getStoragePowerConnectors();
        validateStoragePowerConnectors(storagePowerConnectors);

        final var graphicsCardPowerConnectors = newPsu.getGraphicsCardPowerConnectors();
        validateGraphicsCardPowerConnectors(graphicsCardPowerConnectors);

        final Psu existent = findPsuById(id);

        final Vendor foundVendor = findVendorById(vendor.getId());
        final var foundFormFactor = findFormFactorById(formFactor.getId());
        final var foundCertificate = findCertificateById(certificate.getId());
        final var foundMainPowerConnector = findMainPowerConnectorById(mainPowerConnector.getId());

        existent.getGraphicsCardPowerConnectors().forEach(pc -> entityManager.remove(pc));
        existent.getStoragePowerConnectors().forEach(pc -> entityManager.remove(pc));
        existent.getCpuPowerConnectors().forEach(pc -> entityManager.remove(pc));
        existent.getGraphicsCardPowerConnectors().clear();
        existent.getStoragePowerConnectors().clear();
        existent.getCpuPowerConnectors().clear();
        entityManager.flush();

        cpuPowerConnectors.forEach(psuToConnector -> {
            final UUID connectorId = psuToConnector.getCpuPowerConnector().getId();
            final var connector = findCpuPowerConnectorById(connectorId);
            existent.addCpuPowerConnector(connector, psuToConnector.getCount());
        });

        storagePowerConnectors.forEach(psuToConnector -> {
            final UUID connectorId = psuToConnector.getStoragePowerConnector().getId();
            final var connector = findStoragePowerConnectorById(connectorId);
            existent.addStoragePowerConnector(connector, psuToConnector.getCount());
        });

        graphicsCardPowerConnectors.forEach(psuToConnector -> {
            final UUID connectorId = psuToConnector.getGraphicsCardPowerConnector().getId();
            final var connector = findGraphicsCardPowerConnectorById(connectorId);
            existent.addGraphicsCardPowerConnector(connector, psuToConnector.getCount());
        });

        psuRepository.findByNameAndIdNot(
                newPsu.getName(),
                id
        ).ifPresent(psu -> {
            throw new UniqueNameException(
                    translator.getMessage(
                            MESSAGE_CODE_PSU_UNIQUE,
                            psu.getName()
                    ),
                    FIELD_NAME
            );
        });

        existent.setName(newPsu.getName());
        existent.setPower(newPsu.getPower());
        existent.setPower12V(newPsu.getPower12V());
        existent.setLength(newPsu.getLength());

        existent.setVendor(foundVendor);
        existent.setFormFactor(foundFormFactor);
        existent.setCertificate(foundCertificate);
        existent.setMainPowerConnector(foundMainPowerConnector);

        return psuRepository.save(existent);
    }

    /**
     * Возвращает блок питания с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return блок питания с указанным ID, если он существует
     */
    private Psu findPsuById(final UUID id) {
        return psuRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_PSU_NOT_FOUND,
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

    /**
     * Возвращает форм-фактор блока питания с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return форм-фактор блока питания с указанным ID, если он существует
     */
    private PsuFormFactor findFormFactorById(final UUID id) {
        return formFactorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_PSU_FORM_FACTOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает сертификат блока питания с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return сертификат блока питания с указанным ID, если он существует
     */
    private PsuCertificate findCertificateById(final UUID id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_PSU_CERTIFICATE_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает основной коннектор питания с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return основной коннектор питания с указанным ID, если он существует
     */
    private MainPowerConnector findMainPowerConnectorById(final UUID id) {
        return mainPowerConnectorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_MAIN_POWER_CONNECTOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает коннектор питания процессора с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return коннектор питания процессора с указанным ID, если он существует
     */
    private CpuPowerConnector findCpuPowerConnectorById(final UUID id) {
        return cpuPowerConnectorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_CPU_POWER_CONNECTOR_NOT_FOUND,
                                id
                        ),
                        FIELD_ID
                ));
    }

    /**
     * Возвращает коннектор питания накопителя с указанным ID, если он существует.
     * В противном случае выбрасывает {@link DataNotFoundException}.
     *
     * @param id ID
     * @return коннектор питания накопителя с указанным ID, если он существует
     */
    private StoragePowerConnector findStoragePowerConnectorById(final UUID id) {
        return storagePowerConnectorRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        translator.getMessage(
                                MESSAGE_CODE_STORAGE_POWER_CONNECTOR_NOT_FOUND,
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
    private GraphicsCardPowerConnector findGraphicsCardPowerConnectorById(final UUID id) {
        return graphicsCardPowerConnectorRepository.findById(id)
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
     * у блока питания коннекторов питания процессоров на валидность.
     *
     * @param connectors множество имеющихся у блока питания коннекторов питания процессоров
     */
    private void validateCpuPowerConnectors(final Set<PsuToCpuPowerConnector> connectors) {
        if (connectors == null
                || connectors.isEmpty()
                || connectors.stream().anyMatch(Objects::isNull)
                || connectors.stream().anyMatch(psuToConnector ->
                psuToConnector.getCpuPowerConnector() == null)
                || connectors.stream().anyMatch(psuToConnector ->
                psuToConnector.getCpuPowerConnector().getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_CPU_POWER_CONNECTORS
            );
        }
    }

    /**
     * Проверяет переданное множество имеющихся
     * у блока питания коннекторов питания накопителей на валидность.
     *
     * @param connectors множество имеющихся у блока питания коннекторов питания накопителей
     */
    private void validateStoragePowerConnectors(final Set<PsuToStoragePowerConnector> connectors) {
        if (connectors == null
                || connectors.isEmpty()
                || connectors.stream().anyMatch(Objects::isNull)
                || connectors.stream().anyMatch(psuToConnector ->
                psuToConnector.getStoragePowerConnector() == null)
                || connectors.stream().anyMatch(psuToConnector ->
                psuToConnector.getStoragePowerConnector().getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_STORAGE_POWER_CONNECTORS
            );
        }
    }

    /**
     * Проверяет переданное множество имеющихся
     * у блока питания коннекторов питания видеокарт на валидность.
     *
     * @param connectors множество имеющихся у блока питания коннекторов питания видеокарт
     */
    private void validateGraphicsCardPowerConnectors(final Set<PsuToGraphicsCardPowerConnector> connectors) {
        if (connectors == null
                || connectors.isEmpty()
                || connectors.stream().anyMatch(Objects::isNull)
                || connectors.stream().anyMatch(psuToConnector ->
                psuToConnector.getGraphicsCardPowerConnector() == null)
                || connectors.stream().anyMatch(psuToConnector ->
                psuToConnector.getGraphicsCardPowerConnector().getId() == null)
        ) {
            throw new InvalidParamException(
                    translator.getMessage(MESSAGE_CODE_INVALID_PARAM_VALUE),
                    FIELD_GRAPHICS_CARD_POWER_CONNECTORS
            );
        }
    }
}
