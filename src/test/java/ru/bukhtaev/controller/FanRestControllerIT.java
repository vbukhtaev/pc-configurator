package ru.bukhtaev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.dto.mapper.IFanMapper;
import ru.bukhtaev.dto.request.FanRequestDto;
import ru.bukhtaev.model.Fan;
import ru.bukhtaev.model.dictionary.FanPowerConnector;
import ru.bukhtaev.model.dictionary.FanSize;
import ru.bukhtaev.model.dictionary.Vendor;
import ru.bukhtaev.repository.dictionary.IFanPowerConnectorRepository;
import ru.bukhtaev.repository.IFanRepository;
import ru.bukhtaev.repository.dictionary.IFanSizeRepository;
import ru.bukhtaev.repository.dictionary.IVendorRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.FanRestController.URL_API_V1_FANS;

/**
 * Интеграционные тесты для CRUD операций над вентиляторами.
 */
class FanRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO вентиляторов.
     */
    @Autowired
    private IFanMapper mapper;

    /**
     * Репозиторий вентиляторов.
     */
    @Autowired
    private IFanRepository fanRepository;

    /**
     * Репозиторий вендоров.
     */
    @Autowired
    private IVendorRepository vendorRepository;

    /**
     * Репозиторий размеров вентиляторов.
     */
    @Autowired
    private IFanSizeRepository sizeRepository;

    /**
     * Репозиторий коннекторов питания вентиляторов.
     */
    @Autowired
    private IFanPowerConnectorRepository powerConnectorRepository;

    private FanRequestDto fanRf120B;
    private FanRequestDto fanPureWings2;

    private Vendor vendorDeepcool;
    private Vendor vendorBeQuiet;

    private FanSize size120;
    private FanSize size140;

    private FanPowerConnector connector3Pin;
    private FanPowerConnector connector4Pin;

    @BeforeEach
    void setUp() {
        vendorDeepcool = vendorRepository.save(
                Vendor.builder()
                        .name("DEEPCOOL")
                        .build()
        );
        vendorBeQuiet = vendorRepository.save(
                Vendor.builder()
                        .name("be quiet!")
                        .build()
        );

        size120 = sizeRepository.save(
                FanSize.builder()
                        .length(120)
                        .width(120)
                        .height(25)
                        .build()
        );
        size140 = sizeRepository.save(
                FanSize.builder()
                        .length(140)
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

        fanRf120B = FanRequestDto.builder()
                .name("RF120B")
                .sizeId(size120.getId())
                .vendorId(vendorDeepcool.getId())
                .powerConnectorId(connector3Pin.getId())
                .build();
        fanPureWings2 = FanRequestDto.builder()
                .name("PURE WINGS 2")
                .sizeId(size140.getId())
                .vendorId(vendorBeQuiet.getId())
                .powerConnectorId(connector4Pin.getId())
                .build();
    }

    @AfterEach
    void tearDown() {
        fanRepository.deleteAll();
        sizeRepository.deleteAll();
        vendorRepository.deleteAll();
        powerConnectorRepository.deleteAll();
    }

    @Test
    void getAll_shouldReturnAllEntities() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanRf120B)
        );
        fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_FANS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),

                        jsonPath("$[0].name", is(fanRf120B.getName())),
                        jsonPath("$[0].vendor.id", is(vendorDeepcool.getId().toString())),
                        jsonPath("$[0].vendor.name", is(vendorDeepcool.getName())),
                        jsonPath("$[0].powerConnector.id", is(connector3Pin.getId().toString())),
                        jsonPath("$[0].powerConnector.name", is(connector3Pin.getName())),
                        jsonPath("$[0].size.id", is(size120.getId().toString())),
                        jsonPath("$[0].size.length", is(size120.getLength())),
                        jsonPath("$[0].size.width", is(size120.getWidth())),
                        jsonPath("$[0].size.height", is(size120.getHeight())),

                        jsonPath("$[1].name", is(fanPureWings2.getName())),
                        jsonPath("$[1].vendor.id", is(vendorBeQuiet.getId().toString())),
                        jsonPath("$[1].vendor.name", is(vendorBeQuiet.getName())),
                        jsonPath("$[1].powerConnector.id", is(connector4Pin.getId().toString())),
                        jsonPath("$[1].powerConnector.name", is(connector4Pin.getName())),
                        jsonPath("$[1].size.id", is(size140.getId().toString())),
                        jsonPath("$[1].size.length", is(size140.getLength())),
                        jsonPath("$[1].size.width", is(size140.getWidth())),
                        jsonPath("$[1].size.height", is(size140.getHeight()))
                );
    }

    @Test
    void getAll_withPagination_shouldReturnAllEntitiesAsPage() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanRf120B)
        );
        fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_FANS + "/pageable")
                .params(FAN_PAGE_REQUEST_PARAMS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.content", hasSize(2)),

                        jsonPath("$.content[0].name", is(fanPureWings2.getName())),
                        jsonPath("$.content[0].vendor.id", is(vendorBeQuiet.getId().toString())),
                        jsonPath("$.content[0].vendor.name", is(vendorBeQuiet.getName())),
                        jsonPath("$.content[0].powerConnector.id", is(connector4Pin.getId().toString())),
                        jsonPath("$.content[0].powerConnector.name", is(connector4Pin.getName())),
                        jsonPath("$.content[0].size.id", is(size140.getId().toString())),
                        jsonPath("$.content[0].size.length", is(size140.getLength())),
                        jsonPath("$.content[0].size.width", is(size140.getWidth())),
                        jsonPath("$.content[0].size.height", is(size140.getHeight())),

                        jsonPath("$.content[1].name", is(fanRf120B.getName())),
                        jsonPath("$.content[1].vendor.id", is(vendorDeepcool.getId().toString())),
                        jsonPath("$.content[1].vendor.name", is(vendorDeepcool.getName())),
                        jsonPath("$.content[1].powerConnector.id", is(connector3Pin.getId().toString())),
                        jsonPath("$.content[1].powerConnector.name", is(connector3Pin.getName())),
                        jsonPath("$.content[1].size.id", is(size120.getId().toString())),
                        jsonPath("$.content[1].size.length", is(size120.getLength())),
                        jsonPath("$.content[1].size.width", is(size120.getWidth())),
                        jsonPath("$.content[1].size.height", is(size120.getHeight()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final Fan saved = fanRepository.save(
                mapper.convertFromDto(fanRf120B)
        );
        assertThat(fanRepository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_FANS + "/{id}",
                saved.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(fanRf120B.getName())),
                        jsonPath("$.vendor.id", is(vendorDeepcool.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorDeepcool.getName())),
                        jsonPath("$.powerConnector.id", is(connector3Pin.getId().toString())),
                        jsonPath("$.powerConnector.name", is(connector3Pin.getName())),
                        jsonPath("$.size.id", is(size120.getId().toString())),
                        jsonPath("$.size.length", is(size120.getLength())),
                        jsonPath("$.size.width", is(size120.getWidth())),
                        jsonPath("$.size.height", is(size120.getHeight()))
                );
    }

    @Test
    void getById_withNonExistentId_shouldReturnError() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanRf120B)
        );
        assertThat(fanRepository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_FANS + "/{id}",
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
                                        "Fan with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentEntity_shouldReturnCreatedEntity() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(fanRf120B);
        final var requestBuilder = post(URL_API_V1_FANS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(fanRf120B.getName())),
                        jsonPath("$.vendor.id", is(vendorDeepcool.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorDeepcool.getName())),
                        jsonPath("$.powerConnector.id", is(connector3Pin.getId().toString())),
                        jsonPath("$.powerConnector.name", is(connector3Pin.getName())),
                        jsonPath("$.size.id", is(size120.getId().toString())),
                        jsonPath("$.size.length", is(size120.getLength())),
                        jsonPath("$.size.width", is(size120.getWidth())),
                        jsonPath("$.size.height", is(size120.getHeight()))
                );

        final List<Fan> fans = fanRepository.findAll();
        assertThat(fans).hasSize(2);
        final Fan fan = fans.get(1);
        assertThat(fan.getId()).isNotNull();
        assertThat(fan.getName())
                .isEqualTo(fanRf120B.getName());
        assertThat(fan.getVendor().getId())
                .isEqualTo(vendorDeepcool.getId());
        assertThat(fan.getVendor().getName())
                .isEqualTo(vendorDeepcool.getName());
        assertThat(fan.getPowerConnector().getId())
                .isEqualTo(connector3Pin.getId());
        assertThat(fan.getPowerConnector().getName())
                .isEqualTo(connector3Pin.getName());
        assertThat(fan.getSize().getId())
                .isEqualTo(size120.getId());
        assertThat(fan.getSize().getLength())
                .isEqualTo(size120.getLength());
        assertThat(fan.getSize().getWidth())
                .isEqualTo(size120.getWidth());
        assertThat(fan.getSize().getHeight())
                .isEqualTo(size120.getHeight());
    }

    @Test
    void create_withNonExistentVendorId_shouldReturnError() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(1);
        final UUID nonExistentVendorId = UUID.randomUUID();
        fanRf120B.setVendorId(nonExistentVendorId);
        final String jsonRequest = objectMapper.writeValueAsString(fanRf120B);
        final var requestBuilder = post(URL_API_V1_FANS)
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

        assertThat(fanRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withNonExistentSizeId_shouldReturnError() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(1);
        final UUID nonExistentSizeId = UUID.randomUUID();
        fanRf120B.setSizeId(nonExistentSizeId);
        final String jsonRequest = objectMapper.writeValueAsString(fanRf120B);
        final var requestBuilder = post(URL_API_V1_FANS)
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
                                        nonExistentSizeId
                                )
                        ))
                );

        assertThat(fanRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withNonExistentPowerConnectorId_shouldReturnError() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(1);
        final UUID nonExistentPowerConnectorId = UUID.randomUUID();
        fanRf120B.setPowerConnectorId(nonExistentPowerConnectorId);
        final String jsonRequest = objectMapper.writeValueAsString(fanRf120B);
        final var requestBuilder = post(URL_API_V1_FANS)
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

        assertThat(fanRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectVendorParam_shouldReturnError() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(1);
        fanRf120B.setVendorId(null);
        final String jsonRequest = objectMapper.writeValueAsString(fanRf120B);
        final var requestBuilder = post(URL_API_V1_FANS)
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

        assertThat(fanRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectSizeParam_shouldReturnError() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(1);
        fanRf120B.setSizeId(null);
        final String jsonRequest = objectMapper.writeValueAsString(fanRf120B);
        final var requestBuilder = post(URL_API_V1_FANS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("size")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        assertThat(fanRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectPowerConnectorParam_shouldReturnError() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(1);
        fanRf120B.setPowerConnectorId(null);
        final String jsonRequest = objectMapper.writeValueAsString(fanRf120B);
        final var requestBuilder = post(URL_API_V1_FANS)
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

        assertThat(fanRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withExistentEntity_shouldReturnError() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanRf120B)
        );
        assertThat(fanRepository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(fanRf120B);
        final var requestBuilder = post(URL_API_V1_FANS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("name", "size")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Fan with name <{0}> and size <{1} x {2} x {3}> already exists!",
                                        fanRf120B.getName(),
                                        size120.getLength(),
                                        size120.getWidth(),
                                        size120.getHeight()
                                )
                        ))
                );

        assertThat(fanRepository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentEntity_shouldReturnReplacedEntity() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanRf120B)
        );
        final Fan saved = fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(2);
        final String newName = "Frost 14";
        final FanRequestDto dto = FanRequestDto.builder()
                .name(newName)
                .sizeId(size120.getId())
                .vendorId(vendorDeepcool.getId())
                .powerConnectorId(connector3Pin.getId())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_FANS + "/{id}",
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
                        jsonPath("$.vendor.id", is(vendorDeepcool.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorDeepcool.getName())),
                        jsonPath("$.powerConnector.id", is(connector3Pin.getId().toString())),
                        jsonPath("$.powerConnector.name", is(connector3Pin.getName())),
                        jsonPath("$.size.id", is(size120.getId().toString())),
                        jsonPath("$.size.length", is(size120.getLength())),
                        jsonPath("$.size.width", is(size120.getWidth())),
                        jsonPath("$.size.height", is(size120.getHeight()))
                );

        final Optional<Fan> optFan = fanRepository.findById(saved.getId());
        assertThat(optFan).isPresent();
        assertThat(optFan.get().getName())
                .isEqualTo(newName);
        assertThat(optFan.get().getVendor().getId())
                .isEqualTo(vendorDeepcool.getId());
        assertThat(optFan.get().getVendor().getName())
                .isEqualTo(vendorDeepcool.getName());
        assertThat(optFan.get().getPowerConnector().getId())
                .isEqualTo(connector3Pin.getId());
        assertThat(optFan.get().getPowerConnector().getName())
                .isEqualTo(connector3Pin.getName());
        assertThat(optFan.get().getSize().getId())
                .isEqualTo(size120.getId());
        assertThat(optFan.get().getSize().getLength())
                .isEqualTo(size120.getLength());
        assertThat(optFan.get().getSize().getWidth())
                .isEqualTo(size120.getWidth());
        assertThat(optFan.get().getSize().getHeight())
                .isEqualTo(size120.getHeight());
    }

    @Test
    void replace_withNonExistentVendorId_shouldReturnError() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanRf120B)
        );
        final Fan saved = fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(2);
        final String newName = "Frost 14";
        final UUID nonExistentVendorId = UUID.randomUUID();
        final FanRequestDto dto = FanRequestDto.builder()
                .name(newName)
                .vendorId(nonExistentVendorId)
                .sizeId(size120.getId())
                .powerConnectorId(connector3Pin.getId())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_FANS + "/{id}",
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

        final Optional<Fan> optFan = fanRepository.findById(saved.getId());
        assertThat(optFan).isPresent();
        assertThat(optFan.get().getName())
                .isEqualTo(fanPureWings2.getName());
        assertThat(optFan.get().getVendor().getId())
                .isEqualTo(vendorBeQuiet.getId());
        assertThat(optFan.get().getVendor().getName())
                .isEqualTo(vendorBeQuiet.getName());
        assertThat(optFan.get().getPowerConnector().getId())
                .isEqualTo(connector4Pin.getId());
        assertThat(optFan.get().getPowerConnector().getName())
                .isEqualTo(connector4Pin.getName());
        assertThat(optFan.get().getSize().getId())
                .isEqualTo(size140.getId());
        assertThat(optFan.get().getSize().getLength())
                .isEqualTo(size140.getLength());
        assertThat(optFan.get().getSize().getWidth())
                .isEqualTo(size140.getWidth());
        assertThat(optFan.get().getSize().getHeight())
                .isEqualTo(size140.getHeight());
    }

    @Test
    void replace_withNonExistentSizeId_shouldReturnError() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanRf120B)
        );
        final Fan saved = fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(2);
        final String newName = "Frost 14";
        final UUID nonExistentSizeId = UUID.randomUUID();
        final FanRequestDto dto = FanRequestDto.builder()
                .name(newName)
                .sizeId(nonExistentSizeId)
                .vendorId(vendorDeepcool.getId())
                .powerConnectorId(connector3Pin.getId())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_FANS + "/{id}",
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
                                        nonExistentSizeId
                                )
                        ))
                );

        final Optional<Fan> optFan = fanRepository.findById(saved.getId());
        assertThat(optFan).isPresent();
        assertThat(optFan.get().getName())
                .isEqualTo(fanPureWings2.getName());
        assertThat(optFan.get().getVendor().getId())
                .isEqualTo(vendorBeQuiet.getId());
        assertThat(optFan.get().getVendor().getName())
                .isEqualTo(vendorBeQuiet.getName());
        assertThat(optFan.get().getPowerConnector().getId())
                .isEqualTo(connector4Pin.getId());
        assertThat(optFan.get().getPowerConnector().getName())
                .isEqualTo(connector4Pin.getName());
        assertThat(optFan.get().getSize().getId())
                .isEqualTo(size140.getId());
        assertThat(optFan.get().getSize().getLength())
                .isEqualTo(size140.getLength());
        assertThat(optFan.get().getSize().getWidth())
                .isEqualTo(size140.getWidth());
        assertThat(optFan.get().getSize().getHeight())
                .isEqualTo(size140.getHeight());
    }

    @Test
    void replace_withNonExistentPowerConnectorId_shouldReturnError() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanRf120B)
        );
        final Fan saved = fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(2);
        final String newName = "Frost 14";
        final UUID nonExistentPowerConnectorId = UUID.randomUUID();
        final FanRequestDto dto = FanRequestDto.builder()
                .name(newName)
                .sizeId(size120.getId())
                .vendorId(vendorDeepcool.getId())
                .powerConnectorId(nonExistentPowerConnectorId)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_FANS + "/{id}",
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

        final Optional<Fan> optFan = fanRepository.findById(saved.getId());
        assertThat(optFan).isPresent();
        assertThat(optFan.get().getName())
                .isEqualTo(fanPureWings2.getName());
        assertThat(optFan.get().getVendor().getId())
                .isEqualTo(vendorBeQuiet.getId());
        assertThat(optFan.get().getVendor().getName())
                .isEqualTo(vendorBeQuiet.getName());
        assertThat(optFan.get().getPowerConnector().getId())
                .isEqualTo(connector4Pin.getId());
        assertThat(optFan.get().getPowerConnector().getName())
                .isEqualTo(connector4Pin.getName());
        assertThat(optFan.get().getSize().getId())
                .isEqualTo(size140.getId());
        assertThat(optFan.get().getSize().getLength())
                .isEqualTo(size140.getLength());
        assertThat(optFan.get().getSize().getWidth())
                .isEqualTo(size140.getWidth());
        assertThat(optFan.get().getSize().getHeight())
                .isEqualTo(size140.getHeight());
    }

    @Test
    void replace_withIncorrectVendorParam_shouldReturnError() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanRf120B)
        );
        final Fan saved = fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(2);
        final String newName = "Frost 14";
        final FanRequestDto dto = FanRequestDto.builder()
                .name(newName)
                .vendorId(null)
                .sizeId(size120.getId())
                .powerConnectorId(connector3Pin.getId())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_FANS + "/{id}",
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

        final Optional<Fan> optFan = fanRepository.findById(saved.getId());
        assertThat(optFan).isPresent();
        assertThat(optFan.get().getName())
                .isEqualTo(fanPureWings2.getName());
        assertThat(optFan.get().getVendor().getId())
                .isEqualTo(vendorBeQuiet.getId());
        assertThat(optFan.get().getVendor().getName())
                .isEqualTo(vendorBeQuiet.getName());
        assertThat(optFan.get().getPowerConnector().getId())
                .isEqualTo(connector4Pin.getId());
        assertThat(optFan.get().getPowerConnector().getName())
                .isEqualTo(connector4Pin.getName());
        assertThat(optFan.get().getSize().getId())
                .isEqualTo(size140.getId());
        assertThat(optFan.get().getSize().getLength())
                .isEqualTo(size140.getLength());
        assertThat(optFan.get().getSize().getWidth())
                .isEqualTo(size140.getWidth());
        assertThat(optFan.get().getSize().getHeight())
                .isEqualTo(size140.getHeight());
    }

    @Test
    void replace_withIncorrectSizeParam_shouldReturnError() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanRf120B)
        );
        final Fan saved = fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(2);
        final String newName = "Frost 14";
        final FanRequestDto dto = FanRequestDto.builder()
                .name(newName)
                .sizeId(null)
                .vendorId(vendorDeepcool.getId())
                .powerConnectorId(connector3Pin.getId())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_FANS + "/{id}",
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
                        jsonPath("$.violations[0].paramNames", contains("size")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        final Optional<Fan> optFan = fanRepository.findById(saved.getId());
        assertThat(optFan).isPresent();
        assertThat(optFan.get().getName())
                .isEqualTo(fanPureWings2.getName());
        assertThat(optFan.get().getVendor().getId())
                .isEqualTo(vendorBeQuiet.getId());
        assertThat(optFan.get().getVendor().getName())
                .isEqualTo(vendorBeQuiet.getName());
        assertThat(optFan.get().getPowerConnector().getId())
                .isEqualTo(connector4Pin.getId());
        assertThat(optFan.get().getPowerConnector().getName())
                .isEqualTo(connector4Pin.getName());
        assertThat(optFan.get().getSize().getId())
                .isEqualTo(size140.getId());
        assertThat(optFan.get().getSize().getLength())
                .isEqualTo(size140.getLength());
        assertThat(optFan.get().getSize().getWidth())
                .isEqualTo(size140.getWidth());
        assertThat(optFan.get().getSize().getHeight())
                .isEqualTo(size140.getHeight());
    }

    @Test
    void replace_withIncorrectPowerConnectorParam_shouldReturnError() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanRf120B)
        );
        final Fan saved = fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(2);
        final String newName = "Frost 14";
        final FanRequestDto dto = FanRequestDto.builder()
                .name(newName)
                .vendorId(vendorDeepcool.getId())
                .sizeId(size120.getId())
                .powerConnectorId(null)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_FANS + "/{id}",
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

        final Optional<Fan> optFan = fanRepository.findById(saved.getId());
        assertThat(optFan).isPresent();
        assertThat(optFan.get().getName())
                .isEqualTo(fanPureWings2.getName());
        assertThat(optFan.get().getVendor().getId())
                .isEqualTo(vendorBeQuiet.getId());
        assertThat(optFan.get().getVendor().getName())
                .isEqualTo(vendorBeQuiet.getName());
        assertThat(optFan.get().getPowerConnector().getId())
                .isEqualTo(connector4Pin.getId());
        assertThat(optFan.get().getPowerConnector().getName())
                .isEqualTo(connector4Pin.getName());
        assertThat(optFan.get().getSize().getId())
                .isEqualTo(size140.getId());
        assertThat(optFan.get().getSize().getLength())
                .isEqualTo(size140.getLength());
        assertThat(optFan.get().getSize().getWidth())
                .isEqualTo(size140.getWidth());
        assertThat(optFan.get().getSize().getHeight())
                .isEqualTo(size140.getHeight());
    }

    @Test
    void replace_withExistentEntity_shouldReturnError() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanRf120B)
        );
        final Fan saved = fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(2);
        final FanRequestDto dto = FanRequestDto.builder()
                .name(fanRf120B.getName())
                .sizeId(size120.getId())
                .vendorId(vendorDeepcool.getId())
                .powerConnectorId(connector3Pin.getId())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_FANS + "/{id}",
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
                        jsonPath("$.violations[0].paramNames", contains("name", "size")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Fan with name <{0}> and size <{1} x {2} x {3}> already exists!",
                                        fanRf120B.getName(),
                                        size120.getLength(),
                                        size120.getWidth(),
                                        size120.getHeight()
                                )
                        ))
                );

        final Optional<Fan> optFan = fanRepository.findById(saved.getId());
        assertThat(optFan).isPresent();
        assertThat(optFan.get().getName())
                .isEqualTo(fanPureWings2.getName());
        assertThat(optFan.get().getVendor().getId())
                .isEqualTo(vendorBeQuiet.getId());
        assertThat(optFan.get().getVendor().getName())
                .isEqualTo(vendorBeQuiet.getName());
        assertThat(optFan.get().getPowerConnector().getId())
                .isEqualTo(connector4Pin.getId());
        assertThat(optFan.get().getPowerConnector().getName())
                .isEqualTo(connector4Pin.getName());
        assertThat(optFan.get().getSize().getId())
                .isEqualTo(size140.getId());
        assertThat(optFan.get().getSize().getLength())
                .isEqualTo(size140.getLength());
        assertThat(optFan.get().getSize().getWidth())
                .isEqualTo(size140.getWidth());
        assertThat(optFan.get().getSize().getHeight())
                .isEqualTo(size140.getHeight());
    }

    @Test
    void update_withNonExistentEntity_shouldReturnUpdatedEntity() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanRf120B)
        );
        final Fan saved = fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(2);
        final String newName = "Frost 14";
        final FanRequestDto dto = FanRequestDto.builder()
                .name(newName)
                .sizeId(size120.getId())
                .vendorId(vendorDeepcool.getId())
                .powerConnectorId(connector3Pin.getId())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_FANS + "/{id}",
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
                        jsonPath("$.vendor.id", is(vendorDeepcool.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorDeepcool.getName())),
                        jsonPath("$.powerConnector.id", is(connector3Pin.getId().toString())),
                        jsonPath("$.powerConnector.name", is(connector3Pin.getName())),
                        jsonPath("$.size.id", is(size120.getId().toString())),
                        jsonPath("$.size.length", is(size120.getLength())),
                        jsonPath("$.size.width", is(size120.getWidth())),
                        jsonPath("$.size.height", is(size120.getHeight()))
                );

        final Optional<Fan> optFan = fanRepository.findById(saved.getId());
        assertThat(optFan).isPresent();
        assertThat(optFan.get().getName())
                .isEqualTo(newName);
        assertThat(optFan.get().getVendor().getId())
                .isEqualTo(vendorDeepcool.getId());
        assertThat(optFan.get().getVendor().getName())
                .isEqualTo(vendorDeepcool.getName());
        assertThat(optFan.get().getPowerConnector().getId())
                .isEqualTo(connector3Pin.getId());
        assertThat(optFan.get().getPowerConnector().getName())
                .isEqualTo(connector3Pin.getName());
        assertThat(optFan.get().getSize().getId())
                .isEqualTo(size120.getId());
        assertThat(optFan.get().getSize().getLength())
                .isEqualTo(size120.getLength());
        assertThat(optFan.get().getSize().getWidth())
                .isEqualTo(size120.getWidth());
        assertThat(optFan.get().getSize().getHeight())
                .isEqualTo(size120.getHeight());
    }

    @Test
    void update_withNonExistentVendorId_shouldReturnError() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanRf120B)
        );
        final Fan saved = fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(2);
        final String newName = "Frost 14";
        final UUID nonExistentVendorId = UUID.randomUUID();
        final FanRequestDto dto = FanRequestDto.builder()
                .name(newName)
                .sizeId(size120.getId())
                .vendorId(nonExistentVendorId)
                .powerConnectorId(connector3Pin.getId())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_FANS + "/{id}",
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

        final Optional<Fan> optFan = fanRepository.findById(saved.getId());
        assertThat(optFan).isPresent();
        assertThat(optFan.get().getName())
                .isEqualTo(fanPureWings2.getName());
        assertThat(optFan.get().getVendor().getId())
                .isEqualTo(vendorBeQuiet.getId());
        assertThat(optFan.get().getVendor().getName())
                .isEqualTo(vendorBeQuiet.getName());
        assertThat(optFan.get().getPowerConnector().getId())
                .isEqualTo(connector4Pin.getId());
        assertThat(optFan.get().getPowerConnector().getName())
                .isEqualTo(connector4Pin.getName());
        assertThat(optFan.get().getSize().getId())
                .isEqualTo(size140.getId());
        assertThat(optFan.get().getSize().getLength())
                .isEqualTo(size140.getLength());
        assertThat(optFan.get().getSize().getWidth())
                .isEqualTo(size140.getWidth());
        assertThat(optFan.get().getSize().getHeight())
                .isEqualTo(size140.getHeight());
    }

    @Test
    void update_withNonExistentSizeId_shouldReturnError() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanRf120B)
        );
        final Fan saved = fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(2);
        final String newName = "Frost 14";
        final UUID nonExistentSizeId = UUID.randomUUID();
        final FanRequestDto dto = FanRequestDto.builder()
                .name(newName)
                .sizeId(nonExistentSizeId)
                .vendorId(vendorDeepcool.getId())
                .powerConnectorId(connector3Pin.getId())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_FANS + "/{id}",
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
                                        nonExistentSizeId
                                )
                        ))
                );

        final Optional<Fan> optFan = fanRepository.findById(saved.getId());
        assertThat(optFan).isPresent();
        assertThat(optFan.get().getName())
                .isEqualTo(fanPureWings2.getName());
        assertThat(optFan.get().getVendor().getId())
                .isEqualTo(vendorBeQuiet.getId());
        assertThat(optFan.get().getVendor().getName())
                .isEqualTo(vendorBeQuiet.getName());
        assertThat(optFan.get().getPowerConnector().getId())
                .isEqualTo(connector4Pin.getId());
        assertThat(optFan.get().getPowerConnector().getName())
                .isEqualTo(connector4Pin.getName());
        assertThat(optFan.get().getSize().getId())
                .isEqualTo(size140.getId());
        assertThat(optFan.get().getSize().getLength())
                .isEqualTo(size140.getLength());
        assertThat(optFan.get().getSize().getWidth())
                .isEqualTo(size140.getWidth());
        assertThat(optFan.get().getSize().getHeight())
                .isEqualTo(size140.getHeight());
    }

    @Test
    void update_withNonExistentPowerConnectorId_shouldReturnError() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanRf120B)
        );
        final Fan saved = fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(2);
        final String newName = "Frost 14";
        final UUID nonExistentPowerConnectorId = UUID.randomUUID();
        final FanRequestDto dto = FanRequestDto.builder()
                .name(newName)
                .sizeId(size120.getId())
                .vendorId(vendorDeepcool.getId())
                .powerConnectorId(nonExistentPowerConnectorId)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_FANS + "/{id}",
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

        final Optional<Fan> optFan = fanRepository.findById(saved.getId());
        assertThat(optFan).isPresent();
        assertThat(optFan.get().getName())
                .isEqualTo(fanPureWings2.getName());
        assertThat(optFan.get().getVendor().getId())
                .isEqualTo(vendorBeQuiet.getId());
        assertThat(optFan.get().getVendor().getName())
                .isEqualTo(vendorBeQuiet.getName());
        assertThat(optFan.get().getPowerConnector().getId())
                .isEqualTo(connector4Pin.getId());
        assertThat(optFan.get().getPowerConnector().getName())
                .isEqualTo(connector4Pin.getName());
        assertThat(optFan.get().getSize().getId())
                .isEqualTo(size140.getId());
        assertThat(optFan.get().getSize().getLength())
                .isEqualTo(size140.getLength());
        assertThat(optFan.get().getSize().getWidth())
                .isEqualTo(size140.getWidth());
        assertThat(optFan.get().getSize().getHeight())
                .isEqualTo(size140.getHeight());
    }

    @Test
    void update_withExistentEntity_shouldReturnError() throws Exception {
        // given
        fanRepository.save(
                mapper.convertFromDto(fanPureWings2)
        );
        assertThat(fanRepository.findAll()).hasSize(1);
        final FanRequestDto dto = FanRequestDto.builder()
                .name(fanPureWings2.getName())
                .sizeId(size140.getId())
                .vendorId(vendorBeQuiet.getId())
                .powerConnectorId(connector4Pin.getId())
                .build();

        final Fan saved = fanRepository.save(
                mapper.convertFromDto(fanRf120B)
        );
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_FANS + "/{id}",
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
                        jsonPath("$.violations[0].paramNames", contains("name", "size")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Fan with name <{0}> and size <{1} x {2} x {3}> already exists!",
                                        fanPureWings2.getName(),
                                        size140.getLength(),
                                        size140.getWidth(),
                                        size140.getHeight()
                                )
                        ))
                );

        final Optional<Fan> optFan = fanRepository.findById(saved.getId());
        assertThat(optFan).isPresent();
        assertThat(optFan.get().getName())
                .isEqualTo(fanRf120B.getName());
        assertThat(optFan.get().getVendor().getId())
                .isEqualTo(vendorDeepcool.getId());
        assertThat(optFan.get().getVendor().getName())
                .isEqualTo(vendorDeepcool.getName());
        assertThat(optFan.get().getPowerConnector().getId())
                .isEqualTo(connector3Pin.getId());
        assertThat(optFan.get().getPowerConnector().getName())
                .isEqualTo(connector3Pin.getName());
        assertThat(optFan.get().getSize().getId())
                .isEqualTo(size120.getId());
        assertThat(optFan.get().getSize().getLength())
                .isEqualTo(size120.getLength());
        assertThat(optFan.get().getSize().getWidth())
                .isEqualTo(size120.getWidth());
        assertThat(optFan.get().getSize().getHeight())
                .isEqualTo(size120.getHeight());

    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID fanRf120BId = fanRepository.save(
                mapper.convertFromDto(fanRf120B)
        ).getId();
        assertThat(fanRepository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_FANS + "/{id}",
                fanRf120BId
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(fanRepository.findAll()).isEmpty();
    }
}
