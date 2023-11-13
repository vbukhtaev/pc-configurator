package ru.bukhtaev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.dto.mapper.ISsdMapper;
import ru.bukhtaev.dto.request.SsdRequestDto;
import ru.bukhtaev.model.Ssd;
import ru.bukhtaev.model.StorageConnector;
import ru.bukhtaev.model.StoragePowerConnector;
import ru.bukhtaev.model.Vendor;
import ru.bukhtaev.repository.ISsdRepository;
import ru.bukhtaev.repository.IStorageConnectorRepository;
import ru.bukhtaev.repository.IStoragePowerConnectorRepository;
import ru.bukhtaev.repository.IVendorRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.SsdRestController.URL_API_V1_SSDS;

/**
 * Интеграционные тесты для CRUD операций над SSD накопителями.
 */
class SsdRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO SSD накопителей.
     */
    @Autowired
    private ISsdMapper mapper;

    /**
     * Репозиторий SSD накопителей.
     */
    @Autowired
    private ISsdRepository ssdRepository;

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

    private SsdRequestDto ssdS700;
    private SsdRequestDto ssd980;

    private Vendor vendorHp;
    private Vendor vendorSamsung;

    private StorageConnector connectorSata3;
    private StorageConnector connectorM2;

    private StoragePowerConnector powerConnectorFdd;

    @BeforeEach
    void setUp() {
        vendorHp = vendorRepository.save(
                Vendor.builder()
                        .name("HP")
                        .build()
        );
        vendorSamsung = vendorRepository.save(
                Vendor.builder()
                        .name("Samsung")
                        .build()
        );

        connectorSata3 = connectorRepository.save(
                StorageConnector.builder()
                        .name("SATA 3")
                        .build()
        );
        connectorM2 = connectorRepository.save(
                StorageConnector.builder()
                        .name("M.2")
                        .build()
        );

        powerConnectorFdd = powerConnectorRepository.save(
                StoragePowerConnector.builder()
                        .name("FDD")
                        .build()
        );

        ssdS700 = SsdRequestDto.builder()
                .name("S700")
                .vendorId(vendorHp.getId())
                .connectorId(connectorSata3.getId())
                .powerConnectorId(powerConnectorFdd.getId())
                .capacity(120)
                .readingSpeed(550)
                .writingSpeed(480)
                .build();
        ssd980 = SsdRequestDto.builder()
                .name("980")
                .vendorId(vendorSamsung.getId())
                .connectorId(connectorM2.getId())
                .powerConnectorId(null)
                .capacity(1000)
                .readingSpeed(3500)
                .writingSpeed(3000)
                .build();
    }

    @AfterEach
    void tearDown() {
        ssdRepository.deleteAll();
        vendorRepository.deleteAll();
        connectorRepository.deleteAll();
        powerConnectorRepository.deleteAll();
    }

    @Test
    void getAll_shouldReturnAllEntities() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssdS700)
        );
        ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_SSDS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),

                        jsonPath("$[0].name", is(ssdS700.getName())),
                        jsonPath("$[0].vendor.id", is(vendorHp.getId().toString())),
                        jsonPath("$[0].vendor.name", is(vendorHp.getName())),
                        jsonPath("$[0].connector.id", is(connectorSata3.getId().toString())),
                        jsonPath("$[0].connector.name", is(connectorSata3.getName())),
                        jsonPath("$[0].powerConnector.id", is(powerConnectorFdd.getId().toString())),
                        jsonPath("$[0].powerConnector.name", is(powerConnectorFdd.getName())),
                        jsonPath("$[0].capacity", is(ssdS700.getCapacity())),
                        jsonPath("$[0].readingSpeed", is(ssdS700.getReadingSpeed())),
                        jsonPath("$[0].writingSpeed", is(ssdS700.getWritingSpeed())),

                        jsonPath("$[1].name", is(ssd980.getName())),
                        jsonPath("$[1].vendor.id", is(vendorSamsung.getId().toString())),
                        jsonPath("$[1].vendor.name", is(vendorSamsung.getName())),
                        jsonPath("$[1].connector.id", is(connectorM2.getId().toString())),
                        jsonPath("$[1].connector.name", is(connectorM2.getName())),
                        jsonPath("$[1].powerConnector", nullValue()),
                        jsonPath("$[1].capacity", is(ssd980.getCapacity())),
                        jsonPath("$[1].readingSpeed", is(ssd980.getReadingSpeed())),
                        jsonPath("$[1].writingSpeed", is(ssd980.getWritingSpeed()))
                );
    }

    @Test
    void getAll_withPagination_shouldReturnAllEntitiesAsPage() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssdS700)
        );
        ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_SSDS + "/pageable")
                .params(SSD_PAGE_REQUEST_PARAMS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.content", hasSize(2)),

                        jsonPath("$.content[0].name", is(ssdS700.getName())),
                        jsonPath("$.content[0].vendor.id", is(vendorHp.getId().toString())),
                        jsonPath("$.content[0].vendor.name", is(vendorHp.getName())),
                        jsonPath("$.content[0].connector.id", is(connectorSata3.getId().toString())),
                        jsonPath("$.content[0].connector.name", is(connectorSata3.getName())),
                        jsonPath("$.content[0].powerConnector.id", is(powerConnectorFdd.getId().toString())),
                        jsonPath("$.content[0].powerConnector.name", is(powerConnectorFdd.getName())),
                        jsonPath("$.content[0].capacity", is(ssdS700.getCapacity())),
                        jsonPath("$.content[0].readingSpeed", is(ssdS700.getReadingSpeed())),
                        jsonPath("$.content[0].writingSpeed", is(ssdS700.getWritingSpeed())),

                        jsonPath("$.content[1].name", is(ssd980.getName())),
                        jsonPath("$.content[1].vendor.id", is(vendorSamsung.getId().toString())),
                        jsonPath("$.content[1].vendor.name", is(vendorSamsung.getName())),
                        jsonPath("$.content[1].connector.id", is(connectorM2.getId().toString())),
                        jsonPath("$.content[1].connector.name", is(connectorM2.getName())),
                        jsonPath("$.content[1].powerConnector", nullValue()),
                        jsonPath("$.content[1].capacity", is(ssd980.getCapacity())),
                        jsonPath("$.content[1].readingSpeed", is(ssd980.getReadingSpeed())),
                        jsonPath("$.content[1].writingSpeed", is(ssd980.getWritingSpeed()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final Ssd saved = ssdRepository.save(
                mapper.convertFromDto(ssdS700)
        );
        assertThat(ssdRepository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_SSDS + "/{id}",
                saved.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(ssdS700.getName())),
                        jsonPath("$.vendor.id", is(vendorHp.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorHp.getName())),
                        jsonPath("$.connector.id", is(connectorSata3.getId().toString())),
                        jsonPath("$.connector.name", is(connectorSata3.getName())),
                        jsonPath("$.powerConnector.id", is(powerConnectorFdd.getId().toString())),
                        jsonPath("$.powerConnector.name", is(powerConnectorFdd.getName())),
                        jsonPath("$.capacity", is(ssdS700.getCapacity())),
                        jsonPath("$.readingSpeed", is(ssdS700.getReadingSpeed())),
                        jsonPath("$.writingSpeed", is(ssdS700.getWritingSpeed()))
                );
    }

    @Test
    void getById_withNonExistentId_shouldReturnError() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssdS700)
        );
        assertThat(ssdRepository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_SSDS + "/{id}",
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
                                        "SSD with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentEntity_shouldReturnCreatedEntity() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(ssdS700);
        final var requestBuilder = post(URL_API_V1_SSDS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(ssdS700.getName())),
                        jsonPath("$.vendor.id", is(vendorHp.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorHp.getName())),
                        jsonPath("$.connector.id", is(connectorSata3.getId().toString())),
                        jsonPath("$.connector.name", is(connectorSata3.getName())),
                        jsonPath("$.powerConnector.id", is(powerConnectorFdd.getId().toString())),
                        jsonPath("$.powerConnector.name", is(powerConnectorFdd.getName())),
                        jsonPath("$.capacity", is(ssdS700.getCapacity())),
                        jsonPath("$.readingSpeed", is(ssdS700.getReadingSpeed())),
                        jsonPath("$.writingSpeed", is(ssdS700.getWritingSpeed()))
                );

        final List<Ssd> ssds = ssdRepository.findAll();
        assertThat(ssds).hasSize(2);
        final Ssd ssd = ssds.get(1);
        assertThat(ssd.getId()).isNotNull();
        assertThat(ssd.getName())
                .isEqualTo(ssdS700.getName());
        assertThat(ssd.getVendor().getId())
                .isEqualTo(vendorHp.getId());
        assertThat(ssd.getVendor().getName())
                .isEqualTo(vendorHp.getName());
        assertThat(ssd.getConnector().getId())
                .isEqualTo(connectorSata3.getId());
        assertThat(ssd.getConnector().getName())
                .isEqualTo(connectorSata3.getName());
        assertThat(ssd.getPowerConnector().getId())
                .isEqualTo(powerConnectorFdd.getId());
        assertThat(ssd.getPowerConnector().getName())
                .isEqualTo(powerConnectorFdd.getName());
        assertThat(ssd.getCapacity())
                .isEqualTo(ssdS700.getCapacity());
        assertThat(ssd.getReadingSpeed())
                .isEqualTo(ssdS700.getReadingSpeed());
        assertThat(ssd.getWritingSpeed())
                .isEqualTo(ssdS700.getWritingSpeed());
    }

    @Test
    void create_withNonExistentVendorId_shouldReturnError() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(1);
        final UUID nonExistentVendorId = UUID.randomUUID();
        ssdS700.setVendorId(nonExistentVendorId);
        final String jsonRequest = objectMapper.writeValueAsString(ssdS700);
        final var requestBuilder = post(URL_API_V1_SSDS)
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

        assertThat(ssdRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withNonExistentConnectorId_shouldReturnError() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(1);
        final UUID nonExistentConnectorId = UUID.randomUUID();
        ssdS700.setConnectorId(nonExistentConnectorId);
        final String jsonRequest = objectMapper.writeValueAsString(ssdS700);
        final var requestBuilder = post(URL_API_V1_SSDS)
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

        assertThat(ssdRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withNonExistentPowerConnectorId_shouldReturnError() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(1);
        final UUID nonExistentPowerConnectorId = UUID.randomUUID();
        ssdS700.setPowerConnectorId(nonExistentPowerConnectorId);
        final String jsonRequest = objectMapper.writeValueAsString(ssdS700);
        final var requestBuilder = post(URL_API_V1_SSDS)
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

        assertThat(ssdRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectVendorParam_shouldReturnError() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(1);
        ssdS700.setVendorId(null);
        final String jsonRequest = objectMapper.writeValueAsString(ssdS700);
        final var requestBuilder = post(URL_API_V1_SSDS)
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

        assertThat(ssdRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectConnectorParam_shouldReturnError() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(1);
        ssdS700.setConnectorId(null);
        final String jsonRequest = objectMapper.writeValueAsString(ssdS700);
        final var requestBuilder = post(URL_API_V1_SSDS)
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

        assertThat(ssdRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withExistentEntity_shouldReturnError() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssdS700)
        );
        assertThat(ssdRepository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(ssdS700);
        final var requestBuilder = post(URL_API_V1_SSDS)
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
                                contains("name", "capacity")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "SSD with name <{0}> and capacity <{1}> already exists!",
                                        ssdS700.getName(),
                                        ssdS700.getCapacity()
                                )
                        ))
                );

        assertThat(ssdRepository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentEntity_shouldReturnReplacedEntity() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssdS700)
        );
        final Ssd saved = ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final SsdRequestDto dto = SsdRequestDto.builder()
                .name(newName)
                .vendorId(vendorHp.getId())
                .connectorId(connectorSata3.getId())
                .powerConnectorId(powerConnectorFdd.getId())
                .capacity(ssdS700.getCapacity())
                .readingSpeed(ssdS700.getReadingSpeed())
                .writingSpeed(ssdS700.getWritingSpeed())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_SSDS + "/{id}",
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
                        jsonPath("$.vendor.id", is(vendorHp.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorHp.getName())),
                        jsonPath("$.connector.id", is(connectorSata3.getId().toString())),
                        jsonPath("$.connector.name", is(connectorSata3.getName())),
                        jsonPath("$.powerConnector.id", is(powerConnectorFdd.getId().toString())),
                        jsonPath("$.powerConnector.name", is(powerConnectorFdd.getName())),
                        jsonPath("$.capacity", is(ssdS700.getCapacity())),
                        jsonPath("$.readingSpeed", is(ssdS700.getReadingSpeed())),
                        jsonPath("$.writingSpeed", is(ssdS700.getWritingSpeed()))
                );

        final Optional<Ssd> optSsd = ssdRepository.findById(saved.getId());
        assertThat(optSsd).isPresent();
        final Ssd ssd = optSsd.get();
        assertThat(ssd.getName())
                .isEqualTo(newName);
        assertThat(ssd.getVendor().getId())
                .isEqualTo(vendorHp.getId());
        assertThat(ssd.getVendor().getName())
                .isEqualTo(vendorHp.getName());
        assertThat(ssd.getConnector().getId())
                .isEqualTo(connectorSata3.getId());
        assertThat(ssd.getConnector().getName())
                .isEqualTo(connectorSata3.getName());
        assertThat(ssd.getPowerConnector().getId())
                .isEqualTo(powerConnectorFdd.getId());
        assertThat(ssd.getPowerConnector().getName())
                .isEqualTo(powerConnectorFdd.getName());
        assertThat(ssd.getCapacity())
                .isEqualTo(ssdS700.getCapacity());
        assertThat(ssd.getReadingSpeed())
                .isEqualTo(ssdS700.getReadingSpeed());
        assertThat(ssd.getWritingSpeed())
                .isEqualTo(ssdS700.getWritingSpeed());
    }

    @Test
    void replace_withNonExistentVendorId_shouldReturnError() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssdS700)
        );
        final Ssd saved = ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final UUID nonExistentVendorId = UUID.randomUUID();
        final SsdRequestDto dto = SsdRequestDto.builder()
                .name(newName)
                .vendorId(nonExistentVendorId)
                .connectorId(connectorSata3.getId())
                .powerConnectorId(powerConnectorFdd.getId())
                .capacity(ssdS700.getCapacity())
                .readingSpeed(ssdS700.getReadingSpeed())
                .writingSpeed(ssdS700.getWritingSpeed())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_SSDS + "/{id}",
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

        final Optional<Ssd> optSsd = ssdRepository.findById(saved.getId());
        assertThat(optSsd).isPresent();
        final Ssd ssd = optSsd.get();
        assertThat(ssd.getName())
                .isEqualTo(ssd980.getName());
        assertThat(ssd.getVendor().getId())
                .isEqualTo(vendorSamsung.getId());
        assertThat(ssd.getVendor().getName())
                .isEqualTo(vendorSamsung.getName());
        assertThat(ssd.getConnector().getId())
                .isEqualTo(connectorM2.getId());
        assertThat(ssd.getConnector().getName())
                .isEqualTo(connectorM2.getName());
        assertThat(ssd.getPowerConnector()).isNull();
        assertThat(ssd.getCapacity())
                .isEqualTo(ssd980.getCapacity());
        assertThat(ssd.getReadingSpeed())
                .isEqualTo(ssd980.getReadingSpeed());
        assertThat(ssd.getWritingSpeed())
                .isEqualTo(ssd980.getWritingSpeed());
    }

    @Test
    void replace_withNonExistentConnectorId_shouldReturnError() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssdS700)
        );
        final Ssd saved = ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final UUID nonExistentConnectorId = UUID.randomUUID();
        final SsdRequestDto dto = SsdRequestDto.builder()
                .name(newName)
                .vendorId(vendorHp.getId())
                .connectorId(nonExistentConnectorId)
                .powerConnectorId(powerConnectorFdd.getId())
                .capacity(ssdS700.getCapacity())
                .readingSpeed(ssdS700.getReadingSpeed())
                .writingSpeed(ssdS700.getWritingSpeed())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_SSDS + "/{id}",
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

        final Optional<Ssd> optSsd = ssdRepository.findById(saved.getId());
        assertThat(optSsd).isPresent();
        final Ssd ssd = optSsd.get();
        assertThat(ssd.getName())
                .isEqualTo(ssd980.getName());
        assertThat(ssd.getVendor().getId())
                .isEqualTo(vendorSamsung.getId());
        assertThat(ssd.getVendor().getName())
                .isEqualTo(vendorSamsung.getName());
        assertThat(ssd.getConnector().getId())
                .isEqualTo(connectorM2.getId());
        assertThat(ssd.getConnector().getName())
                .isEqualTo(connectorM2.getName());
        assertThat(ssd.getPowerConnector()).isNull();
        assertThat(ssd.getCapacity())
                .isEqualTo(ssd980.getCapacity());
        assertThat(ssd.getReadingSpeed())
                .isEqualTo(ssd980.getReadingSpeed());
        assertThat(ssd.getWritingSpeed())
                .isEqualTo(ssd980.getWritingSpeed());
    }

    @Test
    void replace_withNonExistentPowerConnectorId_shouldReturnError() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssdS700)
        );
        final Ssd saved = ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final UUID nonExistentPowerConnectorId = UUID.randomUUID();
        final SsdRequestDto dto = SsdRequestDto.builder()
                .name(newName)
                .vendorId(vendorHp.getId())
                .connectorId(connectorSata3.getId())
                .powerConnectorId(nonExistentPowerConnectorId)
                .capacity(ssdS700.getCapacity())
                .readingSpeed(ssdS700.getReadingSpeed())
                .writingSpeed(ssdS700.getWritingSpeed())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_SSDS + "/{id}",
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

        final Optional<Ssd> optSsd = ssdRepository.findById(saved.getId());
        assertThat(optSsd).isPresent();
        final Ssd ssd = optSsd.get();
        assertThat(ssd.getName())
                .isEqualTo(ssd980.getName());
        assertThat(ssd.getVendor().getId())
                .isEqualTo(vendorSamsung.getId());
        assertThat(ssd.getVendor().getName())
                .isEqualTo(vendorSamsung.getName());
        assertThat(ssd.getConnector().getId())
                .isEqualTo(connectorM2.getId());
        assertThat(ssd.getConnector().getName())
                .isEqualTo(connectorM2.getName());
        assertThat(ssd.getPowerConnector()).isNull();
        assertThat(ssd.getCapacity())
                .isEqualTo(ssd980.getCapacity());
        assertThat(ssd.getReadingSpeed())
                .isEqualTo(ssd980.getReadingSpeed());
        assertThat(ssd.getWritingSpeed())
                .isEqualTo(ssd980.getWritingSpeed());
    }

    @Test
    void replace_withIncorrectVendorParam_shouldReturnError() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssdS700)
        );
        final Ssd saved = ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final SsdRequestDto dto = SsdRequestDto.builder()
                .name(newName)
                .vendorId(null)
                .connectorId(connectorSata3.getId())
                .powerConnectorId(powerConnectorFdd.getId())
                .capacity(ssdS700.getCapacity())
                .readingSpeed(ssdS700.getReadingSpeed())
                .writingSpeed(ssdS700.getWritingSpeed())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_SSDS + "/{id}",
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

        final Optional<Ssd> optSsd = ssdRepository.findById(saved.getId());
        assertThat(optSsd).isPresent();
        final Ssd ssd = optSsd.get();
        assertThat(ssd.getName())
                .isEqualTo(ssd980.getName());
        assertThat(ssd.getVendor().getId())
                .isEqualTo(vendorSamsung.getId());
        assertThat(ssd.getVendor().getName())
                .isEqualTo(vendorSamsung.getName());
        assertThat(ssd.getConnector().getId())
                .isEqualTo(connectorM2.getId());
        assertThat(ssd.getConnector().getName())
                .isEqualTo(connectorM2.getName());
        assertThat(ssd.getPowerConnector()).isNull();
        assertThat(ssd.getCapacity())
                .isEqualTo(ssd980.getCapacity());
        assertThat(ssd.getReadingSpeed())
                .isEqualTo(ssd980.getReadingSpeed());
        assertThat(ssd.getWritingSpeed())
                .isEqualTo(ssd980.getWritingSpeed());
    }

    @Test
    void replace_withIncorrectConnectorParam_shouldReturnError() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssdS700)
        );
        final Ssd saved = ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final SsdRequestDto dto = SsdRequestDto.builder()
                .name(newName)
                .vendorId(vendorHp.getId())
                .connectorId(null)
                .powerConnectorId(powerConnectorFdd.getId())
                .capacity(ssdS700.getCapacity())
                .readingSpeed(ssdS700.getReadingSpeed())
                .writingSpeed(ssdS700.getWritingSpeed())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_SSDS + "/{id}",
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

        final Optional<Ssd> optSsd = ssdRepository.findById(saved.getId());
        assertThat(optSsd).isPresent();
        final Ssd ssd = optSsd.get();
        assertThat(ssd.getName())
                .isEqualTo(ssd980.getName());
        assertThat(ssd.getVendor().getId())
                .isEqualTo(vendorSamsung.getId());
        assertThat(ssd.getVendor().getName())
                .isEqualTo(vendorSamsung.getName());
        assertThat(ssd.getConnector().getId())
                .isEqualTo(connectorM2.getId());
        assertThat(ssd.getConnector().getName())
                .isEqualTo(connectorM2.getName());
        assertThat(ssd.getPowerConnector()).isNull();
        assertThat(ssd.getCapacity())
                .isEqualTo(ssd980.getCapacity());
        assertThat(ssd.getReadingSpeed())
                .isEqualTo(ssd980.getReadingSpeed());
        assertThat(ssd.getWritingSpeed())
                .isEqualTo(ssd980.getWritingSpeed());
    }

    @Test
    void replace_withExistentEntity_shouldReturnError() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssdS700)
        );
        final Ssd saved = ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(2);
        final SsdRequestDto dto = SsdRequestDto.builder()
                .name(ssdS700.getName())
                .vendorId(vendorHp.getId())
                .connectorId(connectorSata3.getId())
                .powerConnectorId(powerConnectorFdd.getId())
                .capacity(ssdS700.getCapacity())
                .readingSpeed(ssdS700.getReadingSpeed())
                .writingSpeed(ssdS700.getWritingSpeed())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_SSDS + "/{id}",
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
                                contains("name", "capacity")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "SSD with name <{0}> and capacity <{1}> already exists!",
                                        ssdS700.getName(),
                                        ssdS700.getCapacity()
                                )
                        ))
                );

        final Optional<Ssd> optSsd = ssdRepository.findById(saved.getId());
        assertThat(optSsd).isPresent();
        final Ssd ssd = optSsd.get();
        assertThat(ssd.getName())
                .isEqualTo(ssd980.getName());
        assertThat(ssd.getVendor().getId())
                .isEqualTo(vendorSamsung.getId());
        assertThat(ssd.getVendor().getName())
                .isEqualTo(vendorSamsung.getName());
        assertThat(ssd.getConnector().getId())
                .isEqualTo(connectorM2.getId());
        assertThat(ssd.getConnector().getName())
                .isEqualTo(connectorM2.getName());
        assertThat(ssd.getPowerConnector()).isNull();
        assertThat(ssd.getCapacity())
                .isEqualTo(ssd980.getCapacity());
        assertThat(ssd.getReadingSpeed())
                .isEqualTo(ssd980.getReadingSpeed());
        assertThat(ssd.getWritingSpeed())
                .isEqualTo(ssd980.getWritingSpeed());
    }

    @Test
    void update_withNonExistentEntity_shouldReturnUpdatedEntity() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssdS700)
        );
        final Ssd saved = ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final SsdRequestDto dto = SsdRequestDto.builder()
                .name(newName)
                .vendorId(vendorHp.getId())
                .connectorId(connectorSata3.getId())
                .powerConnectorId(powerConnectorFdd.getId())
                .capacity(ssdS700.getCapacity())
                .readingSpeed(ssdS700.getReadingSpeed())
                .writingSpeed(ssdS700.getWritingSpeed())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_SSDS + "/{id}",
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
                        jsonPath("$.vendor.id", is(vendorHp.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorHp.getName())),
                        jsonPath("$.connector.id", is(connectorSata3.getId().toString())),
                        jsonPath("$.connector.name", is(connectorSata3.getName())),
                        jsonPath("$.powerConnector.id", is(powerConnectorFdd.getId().toString())),
                        jsonPath("$.powerConnector.name", is(powerConnectorFdd.getName())),
                        jsonPath("$.capacity", is(ssdS700.getCapacity())),
                        jsonPath("$.readingSpeed", is(ssdS700.getReadingSpeed())),
                        jsonPath("$.writingSpeed", is(ssdS700.getWritingSpeed()))
                );

        final Optional<Ssd> optSsd = ssdRepository.findById(saved.getId());
        assertThat(optSsd).isPresent();
        final Ssd ssd = optSsd.get();
        assertThat(ssd.getName())
                .isEqualTo(newName);
        assertThat(ssd.getVendor().getId())
                .isEqualTo(vendorHp.getId());
        assertThat(ssd.getVendor().getName())
                .isEqualTo(vendorHp.getName());
        assertThat(ssd.getConnector().getId())
                .isEqualTo(connectorSata3.getId());
        assertThat(ssd.getConnector().getName())
                .isEqualTo(connectorSata3.getName());
        assertThat(ssd.getPowerConnector().getId())
                .isEqualTo(powerConnectorFdd.getId());
        assertThat(ssd.getPowerConnector().getName())
                .isEqualTo(powerConnectorFdd.getName());
        assertThat(ssd.getCapacity())
                .isEqualTo(ssdS700.getCapacity());
        assertThat(ssd.getReadingSpeed())
                .isEqualTo(ssdS700.getReadingSpeed());
        assertThat(ssd.getWritingSpeed())
                .isEqualTo(ssdS700.getWritingSpeed());
    }

    @Test
    void update_withNonExistentVendorId_shouldReturnError() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssdS700)
        );
        final Ssd saved = ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final UUID nonExistentVendorId = UUID.randomUUID();
        final SsdRequestDto dto = SsdRequestDto.builder()
                .name(newName)
                .vendorId(nonExistentVendorId)
                .connectorId(connectorSata3.getId())
                .powerConnectorId(powerConnectorFdd.getId())
                .capacity(ssdS700.getCapacity())
                .readingSpeed(ssdS700.getReadingSpeed())
                .writingSpeed(ssdS700.getWritingSpeed())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_SSDS + "/{id}",
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

        final Optional<Ssd> optSsd = ssdRepository.findById(saved.getId());
        assertThat(optSsd).isPresent();
        final Ssd ssd = optSsd.get();
        assertThat(ssd.getName())
                .isEqualTo(ssd980.getName());
        assertThat(ssd.getVendor().getId())
                .isEqualTo(vendorSamsung.getId());
        assertThat(ssd.getVendor().getName())
                .isEqualTo(vendorSamsung.getName());
        assertThat(ssd.getConnector().getId())
                .isEqualTo(connectorM2.getId());
        assertThat(ssd.getConnector().getName())
                .isEqualTo(connectorM2.getName());
        assertThat(ssd.getPowerConnector()).isNull();
        assertThat(ssd.getCapacity())
                .isEqualTo(ssd980.getCapacity());
        assertThat(ssd.getReadingSpeed())
                .isEqualTo(ssd980.getReadingSpeed());
        assertThat(ssd.getWritingSpeed())
                .isEqualTo(ssd980.getWritingSpeed());
    }

    @Test
    void update_withNonExistentConnectorId_shouldReturnError() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssdS700)
        );
        final Ssd saved = ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final UUID nonExistentConnectorId = UUID.randomUUID();
        final SsdRequestDto dto = SsdRequestDto.builder()
                .name(newName)
                .vendorId(vendorHp.getId())
                .connectorId(nonExistentConnectorId)
                .powerConnectorId(powerConnectorFdd.getId())
                .capacity(ssdS700.getCapacity())
                .readingSpeed(ssdS700.getReadingSpeed())
                .writingSpeed(ssdS700.getWritingSpeed())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_SSDS + "/{id}",
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

        final Optional<Ssd> optSsd = ssdRepository.findById(saved.getId());
        assertThat(optSsd).isPresent();
        final Ssd ssd = optSsd.get();
        assertThat(ssd.getName())
                .isEqualTo(ssd980.getName());
        assertThat(ssd.getVendor().getId())
                .isEqualTo(vendorSamsung.getId());
        assertThat(ssd.getVendor().getName())
                .isEqualTo(vendorSamsung.getName());
        assertThat(ssd.getConnector().getId())
                .isEqualTo(connectorM2.getId());
        assertThat(ssd.getConnector().getName())
                .isEqualTo(connectorM2.getName());
        assertThat(ssd.getPowerConnector()).isNull();
        assertThat(ssd.getCapacity())
                .isEqualTo(ssd980.getCapacity());
        assertThat(ssd.getReadingSpeed())
                .isEqualTo(ssd980.getReadingSpeed());
        assertThat(ssd.getWritingSpeed())
                .isEqualTo(ssd980.getWritingSpeed());
    }

    @Test
    void update_withNonExistentPowerConnectorId_shouldReturnError() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssdS700)
        );
        final Ssd saved = ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(2);
        final String newName = "SkyHawk";
        final UUID nonExistentPowerConnectorId = UUID.randomUUID();
        final SsdRequestDto dto = SsdRequestDto.builder()
                .name(newName)
                .vendorId(vendorHp.getId())
                .connectorId(connectorSata3.getId())
                .powerConnectorId(nonExistentPowerConnectorId)
                .capacity(ssdS700.getCapacity())
                .readingSpeed(ssdS700.getReadingSpeed())
                .writingSpeed(ssdS700.getWritingSpeed())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_SSDS + "/{id}",
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

        final Optional<Ssd> optSsd = ssdRepository.findById(saved.getId());
        assertThat(optSsd).isPresent();
        final Ssd ssd = optSsd.get();
        assertThat(ssd.getName())
                .isEqualTo(ssd980.getName());
        assertThat(ssd.getVendor().getId())
                .isEqualTo(vendorSamsung.getId());
        assertThat(ssd.getVendor().getName())
                .isEqualTo(vendorSamsung.getName());
        assertThat(ssd.getConnector().getId())
                .isEqualTo(connectorM2.getId());
        assertThat(ssd.getConnector().getName())
                .isEqualTo(connectorM2.getName());
        assertThat(ssd.getPowerConnector()).isNull();
        assertThat(ssd.getCapacity())
                .isEqualTo(ssd980.getCapacity());
        assertThat(ssd.getReadingSpeed())
                .isEqualTo(ssd980.getReadingSpeed());
        assertThat(ssd.getWritingSpeed())
                .isEqualTo(ssd980.getWritingSpeed());
    }

    @Test
    void update_withExistentEntity_shouldReturnError() throws Exception {
        // given
        ssdRepository.save(
                mapper.convertFromDto(ssd980)
        );
        assertThat(ssdRepository.findAll()).hasSize(1);
        final SsdRequestDto dto = SsdRequestDto.builder()
                .name(ssd980.getName())
                .vendorId(vendorSamsung.getId())
                .connectorId(connectorM2.getId())
                .powerConnectorId(null)
                .capacity(ssd980.getCapacity())
                .readingSpeed(ssd980.getReadingSpeed())
                .writingSpeed(ssd980.getWritingSpeed())
                .build();

        final Ssd saved = ssdRepository.save(
                mapper.convertFromDto(ssdS700)
        );
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_SSDS + "/{id}",
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
                                contains("name", "capacity")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "SSD with name <{0}> and capacity <{1}> already exists!",
                                        ssd980.getName(),
                                        ssd980.getCapacity()
                                )
                        ))
                );

        final Optional<Ssd> optSsd = ssdRepository.findById(saved.getId());
        assertThat(optSsd).isPresent();
        final Ssd ssd = optSsd.get();
        assertThat(ssd.getName())
                .isEqualTo(ssdS700.getName());
        assertThat(ssd.getVendor().getId())
                .isEqualTo(vendorHp.getId());
        assertThat(ssd.getVendor().getName())
                .isEqualTo(vendorHp.getName());
        assertThat(ssd.getConnector().getId())
                .isEqualTo(connectorSata3.getId());
        assertThat(ssd.getConnector().getName())
                .isEqualTo(connectorSata3.getName());
        assertThat(ssd.getPowerConnector().getId())
                .isEqualTo(powerConnectorFdd.getId());
        assertThat(ssd.getPowerConnector().getName())
                .isEqualTo(powerConnectorFdd.getName());
        assertThat(ssd.getCapacity())
                .isEqualTo(ssdS700.getCapacity());
        assertThat(ssd.getReadingSpeed())
                .isEqualTo(ssdS700.getReadingSpeed());
        assertThat(ssd.getWritingSpeed())
                .isEqualTo(ssdS700.getWritingSpeed());

    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID ssdS700Id = ssdRepository.save(
                mapper.convertFromDto(ssdS700)
        ).getId();
        assertThat(ssdRepository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_SSDS + "/{id}",
                ssdS700Id
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(ssdRepository.findAll()).isEmpty();
    }
}
