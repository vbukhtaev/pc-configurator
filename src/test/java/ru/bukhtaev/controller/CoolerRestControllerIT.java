package ru.bukhtaev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.dto.mapper.ICoolerMapper;
import ru.bukhtaev.dto.request.CoolerRequestDto;
import ru.bukhtaev.model.*;
import ru.bukhtaev.repository.*;
import ru.bukhtaev.service.TransactionService;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.CoolerRestController.URL_API_V1_COOLERS;

/**
 * Интеграционные тесты для CRUD операций над процессорными кулерами.
 */
class CoolerRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO процессорных кулеров.
     */
    @Autowired
    private ICoolerMapper mapper;

    /**
     * Репозиторий процессорных кулеров.
     */
    @Autowired
    private ICoolerRepository coolerRepository;

    /**
     * Репозиторий вендоров.
     */
    @Autowired
    private IVendorRepository vendorRepository;

    /**
     * Репозиторий размеров вентиляторов.
     */
    @Autowired
    private IFanSizeRepository fanSizeRepository;

    /**
     * Репозиторий коннекторов питания вентиляторов.
     */
    @Autowired
    private IFanPowerConnectorRepository powerConnectorRepository;

    /**
     * Репозиторий сокетов.
     */
    @Autowired
    private ISocketRepository socketRepository;

    /**
     * Утилитный сервис выполнения кода в транзакции.
     */
    @Autowired
    private TransactionService transactionService;

    private CoolerRequestDto coolerGammaxx400;
    private CoolerRequestDto coolerNhd15;

    private FanPowerConnector connector3Pin;
    private FanPowerConnector connector4Pin;

    private Socket socketLga1700;
    private Socket socketLga2011V3;
    private Socket socketAm5;

    private Vendor vendorDeepCool;
    private Vendor vendorNoctua;

    private FanSize size120;
    private FanSize size150;

    @BeforeEach
    void setUp() {
        vendorDeepCool = vendorRepository.save(
                Vendor.builder()
                        .name("DEEPCOOL")
                        .build()
        );
        vendorNoctua = vendorRepository.save(
                Vendor.builder()
                        .name("Noctua")
                        .build()
        );

        size120 = fanSizeRepository.save(
                FanSize.builder()
                        .length(120)
                        .width(120)
                        .height(25)
                        .build()
        );
        size150 = fanSizeRepository.save(
                FanSize.builder()
                        .length(150)
                        .width(140)
                        .height(25)
                        .build()
        );

        connector3Pin = powerConnectorRepository.save(
                FanPowerConnector.builder()
                        .name("3 pin")
                        .build()
        );
        connector4Pin = powerConnectorRepository.save(
                FanPowerConnector.builder()
                        .name("4 pin")
                        .build()
        );

        socketLga1700 = socketRepository.save(
                Socket.builder()
                        .name("LGA 1700")
                        .build()
        );
        socketLga2011V3 = socketRepository.save(
                Socket.builder()
                        .name("LGA 2011 V3")
                        .build()
        );
        socketAm5 = socketRepository.save(
                Socket.builder()
                        .name("AM5")
                        .build()
        );

        coolerGammaxx400 = CoolerRequestDto.builder()
                .name("GAMMAXX 400 V2")
                .height(155)
                .powerDissipation(180)
                .vendorId(vendorDeepCool.getId())
                .fanSizeId(size120.getId())
                .powerConnectorId(connector3Pin.getId())
                .supportedSocketIds(Set.of(
                        socketLga1700.getId(),
                        socketAm5.getId()
                ))
                .build();
        coolerNhd15 = CoolerRequestDto.builder()
                .name("NH-D15")
                .height(165)
                .powerDissipation(250)
                .vendorId(vendorNoctua.getId())
                .fanSizeId(size150.getId())
                .powerConnectorId(connector4Pin.getId())
                .supportedSocketIds(Set.of(
                        socketAm5.getId(),
                        socketLga2011V3.getId()
                ))
                .build();
    }

    @AfterEach
    void tearDown() {
        coolerRepository.deleteAll();
        socketRepository.deleteAll();
        vendorRepository.deleteAll();
        fanSizeRepository.deleteAll();
        powerConnectorRepository.deleteAll();
    }

    @Test
    void getAll_shouldReturnAllEntities() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_COOLERS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),

                        jsonPath("$[0].name", is(coolerGammaxx400.getName())),
                        jsonPath("$[0].height", is(coolerGammaxx400.getHeight())),
                        jsonPath("$[0].powerDissipation", is(coolerGammaxx400.getPowerDissipation())),
                        jsonPath("$[0].vendor.id", is(vendorDeepCool.getId().toString())),
                        jsonPath("$[0].vendor.name", is(vendorDeepCool.getName())),
                        jsonPath("$[0].powerConnector.id", is(connector3Pin.getId().toString())),
                        jsonPath("$[0].powerConnector.name", is(connector3Pin.getName())),
                        jsonPath("$[0].fanSize.id", is(size120.getId().toString())),
                        jsonPath("$[0].fanSize.length", is(size120.getLength())),
                        jsonPath("$[0].fanSize.width", is(size120.getWidth())),
                        jsonPath("$[0].fanSize.height", is(size120.getHeight())),
                        jsonPath("$[0].supportedSockets", hasSize(2)),
                        jsonPath("$[0].supportedSockets[*].id").value(containsInAnyOrder(
                                socketLga1700.getId().toString(),
                                socketAm5.getId().toString()
                        )),
                        jsonPath("$[0].supportedSockets[*].name").value(containsInAnyOrder(
                                socketLga1700.getName(),
                                socketAm5.getName()
                        )),

                        jsonPath("$[1].name", is(coolerNhd15.getName())),
                        jsonPath("$[1].height", is(coolerNhd15.getHeight())),
                        jsonPath("$[1].powerDissipation", is(coolerNhd15.getPowerDissipation())),
                        jsonPath("$[1].vendor.id", is(vendorNoctua.getId().toString())),
                        jsonPath("$[1].vendor.name", is(vendorNoctua.getName())),
                        jsonPath("$[1].powerConnector.id", is(connector4Pin.getId().toString())),
                        jsonPath("$[1].powerConnector.name", is(connector4Pin.getName())),
                        jsonPath("$[1].fanSize.id", is(size150.getId().toString())),
                        jsonPath("$[1].fanSize.length", is(size150.getLength())),
                        jsonPath("$[1].fanSize.width", is(size150.getWidth())),
                        jsonPath("$[1].fanSize.height", is(size150.getHeight())),
                        jsonPath("$[1].supportedSockets", hasSize(2)),
                        jsonPath("$[1].supportedSockets[*].id").value(containsInAnyOrder(
                                socketLga2011V3.getId().toString(),
                                socketAm5.getId().toString()
                        )),
                        jsonPath("$[1].supportedSockets[*].name").value(containsInAnyOrder(
                                socketLga2011V3.getName(),
                                socketAm5.getName()
                        ))
                );
    }

    @Test
    void getAll_withPagination_shouldReturnAllEntitiesAsPage() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_COOLERS + "/pageable")
                .params(COOLER_PAGE_REQUEST_PARAMS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.content", hasSize(2)),

                        jsonPath("$.content[0].name", is(coolerGammaxx400.getName())),
                        jsonPath("$.content[0].height", is(coolerGammaxx400.getHeight())),
                        jsonPath("$.content[0].powerDissipation", is(coolerGammaxx400.getPowerDissipation())),
                        jsonPath("$.content[0].vendor.id", is(vendorDeepCool.getId().toString())),
                        jsonPath("$.content[0].vendor.name", is(vendorDeepCool.getName())),
                        jsonPath("$.content[0].powerConnector.id", is(connector3Pin.getId().toString())),
                        jsonPath("$.content[0].powerConnector.name", is(connector3Pin.getName())),
                        jsonPath("$.content[0].fanSize.id", is(size120.getId().toString())),
                        jsonPath("$.content[0].fanSize.length", is(size120.getLength())),
                        jsonPath("$.content[0].fanSize.width", is(size120.getWidth())),
                        jsonPath("$.content[0].fanSize.height", is(size120.getHeight())),
                        jsonPath("$.content[0].supportedSockets", hasSize(2)),
                        jsonPath("$.content[0].supportedSockets[*].id").value(containsInAnyOrder(
                                socketLga1700.getId().toString(),
                                socketAm5.getId().toString()
                        )),
                        jsonPath("$.content[0].supportedSockets[*].name").value(containsInAnyOrder(
                                socketLga1700.getName(),
                                socketAm5.getName()
                        )),

                        jsonPath("$.content[1].name", is(coolerNhd15.getName())),
                        jsonPath("$.content[1].height", is(coolerNhd15.getHeight())),
                        jsonPath("$.content[1].powerDissipation", is(coolerNhd15.getPowerDissipation())),
                        jsonPath("$.content[1].vendor.id", is(vendorNoctua.getId().toString())),
                        jsonPath("$.content[1].vendor.name", is(vendorNoctua.getName())),
                        jsonPath("$.content[1].powerConnector.id", is(connector4Pin.getId().toString())),
                        jsonPath("$.content[1].powerConnector.name", is(connector4Pin.getName())),
                        jsonPath("$.content[1].fanSize.id", is(size150.getId().toString())),
                        jsonPath("$.content[1].fanSize.length", is(size150.getLength())),
                        jsonPath("$.content[1].fanSize.width", is(size150.getWidth())),
                        jsonPath("$.content[1].fanSize.height", is(size150.getHeight())),
                        jsonPath("$.content[1].supportedSockets", hasSize(2)),
                        jsonPath("$.content[1].supportedSockets[*].id").value(containsInAnyOrder(
                                socketLga2011V3.getId().toString(),
                                socketAm5.getId().toString()
                        )),
                        jsonPath("$.content[1].supportedSockets[*].name").value(containsInAnyOrder(
                                socketLga2011V3.getName(),
                                socketAm5.getName()
                        ))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final Cooler saved = coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        assertThat(coolerRepository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_COOLERS + "/{id}",
                saved.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),

                        jsonPath("$.name", is(coolerGammaxx400.getName())),
                        jsonPath("$.height", is(coolerGammaxx400.getHeight())),
                        jsonPath("$.powerDissipation", is(coolerGammaxx400.getPowerDissipation())),
                        jsonPath("$.vendor.id", is(vendorDeepCool.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorDeepCool.getName())),
                        jsonPath("$.powerConnector.id", is(connector3Pin.getId().toString())),
                        jsonPath("$.powerConnector.name", is(connector3Pin.getName())),
                        jsonPath("$.fanSize.id", is(size120.getId().toString())),
                        jsonPath("$.fanSize.length", is(size120.getLength())),
                        jsonPath("$.fanSize.width", is(size120.getWidth())),
                        jsonPath("$.fanSize.height", is(size120.getHeight())),
                        jsonPath("$.supportedSockets", hasSize(2)),
                        jsonPath("$.supportedSockets[*].id").value(containsInAnyOrder(
                                socketLga1700.getId().toString(),
                                socketAm5.getId().toString()
                        )),
                        jsonPath("$.supportedSockets[*].name").value(containsInAnyOrder(
                                socketLga1700.getName(),
                                socketAm5.getName()
                        ))
                );
    }

    @Test
    void getById_withNonExistentId_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        assertThat(coolerRepository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_COOLERS + "/{id}",
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
                                        "Cooler with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentEntity_shouldReturnCreatedEntity() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(coolerGammaxx400);
        final var requestBuilder = post(URL_API_V1_COOLERS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(coolerGammaxx400.getName())),
                        jsonPath("$.height", is(coolerGammaxx400.getHeight())),
                        jsonPath("$.powerDissipation", is(coolerGammaxx400.getPowerDissipation())),
                        jsonPath("$.vendor.id", is(vendorDeepCool.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorDeepCool.getName())),
                        jsonPath("$.powerConnector.id", is(connector3Pin.getId().toString())),
                        jsonPath("$.powerConnector.name", is(connector3Pin.getName())),
                        jsonPath("$.fanSize.id", is(size120.getId().toString())),
                        jsonPath("$.fanSize.length", is(size120.getLength())),
                        jsonPath("$.fanSize.width", is(size120.getWidth())),
                        jsonPath("$.fanSize.height", is(size120.getHeight())),
                        jsonPath("$.supportedSockets", hasSize(2)),
                        jsonPath("$.supportedSockets[*].id").value(containsInAnyOrder(
                                socketLga1700.getId().toString(),
                                socketAm5.getId().toString()
                        )),
                        jsonPath("$.supportedSockets[*].name").value(containsInAnyOrder(
                                socketLga1700.getName(),
                                socketAm5.getName()
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final List<Cooler> coolers = coolerRepository.findAll();
            assertThat(coolers).hasSize(2);
            final Cooler cooler = coolers.get(1);
            assertThat(cooler.getId()).isNotNull();
            assertThat(cooler.getName())
                    .isEqualTo(coolerGammaxx400.getName());
            assertThat(cooler.getHeight())
                    .isEqualTo(coolerGammaxx400.getHeight());
            assertThat(cooler.getPowerDissipation())
                    .isEqualTo(coolerGammaxx400.getPowerDissipation());
            assertThat(cooler.getVendor().getId())
                    .isEqualTo(vendorDeepCool.getId());
            assertThat(cooler.getVendor().getName())
                    .isEqualTo(vendorDeepCool.getName());
            assertThat(cooler.getPowerConnector().getId())
                    .isEqualTo(connector3Pin.getId());
            assertThat(cooler.getPowerConnector().getName())
                    .isEqualTo(connector3Pin.getName());
            assertThat(cooler.getFanSize().getId())
                    .isEqualTo(size120.getId());
            assertThat(cooler.getFanSize().getLength())
                    .isEqualTo(size120.getLength());
            assertThat(cooler.getFanSize().getWidth())
                    .isEqualTo(size120.getWidth());
            assertThat(cooler.getFanSize().getHeight())
                    .isEqualTo(size120.getHeight());
            assertThat(cooler.getSupportedSockets())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            socketLga1700,
                            socketAm5
                    );
        });
    }

    @Test
    void create_withNonExistentVendorId_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(1);
        final UUID nonExistentVendorId = UUID.randomUUID();
        coolerGammaxx400.setVendorId(nonExistentVendorId);
        final String jsonRequest = objectMapper.writeValueAsString(coolerGammaxx400);
        final var requestBuilder = post(URL_API_V1_COOLERS)
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

        assertThat(coolerRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withNonExistentFanSizeId_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(1);
        final UUID nonExistentFanSizeId = UUID.randomUUID();
        coolerGammaxx400.setFanSizeId(nonExistentFanSizeId);
        final String jsonRequest = objectMapper.writeValueAsString(coolerGammaxx400);
        final var requestBuilder = post(URL_API_V1_COOLERS)
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
                                        "Fan size with ID = <{0}> not found!",
                                        nonExistentFanSizeId
                                )
                        ))
                );

        assertThat(coolerRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withNonExistentPowerConnectorId_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(1);
        final UUID nonExistentPowerConnectorId = UUID.randomUUID();
        coolerGammaxx400.setPowerConnectorId(nonExistentPowerConnectorId);
        final String jsonRequest = objectMapper.writeValueAsString(coolerGammaxx400);
        final var requestBuilder = post(URL_API_V1_COOLERS)
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
                                        "Fan power connector with ID = <{0}> not found!",
                                        nonExistentPowerConnectorId
                                )
                        ))
                );

        assertThat(coolerRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withNonExistentSocketId_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(1);
        final UUID nonExistentSocketId = UUID.randomUUID();
        coolerGammaxx400.setSupportedSocketIds(Set.of(nonExistentSocketId));
        final String jsonRequest = objectMapper.writeValueAsString(coolerGammaxx400);
        final var requestBuilder = post(URL_API_V1_COOLERS)
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
                                        "Socket with ID = <{0}> not found!",
                                        nonExistentSocketId
                                )
                        ))
                );

        assertThat(coolerRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectVendorParam_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(1);
        coolerGammaxx400.setVendorId(null);
        final String jsonRequest = objectMapper.writeValueAsString(coolerGammaxx400);
        final var requestBuilder = post(URL_API_V1_COOLERS)
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

        assertThat(coolerRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectFanSizeParam_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(1);
        coolerGammaxx400.setFanSizeId(null);
        final String jsonRequest = objectMapper.writeValueAsString(coolerGammaxx400);
        final var requestBuilder = post(URL_API_V1_COOLERS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("fanSize")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        assertThat(coolerRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectPowerConnectorParam_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(1);
        coolerGammaxx400.setPowerConnectorId(null);
        final String jsonRequest = objectMapper.writeValueAsString(coolerGammaxx400);
        final var requestBuilder = post(URL_API_V1_COOLERS)
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

        assertThat(coolerRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectSupportedSocketIdsParam_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(1);
        coolerGammaxx400.setSupportedSocketIds(null);
        final String jsonRequest = objectMapper.writeValueAsString(coolerGammaxx400);
        final var requestBuilder = post(URL_API_V1_COOLERS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("supportedSockets")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        assertThat(coolerRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withExistentEntity_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        assertThat(coolerRepository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(coolerGammaxx400);
        final var requestBuilder = post(URL_API_V1_COOLERS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("name")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Cooler with name <{0}> already exists!",
                                        coolerGammaxx400.getName()
                                )
                        ))
                );

        assertThat(coolerRepository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentEntity_shouldReturnReplacedEntity() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        final Cooler saved = coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(2);
        final String newName = "NH-U9S";
        final CoolerRequestDto dto = CoolerRequestDto.builder()
                .name(newName)
                .powerDissipation(coolerGammaxx400.getPowerDissipation())
                .height(coolerGammaxx400.getHeight())
                .vendorId(coolerGammaxx400.getVendorId())
                .fanSizeId(coolerGammaxx400.getFanSizeId())
                .powerConnectorId(coolerGammaxx400.getPowerConnectorId())
                .supportedSocketIds(coolerGammaxx400.getSupportedSocketIds())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_COOLERS + "/{id}",
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
                        jsonPath("$.height", is(coolerGammaxx400.getHeight())),
                        jsonPath("$.powerDissipation", is(coolerGammaxx400.getPowerDissipation())),
                        jsonPath("$.vendor.id", is(vendorDeepCool.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorDeepCool.getName())),
                        jsonPath("$.powerConnector.id", is(connector3Pin.getId().toString())),
                        jsonPath("$.powerConnector.name", is(connector3Pin.getName())),
                        jsonPath("$.fanSize.id", is(size120.getId().toString())),
                        jsonPath("$.fanSize.length", is(size120.getLength())),
                        jsonPath("$.fanSize.width", is(size120.getWidth())),
                        jsonPath("$.fanSize.height", is(size120.getHeight())),
                        jsonPath("$.supportedSockets", hasSize(2)),
                        jsonPath("$.supportedSockets[*].id").value(containsInAnyOrder(
                                socketLga1700.getId().toString(),
                                socketAm5.getId().toString()
                        )),
                        jsonPath("$.supportedSockets[*].name").value(containsInAnyOrder(
                                socketLga1700.getName(),
                                socketAm5.getName()
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cooler> optCooler = coolerRepository.findById(saved.getId());
            assertThat(optCooler).isPresent();
            final Cooler cooler = optCooler.get();
            assertThat(cooler.getName())
                    .isEqualTo(newName);
            assertThat(cooler.getHeight())
                    .isEqualTo(coolerGammaxx400.getHeight());
            assertThat(cooler.getPowerDissipation())
                    .isEqualTo(coolerGammaxx400.getPowerDissipation());
            assertThat(cooler.getVendor().getId())
                    .isEqualTo(vendorDeepCool.getId());
            assertThat(cooler.getVendor().getName())
                    .isEqualTo(vendorDeepCool.getName());
            assertThat(cooler.getPowerConnector().getId())
                    .isEqualTo(connector3Pin.getId());
            assertThat(cooler.getPowerConnector().getName())
                    .isEqualTo(connector3Pin.getName());
            assertThat(cooler.getFanSize().getId())
                    .isEqualTo(size120.getId());
            assertThat(cooler.getFanSize().getLength())
                    .isEqualTo(size120.getLength());
            assertThat(cooler.getFanSize().getWidth())
                    .isEqualTo(size120.getWidth());
            assertThat(cooler.getFanSize().getHeight())
                    .isEqualTo(size120.getHeight());
            assertThat(cooler.getSupportedSockets())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            socketLga1700,
                            socketAm5
                    );
        });
    }

    @Test
    void replace_withNonExistentVendorId_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        final Cooler saved = coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(2);
        final String newName = "NH-U9S";
        final UUID nonExistentVendorId = UUID.randomUUID();
        final CoolerRequestDto dto = CoolerRequestDto.builder()
                .name(newName)
                .powerDissipation(coolerGammaxx400.getPowerDissipation())
                .height(coolerGammaxx400.getHeight())
                .vendorId(nonExistentVendorId)
                .fanSizeId(coolerGammaxx400.getFanSizeId())
                .powerConnectorId(coolerGammaxx400.getPowerConnectorId())
                .supportedSocketIds(coolerGammaxx400.getSupportedSocketIds())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_COOLERS + "/{id}",
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

        transactionService.doInTransaction(true, () -> {

            final Optional<Cooler> optCooler = coolerRepository.findById(saved.getId());
            assertThat(optCooler).isPresent();
            final Cooler cooler = optCooler.get();
            assertThat(cooler.getName())
                    .isEqualTo(coolerNhd15.getName());
            assertThat(cooler.getHeight())
                    .isEqualTo(coolerNhd15.getHeight());
            assertThat(cooler.getPowerDissipation())
                    .isEqualTo(coolerNhd15.getPowerDissipation());
            assertThat(cooler.getVendor().getId())
                    .isEqualTo(vendorNoctua.getId());
            assertThat(cooler.getVendor().getName())
                    .isEqualTo(vendorNoctua.getName());
            assertThat(cooler.getPowerConnector().getId())
                    .isEqualTo(connector4Pin.getId());
            assertThat(cooler.getPowerConnector().getName())
                    .isEqualTo(connector4Pin.getName());
            assertThat(cooler.getFanSize().getId())
                    .isEqualTo(size150.getId());
            assertThat(cooler.getFanSize().getLength())
                    .isEqualTo(size150.getLength());
            assertThat(cooler.getFanSize().getWidth())
                    .isEqualTo(size150.getWidth());
            assertThat(cooler.getFanSize().getHeight())
                    .isEqualTo(size150.getHeight());
            assertThat(cooler.getSupportedSockets())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            socketLga2011V3,
                            socketAm5
                    );
        });
    }

    @Test
    void replace_withNonExistentFanSizeId_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        final Cooler saved = coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(2);
        final String newName = "NH-U9S";
        final UUID nonExistentFanSizeId = UUID.randomUUID();
        final CoolerRequestDto dto = CoolerRequestDto.builder()
                .name(newName)
                .powerDissipation(coolerGammaxx400.getPowerDissipation())
                .height(coolerGammaxx400.getHeight())
                .vendorId(coolerGammaxx400.getVendorId())
                .fanSizeId(nonExistentFanSizeId)
                .powerConnectorId(coolerGammaxx400.getPowerConnectorId())
                .supportedSocketIds(coolerGammaxx400.getSupportedSocketIds())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_COOLERS + "/{id}",
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
                                        "Fan size with ID = <{0}> not found!",
                                        nonExistentFanSizeId
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cooler> optCooler = coolerRepository.findById(saved.getId());
            assertThat(optCooler).isPresent();
            final Cooler cooler = optCooler.get();
            assertThat(cooler.getName())
                    .isEqualTo(coolerNhd15.getName());
            assertThat(cooler.getHeight())
                    .isEqualTo(coolerNhd15.getHeight());
            assertThat(cooler.getPowerDissipation())
                    .isEqualTo(coolerNhd15.getPowerDissipation());
            assertThat(cooler.getVendor().getId())
                    .isEqualTo(vendorNoctua.getId());
            assertThat(cooler.getVendor().getName())
                    .isEqualTo(vendorNoctua.getName());
            assertThat(cooler.getPowerConnector().getId())
                    .isEqualTo(connector4Pin.getId());
            assertThat(cooler.getPowerConnector().getName())
                    .isEqualTo(connector4Pin.getName());
            assertThat(cooler.getFanSize().getId())
                    .isEqualTo(size150.getId());
            assertThat(cooler.getFanSize().getLength())
                    .isEqualTo(size150.getLength());
            assertThat(cooler.getFanSize().getWidth())
                    .isEqualTo(size150.getWidth());
            assertThat(cooler.getFanSize().getHeight())
                    .isEqualTo(size150.getHeight());
            assertThat(cooler.getSupportedSockets())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            socketLga2011V3,
                            socketAm5
                    );
        });
    }

    @Test
    void replace_withNonExistentPowerConnectorId_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        final Cooler saved = coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(2);
        final String newName = "NH-U9S";
        final UUID nonExistentPowerConnectorId = UUID.randomUUID();
        final CoolerRequestDto dto = CoolerRequestDto.builder()
                .name(newName)
                .powerDissipation(coolerGammaxx400.getPowerDissipation())
                .height(coolerGammaxx400.getHeight())
                .vendorId(coolerGammaxx400.getVendorId())
                .fanSizeId(coolerGammaxx400.getFanSizeId())
                .powerConnectorId(nonExistentPowerConnectorId)
                .supportedSocketIds(coolerGammaxx400.getSupportedSocketIds())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_COOLERS + "/{id}",
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
                                        "Fan power connector with ID = <{0}> not found!",
                                        nonExistentPowerConnectorId
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cooler> optCooler = coolerRepository.findById(saved.getId());
            assertThat(optCooler).isPresent();
            final Cooler cooler = optCooler.get();
            assertThat(cooler.getName())
                    .isEqualTo(coolerNhd15.getName());
            assertThat(cooler.getHeight())
                    .isEqualTo(coolerNhd15.getHeight());
            assertThat(cooler.getPowerDissipation())
                    .isEqualTo(coolerNhd15.getPowerDissipation());
            assertThat(cooler.getVendor().getId())
                    .isEqualTo(vendorNoctua.getId());
            assertThat(cooler.getVendor().getName())
                    .isEqualTo(vendorNoctua.getName());
            assertThat(cooler.getPowerConnector().getId())
                    .isEqualTo(connector4Pin.getId());
            assertThat(cooler.getPowerConnector().getName())
                    .isEqualTo(connector4Pin.getName());
            assertThat(cooler.getFanSize().getId())
                    .isEqualTo(size150.getId());
            assertThat(cooler.getFanSize().getLength())
                    .isEqualTo(size150.getLength());
            assertThat(cooler.getFanSize().getWidth())
                    .isEqualTo(size150.getWidth());
            assertThat(cooler.getFanSize().getHeight())
                    .isEqualTo(size150.getHeight());
            assertThat(cooler.getSupportedSockets())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            socketLga2011V3,
                            socketAm5
                    );
        });
    }

    @Test
    void replace_withNonExistentSocketId_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        final Cooler saved = coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(2);
        final String newName = "NH-U9S";
        final UUID nonExistentSocketId = UUID.randomUUID();
        final CoolerRequestDto dto = CoolerRequestDto.builder()
                .name(newName)
                .powerDissipation(coolerGammaxx400.getPowerDissipation())
                .height(coolerGammaxx400.getHeight())
                .vendorId(coolerGammaxx400.getVendorId())
                .fanSizeId(coolerGammaxx400.getFanSizeId())
                .powerConnectorId(coolerGammaxx400.getPowerConnectorId())
                .supportedSocketIds(Set.of(nonExistentSocketId))
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_COOLERS + "/{id}",
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
                                        "Socket with ID = <{0}> not found!",
                                        nonExistentSocketId
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cooler> optCooler = coolerRepository.findById(saved.getId());
            assertThat(optCooler).isPresent();
            final Cooler cooler = optCooler.get();
            assertThat(cooler.getName())
                    .isEqualTo(coolerNhd15.getName());
            assertThat(cooler.getHeight())
                    .isEqualTo(coolerNhd15.getHeight());
            assertThat(cooler.getPowerDissipation())
                    .isEqualTo(coolerNhd15.getPowerDissipation());
            assertThat(cooler.getVendor().getId())
                    .isEqualTo(vendorNoctua.getId());
            assertThat(cooler.getVendor().getName())
                    .isEqualTo(vendorNoctua.getName());
            assertThat(cooler.getPowerConnector().getId())
                    .isEqualTo(connector4Pin.getId());
            assertThat(cooler.getPowerConnector().getName())
                    .isEqualTo(connector4Pin.getName());
            assertThat(cooler.getFanSize().getId())
                    .isEqualTo(size150.getId());
            assertThat(cooler.getFanSize().getLength())
                    .isEqualTo(size150.getLength());
            assertThat(cooler.getFanSize().getWidth())
                    .isEqualTo(size150.getWidth());
            assertThat(cooler.getFanSize().getHeight())
                    .isEqualTo(size150.getHeight());
            assertThat(cooler.getSupportedSockets())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            socketLga2011V3,
                            socketAm5
                    );
        });
    }

    @Test
    void replace_withIncorrectVendorParam_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        final Cooler saved = coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(2);
        final String newName = "NH-U9S";
        final CoolerRequestDto dto = CoolerRequestDto.builder()
                .name(newName)
                .powerDissipation(coolerGammaxx400.getPowerDissipation())
                .height(coolerGammaxx400.getHeight())
                .vendorId(null)
                .fanSizeId(coolerGammaxx400.getFanSizeId())
                .powerConnectorId(coolerGammaxx400.getPowerConnectorId())
                .supportedSocketIds(coolerGammaxx400.getSupportedSocketIds())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_COOLERS + "/{id}",
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

        transactionService.doInTransaction(true, () -> {

            final Optional<Cooler> optCooler = coolerRepository.findById(saved.getId());
            assertThat(optCooler).isPresent();
            final Cooler cooler = optCooler.get();
            assertThat(cooler.getName())
                    .isEqualTo(coolerNhd15.getName());
            assertThat(cooler.getHeight())
                    .isEqualTo(coolerNhd15.getHeight());
            assertThat(cooler.getPowerDissipation())
                    .isEqualTo(coolerNhd15.getPowerDissipation());
            assertThat(cooler.getVendor().getId())
                    .isEqualTo(vendorNoctua.getId());
            assertThat(cooler.getVendor().getName())
                    .isEqualTo(vendorNoctua.getName());
            assertThat(cooler.getPowerConnector().getId())
                    .isEqualTo(connector4Pin.getId());
            assertThat(cooler.getPowerConnector().getName())
                    .isEqualTo(connector4Pin.getName());
            assertThat(cooler.getFanSize().getId())
                    .isEqualTo(size150.getId());
            assertThat(cooler.getFanSize().getLength())
                    .isEqualTo(size150.getLength());
            assertThat(cooler.getFanSize().getWidth())
                    .isEqualTo(size150.getWidth());
            assertThat(cooler.getFanSize().getHeight())
                    .isEqualTo(size150.getHeight());
            assertThat(cooler.getSupportedSockets())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            socketLga2011V3,
                            socketAm5
                    );
        });
    }

    @Test
    void replace_withIncorrectFanSizeParam_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        final Cooler saved = coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(2);
        final String newName = "NH-U9S";
        final CoolerRequestDto dto = CoolerRequestDto.builder()
                .name(newName)
                .powerDissipation(coolerGammaxx400.getPowerDissipation())
                .height(coolerGammaxx400.getHeight())
                .vendorId(coolerGammaxx400.getVendorId())
                .fanSizeId(null)
                .powerConnectorId(coolerGammaxx400.getPowerConnectorId())
                .supportedSocketIds(coolerGammaxx400.getSupportedSocketIds())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_COOLERS + "/{id}",
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
                        jsonPath("$.violations[0].paramNames", contains("fanSize")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cooler> optCooler = coolerRepository.findById(saved.getId());
            assertThat(optCooler).isPresent();
            final Cooler cooler = optCooler.get();
            assertThat(cooler.getName())
                    .isEqualTo(coolerNhd15.getName());
            assertThat(cooler.getHeight())
                    .isEqualTo(coolerNhd15.getHeight());
            assertThat(cooler.getPowerDissipation())
                    .isEqualTo(coolerNhd15.getPowerDissipation());
            assertThat(cooler.getVendor().getId())
                    .isEqualTo(vendorNoctua.getId());
            assertThat(cooler.getVendor().getName())
                    .isEqualTo(vendorNoctua.getName());
            assertThat(cooler.getPowerConnector().getId())
                    .isEqualTo(connector4Pin.getId());
            assertThat(cooler.getPowerConnector().getName())
                    .isEqualTo(connector4Pin.getName());
            assertThat(cooler.getFanSize().getId())
                    .isEqualTo(size150.getId());
            assertThat(cooler.getFanSize().getLength())
                    .isEqualTo(size150.getLength());
            assertThat(cooler.getFanSize().getWidth())
                    .isEqualTo(size150.getWidth());
            assertThat(cooler.getFanSize().getHeight())
                    .isEqualTo(size150.getHeight());
            assertThat(cooler.getSupportedSockets())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            socketLga2011V3,
                            socketAm5
                    );
        });
    }

    @Test
    void replace_withIncorrectPowerConnectorParam_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        final Cooler saved = coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(2);
        final String newName = "NH-U9S";
        final CoolerRequestDto dto = CoolerRequestDto.builder()
                .name(newName)
                .powerDissipation(coolerGammaxx400.getPowerDissipation())
                .height(coolerGammaxx400.getHeight())
                .vendorId(coolerGammaxx400.getVendorId())
                .fanSizeId(coolerGammaxx400.getFanSizeId())
                .powerConnectorId(null)
                .supportedSocketIds(coolerGammaxx400.getSupportedSocketIds())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_COOLERS + "/{id}",
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

        transactionService.doInTransaction(true, () -> {

            final Optional<Cooler> optCooler = coolerRepository.findById(saved.getId());
            assertThat(optCooler).isPresent();
            final Cooler cooler = optCooler.get();
            assertThat(cooler.getName())
                    .isEqualTo(coolerNhd15.getName());
            assertThat(cooler.getHeight())
                    .isEqualTo(coolerNhd15.getHeight());
            assertThat(cooler.getPowerDissipation())
                    .isEqualTo(coolerNhd15.getPowerDissipation());
            assertThat(cooler.getVendor().getId())
                    .isEqualTo(vendorNoctua.getId());
            assertThat(cooler.getVendor().getName())
                    .isEqualTo(vendorNoctua.getName());
            assertThat(cooler.getPowerConnector().getId())
                    .isEqualTo(connector4Pin.getId());
            assertThat(cooler.getPowerConnector().getName())
                    .isEqualTo(connector4Pin.getName());
            assertThat(cooler.getFanSize().getId())
                    .isEqualTo(size150.getId());
            assertThat(cooler.getFanSize().getLength())
                    .isEqualTo(size150.getLength());
            assertThat(cooler.getFanSize().getWidth())
                    .isEqualTo(size150.getWidth());
            assertThat(cooler.getFanSize().getHeight())
                    .isEqualTo(size150.getHeight());
            assertThat(cooler.getSupportedSockets())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            socketLga2011V3,
                            socketAm5
                    );
        });
    }

    @Test
    void replace_withIncorrectSupportedSocketIdsParam_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        final Cooler saved = coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(2);
        final String newName = "NH-U9S";
        final CoolerRequestDto dto = CoolerRequestDto.builder()
                .name(newName)
                .powerDissipation(coolerGammaxx400.getPowerDissipation())
                .height(coolerGammaxx400.getHeight())
                .vendorId(coolerGammaxx400.getVendorId())
                .fanSizeId(coolerGammaxx400.getFanSizeId())
                .powerConnectorId(coolerGammaxx400.getPowerConnectorId())
                .supportedSocketIds(null)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_COOLERS + "/{id}",
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
                        jsonPath("$.violations[0].paramNames", contains("supportedSockets")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cooler> optCooler = coolerRepository.findById(saved.getId());
            assertThat(optCooler).isPresent();
            final Cooler cooler = optCooler.get();
            assertThat(cooler.getName())
                    .isEqualTo(coolerNhd15.getName());
            assertThat(cooler.getHeight())
                    .isEqualTo(coolerNhd15.getHeight());
            assertThat(cooler.getPowerDissipation())
                    .isEqualTo(coolerNhd15.getPowerDissipation());
            assertThat(cooler.getVendor().getId())
                    .isEqualTo(vendorNoctua.getId());
            assertThat(cooler.getVendor().getName())
                    .isEqualTo(vendorNoctua.getName());
            assertThat(cooler.getPowerConnector().getId())
                    .isEqualTo(connector4Pin.getId());
            assertThat(cooler.getPowerConnector().getName())
                    .isEqualTo(connector4Pin.getName());
            assertThat(cooler.getFanSize().getId())
                    .isEqualTo(size150.getId());
            assertThat(cooler.getFanSize().getLength())
                    .isEqualTo(size150.getLength());
            assertThat(cooler.getFanSize().getWidth())
                    .isEqualTo(size150.getWidth());
            assertThat(cooler.getFanSize().getHeight())
                    .isEqualTo(size150.getHeight());
            assertThat(cooler.getSupportedSockets())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            socketLga2011V3,
                            socketAm5
                    );
        });
    }

    @Test
    void replace_withExistentEntity_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        final Cooler saved = coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(2);
        final CoolerRequestDto dto = CoolerRequestDto.builder()
                .name(coolerGammaxx400.getName())
                .powerDissipation(coolerGammaxx400.getPowerDissipation())
                .height(coolerGammaxx400.getHeight())
                .vendorId(coolerGammaxx400.getVendorId())
                .fanSizeId(coolerGammaxx400.getFanSizeId())
                .powerConnectorId(coolerGammaxx400.getPowerConnectorId())
                .supportedSocketIds(coolerGammaxx400.getSupportedSocketIds())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_COOLERS + "/{id}",
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
                        jsonPath("$.violations[0].paramNames", contains("name")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Cooler with name <{0}> already exists!",
                                        coolerGammaxx400.getName()
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cooler> optCooler = coolerRepository.findById(saved.getId());
            assertThat(optCooler).isPresent();
            final Cooler cooler = optCooler.get();
            assertThat(cooler.getName())
                    .isEqualTo(coolerNhd15.getName());
            assertThat(cooler.getHeight())
                    .isEqualTo(coolerNhd15.getHeight());
            assertThat(cooler.getPowerDissipation())
                    .isEqualTo(coolerNhd15.getPowerDissipation());
            assertThat(cooler.getVendor().getId())
                    .isEqualTo(vendorNoctua.getId());
            assertThat(cooler.getVendor().getName())
                    .isEqualTo(vendorNoctua.getName());
            assertThat(cooler.getPowerConnector().getId())
                    .isEqualTo(connector4Pin.getId());
            assertThat(cooler.getPowerConnector().getName())
                    .isEqualTo(connector4Pin.getName());
            assertThat(cooler.getFanSize().getId())
                    .isEqualTo(size150.getId());
            assertThat(cooler.getFanSize().getLength())
                    .isEqualTo(size150.getLength());
            assertThat(cooler.getFanSize().getWidth())
                    .isEqualTo(size150.getWidth());
            assertThat(cooler.getFanSize().getHeight())
                    .isEqualTo(size150.getHeight());
            assertThat(cooler.getSupportedSockets())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            socketLga2011V3,
                            socketAm5
                    );
        });
    }

    @Test
    void update_withNonExistentEntity_shouldReturnUpdatedEntity() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        final Cooler saved = coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(2);
        final String newName = "NH-U9S";
        final CoolerRequestDto dto = CoolerRequestDto.builder()
                .name(newName)
                .powerDissipation(coolerGammaxx400.getPowerDissipation())
                .height(coolerGammaxx400.getHeight())
                .vendorId(coolerGammaxx400.getVendorId())
                .fanSizeId(coolerGammaxx400.getFanSizeId())
                .powerConnectorId(coolerGammaxx400.getPowerConnectorId())
                .supportedSocketIds(coolerGammaxx400.getSupportedSocketIds())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_COOLERS + "/{id}",
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
                        jsonPath("$.height", is(coolerGammaxx400.getHeight())),
                        jsonPath("$.powerDissipation", is(coolerGammaxx400.getPowerDissipation())),
                        jsonPath("$.vendor.id", is(vendorDeepCool.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorDeepCool.getName())),
                        jsonPath("$.powerConnector.id", is(connector3Pin.getId().toString())),
                        jsonPath("$.powerConnector.name", is(connector3Pin.getName())),
                        jsonPath("$.fanSize.id", is(size120.getId().toString())),
                        jsonPath("$.fanSize.length", is(size120.getLength())),
                        jsonPath("$.fanSize.width", is(size120.getWidth())),
                        jsonPath("$.fanSize.height", is(size120.getHeight())),
                        jsonPath("$.supportedSockets", hasSize(2)),
                        jsonPath("$.supportedSockets[*].id").value(containsInAnyOrder(
                                socketLga1700.getId().toString(),
                                socketAm5.getId().toString()
                        )),
                        jsonPath("$.supportedSockets[*].name").value(containsInAnyOrder(
                                socketLga1700.getName(),
                                socketAm5.getName()
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cooler> optCooler = coolerRepository.findById(saved.getId());
            assertThat(optCooler).isPresent();
            final Cooler cooler = optCooler.get();
            assertThat(cooler.getName())
                    .isEqualTo(newName);
            assertThat(cooler.getHeight())
                    .isEqualTo(coolerGammaxx400.getHeight());
            assertThat(cooler.getPowerDissipation())
                    .isEqualTo(coolerGammaxx400.getPowerDissipation());
            assertThat(cooler.getVendor().getId())
                    .isEqualTo(vendorDeepCool.getId());
            assertThat(cooler.getVendor().getName())
                    .isEqualTo(vendorDeepCool.getName());
            assertThat(cooler.getPowerConnector().getId())
                    .isEqualTo(connector3Pin.getId());
            assertThat(cooler.getPowerConnector().getName())
                    .isEqualTo(connector3Pin.getName());
            assertThat(cooler.getFanSize().getId())
                    .isEqualTo(size120.getId());
            assertThat(cooler.getFanSize().getLength())
                    .isEqualTo(size120.getLength());
            assertThat(cooler.getFanSize().getWidth())
                    .isEqualTo(size120.getWidth());
            assertThat(cooler.getFanSize().getHeight())
                    .isEqualTo(size120.getHeight());
            assertThat(cooler.getSupportedSockets())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            socketLga1700,
                            socketAm5
                    );
        });
    }

    @Test
    void update_withNonExistentVendorId_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        final Cooler saved = coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(2);
        final String newName = "NH-U9S";
        final UUID nonExistentVendorId = UUID.randomUUID();
        final CoolerRequestDto dto = CoolerRequestDto.builder()
                .name(newName)
                .powerDissipation(coolerGammaxx400.getPowerDissipation())
                .height(coolerGammaxx400.getHeight())
                .vendorId(nonExistentVendorId)
                .fanSizeId(coolerGammaxx400.getFanSizeId())
                .powerConnectorId(coolerGammaxx400.getPowerConnectorId())
                .supportedSocketIds(coolerGammaxx400.getSupportedSocketIds())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_COOLERS + "/{id}",
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

        transactionService.doInTransaction(true, () -> {

            final Optional<Cooler> optCooler = coolerRepository.findById(saved.getId());
            assertThat(optCooler).isPresent();
            final Cooler cooler = optCooler.get();
            assertThat(cooler.getName())
                    .isEqualTo(coolerNhd15.getName());
            assertThat(cooler.getHeight())
                    .isEqualTo(coolerNhd15.getHeight());
            assertThat(cooler.getPowerDissipation())
                    .isEqualTo(coolerNhd15.getPowerDissipation());
            assertThat(cooler.getVendor().getId())
                    .isEqualTo(vendorNoctua.getId());
            assertThat(cooler.getVendor().getName())
                    .isEqualTo(vendorNoctua.getName());
            assertThat(cooler.getPowerConnector().getId())
                    .isEqualTo(connector4Pin.getId());
            assertThat(cooler.getPowerConnector().getName())
                    .isEqualTo(connector4Pin.getName());
            assertThat(cooler.getFanSize().getId())
                    .isEqualTo(size150.getId());
            assertThat(cooler.getFanSize().getLength())
                    .isEqualTo(size150.getLength());
            assertThat(cooler.getFanSize().getWidth())
                    .isEqualTo(size150.getWidth());
            assertThat(cooler.getFanSize().getHeight())
                    .isEqualTo(size150.getHeight());
            assertThat(cooler.getSupportedSockets())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            socketLga2011V3,
                            socketAm5
                    );
        });
    }

    @Test
    void update_withNonExistentFanSizeId_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        final Cooler saved = coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(2);
        final String newName = "NH-U9S";
        final UUID nonExistentFanSizeId = UUID.randomUUID();
        final CoolerRequestDto dto = CoolerRequestDto.builder()
                .name(newName)
                .powerDissipation(coolerGammaxx400.getPowerDissipation())
                .height(coolerGammaxx400.getHeight())
                .vendorId(coolerGammaxx400.getVendorId())
                .fanSizeId(nonExistentFanSizeId)
                .powerConnectorId(coolerGammaxx400.getPowerConnectorId())
                .supportedSocketIds(coolerGammaxx400.getSupportedSocketIds())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_COOLERS + "/{id}",
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
                                        "Fan size with ID = <{0}> not found!",
                                        nonExistentFanSizeId
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cooler> optCooler = coolerRepository.findById(saved.getId());
            assertThat(optCooler).isPresent();
            final Cooler cooler = optCooler.get();
            assertThat(cooler.getName())
                    .isEqualTo(coolerNhd15.getName());
            assertThat(cooler.getHeight())
                    .isEqualTo(coolerNhd15.getHeight());
            assertThat(cooler.getPowerDissipation())
                    .isEqualTo(coolerNhd15.getPowerDissipation());
            assertThat(cooler.getVendor().getId())
                    .isEqualTo(vendorNoctua.getId());
            assertThat(cooler.getVendor().getName())
                    .isEqualTo(vendorNoctua.getName());
            assertThat(cooler.getPowerConnector().getId())
                    .isEqualTo(connector4Pin.getId());
            assertThat(cooler.getPowerConnector().getName())
                    .isEqualTo(connector4Pin.getName());
            assertThat(cooler.getFanSize().getId())
                    .isEqualTo(size150.getId());
            assertThat(cooler.getFanSize().getLength())
                    .isEqualTo(size150.getLength());
            assertThat(cooler.getFanSize().getWidth())
                    .isEqualTo(size150.getWidth());
            assertThat(cooler.getFanSize().getHeight())
                    .isEqualTo(size150.getHeight());
            assertThat(cooler.getSupportedSockets())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            socketLga2011V3,
                            socketAm5
                    );
        });
    }

    @Test
    void update_withNonExistentPowerConnectorId_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        final Cooler saved = coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(2);
        final String newName = "NH-U9S";
        final UUID nonExistentPowerConnectorId = UUID.randomUUID();
        final CoolerRequestDto dto = CoolerRequestDto.builder()
                .name(newName)
                .powerDissipation(coolerGammaxx400.getPowerDissipation())
                .height(coolerGammaxx400.getHeight())
                .vendorId(coolerGammaxx400.getVendorId())
                .fanSizeId(coolerGammaxx400.getFanSizeId())
                .powerConnectorId(nonExistentPowerConnectorId)
                .supportedSocketIds(coolerGammaxx400.getSupportedSocketIds())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_COOLERS + "/{id}",
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
                                        "Fan power connector with ID = <{0}> not found!",
                                        nonExistentPowerConnectorId
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cooler> optCooler = coolerRepository.findById(saved.getId());
            assertThat(optCooler).isPresent();
            final Cooler cooler = optCooler.get();
            assertThat(cooler.getName())
                    .isEqualTo(coolerNhd15.getName());
            assertThat(cooler.getHeight())
                    .isEqualTo(coolerNhd15.getHeight());
            assertThat(cooler.getPowerDissipation())
                    .isEqualTo(coolerNhd15.getPowerDissipation());
            assertThat(cooler.getVendor().getId())
                    .isEqualTo(vendorNoctua.getId());
            assertThat(cooler.getVendor().getName())
                    .isEqualTo(vendorNoctua.getName());
            assertThat(cooler.getPowerConnector().getId())
                    .isEqualTo(connector4Pin.getId());
            assertThat(cooler.getPowerConnector().getName())
                    .isEqualTo(connector4Pin.getName());
            assertThat(cooler.getFanSize().getId())
                    .isEqualTo(size150.getId());
            assertThat(cooler.getFanSize().getLength())
                    .isEqualTo(size150.getLength());
            assertThat(cooler.getFanSize().getWidth())
                    .isEqualTo(size150.getWidth());
            assertThat(cooler.getFanSize().getHeight())
                    .isEqualTo(size150.getHeight());
            assertThat(cooler.getSupportedSockets())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            socketLga2011V3,
                            socketAm5
                    );
        });
    }

    @Test
    void update_withNonExistentSocketId_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        final Cooler saved = coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(2);
        final String newName = "NH-U9S";
        final UUID nonExistentSocketId = UUID.randomUUID();
        final CoolerRequestDto dto = CoolerRequestDto.builder()
                .name(newName)
                .powerDissipation(coolerGammaxx400.getPowerDissipation())
                .height(coolerGammaxx400.getHeight())
                .vendorId(coolerGammaxx400.getVendorId())
                .fanSizeId(coolerGammaxx400.getFanSizeId())
                .powerConnectorId(coolerGammaxx400.getPowerConnectorId())
                .supportedSocketIds(Set.of(nonExistentSocketId))
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_COOLERS + "/{id}",
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
                                        "Socket with ID = <{0}> not found!",
                                        nonExistentSocketId
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cooler> optCooler = coolerRepository.findById(saved.getId());
            assertThat(optCooler).isPresent();
            final Cooler cooler = optCooler.get();
            assertThat(cooler.getName())
                    .isEqualTo(coolerNhd15.getName());
            assertThat(cooler.getHeight())
                    .isEqualTo(coolerNhd15.getHeight());
            assertThat(cooler.getPowerDissipation())
                    .isEqualTo(coolerNhd15.getPowerDissipation());
            assertThat(cooler.getVendor().getId())
                    .isEqualTo(vendorNoctua.getId());
            assertThat(cooler.getVendor().getName())
                    .isEqualTo(vendorNoctua.getName());
            assertThat(cooler.getPowerConnector().getId())
                    .isEqualTo(connector4Pin.getId());
            assertThat(cooler.getPowerConnector().getName())
                    .isEqualTo(connector4Pin.getName());
            assertThat(cooler.getFanSize().getId())
                    .isEqualTo(size150.getId());
            assertThat(cooler.getFanSize().getLength())
                    .isEqualTo(size150.getLength());
            assertThat(cooler.getFanSize().getWidth())
                    .isEqualTo(size150.getWidth());
            assertThat(cooler.getFanSize().getHeight())
                    .isEqualTo(size150.getHeight());
            assertThat(cooler.getSupportedSockets())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            socketLga2011V3,
                            socketAm5
                    );
        });
    }

    @Test
    void update_withExistentEntity_shouldReturnError() throws Exception {
        // given
        coolerRepository.save(
                mapper.convertFromDto(coolerNhd15)
        );
        assertThat(coolerRepository.findAll()).hasSize(1);
        final CoolerRequestDto dto = CoolerRequestDto.builder()
                .name(coolerNhd15.getName())
                .powerDissipation(coolerNhd15.getPowerDissipation())
                .height(coolerNhd15.getHeight())
                .vendorId(coolerNhd15.getVendorId())
                .fanSizeId(coolerNhd15.getFanSizeId())
                .powerConnectorId(coolerNhd15.getPowerConnectorId())
                .supportedSocketIds(coolerNhd15.getSupportedSocketIds())
                .build();

        final Cooler saved = coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        );
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_COOLERS + "/{id}",
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
                        jsonPath("$.violations[0].paramNames", contains("name")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Cooler with name <{0}> already exists!",
                                        coolerNhd15.getName()
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cooler> optCooler = coolerRepository.findById(saved.getId());
            assertThat(optCooler).isPresent();
            final Cooler cooler = optCooler.get();
            assertThat(cooler.getName())
                    .isEqualTo(coolerGammaxx400.getName());
            assertThat(cooler.getHeight())
                    .isEqualTo(coolerGammaxx400.getHeight());
            assertThat(cooler.getPowerDissipation())
                    .isEqualTo(coolerGammaxx400.getPowerDissipation());
            assertThat(cooler.getVendor().getId())
                    .isEqualTo(vendorDeepCool.getId());
            assertThat(cooler.getVendor().getName())
                    .isEqualTo(vendorDeepCool.getName());
            assertThat(cooler.getPowerConnector().getId())
                    .isEqualTo(connector3Pin.getId());
            assertThat(cooler.getPowerConnector().getName())
                    .isEqualTo(connector3Pin.getName());
            assertThat(cooler.getFanSize().getId())
                    .isEqualTo(size120.getId());
            assertThat(cooler.getFanSize().getLength())
                    .isEqualTo(size120.getLength());
            assertThat(cooler.getFanSize().getWidth())
                    .isEqualTo(size120.getWidth());
            assertThat(cooler.getFanSize().getHeight())
                    .isEqualTo(size120.getHeight());
            assertThat(cooler.getSupportedSockets())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            socketLga1700,
                            socketAm5
                    );
        });
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID coolerGammaxx400Id = coolerRepository.save(
                mapper.convertFromDto(coolerGammaxx400)
        ).getId();
        assertThat(coolerRepository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_COOLERS + "/{id}",
                coolerGammaxx400Id
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(coolerRepository.findAll()).isEmpty();
    }
}
