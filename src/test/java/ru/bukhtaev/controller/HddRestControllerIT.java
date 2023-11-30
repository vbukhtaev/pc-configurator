package ru.bukhtaev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.dto.mapper.IHddMapper;
import ru.bukhtaev.dto.request.HddRequestDto;
import ru.bukhtaev.model.Hdd;
import ru.bukhtaev.model.dictionary.ExpansionBayFormat;
import ru.bukhtaev.model.dictionary.StorageConnector;
import ru.bukhtaev.model.dictionary.StoragePowerConnector;
import ru.bukhtaev.model.dictionary.Vendor;
import ru.bukhtaev.repository.IHddRepository;
import ru.bukhtaev.repository.dictionary.IExpansionBayFormatRepository;
import ru.bukhtaev.repository.dictionary.IStorageConnectorRepository;
import ru.bukhtaev.repository.dictionary.IStoragePowerConnectorRepository;
import ru.bukhtaev.repository.dictionary.IVendorRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.HddRestController.URL_API_V1_HDDS;

/**
 * Интеграционные тесты для CRUD операций над жесткими дисками.
 */
class HddRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO жестких дисков.
     */
    @Autowired
    private IHddMapper mapper;

    /**
     * Репозиторий жестких дисков.
     */
    @Autowired
    private IHddRepository hddRepository;

    /**
     * Репозиторий вендоров.
     */
    @Autowired
    private IVendorRepository vendorRepository;

    /**
     * Репозиторий коннекторов подключения накопителей.
     */
    @Autowired
    private IStorageConnectorRepository connectorRepository;

    /**
     * Репозиторий коннекторов питания накопителей.
     */
    @Autowired
    private IStoragePowerConnectorRepository powerConnectorRepository;

    /**
     * Репозиторий форматов отсеков расширения.
     */
    @Autowired
    private IExpansionBayFormatRepository expansionBayFormatRepository;

    private HddRequestDto hddBarracuda;
    private HddRequestDto hddP300;

    private Vendor vendorSeagate;
    private Vendor vendorToshiba;

    private StorageConnector connectorSata3;
    private StorageConnector connectorSata2;

    private StoragePowerConnector powerConnectorFdd;
    private StoragePowerConnector powerConnectorMolex;

    private ExpansionBayFormat format25;
    private ExpansionBayFormat format35;

    @BeforeEach
    void setUp() {
        vendorSeagate = vendorRepository.save(
                Vendor.builder()
                        .name("Seagate")
                        .build()
        );
        vendorToshiba = vendorRepository.save(
                Vendor.builder()
                        .name("Toshiba")
                        .build()
        );

        connectorSata3 = connectorRepository.save(
                StorageConnector.builder()
                        .name("SATA 3")
                        .build()
        );
        connectorSata2 = connectorRepository.save(
                StorageConnector.builder()
                        .name("SATA 2")
                        .build()
        );

        powerConnectorFdd = powerConnectorRepository.save(
                StoragePowerConnector.builder()
                        .name("FDD")
                        .build()
        );
        powerConnectorMolex = powerConnectorRepository.save(
                StoragePowerConnector.builder()
                        .name("Molex")
                        .build()
        );

        format25 = expansionBayFormatRepository.save(
                ExpansionBayFormat.builder()
                        .name("2.5")
                        .build()
        );
        format35 = expansionBayFormatRepository.save(
                ExpansionBayFormat.builder()
                        .name("3.5")
                        .build()
        );

        hddBarracuda = HddRequestDto.builder()
                .name("Barracuda")
                .vendorId(vendorSeagate.getId())
                .connectorId(connectorSata3.getId())
                .powerConnectorId(powerConnectorFdd.getId())
                .expansionBayFormatId(format25.getId())
                .capacity(1024)
                .readingSpeed(210)
                .writingSpeed(210)
                .spindleSpeed(7200)
                .cacheSize(64)
                .build();
        hddP300 = HddRequestDto.builder()
                .name("P300")
                .vendorId(vendorToshiba.getId())
                .connectorId(connectorSata2.getId())
                .powerConnectorId(powerConnectorMolex.getId())
                .expansionBayFormatId(format35.getId())
                .capacity(2000)
                .readingSpeed(190)
                .writingSpeed(190)
                .spindleSpeed(5400)
                .cacheSize(128)
                .build();
    }

    @AfterEach
    void tearDown() {
        hddRepository.deleteAll();
        vendorRepository.deleteAll();
        connectorRepository.deleteAll();
        powerConnectorRepository.deleteAll();
        expansionBayFormatRepository.deleteAll();
    }

    @Test
    void getAll_shouldReturnAllEntities() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_HDDS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),

                        jsonPath("$[0].name", is(hddBarracuda.getName())),
                        jsonPath("$[0].vendor.id", is(vendorSeagate.getId().toString())),
                        jsonPath("$[0].vendor.name", is(vendorSeagate.getName())),
                        jsonPath("$[0].connector.id", is(connectorSata3.getId().toString())),
                        jsonPath("$[0].connector.name", is(connectorSata3.getName())),
                        jsonPath("$[0].powerConnector.id", is(powerConnectorFdd.getId().toString())),
                        jsonPath("$[0].powerConnector.name", is(powerConnectorFdd.getName())),
                        jsonPath("$[0].expansionBayFormat.id", is(format25.getId().toString())),
                        jsonPath("$[0].expansionBayFormat.name", is(format25.getName())),
                        jsonPath("$[0].capacity", is(hddBarracuda.getCapacity())),
                        jsonPath("$[0].readingSpeed", is(hddBarracuda.getReadingSpeed())),
                        jsonPath("$[0].writingSpeed", is(hddBarracuda.getWritingSpeed())),
                        jsonPath("$[0].spindleSpeed", is(hddBarracuda.getSpindleSpeed())),
                        jsonPath("$[0].cacheSize", is(hddBarracuda.getCacheSize())),

                        jsonPath("$[1].name", is(hddP300.getName())),
                        jsonPath("$[1].vendor.id", is(vendorToshiba.getId().toString())),
                        jsonPath("$[1].vendor.name", is(vendorToshiba.getName())),
                        jsonPath("$[1].connector.id", is(connectorSata2.getId().toString())),
                        jsonPath("$[1].connector.name", is(connectorSata2.getName())),
                        jsonPath("$[1].powerConnector.id", is(powerConnectorMolex.getId().toString())),
                        jsonPath("$[1].powerConnector.name", is(powerConnectorMolex.getName())),
                        jsonPath("$[1].expansionBayFormat.id", is(format35.getId().toString())),
                        jsonPath("$[1].expansionBayFormat.name", is(format35.getName())),
                        jsonPath("$[1].capacity", is(hddP300.getCapacity())),
                        jsonPath("$[1].readingSpeed", is(hddP300.getReadingSpeed())),
                        jsonPath("$[1].writingSpeed", is(hddP300.getWritingSpeed())),
                        jsonPath("$[1].spindleSpeed", is(hddP300.getSpindleSpeed())),
                        jsonPath("$[1].cacheSize", is(hddP300.getCacheSize()))
                );
    }

    @Test
    void getAll_withPagination_shouldReturnAllEntitiesAsPage() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_HDDS + "/pageable")
                .params(HDD_PAGE_REQUEST_PARAMS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.content", hasSize(2)),

                        jsonPath("$.content[0].name", is(hddBarracuda.getName())),
                        jsonPath("$.content[0].vendor.id", is(vendorSeagate.getId().toString())),
                        jsonPath("$.content[0].vendor.name", is(vendorSeagate.getName())),
                        jsonPath("$.content[0].connector.id", is(connectorSata3.getId().toString())),
                        jsonPath("$.content[0].connector.name", is(connectorSata3.getName())),
                        jsonPath("$.content[0].powerConnector.id", is(powerConnectorFdd.getId().toString())),
                        jsonPath("$.content[0].powerConnector.name", is(powerConnectorFdd.getName())),
                        jsonPath("$.content[0].expansionBayFormat.id", is(format25.getId().toString())),
                        jsonPath("$.content[0].expansionBayFormat.name", is(format25.getName())),
                        jsonPath("$.content[0].capacity", is(hddBarracuda.getCapacity())),
                        jsonPath("$.content[0].readingSpeed", is(hddBarracuda.getReadingSpeed())),
                        jsonPath("$.content[0].writingSpeed", is(hddBarracuda.getWritingSpeed())),
                        jsonPath("$.content[0].spindleSpeed", is(hddBarracuda.getSpindleSpeed())),
                        jsonPath("$.content[0].cacheSize", is(hddBarracuda.getCacheSize())),

                        jsonPath("$.content[1].name", is(hddP300.getName())),
                        jsonPath("$.content[1].vendor.id", is(vendorToshiba.getId().toString())),
                        jsonPath("$.content[1].vendor.name", is(vendorToshiba.getName())),
                        jsonPath("$.content[1].connector.id", is(connectorSata2.getId().toString())),
                        jsonPath("$.content[1].connector.name", is(connectorSata2.getName())),
                        jsonPath("$.content[1].powerConnector.id", is(powerConnectorMolex.getId().toString())),
                        jsonPath("$.content[1].powerConnector.name", is(powerConnectorMolex.getName())),
                        jsonPath("$.content[1].expansionBayFormat.id", is(format35.getId().toString())),
                        jsonPath("$.content[1].expansionBayFormat.name", is(format35.getName())),
                        jsonPath("$.content[1].capacity", is(hddP300.getCapacity())),
                        jsonPath("$.content[1].readingSpeed", is(hddP300.getReadingSpeed())),
                        jsonPath("$.content[1].writingSpeed", is(hddP300.getWritingSpeed())),
                        jsonPath("$.content[1].spindleSpeed", is(hddP300.getSpindleSpeed())),
                        jsonPath("$.content[1].cacheSize", is(hddP300.getCacheSize()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final Hdd saved = hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        assertThat(hddRepository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_HDDS + "/{id}",
                saved.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(hddBarracuda.getName())),
                        jsonPath("$.vendor.id", is(vendorSeagate.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorSeagate.getName())),
                        jsonPath("$.connector.id", is(connectorSata3.getId().toString())),
                        jsonPath("$.connector.name", is(connectorSata3.getName())),
                        jsonPath("$.powerConnector.id", is(powerConnectorFdd.getId().toString())),
                        jsonPath("$.powerConnector.name", is(powerConnectorFdd.getName())),
                        jsonPath("$.expansionBayFormat.id", is(format25.getId().toString())),
                        jsonPath("$.expansionBayFormat.name", is(format25.getName())),
                        jsonPath("$.capacity", is(hddBarracuda.getCapacity())),
                        jsonPath("$.readingSpeed", is(hddBarracuda.getReadingSpeed())),
                        jsonPath("$.writingSpeed", is(hddBarracuda.getWritingSpeed())),
                        jsonPath("$.spindleSpeed", is(hddBarracuda.getSpindleSpeed())),
                        jsonPath("$.cacheSize", is(hddBarracuda.getCacheSize()))
                );
    }

    @Test
    void getById_withNonExistentId_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        assertThat(hddRepository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_HDDS + "/{id}",
                nonExistentId
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "HDD with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentEntity_shouldReturnCreatedEntity() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(hddBarracuda);
        final var requestBuilder = post(URL_API_V1_HDDS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(hddBarracuda.getName())),
                        jsonPath("$.vendor.id", is(vendorSeagate.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorSeagate.getName())),
                        jsonPath("$.connector.id", is(connectorSata3.getId().toString())),
                        jsonPath("$.connector.name", is(connectorSata3.getName())),
                        jsonPath("$.powerConnector.id", is(powerConnectorFdd.getId().toString())),
                        jsonPath("$.powerConnector.name", is(powerConnectorFdd.getName())),
                        jsonPath("$.expansionBayFormat.id", is(format25.getId().toString())),
                        jsonPath("$.expansionBayFormat.name", is(format25.getName())),
                        jsonPath("$.capacity", is(hddBarracuda.getCapacity())),
                        jsonPath("$.readingSpeed", is(hddBarracuda.getReadingSpeed())),
                        jsonPath("$.writingSpeed", is(hddBarracuda.getWritingSpeed())),
                        jsonPath("$.spindleSpeed", is(hddBarracuda.getSpindleSpeed())),
                        jsonPath("$.cacheSize", is(hddBarracuda.getCacheSize()))
                );

        final List<Hdd> hdds = hddRepository.findAll();
        assertThat(hdds).hasSize(2);
        final Hdd hdd = hdds.get(1);
        assertThat(hdd.getId()).isNotNull();
        assertThat(hdd.getName())
                .isEqualTo(hddBarracuda.getName());
        assertThat(hdd.getVendor().getId())
                .isEqualTo(vendorSeagate.getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(vendorSeagate.getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(connectorSata3.getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(connectorSata3.getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(powerConnectorFdd.getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(powerConnectorFdd.getName());
        assertThat(hdd.getExpansionBayFormat().getId())
                .isEqualTo(format25.getId());
        assertThat(hdd.getExpansionBayFormat().getName())
                .isEqualTo(format25.getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddBarracuda.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddBarracuda.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddBarracuda.getWritingSpeed());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddBarracuda.getSpindleSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddBarracuda.getCacheSize());
    }

    @Test
    void create_withNonExistentVendorId_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(1);
        final UUID nonExistentVendorId = UUID.randomUUID();
        hddBarracuda.setVendorId(nonExistentVendorId);
        final String jsonRequest = objectMapper.writeValueAsString(hddBarracuda);
        final var requestBuilder = post(URL_API_V1_HDDS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("id")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Vendor with ID = <{0}> not found!",
                                        nonExistentVendorId
                                )
                        ))
                );

        assertThat(hddRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withNonExistentConnectorId_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(1);
        final UUID nonExistentConnectorId = UUID.randomUUID();
        hddBarracuda.setConnectorId(nonExistentConnectorId);
        final String jsonRequest = objectMapper.writeValueAsString(hddBarracuda);
        final var requestBuilder = post(URL_API_V1_HDDS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("id")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Storage connector with ID = <{0}> not found!",
                                        nonExistentConnectorId
                                )
                        ))
                );

        assertThat(hddRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withNonExistentPowerConnectorId_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(1);
        final UUID nonExistentPowerConnectorId = UUID.randomUUID();
        hddBarracuda.setPowerConnectorId(nonExistentPowerConnectorId);
        final String jsonRequest = objectMapper.writeValueAsString(hddBarracuda);
        final var requestBuilder = post(URL_API_V1_HDDS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("id")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Storage power connector with ID = <{0}> not found!",
                                        nonExistentPowerConnectorId
                                )
                        ))
                );

        assertThat(hddRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withNonExistentExpansionBayFormatId_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(1);
        final UUID nonExistentExpansionBayFormatId = UUID.randomUUID();
        hddBarracuda.setExpansionBayFormatId(nonExistentExpansionBayFormatId);
        final String jsonRequest = objectMapper.writeValueAsString(hddBarracuda);
        final var requestBuilder = post(URL_API_V1_HDDS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("id")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Expansion bay format with ID = <{0}> not found!",
                                        nonExistentExpansionBayFormatId
                                )
                        ))
                );

        assertThat(hddRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectVendorParam_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(1);
        hddBarracuda.setVendorId(null);
        final String jsonRequest = objectMapper.writeValueAsString(hddBarracuda);
        final var requestBuilder = post(URL_API_V1_HDDS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("vendor")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        assertThat(hddRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectConnectorParam_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(1);
        hddBarracuda.setConnectorId(null);
        final String jsonRequest = objectMapper.writeValueAsString(hddBarracuda);
        final var requestBuilder = post(URL_API_V1_HDDS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("connector")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        assertThat(hddRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectPowerConnectorParam_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(1);
        hddBarracuda.setPowerConnectorId(null);
        final String jsonRequest = objectMapper.writeValueAsString(hddBarracuda);
        final var requestBuilder = post(URL_API_V1_HDDS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("powerConnector")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        assertThat(hddRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectExpansionBayFormatParam_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(1);
        hddBarracuda.setExpansionBayFormatId(null);
        final String jsonRequest = objectMapper.writeValueAsString(hddBarracuda);
        final var requestBuilder = post(URL_API_V1_HDDS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("expansionBayFormat")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        assertThat(hddRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withExistentEntity_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        assertThat(hddRepository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(hddBarracuda);
        final var requestBuilder = post(URL_API_V1_HDDS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames",
                                contains("name", "capacity", "spindleSpeed", "cacheSize")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "HDD with name <{0}> capacity <{1}> spindle speed <{2}> " +
                                                "and cache size <{3}> already exists!",
                                        hddBarracuda.getName(),
                                        hddBarracuda.getCapacity(),
                                        hddBarracuda.getSpindleSpeed(),
                                        hddBarracuda.getCacheSize()
                                )
                        ))
                );

        assertThat(hddRepository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentEntity_shouldReturnReplacedEntity() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        final Hdd saved = hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final HddRequestDto dto = HddRequestDto.builder()
                .name(newName)
                .vendorId(vendorSeagate.getId())
                .connectorId(connectorSata3.getId())
                .powerConnectorId(powerConnectorFdd.getId())
                .expansionBayFormatId(format25.getId())
                .capacity(hddBarracuda.getCapacity())
                .readingSpeed(hddBarracuda.getReadingSpeed())
                .writingSpeed(hddBarracuda.getWritingSpeed())
                .spindleSpeed(hddBarracuda.getSpindleSpeed())
                .cacheSize(hddBarracuda.getCacheSize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_HDDS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(newName)),
                        jsonPath("$.vendor.id", is(vendorSeagate.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorSeagate.getName())),
                        jsonPath("$.connector.id", is(connectorSata3.getId().toString())),
                        jsonPath("$.connector.name", is(connectorSata3.getName())),
                        jsonPath("$.powerConnector.id", is(powerConnectorFdd.getId().toString())),
                        jsonPath("$.powerConnector.name", is(powerConnectorFdd.getName())),
                        jsonPath("$.expansionBayFormat.id", is(format25.getId().toString())),
                        jsonPath("$.expansionBayFormat.name", is(format25.getName())),
                        jsonPath("$.capacity", is(hddBarracuda.getCapacity())),
                        jsonPath("$.readingSpeed", is(hddBarracuda.getReadingSpeed())),
                        jsonPath("$.writingSpeed", is(hddBarracuda.getWritingSpeed())),
                        jsonPath("$.spindleSpeed", is(hddBarracuda.getSpindleSpeed())),
                        jsonPath("$.cacheSize", is(hddBarracuda.getCacheSize()))
                );

        final Optional<Hdd> optHdd = hddRepository.findById(saved.getId());
        assertThat(optHdd).isPresent();
        final Hdd hdd = optHdd.get();
        assertThat(hdd.getName())
                .isEqualTo(newName);
        assertThat(hdd.getVendor().getId())
                .isEqualTo(vendorSeagate.getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(vendorSeagate.getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(connectorSata3.getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(connectorSata3.getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(powerConnectorFdd.getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(powerConnectorFdd.getName());
        assertThat(hdd.getExpansionBayFormat().getId())
                .isEqualTo(format25.getId());
        assertThat(hdd.getExpansionBayFormat().getName())
                .isEqualTo(format25.getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddBarracuda.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddBarracuda.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddBarracuda.getWritingSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddBarracuda.getCacheSize());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddBarracuda.getSpindleSpeed());
    }

    @Test
    void replace_withNonExistentVendorId_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        final Hdd saved = hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final UUID nonExistentVendorId = UUID.randomUUID();
        final HddRequestDto dto = HddRequestDto.builder()
                .name(newName)
                .vendorId(nonExistentVendorId)
                .connectorId(connectorSata3.getId())
                .powerConnectorId(powerConnectorFdd.getId())
                .expansionBayFormatId(format25.getId())
                .capacity(hddBarracuda.getCapacity())
                .readingSpeed(hddBarracuda.getReadingSpeed())
                .writingSpeed(hddBarracuda.getWritingSpeed())
                .spindleSpeed(hddBarracuda.getSpindleSpeed())
                .cacheSize(hddBarracuda.getCacheSize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_HDDS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("id")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Vendor with ID = <{0}> not found!",
                                        nonExistentVendorId
                                )
                        ))
                );

        final Optional<Hdd> optHdd = hddRepository.findById(saved.getId());
        assertThat(optHdd).isPresent();
        final Hdd hdd = optHdd.get();
        assertThat(hdd.getName())
                .isEqualTo(hddP300.getName());
        assertThat(hdd.getVendor().getId())
                .isEqualTo(vendorToshiba.getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(vendorToshiba.getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(connectorSata2.getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(connectorSata2.getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(powerConnectorMolex.getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(powerConnectorMolex.getName());
        assertThat(hdd.getExpansionBayFormat().getId())
                .isEqualTo(format35.getId());
        assertThat(hdd.getExpansionBayFormat().getName())
                .isEqualTo(format35.getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddP300.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddP300.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddP300.getWritingSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddP300.getCacheSize());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddP300.getSpindleSpeed());
    }

    @Test
    void replace_withNonExistentConnectorId_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        final Hdd saved = hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final UUID nonExistentConnectorId = UUID.randomUUID();
        final HddRequestDto dto = HddRequestDto.builder()
                .name(newName)
                .vendorId(vendorSeagate.getId())
                .connectorId(nonExistentConnectorId)
                .powerConnectorId(powerConnectorFdd.getId())
                .expansionBayFormatId(format25.getId())
                .capacity(hddBarracuda.getCapacity())
                .readingSpeed(hddBarracuda.getReadingSpeed())
                .writingSpeed(hddBarracuda.getWritingSpeed())
                .spindleSpeed(hddBarracuda.getSpindleSpeed())
                .cacheSize(hddBarracuda.getCacheSize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_HDDS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("id")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Storage connector with ID = <{0}> not found!",
                                        nonExistentConnectorId
                                )
                        ))
                );

        final Optional<Hdd> optHdd = hddRepository.findById(saved.getId());
        assertThat(optHdd).isPresent();
        final Hdd hdd = optHdd.get();
        assertThat(hdd.getName())
                .isEqualTo(hddP300.getName());
        assertThat(hdd.getVendor().getId())
                .isEqualTo(vendorToshiba.getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(vendorToshiba.getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(connectorSata2.getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(connectorSata2.getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(powerConnectorMolex.getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(powerConnectorMolex.getName());
        assertThat(hdd.getExpansionBayFormat().getId())
                .isEqualTo(format35.getId());
        assertThat(hdd.getExpansionBayFormat().getName())
                .isEqualTo(format35.getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddP300.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddP300.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddP300.getWritingSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddP300.getCacheSize());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddP300.getSpindleSpeed());
    }

    @Test
    void replace_withNonExistentPowerConnectorId_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        final Hdd saved = hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final UUID nonExistentPowerConnectorId = UUID.randomUUID();
        final HddRequestDto dto = HddRequestDto.builder()
                .name(newName)
                .vendorId(vendorSeagate.getId())
                .connectorId(connectorSata3.getId())
                .powerConnectorId(nonExistentPowerConnectorId)
                .expansionBayFormatId(format25.getId())
                .capacity(hddBarracuda.getCapacity())
                .readingSpeed(hddBarracuda.getReadingSpeed())
                .writingSpeed(hddBarracuda.getWritingSpeed())
                .spindleSpeed(hddBarracuda.getSpindleSpeed())
                .cacheSize(hddBarracuda.getCacheSize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_HDDS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("id")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Storage power connector with ID = <{0}> not found!",
                                        nonExistentPowerConnectorId
                                )
                        ))
                );

        final Optional<Hdd> optHdd = hddRepository.findById(saved.getId());
        assertThat(optHdd).isPresent();
        final Hdd hdd = optHdd.get();
        assertThat(hdd.getName())
                .isEqualTo(hddP300.getName());
        assertThat(hdd.getVendor().getId())
                .isEqualTo(vendorToshiba.getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(vendorToshiba.getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(connectorSata2.getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(connectorSata2.getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(powerConnectorMolex.getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(powerConnectorMolex.getName());
        assertThat(hdd.getExpansionBayFormat().getId())
                .isEqualTo(format35.getId());
        assertThat(hdd.getExpansionBayFormat().getName())
                .isEqualTo(format35.getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddP300.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddP300.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddP300.getWritingSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddP300.getCacheSize());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddP300.getSpindleSpeed());
    }

    @Test
    void replace_withNonExistentExpansionBayFormatId_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        final Hdd saved = hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final UUID nonExistentExpansionBayFormatId = UUID.randomUUID();
        final HddRequestDto dto = HddRequestDto.builder()
                .name(newName)
                .vendorId(vendorSeagate.getId())
                .connectorId(connectorSata3.getId())
                .powerConnectorId(powerConnectorFdd.getId())
                .expansionBayFormatId(nonExistentExpansionBayFormatId)
                .capacity(hddBarracuda.getCapacity())
                .readingSpeed(hddBarracuda.getReadingSpeed())
                .writingSpeed(hddBarracuda.getWritingSpeed())
                .spindleSpeed(hddBarracuda.getSpindleSpeed())
                .cacheSize(hddBarracuda.getCacheSize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_HDDS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("id")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Expansion bay format with ID = <{0}> not found!",
                                        nonExistentExpansionBayFormatId
                                )
                        ))
                );

        final Optional<Hdd> optHdd = hddRepository.findById(saved.getId());
        assertThat(optHdd).isPresent();
        final Hdd hdd = optHdd.get();
        assertThat(hdd.getName())
                .isEqualTo(hddP300.getName());
        assertThat(hdd.getVendor().getId())
                .isEqualTo(vendorToshiba.getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(vendorToshiba.getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(connectorSata2.getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(connectorSata2.getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(powerConnectorMolex.getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(powerConnectorMolex.getName());
        assertThat(hdd.getExpansionBayFormat().getId())
                .isEqualTo(format35.getId());
        assertThat(hdd.getExpansionBayFormat().getName())
                .isEqualTo(format35.getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddP300.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddP300.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddP300.getWritingSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddP300.getCacheSize());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddP300.getSpindleSpeed());
    }

    @Test
    void replace_withIncorrectVendorParam_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        final Hdd saved = hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final HddRequestDto dto = HddRequestDto.builder()
                .name(newName)
                .vendorId(null)
                .connectorId(connectorSata3.getId())
                .powerConnectorId(powerConnectorFdd.getId())
                .expansionBayFormatId(format25.getId())
                .capacity(hddBarracuda.getCapacity())
                .readingSpeed(hddBarracuda.getReadingSpeed())
                .writingSpeed(hddBarracuda.getWritingSpeed())
                .spindleSpeed(hddBarracuda.getSpindleSpeed())
                .cacheSize(hddBarracuda.getCacheSize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_HDDS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("vendor")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        final Optional<Hdd> optHdd = hddRepository.findById(saved.getId());
        assertThat(optHdd).isPresent();
        final Hdd hdd = optHdd.get();
        assertThat(hdd.getName())
                .isEqualTo(hddP300.getName());
        assertThat(hdd.getVendor().getId())
                .isEqualTo(vendorToshiba.getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(vendorToshiba.getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(connectorSata2.getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(connectorSata2.getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(powerConnectorMolex.getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(powerConnectorMolex.getName());
        assertThat(hdd.getExpansionBayFormat().getId())
                .isEqualTo(format35.getId());
        assertThat(hdd.getExpansionBayFormat().getName())
                .isEqualTo(format35.getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddP300.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddP300.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddP300.getWritingSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddP300.getCacheSize());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddP300.getSpindleSpeed());
    }

    @Test
    void replace_withIncorrectConnectorParam_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        final Hdd saved = hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final HddRequestDto dto = HddRequestDto.builder()
                .name(newName)
                .vendorId(vendorSeagate.getId())
                .connectorId(null)
                .powerConnectorId(powerConnectorFdd.getId())
                .expansionBayFormatId(format25.getId())
                .capacity(hddBarracuda.getCapacity())
                .readingSpeed(hddBarracuda.getReadingSpeed())
                .writingSpeed(hddBarracuda.getWritingSpeed())
                .spindleSpeed(hddBarracuda.getSpindleSpeed())
                .cacheSize(hddBarracuda.getCacheSize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_HDDS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("connector")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        final Optional<Hdd> optHdd = hddRepository.findById(saved.getId());
        assertThat(optHdd).isPresent();
        final Hdd hdd = optHdd.get();
        assertThat(hdd.getName())
                .isEqualTo(hddP300.getName());
        assertThat(hdd.getVendor().getId())
                .isEqualTo(vendorToshiba.getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(vendorToshiba.getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(connectorSata2.getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(connectorSata2.getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(powerConnectorMolex.getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(powerConnectorMolex.getName());
        assertThat(hdd.getExpansionBayFormat().getId())
                .isEqualTo(format35.getId());
        assertThat(hdd.getExpansionBayFormat().getName())
                .isEqualTo(format35.getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddP300.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddP300.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddP300.getWritingSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddP300.getCacheSize());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddP300.getSpindleSpeed());
    }

    @Test
    void replace_withIncorrectPowerConnectorParam_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        final Hdd saved = hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final HddRequestDto dto = HddRequestDto.builder()
                .name(newName)
                .vendorId(vendorSeagate.getId())
                .connectorId(connectorSata3.getId())
                .powerConnectorId(null)
                .expansionBayFormatId(format25.getId())
                .capacity(hddBarracuda.getCapacity())
                .readingSpeed(hddBarracuda.getReadingSpeed())
                .writingSpeed(hddBarracuda.getWritingSpeed())
                .spindleSpeed(hddBarracuda.getSpindleSpeed())
                .cacheSize(hddBarracuda.getCacheSize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_HDDS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("powerConnector")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        final Optional<Hdd> optHdd = hddRepository.findById(saved.getId());
        assertThat(optHdd).isPresent();
        final Hdd hdd = optHdd.get();
        assertThat(hdd.getName())
                .isEqualTo(hddP300.getName());
        assertThat(hdd.getVendor().getId())
                .isEqualTo(vendorToshiba.getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(vendorToshiba.getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(connectorSata2.getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(connectorSata2.getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(powerConnectorMolex.getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(powerConnectorMolex.getName());
        assertThat(hdd.getExpansionBayFormat().getId())
                .isEqualTo(format35.getId());
        assertThat(hdd.getExpansionBayFormat().getName())
                .isEqualTo(format35.getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddP300.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddP300.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddP300.getWritingSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddP300.getCacheSize());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddP300.getSpindleSpeed());
    }

    @Test
    void replace_withIncorrectExpansionBayFormatParam_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        final Hdd saved = hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final HddRequestDto dto = HddRequestDto.builder()
                .name(newName)
                .vendorId(vendorSeagate.getId())
                .connectorId(connectorSata3.getId())
                .powerConnectorId(powerConnectorFdd.getId())
                .expansionBayFormatId(null)
                .capacity(hddBarracuda.getCapacity())
                .readingSpeed(hddBarracuda.getReadingSpeed())
                .writingSpeed(hddBarracuda.getWritingSpeed())
                .spindleSpeed(hddBarracuda.getSpindleSpeed())
                .cacheSize(hddBarracuda.getCacheSize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_HDDS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("expansionBayFormat")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        final Optional<Hdd> optHdd = hddRepository.findById(saved.getId());
        assertThat(optHdd).isPresent();
        final Hdd hdd = optHdd.get();
        assertThat(hdd.getName())
                .isEqualTo(hddP300.getName());
        assertThat(hdd.getVendor().getId())
                .isEqualTo(vendorToshiba.getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(vendorToshiba.getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(connectorSata2.getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(connectorSata2.getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(powerConnectorMolex.getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(powerConnectorMolex.getName());
        assertThat(hdd.getExpansionBayFormat().getId())
                .isEqualTo(format35.getId());
        assertThat(hdd.getExpansionBayFormat().getName())
                .isEqualTo(format35.getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddP300.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddP300.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddP300.getWritingSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddP300.getCacheSize());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddP300.getSpindleSpeed());
    }

    @Test
    void replace_withExistentEntity_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        final Hdd saved = hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(2);
        final HddRequestDto dto = HddRequestDto.builder()
                .name(hddBarracuda.getName())
                .vendorId(vendorSeagate.getId())
                .connectorId(connectorSata3.getId())
                .powerConnectorId(powerConnectorFdd.getId())
                .expansionBayFormatId(format25.getId())
                .capacity(hddBarracuda.getCapacity())
                .readingSpeed(hddBarracuda.getReadingSpeed())
                .writingSpeed(hddBarracuda.getWritingSpeed())
                .spindleSpeed(hddBarracuda.getSpindleSpeed())
                .cacheSize(hddBarracuda.getCacheSize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_HDDS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames",
                                contains("name", "capacity", "spindleSpeed", "cacheSize")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "HDD with name <{0}> capacity <{1}> spindle speed <{2}> " +
                                                "and cache size <{3}> already exists!",
                                        hddBarracuda.getName(),
                                        hddBarracuda.getCapacity(),
                                        hddBarracuda.getSpindleSpeed(),
                                        hddBarracuda.getCacheSize()
                                )
                        ))
                );

        final Optional<Hdd> optHdd = hddRepository.findById(saved.getId());
        assertThat(optHdd).isPresent();
        final Hdd hdd = optHdd.get();
        assertThat(hdd.getName())
                .isEqualTo(hddP300.getName());
        assertThat(hdd.getVendor().getId())
                .isEqualTo(vendorToshiba.getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(vendorToshiba.getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(connectorSata2.getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(connectorSata2.getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(powerConnectorMolex.getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(powerConnectorMolex.getName());
        assertThat(hdd.getExpansionBayFormat().getId())
                .isEqualTo(format35.getId());
        assertThat(hdd.getExpansionBayFormat().getName())
                .isEqualTo(format35.getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddP300.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddP300.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddP300.getWritingSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddP300.getCacheSize());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddP300.getSpindleSpeed());
    }

    @Test
    void update_withNonExistentEntity_shouldReturnUpdatedEntity() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        final Hdd saved = hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final HddRequestDto dto = HddRequestDto.builder()
                .name(newName)
                .vendorId(vendorSeagate.getId())
                .connectorId(connectorSata3.getId())
                .powerConnectorId(powerConnectorFdd.getId())
                .expansionBayFormatId(format25.getId())
                .capacity(hddBarracuda.getCapacity())
                .readingSpeed(hddBarracuda.getReadingSpeed())
                .writingSpeed(hddBarracuda.getWritingSpeed())
                .spindleSpeed(hddBarracuda.getSpindleSpeed())
                .cacheSize(hddBarracuda.getCacheSize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_HDDS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(newName)),
                        jsonPath("$.vendor.id", is(vendorSeagate.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorSeagate.getName())),
                        jsonPath("$.connector.id", is(connectorSata3.getId().toString())),
                        jsonPath("$.connector.name", is(connectorSata3.getName())),
                        jsonPath("$.powerConnector.id", is(powerConnectorFdd.getId().toString())),
                        jsonPath("$.powerConnector.name", is(powerConnectorFdd.getName())),
                        jsonPath("$.expansionBayFormat.id", is(format25.getId().toString())),
                        jsonPath("$.expansionBayFormat.name", is(format25.getName())),
                        jsonPath("$.capacity", is(hddBarracuda.getCapacity())),
                        jsonPath("$.readingSpeed", is(hddBarracuda.getReadingSpeed())),
                        jsonPath("$.writingSpeed", is(hddBarracuda.getWritingSpeed())),
                        jsonPath("$.spindleSpeed", is(hddBarracuda.getSpindleSpeed())),
                        jsonPath("$.cacheSize", is(hddBarracuda.getCacheSize()))
                );

        final Optional<Hdd> optHdd = hddRepository.findById(saved.getId());
        assertThat(optHdd).isPresent();
        final Hdd hdd = optHdd.get();
        assertThat(hdd.getName())
                .isEqualTo(newName);
        assertThat(hdd.getVendor().getId())
                .isEqualTo(vendorSeagate.getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(vendorSeagate.getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(connectorSata3.getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(connectorSata3.getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(powerConnectorFdd.getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(powerConnectorFdd.getName());
        assertThat(hdd.getExpansionBayFormat().getId())
                .isEqualTo(format25.getId());
        assertThat(hdd.getExpansionBayFormat().getName())
                .isEqualTo(format25.getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddBarracuda.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddBarracuda.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddBarracuda.getWritingSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddBarracuda.getCacheSize());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddBarracuda.getSpindleSpeed());
    }

    @Test
    void update_withNonExistentVendorId_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        final Hdd saved = hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final UUID nonExistentVendorId = UUID.randomUUID();
        final HddRequestDto dto = HddRequestDto.builder()
                .name(newName)
                .vendorId(nonExistentVendorId)
                .connectorId(connectorSata3.getId())
                .powerConnectorId(powerConnectorFdd.getId())
                .expansionBayFormatId(format25.getId())
                .capacity(hddBarracuda.getCapacity())
                .readingSpeed(hddBarracuda.getReadingSpeed())
                .writingSpeed(hddBarracuda.getWritingSpeed())
                .spindleSpeed(hddBarracuda.getSpindleSpeed())
                .cacheSize(hddBarracuda.getCacheSize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_HDDS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("id")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Vendor with ID = <{0}> not found!",
                                        nonExistentVendorId
                                )
                        ))
                );

        final Optional<Hdd> optHdd = hddRepository.findById(saved.getId());
        assertThat(optHdd).isPresent();
        final Hdd hdd = optHdd.get();
        assertThat(hdd.getName())
                .isEqualTo(hddP300.getName());
        assertThat(hdd.getVendor().getId())
                .isEqualTo(vendorToshiba.getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(vendorToshiba.getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(connectorSata2.getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(connectorSata2.getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(powerConnectorMolex.getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(powerConnectorMolex.getName());
        assertThat(hdd.getExpansionBayFormat().getId())
                .isEqualTo(format35.getId());
        assertThat(hdd.getExpansionBayFormat().getName())
                .isEqualTo(format35.getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddP300.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddP300.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddP300.getWritingSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddP300.getCacheSize());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddP300.getSpindleSpeed());
    }

    @Test
    void update_withNonExistentConnectorId_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        final Hdd saved = hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final UUID nonExistentConnectorId = UUID.randomUUID();
        final HddRequestDto dto = HddRequestDto.builder()
                .name(newName)
                .vendorId(vendorSeagate.getId())
                .connectorId(nonExistentConnectorId)
                .powerConnectorId(powerConnectorFdd.getId())
                .expansionBayFormatId(format25.getId())
                .capacity(hddBarracuda.getCapacity())
                .readingSpeed(hddBarracuda.getReadingSpeed())
                .writingSpeed(hddBarracuda.getWritingSpeed())
                .spindleSpeed(hddBarracuda.getSpindleSpeed())
                .cacheSize(hddBarracuda.getCacheSize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_HDDS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("id")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Storage connector with ID = <{0}> not found!",
                                        nonExistentConnectorId
                                )
                        ))
                );

        final Optional<Hdd> optHdd = hddRepository.findById(saved.getId());
        assertThat(optHdd).isPresent();
        final Hdd hdd = optHdd.get();
        assertThat(hdd.getName())
                .isEqualTo(hddP300.getName());
        assertThat(hdd.getVendor().getId())
                .isEqualTo(vendorToshiba.getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(vendorToshiba.getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(connectorSata2.getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(connectorSata2.getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(powerConnectorMolex.getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(powerConnectorMolex.getName());
        assertThat(hdd.getExpansionBayFormat().getId())
                .isEqualTo(format35.getId());
        assertThat(hdd.getExpansionBayFormat().getName())
                .isEqualTo(format35.getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddP300.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddP300.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddP300.getWritingSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddP300.getCacheSize());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddP300.getSpindleSpeed());
    }

    @Test
    void update_withNonExistentPowerConnectorId_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        final Hdd saved = hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final UUID nonExistentPowerConnectorId = UUID.randomUUID();
        final HddRequestDto dto = HddRequestDto.builder()
                .name(newName)
                .vendorId(vendorSeagate.getId())
                .connectorId(connectorSata3.getId())
                .powerConnectorId(nonExistentPowerConnectorId)
                .expansionBayFormatId(format25.getId())
                .capacity(hddBarracuda.getCapacity())
                .readingSpeed(hddBarracuda.getReadingSpeed())
                .writingSpeed(hddBarracuda.getWritingSpeed())
                .spindleSpeed(hddBarracuda.getSpindleSpeed())
                .cacheSize(hddBarracuda.getCacheSize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_HDDS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("id")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Storage power connector with ID = <{0}> not found!",
                                        nonExistentPowerConnectorId
                                )
                        ))
                );

        final Optional<Hdd> optHdd = hddRepository.findById(saved.getId());
        assertThat(optHdd).isPresent();
        final Hdd hdd = optHdd.get();
        assertThat(hdd.getName())
                .isEqualTo(hddP300.getName());
        assertThat(hdd.getVendor().getId())
                .isEqualTo(vendorToshiba.getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(vendorToshiba.getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(connectorSata2.getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(connectorSata2.getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(powerConnectorMolex.getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(powerConnectorMolex.getName());
        assertThat(hdd.getExpansionBayFormat().getId())
                .isEqualTo(format35.getId());
        assertThat(hdd.getExpansionBayFormat().getName())
                .isEqualTo(format35.getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddP300.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddP300.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddP300.getWritingSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddP300.getCacheSize());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddP300.getSpindleSpeed());
    }

    @Test
    void update_withNonExistentExpansionBayFormatId_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        final Hdd saved = hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final UUID nonExistentExpansionBayFormatId = UUID.randomUUID();
        final HddRequestDto dto = HddRequestDto.builder()
                .name(newName)
                .vendorId(vendorSeagate.getId())
                .connectorId(connectorSata3.getId())
                .powerConnectorId(powerConnectorFdd.getId())
                .expansionBayFormatId(nonExistentExpansionBayFormatId)
                .capacity(hddBarracuda.getCapacity())
                .readingSpeed(hddBarracuda.getReadingSpeed())
                .writingSpeed(hddBarracuda.getWritingSpeed())
                .spindleSpeed(hddBarracuda.getSpindleSpeed())
                .cacheSize(hddBarracuda.getCacheSize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_HDDS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("id")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Expansion bay format with ID = <{0}> not found!",
                                        nonExistentExpansionBayFormatId
                                )
                        ))
                );

        final Optional<Hdd> optHdd = hddRepository.findById(saved.getId());
        assertThat(optHdd).isPresent();
        final Hdd hdd = optHdd.get();
        assertThat(hdd.getName())
                .isEqualTo(hddP300.getName());
        assertThat(hdd.getVendor().getId())
                .isEqualTo(vendorToshiba.getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(vendorToshiba.getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(connectorSata2.getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(connectorSata2.getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(powerConnectorMolex.getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(powerConnectorMolex.getName());
        assertThat(hdd.getExpansionBayFormat().getId())
                .isEqualTo(format35.getId());
        assertThat(hdd.getExpansionBayFormat().getName())
                .isEqualTo(format35.getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddP300.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddP300.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddP300.getWritingSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddP300.getCacheSize());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddP300.getSpindleSpeed());
    }

    @Test
    void update_withExistentEntity_shouldReturnError() throws Exception {
        // given
        hddRepository.save(
                mapper.convertFromDto(hddP300)
        );
        assertThat(hddRepository.findAll()).hasSize(1);
        final HddRequestDto dto = HddRequestDto.builder()
                .name(hddP300.getName())
                .vendorId(vendorToshiba.getId())
                .connectorId(connectorSata2.getId())
                .powerConnectorId(powerConnectorMolex.getId())
                .expansionBayFormatId(format25.getId())
                .capacity(hddP300.getCapacity())
                .readingSpeed(hddP300.getReadingSpeed())
                .writingSpeed(hddP300.getWritingSpeed())
                .spindleSpeed(hddP300.getSpindleSpeed())
                .cacheSize(hddP300.getCacheSize())
                .build();

        final Hdd saved = hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        );
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_HDDS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames",
                                contains("name", "capacity", "spindleSpeed", "cacheSize")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "HDD with name <{0}> capacity <{1}> spindle speed <{2}> " +
                                                "and cache size <{3}> already exists!",
                                        hddP300.getName(),
                                        hddP300.getCapacity(),
                                        hddP300.getSpindleSpeed(),
                                        hddP300.getCacheSize()
                                )
                        ))
                );

        final Optional<Hdd> optHdd = hddRepository.findById(saved.getId());
        assertThat(optHdd).isPresent();
        final Hdd hdd = optHdd.get();
        assertThat(hdd.getName())
                .isEqualTo(hddBarracuda.getName());
        assertThat(hdd.getVendor().getId())
                .isEqualTo(vendorSeagate.getId());
        assertThat(hdd.getVendor().getName())
                .isEqualTo(vendorSeagate.getName());
        assertThat(hdd.getConnector().getId())
                .isEqualTo(connectorSata3.getId());
        assertThat(hdd.getConnector().getName())
                .isEqualTo(connectorSata3.getName());
        assertThat(hdd.getPowerConnector().getId())
                .isEqualTo(powerConnectorFdd.getId());
        assertThat(hdd.getPowerConnector().getName())
                .isEqualTo(powerConnectorFdd.getName());
        assertThat(hdd.getExpansionBayFormat().getId())
                .isEqualTo(format25.getId());
        assertThat(hdd.getExpansionBayFormat().getName())
                .isEqualTo(format25.getName());
        assertThat(hdd.getCapacity())
                .isEqualTo(hddBarracuda.getCapacity());
        assertThat(hdd.getReadingSpeed())
                .isEqualTo(hddBarracuda.getReadingSpeed());
        assertThat(hdd.getWritingSpeed())
                .isEqualTo(hddBarracuda.getWritingSpeed());
        assertThat(hdd.getCacheSize())
                .isEqualTo(hddBarracuda.getCacheSize());
        assertThat(hdd.getSpindleSpeed())
                .isEqualTo(hddBarracuda.getSpindleSpeed());

    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID hddBarracudaId = hddRepository.save(
                mapper.convertFromDto(hddBarracuda)
        ).getId();
        assertThat(hddRepository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_HDDS + "/{id}",
                hddBarracudaId
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(hddRepository.findAll()).isEmpty();
    }
}
