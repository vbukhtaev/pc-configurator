package ru.bukhtaev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.dto.mapper.IRamModuleMapper;
import ru.bukhtaev.dto.request.RamModuleRequestDto;
import ru.bukhtaev.model.Design;
import ru.bukhtaev.model.RamModule;
import ru.bukhtaev.model.RamType;
import ru.bukhtaev.model.Vendor;
import ru.bukhtaev.repository.IDesignRepository;
import ru.bukhtaev.repository.IRamModuleRepository;
import ru.bukhtaev.repository.IRamTypeRepository;
import ru.bukhtaev.repository.IVendorRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.RamModuleRestController.URL_API_V1_RAM_MODULES;

/**
 * Интеграционные тесты для CRUD операций над модулями оперативной памяти.
 */
class RamModuleRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO модулей оперативной памяти.
     */
    @Autowired
    private IRamModuleMapper mapper;

    /**
     * Репозиторий модулей оперативной памяти.
     */
    @Autowired
    private IRamModuleRepository moduleRepository;

    /**
     * Репозиторий вариантов исполнения.
     */
    @Autowired
    private IDesignRepository designRepository;

    /**
     * Репозиторий вендоров.
     */
    @Autowired
    private IVendorRepository vendorRepository;

    /**
     * Репозиторий типов оперативной памяти.
     */
    @Autowired
    private IRamTypeRepository typeRepository;

    private RamModuleRequestDto ramModuleDdr4;
    private RamModuleRequestDto ramModuleDdr5;

    private RamType typeDdr4;
    private RamType typeDdr5;

    private Design designHyperxFury;
    private Design designBallistix;

    @BeforeEach
    void setUp() {
        final Vendor vendorKingston = vendorRepository.save(
                Vendor.builder()
                        .name("Kingston")
                        .build()
        );
        final Vendor vendorCrucial = vendorRepository.save(
                Vendor.builder()
                        .name("Crucial")
                        .build()
        );

        typeDdr4 = typeRepository.save(
                RamType.builder()
                        .name("DDR4")
                        .build()
        );
        typeDdr5 = typeRepository.save(
                RamType.builder()
                        .name("DDR5")
                        .build()
        );

        designHyperxFury = designRepository.save(
                Design.builder()
                        .name("HyperX Fury")
                        .vendor(vendorKingston)
                        .build()
        );
        designBallistix = designRepository.save(
                Design.builder()
                        .name("Ballistix")
                        .vendor(vendorCrucial)
                        .build()
        );

        ramModuleDdr4 = RamModuleRequestDto.builder()
                .clock(3200)
                .capacity(8192)
                .typeId(typeDdr4.getId())
                .designId(designHyperxFury.getId())
                .build();
        ramModuleDdr5 = RamModuleRequestDto.builder()
                .clock(6000)
                .capacity(16384)
                .typeId(typeDdr5.getId())
                .designId(designBallistix.getId())
                .build();
    }

    @AfterEach
    void tearDown() {
        moduleRepository.deleteAll();
        designRepository.deleteAll();
        vendorRepository.deleteAll();
        typeRepository.deleteAll();
    }

    @Test
    void getAll_shouldReturnAllEntities() throws Exception {
        // given
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr4)
        );
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr5)
        );
        assertThat(moduleRepository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_RAM_MODULES);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),

                        jsonPath("$[0].type.id", is(typeDdr4.getId().toString())),
                        jsonPath("$[0].type.name", is(typeDdr4.getName())),
                        jsonPath("$[0].design.id", is(designHyperxFury.getId().toString())),
                        jsonPath("$[0].design.name", is(designHyperxFury.getName())),
                        jsonPath("$[0].clock", is(ramModuleDdr4.getClock())),
                        jsonPath("$[0].capacity", is(ramModuleDdr4.getCapacity())),

                        jsonPath("$[1].type.id", is(typeDdr5.getId().toString())),
                        jsonPath("$[1].type.name", is(typeDdr5.getName())),
                        jsonPath("$[1].design.id", is(designBallistix.getId().toString())),
                        jsonPath("$[1].design.name", is(designBallistix.getName())),
                        jsonPath("$[1].clock", is(ramModuleDdr5.getClock())),
                        jsonPath("$[1].capacity", is(ramModuleDdr5.getCapacity()))
                );
    }

    @Test
    void getAll_withPagination_shouldReturnAllEntitiesAsPage() throws Exception {
        // given
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr4)
        );
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr5)
        );
        assertThat(moduleRepository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_RAM_MODULES + "/pageable")
                .params(RAM_MODULE_PAGE_REQUEST_PARAMS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.content", hasSize(2)),

                        jsonPath("$.content[0].type.id", is(typeDdr5.getId().toString())),
                        jsonPath("$.content[0].type.name", is(typeDdr5.getName())),
                        jsonPath("$.content[0].design.id", is(designBallistix.getId().toString())),
                        jsonPath("$.content[0].design.name", is(designBallistix.getName())),
                        jsonPath("$.content[0].clock", is(ramModuleDdr5.getClock())),
                        jsonPath("$.content[0].capacity", is(ramModuleDdr5.getCapacity())),

                        jsonPath("$.content[1].type.id", is(typeDdr4.getId().toString())),
                        jsonPath("$.content[1].type.name", is(typeDdr4.getName())),
                        jsonPath("$.content[1].design.id", is(designHyperxFury.getId().toString())),
                        jsonPath("$.content[1].design.name", is(designHyperxFury.getName())),
                        jsonPath("$.content[1].clock", is(ramModuleDdr4.getClock())),
                        jsonPath("$.content[1].capacity", is(ramModuleDdr4.getCapacity()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final RamModule saved = moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr4)
        );
        assertThat(moduleRepository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_RAM_MODULES + "/{id}",
                saved.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.type.id", is(typeDdr4.getId().toString())),
                        jsonPath("$.type.name", is(typeDdr4.getName())),
                        jsonPath("$.design.id", is(designHyperxFury.getId().toString())),
                        jsonPath("$.design.name", is(designHyperxFury.getName())),
                        jsonPath("$.clock", is(ramModuleDdr4.getClock())),
                        jsonPath("$.capacity", is(ramModuleDdr4.getCapacity()))
                );
    }

    @Test
    void getById_withNonExistentId_shouldReturnError() throws Exception {
        // given
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr4)
        );
        assertThat(moduleRepository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_RAM_MODULES + "/{id}",
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
                                        "RAM module with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentEntity_shouldReturnCreatedEntity() throws Exception {
        // given
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr5)
        );
        assertThat(moduleRepository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(ramModuleDdr4);
        final var requestBuilder = post(URL_API_V1_RAM_MODULES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.type.id", is(typeDdr4.getId().toString())),
                        jsonPath("$.type.name", is(typeDdr4.getName())),
                        jsonPath("$.design.id", is(designHyperxFury.getId().toString())),
                        jsonPath("$.design.name", is(designHyperxFury.getName())),
                        jsonPath("$.clock", is(ramModuleDdr4.getClock())),
                        jsonPath("$.capacity", is(ramModuleDdr4.getCapacity()))
                );

        final List<RamModule> modules = moduleRepository.findAll();
        assertThat(modules).hasSize(2);
        final RamModule module = modules.get(1);
        assertThat(module.getId()).isNotNull();
        assertThat(module.getType().getId())
                .isEqualTo(typeDdr4.getId());
        assertThat(module.getType().getName())
                .isEqualTo(typeDdr4.getName());
        assertThat(module.getDesign().getId())
                .isEqualTo(designHyperxFury.getId());
        assertThat(module.getDesign().getName())
                .isEqualTo(designHyperxFury.getName());
        assertThat(module.getClock())
                .isEqualTo(ramModuleDdr4.getClock());
        assertThat(module.getCapacity())
                .isEqualTo(ramModuleDdr4.getCapacity());
    }

    @Test
    void create_withNonExistentTypeId_shouldReturnError() throws Exception {
        // given
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr5)
        );
        assertThat(moduleRepository.findAll()).hasSize(1);
        final UUID nonExistentTypeId = UUID.randomUUID();
        ramModuleDdr4.setTypeId(nonExistentTypeId);
        final String jsonRequest = objectMapper.writeValueAsString(ramModuleDdr4);
        final var requestBuilder = post(URL_API_V1_RAM_MODULES)
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
                                        "RAM type with ID = <{0}> not found!",
                                        nonExistentTypeId
                                )
                        ))
                );

        assertThat(moduleRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withNonExistentDesignId_shouldReturnError() throws Exception {
        // given
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr5)
        );
        assertThat(moduleRepository.findAll()).hasSize(1);
        final UUID nonExistentDesignId = UUID.randomUUID();
        ramModuleDdr4.setDesignId(nonExistentDesignId);
        final String jsonRequest = objectMapper.writeValueAsString(ramModuleDdr4);
        final var requestBuilder = post(URL_API_V1_RAM_MODULES)
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
                                        "Design with ID = <{0}> not found!",
                                        nonExistentDesignId
                                )
                        ))
                );

        assertThat(moduleRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectTypeParam_shouldReturnError() throws Exception {
        // given
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr5)
        );
        assertThat(moduleRepository.findAll()).hasSize(1);
        ramModuleDdr4.setTypeId(null);
        final String jsonRequest = objectMapper.writeValueAsString(ramModuleDdr4);
        final var requestBuilder = post(URL_API_V1_RAM_MODULES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("type")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        assertThat(moduleRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectDesignParam_shouldReturnError() throws Exception {
        // given
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr5)
        );
        assertThat(moduleRepository.findAll()).hasSize(1);
        ramModuleDdr4.setDesignId(null);
        final String jsonRequest = objectMapper.writeValueAsString(ramModuleDdr4);
        final var requestBuilder = post(URL_API_V1_RAM_MODULES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("design")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        assertThat(moduleRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withExistentEntity_shouldReturnError() throws Exception {
        // given
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr4)
        );
        assertThat(moduleRepository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(ramModuleDdr4);
        final var requestBuilder = post(URL_API_V1_RAM_MODULES)
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
                                contains("clock", "capacity", "type", "design")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "RAM module with clock <{0}> capacity <{1}> " +
                                                "type <{2}> and design <{3}> already exists!",
                                        ramModuleDdr4.getClock(),
                                        ramModuleDdr4.getCapacity(),
                                        typeDdr4.getName(),
                                        designHyperxFury.getName()
                                )
                        ))
                );

        assertThat(moduleRepository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentEntity_shouldReturnReplacedEntity() throws Exception {
        // given
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr4)
        );
        final RamModule saved = moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr5)
        );
        assertThat(moduleRepository.findAll()).hasSize(2);
        final Integer newClock = 8000;
        final RamModuleRequestDto dto = RamModuleRequestDto.builder()
                .typeId(typeDdr4.getId())
                .designId(designHyperxFury.getId())
                .clock(newClock)
                .capacity(ramModuleDdr4.getCapacity())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_RAM_MODULES + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.type.id", is(typeDdr4.getId().toString())),
                        jsonPath("$.type.name", is(typeDdr4.getName())),
                        jsonPath("$.design.id", is(designHyperxFury.getId().toString())),
                        jsonPath("$.design.name", is(designHyperxFury.getName())),
                        jsonPath("$.clock", is(newClock)),
                        jsonPath("$.capacity", is(ramModuleDdr4.getCapacity()))
                );

        final Optional<RamModule> optRamModule = moduleRepository.findById(saved.getId());
        assertThat(optRamModule).isPresent();
        final RamModule module = optRamModule.get();
        assertThat(module.getType().getId())
                .isEqualTo(typeDdr4.getId());
        assertThat(module.getType().getName())
                .isEqualTo(typeDdr4.getName());
        assertThat(module.getDesign().getId())
                .isEqualTo(designHyperxFury.getId());
        assertThat(module.getDesign().getName())
                .isEqualTo(designHyperxFury.getName());
        assertThat(module.getClock())
                .isEqualTo(newClock);
        assertThat(module.getCapacity())
                .isEqualTo(ramModuleDdr4.getCapacity());
    }

    @Test
    void replace_withNonExistentTypeId_shouldReturnError() throws Exception {
        // given
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr4)
        );
        final RamModule saved = moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr5)
        );
        assertThat(moduleRepository.findAll()).hasSize(2);
        final Integer newClock = 8000;
        final UUID nonExistentTypeId = UUID.randomUUID();
        final RamModuleRequestDto dto = RamModuleRequestDto.builder()
                .typeId(nonExistentTypeId)
                .designId(designHyperxFury.getId())
                .clock(newClock)
                .capacity(ramModuleDdr4.getCapacity())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_RAM_MODULES + "/{id}",
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
                                        "RAM type with ID = <{0}> not found!",
                                        nonExistentTypeId
                                )
                        ))
                );

        final Optional<RamModule> optRamModule = moduleRepository.findById(saved.getId());
        assertThat(optRamModule).isPresent();
        final RamModule module = optRamModule.get();
        assertThat(module.getType().getId())
                .isEqualTo(typeDdr5.getId());
        assertThat(module.getType().getName())
                .isEqualTo(typeDdr5.getName());
        assertThat(module.getDesign().getId())
                .isEqualTo(designBallistix.getId());
        assertThat(module.getDesign().getName())
                .isEqualTo(designBallistix.getName());
        assertThat(module.getClock())
                .isEqualTo(ramModuleDdr5.getClock());
        assertThat(module.getCapacity())
                .isEqualTo(ramModuleDdr5.getCapacity());
    }

    @Test
    void replace_withNonExistentDesignId_shouldReturnError() throws Exception {
        // given
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr4)
        );
        final RamModule saved = moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr5)
        );
        assertThat(moduleRepository.findAll()).hasSize(2);
        final Integer newClock = 8000;
        final UUID nonExistentDesignId = UUID.randomUUID();
        final RamModuleRequestDto dto = RamModuleRequestDto.builder()
                .typeId(typeDdr4.getId())
                .designId(nonExistentDesignId)
                .clock(newClock)
                .capacity(ramModuleDdr4.getCapacity())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_RAM_MODULES + "/{id}",
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
                                        "Design with ID = <{0}> not found!",
                                        nonExistentDesignId
                                )
                        ))
                );

        final Optional<RamModule> optRamModule = moduleRepository.findById(saved.getId());
        assertThat(optRamModule).isPresent();
        final RamModule module = optRamModule.get();
        assertThat(module.getType().getId())
                .isEqualTo(typeDdr5.getId());
        assertThat(module.getType().getName())
                .isEqualTo(typeDdr5.getName());
        assertThat(module.getDesign().getId())
                .isEqualTo(designBallistix.getId());
        assertThat(module.getDesign().getName())
                .isEqualTo(designBallistix.getName());
        assertThat(module.getClock())
                .isEqualTo(ramModuleDdr5.getClock());
        assertThat(module.getCapacity())
                .isEqualTo(ramModuleDdr5.getCapacity());
    }

    @Test
    void replace_withIncorrectTypeParam_shouldReturnError() throws Exception {
        // given
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr4)
        );
        final RamModule saved = moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr5)
        );
        assertThat(moduleRepository.findAll()).hasSize(2);
        final Integer newClock = 8000;
        final RamModuleRequestDto dto = RamModuleRequestDto.builder()
                .typeId(null)
                .designId(designHyperxFury.getId())
                .clock(newClock)
                .capacity(ramModuleDdr4.getCapacity())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_RAM_MODULES + "/{id}",
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
                        jsonPath("$.violations[0].paramNames", contains("type")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        final Optional<RamModule> optRamModule = moduleRepository.findById(saved.getId());
        assertThat(optRamModule).isPresent();
        final RamModule module = optRamModule.get();
        assertThat(module.getType().getId())
                .isEqualTo(typeDdr5.getId());
        assertThat(module.getType().getName())
                .isEqualTo(typeDdr5.getName());
        assertThat(module.getDesign().getId())
                .isEqualTo(designBallistix.getId());
        assertThat(module.getDesign().getName())
                .isEqualTo(designBallistix.getName());
        assertThat(module.getClock())
                .isEqualTo(ramModuleDdr5.getClock());
        assertThat(module.getCapacity())
                .isEqualTo(ramModuleDdr5.getCapacity());
    }

    @Test
    void replace_withIncorrectDesignParam_shouldReturnError() throws Exception {
        // given
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr4)
        );
        final RamModule saved = moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr5)
        );
        assertThat(moduleRepository.findAll()).hasSize(2);
        final Integer newClock = 8000;
        final RamModuleRequestDto dto = RamModuleRequestDto.builder()
                .designId(null)
                .typeId(typeDdr4.getId())
                .clock(newClock)
                .capacity(ramModuleDdr4.getCapacity())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_RAM_MODULES + "/{id}",
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
                        jsonPath("$.violations[0].paramNames", contains("design")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        final Optional<RamModule> optRamModule = moduleRepository.findById(saved.getId());
        assertThat(optRamModule).isPresent();
        final RamModule module = optRamModule.get();
        assertThat(module.getType().getId())
                .isEqualTo(typeDdr5.getId());
        assertThat(module.getType().getName())
                .isEqualTo(typeDdr5.getName());
        assertThat(module.getDesign().getId())
                .isEqualTo(designBallistix.getId());
        assertThat(module.getDesign().getName())
                .isEqualTo(designBallistix.getName());
        assertThat(module.getClock())
                .isEqualTo(ramModuleDdr5.getClock());
        assertThat(module.getCapacity())
                .isEqualTo(ramModuleDdr5.getCapacity());
    }

    @Test
    void replace_withExistentEntity_shouldReturnError() throws Exception {
        // given
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr4)
        );
        final RamModule saved = moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr5)
        );
        assertThat(moduleRepository.findAll()).hasSize(2);
        final RamModuleRequestDto dto = RamModuleRequestDto.builder()
                .typeId(typeDdr4.getId())
                .designId(designHyperxFury.getId())
                .clock(ramModuleDdr4.getClock())
                .capacity(ramModuleDdr4.getCapacity())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_RAM_MODULES + "/{id}",
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
                                contains("clock", "capacity", "type", "design")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "RAM module with clock <{0}> capacity <{1}> " +
                                                "type <{2}> and design <{3}> already exists!",
                                        ramModuleDdr4.getClock(),
                                        ramModuleDdr4.getCapacity(),
                                        typeDdr4.getName(),
                                        designHyperxFury.getName()
                                )
                        ))
                );

        final Optional<RamModule> optRamModule = moduleRepository.findById(saved.getId());
        assertThat(optRamModule).isPresent();
        final RamModule module = optRamModule.get();
        assertThat(module.getType().getId())
                .isEqualTo(typeDdr5.getId());
        assertThat(module.getType().getName())
                .isEqualTo(typeDdr5.getName());
        assertThat(module.getDesign().getId())
                .isEqualTo(designBallistix.getId());
        assertThat(module.getDesign().getName())
                .isEqualTo(designBallistix.getName());
        assertThat(module.getClock())
                .isEqualTo(ramModuleDdr5.getClock());
        assertThat(module.getCapacity())
                .isEqualTo(ramModuleDdr5.getCapacity());
    }

    @Test
    void update_withNonExistentEntity_shouldReturnUpdatedEntity() throws Exception {
        // given
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr4)
        );
        final RamModule saved = moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr5)
        );
        assertThat(moduleRepository.findAll()).hasSize(2);
        final Integer newClock = 8000;
        final RamModuleRequestDto dto = RamModuleRequestDto.builder()
                .typeId(typeDdr4.getId())
                .designId(designHyperxFury.getId())
                .clock(newClock)
                .capacity(ramModuleDdr4.getCapacity())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_RAM_MODULES + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.type.id", is(typeDdr4.getId().toString())),
                        jsonPath("$.type.name", is(typeDdr4.getName())),
                        jsonPath("$.design.id", is(designHyperxFury.getId().toString())),
                        jsonPath("$.design.name", is(designHyperxFury.getName())),
                        jsonPath("$.clock", is(newClock)),
                        jsonPath("$.capacity", is(ramModuleDdr4.getCapacity()))
                );

        final Optional<RamModule> optRamModule = moduleRepository.findById(saved.getId());
        assertThat(optRamModule).isPresent();
        final RamModule module = optRamModule.get();
        assertThat(module.getType().getId())
                .isEqualTo(typeDdr4.getId());
        assertThat(module.getType().getName())
                .isEqualTo(typeDdr4.getName());
        assertThat(module.getDesign().getId())
                .isEqualTo(designHyperxFury.getId());
        assertThat(module.getDesign().getName())
                .isEqualTo(designHyperxFury.getName());
        assertThat(module.getClock())
                .isEqualTo(newClock);
        assertThat(module.getCapacity())
                .isEqualTo(ramModuleDdr4.getCapacity());
    }

    @Test
    void update_withNonExistentTypeId_shouldReturnError() throws Exception {
        // given
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr4)
        );
        final RamModule saved = moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr5)
        );
        assertThat(moduleRepository.findAll()).hasSize(2);
        final Integer newClock = 8000;
        final UUID nonExistentTypeId = UUID.randomUUID();
        final RamModuleRequestDto dto = RamModuleRequestDto.builder()
                .typeId(nonExistentTypeId)
                .designId(designHyperxFury.getId())
                .clock(newClock)
                .capacity(ramModuleDdr4.getCapacity())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_RAM_MODULES + "/{id}",
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
                                        "RAM type with ID = <{0}> not found!",
                                        nonExistentTypeId
                                )
                        ))
                );

        final Optional<RamModule> optRamModule = moduleRepository.findById(saved.getId());
        assertThat(optRamModule).isPresent();
        final RamModule module = optRamModule.get();
        assertThat(module.getType().getId())
                .isEqualTo(typeDdr5.getId());
        assertThat(module.getType().getName())
                .isEqualTo(typeDdr5.getName());
        assertThat(module.getDesign().getId())
                .isEqualTo(designBallistix.getId());
        assertThat(module.getDesign().getName())
                .isEqualTo(designBallistix.getName());
        assertThat(module.getClock())
                .isEqualTo(ramModuleDdr5.getClock());
        assertThat(module.getCapacity())
                .isEqualTo(ramModuleDdr5.getCapacity());
    }

    @Test
    void update_withNonExistentDesignId_shouldReturnError() throws Exception {
        // given
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr4)
        );
        final RamModule saved = moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr5)
        );
        assertThat(moduleRepository.findAll()).hasSize(2);
        final Integer newClock = 8000;
        final UUID nonExistentDesignId = UUID.randomUUID();
        final RamModuleRequestDto dto = RamModuleRequestDto.builder()
                .typeId(typeDdr4.getId())
                .designId(nonExistentDesignId)
                .clock(newClock)
                .capacity(ramModuleDdr4.getCapacity())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_RAM_MODULES + "/{id}",
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
                                        "Design with ID = <{0}> not found!",
                                        nonExistentDesignId
                                )
                        ))
                );

        final Optional<RamModule> optRamModule = moduleRepository.findById(saved.getId());
        assertThat(optRamModule).isPresent();
        final RamModule module = optRamModule.get();
        assertThat(module.getType().getId())
                .isEqualTo(typeDdr5.getId());
        assertThat(module.getType().getName())
                .isEqualTo(typeDdr5.getName());
        assertThat(module.getDesign().getId())
                .isEqualTo(designBallistix.getId());
        assertThat(module.getDesign().getName())
                .isEqualTo(designBallistix.getName());
        assertThat(module.getClock())
                .isEqualTo(ramModuleDdr5.getClock());
        assertThat(module.getCapacity())
                .isEqualTo(ramModuleDdr5.getCapacity());
    }

    @Test
    void update_withExistentEntity_shouldReturnError() throws Exception {
        // given
        moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr5)
        );
        assertThat(moduleRepository.findAll()).hasSize(1);
        final RamModuleRequestDto dto = RamModuleRequestDto.builder()
                .typeId(typeDdr5.getId())
                .designId(designBallistix.getId())
                .clock(ramModuleDdr5.getClock())
                .capacity(ramModuleDdr5.getCapacity())
                .build();

        final RamModule saved = moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr4)
        );
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_RAM_MODULES + "/{id}",
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
                                contains("clock", "capacity", "type", "design")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "RAM module with clock <{0}> capacity <{1}> " +
                                                "type <{2}> and design <{3}> already exists!",
                                        ramModuleDdr5.getClock(),
                                        ramModuleDdr5.getCapacity(),
                                        typeDdr5.getName(),
                                        designBallistix.getName()
                                )
                        ))
                );

        final Optional<RamModule> optRamModule = moduleRepository.findById(saved.getId());
        assertThat(optRamModule).isPresent();
        final RamModule module = optRamModule.get();
        assertThat(module.getType().getId())
                .isEqualTo(typeDdr4.getId());
        assertThat(module.getType().getName())
                .isEqualTo(typeDdr4.getName());
        assertThat(module.getDesign().getId())
                .isEqualTo(designHyperxFury.getId());
        assertThat(module.getDesign().getName())
                .isEqualTo(designHyperxFury.getName());
        assertThat(module.getClock())
                .isEqualTo(ramModuleDdr4.getClock());
        assertThat(module.getCapacity())
                .isEqualTo(ramModuleDdr4.getCapacity());

    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID ramModuleDdr4Id = moduleRepository.save(
                mapper.convertFromDto(ramModuleDdr4)
        ).getId();
        assertThat(moduleRepository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_RAM_MODULES + "/{id}",
                ramModuleDdr4Id
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(moduleRepository.findAll()).isEmpty();
    }
}
