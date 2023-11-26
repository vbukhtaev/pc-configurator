package ru.bukhtaev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.dto.mapper.IGpuMapper;
import ru.bukhtaev.dto.request.GpuRequestDto;
import ru.bukhtaev.model.Gpu;
import ru.bukhtaev.model.dictionary.Manufacturer;
import ru.bukhtaev.model.dictionary.VideoMemoryType;
import ru.bukhtaev.repository.IGpuRepository;
import ru.bukhtaev.repository.dictionary.IManufacturerRepository;
import ru.bukhtaev.repository.dictionary.IVideoMemoryTypeRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.GpuRestController.URL_API_V1_GPUS;

/**
 * Интеграционные тесты для CRUD операций над графическими процессорами.
 */
class GpuRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO графических процессоров.
     */
    @Autowired
    private IGpuMapper mapper;

    /**
     * Репозиторий графических процессоров.
     */
    @Autowired
    private IGpuRepository gpuRepository;

    /**
     * Репозиторий производителей.
     */
    @Autowired
    private IManufacturerRepository manufacturerRepository;

    /**
     * Репозиторий типов видеопамяти.
     */
    @Autowired
    private IVideoMemoryTypeRepository memoryTypeRepository;

    private GpuRequestDto gpuGtx1060;
    private GpuRequestDto gpuRx5700;

    private Manufacturer manufacturerNvidia;
    private Manufacturer manufacturerAmd;

    private VideoMemoryType memoryTypeGddr5;
    private VideoMemoryType memoryTypeGddr6;

    @BeforeEach
    void setUp() {
        manufacturerNvidia = manufacturerRepository.save(
                Manufacturer.builder()
                        .name("Nvidia")
                        .build()
        );
        manufacturerAmd = manufacturerRepository.save(
                Manufacturer.builder()
                        .name("AMD")
                        .build()
        );

        memoryTypeGddr5 = memoryTypeRepository.save(
                VideoMemoryType.builder()
                        .name("GDDR5")
                        .build()
        );
        memoryTypeGddr6 = memoryTypeRepository.save(
                VideoMemoryType.builder()
                        .name("GDDR6")
                        .build()
        );

        gpuGtx1060 = GpuRequestDto.builder()
                .name("GeForce GTX 1060")
                .manufacturerId(manufacturerNvidia.getId())
                .memoryTypeId(memoryTypeGddr5.getId())
                .powerConsumption(120)
                .memorySize(6144)
                .build();
        gpuRx5700 = GpuRequestDto.builder()
                .name("Radeon RX 5700")
                .manufacturerId(manufacturerAmd.getId())
                .memoryTypeId(memoryTypeGddr6.getId())
                .powerConsumption(180)
                .memorySize(8192)
                .build();
    }

    @AfterEach
    void tearDown() {
        gpuRepository.deleteAll();
        memoryTypeRepository.deleteAll();
        manufacturerRepository.deleteAll();
    }

    @Test
    void getAll_shouldReturnAllEntities() throws Exception {
        // given
        gpuRepository.save(
                mapper.convertFromDto(gpuGtx1060)
        );
        gpuRepository.save(
                mapper.convertFromDto(gpuRx5700)
        );
        assertThat(gpuRepository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_GPUS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),

                        jsonPath("$[0].name", is(gpuGtx1060.getName())),
                        jsonPath("$[0].manufacturer.id", is(manufacturerNvidia.getId().toString())),
                        jsonPath("$[0].manufacturer.name", is(manufacturerNvidia.getName())),
                        jsonPath("$[0].memoryType.id", is(memoryTypeGddr5.getId().toString())),
                        jsonPath("$[0].memoryType.name", is(memoryTypeGddr5.getName())),
                        jsonPath("$[0].powerConsumption", is(gpuGtx1060.getPowerConsumption())),
                        jsonPath("$[0].memorySize", is(gpuGtx1060.getMemorySize())),

                        jsonPath("$[1].name", is(gpuRx5700.getName())),
                        jsonPath("$[1].manufacturer.id", is(manufacturerAmd.getId().toString())),
                        jsonPath("$[1].manufacturer.name", is(manufacturerAmd.getName())),
                        jsonPath("$[1].memoryType.id", is(memoryTypeGddr6.getId().toString())),
                        jsonPath("$[1].memoryType.name", is(memoryTypeGddr6.getName())),
                        jsonPath("$[1].powerConsumption", is(gpuRx5700.getPowerConsumption())),
                        jsonPath("$[1].memorySize", is(gpuRx5700.getMemorySize()))
                );
    }

    @Test
    void getAll_withPagination_shouldReturnAllEntitiesAsPage() throws Exception {
        // given
        gpuRepository.save(
                mapper.convertFromDto(gpuGtx1060)
        );
        gpuRepository.save(
                mapper.convertFromDto(gpuRx5700)
        );
        assertThat(gpuRepository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_GPUS + "/pageable")
                .params(GPU_PAGE_REQUEST_PARAMS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.content", hasSize(2)),

                        jsonPath("$.content[0].name", is(gpuRx5700.getName())),
                        jsonPath("$.content[0].manufacturer.id", is(manufacturerAmd.getId().toString())),
                        jsonPath("$.content[0].manufacturer.name", is(manufacturerAmd.getName())),
                        jsonPath("$.content[0].memoryType.id", is(memoryTypeGddr6.getId().toString())),
                        jsonPath("$.content[0].memoryType.name", is(memoryTypeGddr6.getName())),
                        jsonPath("$.content[0].powerConsumption", is(gpuRx5700.getPowerConsumption())),
                        jsonPath("$.content[0].memorySize", is(gpuRx5700.getMemorySize())),

                        jsonPath("$.content[1].name", is(gpuGtx1060.getName())),
                        jsonPath("$.content[1].manufacturer.id", is(manufacturerNvidia.getId().toString())),
                        jsonPath("$.content[1].manufacturer.name", is(manufacturerNvidia.getName())),
                        jsonPath("$.content[1].memoryType.id", is(memoryTypeGddr5.getId().toString())),
                        jsonPath("$.content[1].memoryType.name", is(memoryTypeGddr5.getName())),
                        jsonPath("$.content[1].powerConsumption", is(gpuGtx1060.getPowerConsumption())),
                        jsonPath("$.content[1].memorySize", is(gpuGtx1060.getMemorySize()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final Gpu saved = gpuRepository.save(
                mapper.convertFromDto(gpuGtx1060)
        );
        assertThat(gpuRepository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_GPUS + "/{id}",
                saved.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(gpuGtx1060.getName())),
                        jsonPath("$.manufacturer.id", is(manufacturerNvidia.getId().toString())),
                        jsonPath("$.manufacturer.name", is(manufacturerNvidia.getName())),
                        jsonPath("$.memoryType.id", is(memoryTypeGddr5.getId().toString())),
                        jsonPath("$.memoryType.name", is(memoryTypeGddr5.getName())),
                        jsonPath("$.powerConsumption", is(gpuGtx1060.getPowerConsumption())),
                        jsonPath("$.memorySize", is(gpuGtx1060.getMemorySize()))
                );
    }

    @Test
    void getById_withNonExistentId_shouldReturnError() throws Exception {
        // given
        gpuRepository.save(
                mapper.convertFromDto(gpuGtx1060)
        );
        assertThat(gpuRepository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_GPUS + "/{id}",
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
                                        "GPU with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentEntity_shouldReturnCreatedEntity() throws Exception {
        // given
        gpuRepository.save(
                mapper.convertFromDto(gpuRx5700)
        );
        assertThat(gpuRepository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(gpuGtx1060);
        final var requestBuilder = post(URL_API_V1_GPUS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(gpuGtx1060.getName())),
                        jsonPath("$.manufacturer.id", is(manufacturerNvidia.getId().toString())),
                        jsonPath("$.manufacturer.name", is(manufacturerNvidia.getName())),
                        jsonPath("$.memoryType.id", is(memoryTypeGddr5.getId().toString())),
                        jsonPath("$.memoryType.name", is(memoryTypeGddr5.getName())),
                        jsonPath("$.powerConsumption", is(gpuGtx1060.getPowerConsumption())),
                        jsonPath("$.memorySize", is(gpuGtx1060.getMemorySize()))
                );

        final List<Gpu> gpus = gpuRepository.findAll();
        assertThat(gpus).hasSize(2);
        final Gpu gpu = gpus.get(1);
        assertThat(gpu.getId()).isNotNull();
        assertThat(gpu.getName())
                .isEqualTo(gpuGtx1060.getName());
        assertThat(gpu.getManufacturer().getId())
                .isEqualTo(manufacturerNvidia.getId());
        assertThat(gpu.getManufacturer().getName())
                .isEqualTo(manufacturerNvidia.getName());
        assertThat(gpu.getMemoryType().getId())
                .isEqualTo(memoryTypeGddr5.getId());
        assertThat(gpu.getMemoryType().getName())
                .isEqualTo(memoryTypeGddr5.getName());
        assertThat(gpu.getPowerConsumption())
                .isEqualTo(gpuGtx1060.getPowerConsumption());
        assertThat(gpu.getMemorySize())
                .isEqualTo(gpuGtx1060.getMemorySize());
    }

    @Test
    void create_withNonExistentManufacturerId_shouldReturnError() throws Exception {
        // given
        gpuRepository.save(
                mapper.convertFromDto(gpuRx5700)
        );
        assertThat(gpuRepository.findAll()).hasSize(1);
        final UUID nonExistentManufacturerId = UUID.randomUUID();
        gpuGtx1060.setManufacturerId(nonExistentManufacturerId);
        final String jsonRequest = objectMapper.writeValueAsString(gpuGtx1060);
        final var requestBuilder = post(URL_API_V1_GPUS)
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
                                        "Manufacturer with ID = <{0}> not found!",
                                        nonExistentManufacturerId
                                )
                        ))
                );

        assertThat(gpuRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withNonExistentMemoryTypeId_shouldReturnError() throws Exception {
        // given
        gpuRepository.save(
                mapper.convertFromDto(gpuRx5700)
        );
        assertThat(gpuRepository.findAll()).hasSize(1);
        final UUID nonExistentMemoryTypeId = UUID.randomUUID();
        gpuGtx1060.setMemoryTypeId(nonExistentMemoryTypeId);
        final String jsonRequest = objectMapper.writeValueAsString(gpuGtx1060);
        final var requestBuilder = post(URL_API_V1_GPUS)
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
                                        "Video memory type with ID = <{0}> not found!",
                                        nonExistentMemoryTypeId
                                )
                        ))
                );

        assertThat(gpuRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectManufacturerParam_shouldReturnError() throws Exception {
        // given
        gpuRepository.save(
                mapper.convertFromDto(gpuRx5700)
        );
        assertThat(gpuRepository.findAll()).hasSize(1);
        gpuGtx1060.setManufacturerId(null);
        final String jsonRequest = objectMapper.writeValueAsString(gpuGtx1060);
        final var requestBuilder = post(URL_API_V1_GPUS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("manufacturer")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        assertThat(gpuRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectMemoryTypeParam_shouldReturnError() throws Exception {
        // given
        gpuRepository.save(
                mapper.convertFromDto(gpuRx5700)
        );
        assertThat(gpuRepository.findAll()).hasSize(1);
        gpuGtx1060.setMemoryTypeId(null);
        final String jsonRequest = objectMapper.writeValueAsString(gpuGtx1060);
        final var requestBuilder = post(URL_API_V1_GPUS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("memoryType")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        assertThat(gpuRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withExistentEntity_shouldReturnError() throws Exception {
        // given
        gpuRepository.save(
                mapper.convertFromDto(gpuGtx1060)
        );
        assertThat(gpuRepository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(gpuGtx1060);
        final var requestBuilder = post(URL_API_V1_GPUS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("name", "memorySize", "memoryType")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "GPU with name <{0}> memory size <{1}> and memory type <{2}> already exists!",
                                        gpuGtx1060.getName(),
                                        gpuGtx1060.getMemorySize(),
                                        memoryTypeGddr5.getName()
                                )
                        ))
                );

        assertThat(gpuRepository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentEntity_shouldReturnReplacedEntity() throws Exception {
        // given
        gpuRepository.save(
                mapper.convertFromDto(gpuGtx1060)
        );
        final Gpu saved = gpuRepository.save(
                mapper.convertFromDto(gpuRx5700)
        );
        assertThat(gpuRepository.findAll()).hasSize(2);
        final String newName = "Arc A770";
        final GpuRequestDto dto = GpuRequestDto.builder()
                .name(newName)
                .manufacturerId(manufacturerNvidia.getId())
                .memoryTypeId(memoryTypeGddr5.getId())
                .powerConsumption(gpuGtx1060.getPowerConsumption())
                .memorySize(gpuGtx1060.getMemorySize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_GPUS + "/{id}",
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
                        jsonPath("$.manufacturer.id", is(manufacturerNvidia.getId().toString())),
                        jsonPath("$.manufacturer.name", is(manufacturerNvidia.getName())),
                        jsonPath("$.memoryType.id", is(memoryTypeGddr5.getId().toString())),
                        jsonPath("$.memoryType.name", is(memoryTypeGddr5.getName())),
                        jsonPath("$.powerConsumption", is(gpuGtx1060.getPowerConsumption())),
                        jsonPath("$.memorySize", is(gpuGtx1060.getMemorySize()))
                );

        final Optional<Gpu> optGpu = gpuRepository.findById(saved.getId());
        assertThat(optGpu).isPresent();
        assertThat(optGpu.get().getName())
                .isEqualTo(newName);
        assertThat(optGpu.get().getManufacturer().getId())
                .isEqualTo(manufacturerNvidia.getId());
        assertThat(optGpu.get().getManufacturer().getName())
                .isEqualTo(manufacturerNvidia.getName());
        assertThat(optGpu.get().getMemoryType().getId())
                .isEqualTo(memoryTypeGddr5.getId());
        assertThat(optGpu.get().getMemoryType().getName())
                .isEqualTo(memoryTypeGddr5.getName());
        assertThat(optGpu.get().getPowerConsumption())
                .isEqualTo(gpuGtx1060.getPowerConsumption());
        assertThat(optGpu.get().getMemorySize())
                .isEqualTo(gpuGtx1060.getMemorySize());
    }

    @Test
    void replace_withNonExistentManufacturerId_shouldReturnError() throws Exception {
        // given
        gpuRepository.save(
                mapper.convertFromDto(gpuGtx1060)
        );
        final Gpu saved = gpuRepository.save(
                mapper.convertFromDto(gpuRx5700)
        );
        assertThat(gpuRepository.findAll()).hasSize(2);
        final String newName = "Arc A770";
        final UUID nonExistentManufacturerId = UUID.randomUUID();
        final GpuRequestDto dto = GpuRequestDto.builder()
                .name(newName)
                .manufacturerId(nonExistentManufacturerId)
                .memoryTypeId(memoryTypeGddr5.getId())
                .powerConsumption(gpuGtx1060.getPowerConsumption())
                .memorySize(gpuGtx1060.getMemorySize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_GPUS + "/{id}",
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
                                        "Manufacturer with ID = <{0}> not found!",
                                        nonExistentManufacturerId
                                )
                        ))
                );

        final Optional<Gpu> optGpu = gpuRepository.findById(saved.getId());
        assertThat(optGpu).isPresent();
        assertThat(optGpu.get().getName())
                .isEqualTo(gpuRx5700.getName());
        assertThat(optGpu.get().getManufacturer().getId())
                .isEqualTo(manufacturerAmd.getId());
        assertThat(optGpu.get().getManufacturer().getName())
                .isEqualTo(manufacturerAmd.getName());
        assertThat(optGpu.get().getMemoryType().getId())
                .isEqualTo(memoryTypeGddr6.getId());
        assertThat(optGpu.get().getMemoryType().getName())
                .isEqualTo(memoryTypeGddr6.getName());
        assertThat(optGpu.get().getPowerConsumption())
                .isEqualTo(gpuRx5700.getPowerConsumption());
        assertThat(optGpu.get().getMemorySize())
                .isEqualTo(gpuRx5700.getMemorySize());
    }

    @Test
    void replace_withNonExistentMemoryTypeId_shouldReturnError() throws Exception {
        // given
        gpuRepository.save(
                mapper.convertFromDto(gpuGtx1060)
        );
        final Gpu saved = gpuRepository.save(
                mapper.convertFromDto(gpuRx5700)
        );
        assertThat(gpuRepository.findAll()).hasSize(2);
        final String newName = "Arc A770";
        final UUID nonExistentMemoryTypeId = UUID.randomUUID();
        final GpuRequestDto dto = GpuRequestDto.builder()
                .name(newName)
                .manufacturerId(manufacturerNvidia.getId())
                .memoryTypeId(nonExistentMemoryTypeId)
                .powerConsumption(gpuGtx1060.getPowerConsumption())
                .memorySize(gpuGtx1060.getMemorySize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_GPUS + "/{id}",
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
                                        "Video memory type with ID = <{0}> not found!",
                                        nonExistentMemoryTypeId
                                )
                        ))
                );

        final Optional<Gpu> optGpu = gpuRepository.findById(saved.getId());
        assertThat(optGpu).isPresent();
        assertThat(optGpu.get().getName())
                .isEqualTo(gpuRx5700.getName());
        assertThat(optGpu.get().getManufacturer().getId())
                .isEqualTo(manufacturerAmd.getId());
        assertThat(optGpu.get().getManufacturer().getName())
                .isEqualTo(manufacturerAmd.getName());
        assertThat(optGpu.get().getMemoryType().getId())
                .isEqualTo(memoryTypeGddr6.getId());
        assertThat(optGpu.get().getMemoryType().getName())
                .isEqualTo(memoryTypeGddr6.getName());
        assertThat(optGpu.get().getPowerConsumption())
                .isEqualTo(gpuRx5700.getPowerConsumption());
        assertThat(optGpu.get().getMemorySize())
                .isEqualTo(gpuRx5700.getMemorySize());
    }

    @Test
    void replace_withIncorrectManufacturerParam_shouldReturnError() throws Exception {
        // given
        gpuRepository.save(
                mapper.convertFromDto(gpuGtx1060)
        );
        final Gpu saved = gpuRepository.save(
                mapper.convertFromDto(gpuRx5700)
        );
        assertThat(gpuRepository.findAll()).hasSize(2);
        final String newName = "Arc A770";
        final GpuRequestDto dto = GpuRequestDto.builder()
                .name(newName)
                .manufacturerId(null)
                .memoryTypeId(memoryTypeGddr5.getId())
                .powerConsumption(gpuGtx1060.getPowerConsumption())
                .memorySize(gpuGtx1060.getMemorySize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_GPUS + "/{id}",
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
                        jsonPath("$.violations[0].paramNames", contains("manufacturer")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        final Optional<Gpu> optGpu = gpuRepository.findById(saved.getId());
        assertThat(optGpu).isPresent();
        assertThat(optGpu.get().getName())
                .isEqualTo(gpuRx5700.getName());
        assertThat(optGpu.get().getManufacturer().getId())
                .isEqualTo(manufacturerAmd.getId());
        assertThat(optGpu.get().getManufacturer().getName())
                .isEqualTo(manufacturerAmd.getName());
        assertThat(optGpu.get().getMemoryType().getId())
                .isEqualTo(memoryTypeGddr6.getId());
        assertThat(optGpu.get().getMemoryType().getName())
                .isEqualTo(memoryTypeGddr6.getName());
        assertThat(optGpu.get().getPowerConsumption())
                .isEqualTo(gpuRx5700.getPowerConsumption());
        assertThat(optGpu.get().getMemorySize())
                .isEqualTo(gpuRx5700.getMemorySize());
    }

    @Test
    void replace_withIncorrectMemoryTypeParam_shouldReturnError() throws Exception {
        // given
        gpuRepository.save(
                mapper.convertFromDto(gpuGtx1060)
        );
        final Gpu saved = gpuRepository.save(
                mapper.convertFromDto(gpuRx5700)
        );
        assertThat(gpuRepository.findAll()).hasSize(2);
        final String newName = "Arc A770";
        final GpuRequestDto dto = GpuRequestDto.builder()
                .name(newName)
                .memoryTypeId(null)
                .manufacturerId(manufacturerNvidia.getId())
                .powerConsumption(gpuGtx1060.getPowerConsumption())
                .memorySize(gpuGtx1060.getMemorySize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_GPUS + "/{id}",
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
                        jsonPath("$.violations[0].paramNames", contains("memoryType")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        final Optional<Gpu> optGpu = gpuRepository.findById(saved.getId());
        assertThat(optGpu).isPresent();
        assertThat(optGpu.get().getName())
                .isEqualTo(gpuRx5700.getName());
        assertThat(optGpu.get().getManufacturer().getId())
                .isEqualTo(manufacturerAmd.getId());
        assertThat(optGpu.get().getManufacturer().getName())
                .isEqualTo(manufacturerAmd.getName());
        assertThat(optGpu.get().getMemoryType().getId())
                .isEqualTo(memoryTypeGddr6.getId());
        assertThat(optGpu.get().getMemoryType().getName())
                .isEqualTo(memoryTypeGddr6.getName());
        assertThat(optGpu.get().getPowerConsumption())
                .isEqualTo(gpuRx5700.getPowerConsumption());
        assertThat(optGpu.get().getMemorySize())
                .isEqualTo(gpuRx5700.getMemorySize());
    }

    @Test
    void replace_withExistentEntity_shouldReturnError() throws Exception {
        // given
        gpuRepository.save(
                mapper.convertFromDto(gpuGtx1060)
        );
        final Gpu saved = gpuRepository.save(
                mapper.convertFromDto(gpuRx5700)
        );
        assertThat(gpuRepository.findAll()).hasSize(2);
        final GpuRequestDto dto = GpuRequestDto.builder()
                .name(gpuGtx1060.getName())
                .manufacturerId(manufacturerNvidia.getId())
                .memoryTypeId(memoryTypeGddr5.getId())
                .powerConsumption(gpuGtx1060.getPowerConsumption())
                .memorySize(gpuGtx1060.getMemorySize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_GPUS + "/{id}",
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
                        jsonPath("$.violations[0].paramNames", contains("name", "memorySize", "memoryType")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "GPU with name <{0}> memory size <{1}> and memory type <{2}> already exists!",
                                        gpuGtx1060.getName(),
                                        gpuGtx1060.getMemorySize(),
                                        memoryTypeGddr5.getName()
                                )
                        ))
                );

        final Optional<Gpu> optGpu = gpuRepository.findById(saved.getId());
        assertThat(optGpu).isPresent();
        assertThat(optGpu.get().getName())
                .isEqualTo(gpuRx5700.getName());
        assertThat(optGpu.get().getManufacturer().getId())
                .isEqualTo(manufacturerAmd.getId());
        assertThat(optGpu.get().getManufacturer().getName())
                .isEqualTo(manufacturerAmd.getName());
        assertThat(optGpu.get().getMemoryType().getId())
                .isEqualTo(memoryTypeGddr6.getId());
        assertThat(optGpu.get().getMemoryType().getName())
                .isEqualTo(memoryTypeGddr6.getName());
        assertThat(optGpu.get().getPowerConsumption())
                .isEqualTo(gpuRx5700.getPowerConsumption());
        assertThat(optGpu.get().getMemorySize())
                .isEqualTo(gpuRx5700.getMemorySize());
    }

    @Test
    void update_withNonExistentEntity_shouldReturnUpdatedEntity() throws Exception {
        // given
        gpuRepository.save(
                mapper.convertFromDto(gpuGtx1060)
        );
        final Gpu saved = gpuRepository.save(
                mapper.convertFromDto(gpuRx5700)
        );
        assertThat(gpuRepository.findAll()).hasSize(2);
        final String newName = "Arc A770";
        final GpuRequestDto dto = GpuRequestDto.builder()
                .name(newName)
                .manufacturerId(manufacturerNvidia.getId())
                .memoryTypeId(memoryTypeGddr5.getId())
                .powerConsumption(gpuGtx1060.getPowerConsumption())
                .memorySize(gpuGtx1060.getMemorySize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_GPUS + "/{id}",
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
                        jsonPath("$.manufacturer.id", is(manufacturerNvidia.getId().toString())),
                        jsonPath("$.manufacturer.name", is(manufacturerNvidia.getName())),
                        jsonPath("$.memoryType.id", is(memoryTypeGddr5.getId().toString())),
                        jsonPath("$.memoryType.name", is(memoryTypeGddr5.getName())),
                        jsonPath("$.powerConsumption", is(gpuGtx1060.getPowerConsumption())),
                        jsonPath("$.memorySize", is(gpuGtx1060.getMemorySize()))
                );

        final Optional<Gpu> optGpu = gpuRepository.findById(saved.getId());
        assertThat(optGpu).isPresent();
        assertThat(optGpu.get().getName())
                .isEqualTo(newName);
        assertThat(optGpu.get().getManufacturer().getId())
                .isEqualTo(manufacturerNvidia.getId());
        assertThat(optGpu.get().getManufacturer().getName())
                .isEqualTo(manufacturerNvidia.getName());
        assertThat(optGpu.get().getMemoryType().getId())
                .isEqualTo(memoryTypeGddr5.getId());
        assertThat(optGpu.get().getMemoryType().getName())
                .isEqualTo(memoryTypeGddr5.getName());
        assertThat(optGpu.get().getPowerConsumption())
                .isEqualTo(gpuGtx1060.getPowerConsumption());
        assertThat(optGpu.get().getMemorySize())
                .isEqualTo(gpuGtx1060.getMemorySize());
    }

    @Test
    void update_withNonExistentManufacturerId_shouldReturnError() throws Exception {
        // given
        gpuRepository.save(
                mapper.convertFromDto(gpuGtx1060)
        );
        final Gpu saved = gpuRepository.save(
                mapper.convertFromDto(gpuRx5700)
        );
        assertThat(gpuRepository.findAll()).hasSize(2);
        final String newName = "Arc A770";
        final UUID nonExistentManufacturerId = UUID.randomUUID();
        final GpuRequestDto dto = GpuRequestDto.builder()
                .name(newName)
                .manufacturerId(nonExistentManufacturerId)
                .memoryTypeId(memoryTypeGddr5.getId())
                .powerConsumption(gpuGtx1060.getPowerConsumption())
                .memorySize(gpuGtx1060.getMemorySize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_GPUS + "/{id}",
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
                                        "Manufacturer with ID = <{0}> not found!",
                                        nonExistentManufacturerId
                                )
                        ))
                );

        final Optional<Gpu> optGpu = gpuRepository.findById(saved.getId());
        assertThat(optGpu).isPresent();
        assertThat(optGpu.get().getName())
                .isEqualTo(gpuRx5700.getName());
        assertThat(optGpu.get().getManufacturer().getId())
                .isEqualTo(manufacturerAmd.getId());
        assertThat(optGpu.get().getManufacturer().getName())
                .isEqualTo(manufacturerAmd.getName());
        assertThat(optGpu.get().getMemoryType().getId())
                .isEqualTo(memoryTypeGddr6.getId());
        assertThat(optGpu.get().getMemoryType().getName())
                .isEqualTo(memoryTypeGddr6.getName());
        assertThat(optGpu.get().getPowerConsumption())
                .isEqualTo(gpuRx5700.getPowerConsumption());
        assertThat(optGpu.get().getMemorySize())
                .isEqualTo(gpuRx5700.getMemorySize());
    }

    @Test
    void update_withNonExistentMemoryTypeId_shouldReturnError() throws Exception {
        // given
        gpuRepository.save(
                mapper.convertFromDto(gpuGtx1060)
        );
        final Gpu saved = gpuRepository.save(
                mapper.convertFromDto(gpuRx5700)
        );
        assertThat(gpuRepository.findAll()).hasSize(2);
        final String newName = "Arc A770";
        final UUID nonExistentMemoryTypeId = UUID.randomUUID();
        final GpuRequestDto dto = GpuRequestDto.builder()
                .name(newName)
                .manufacturerId(manufacturerNvidia.getId())
                .memoryTypeId(nonExistentMemoryTypeId)
                .powerConsumption(gpuGtx1060.getPowerConsumption())
                .memorySize(gpuGtx1060.getMemorySize())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_GPUS + "/{id}",
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
                                        "Video memory type with ID = <{0}> not found!",
                                        nonExistentMemoryTypeId
                                )
                        ))
                );

        final Optional<Gpu> optGpu = gpuRepository.findById(saved.getId());
        assertThat(optGpu).isPresent();
        assertThat(optGpu.get().getName())
                .isEqualTo(gpuRx5700.getName());
        assertThat(optGpu.get().getManufacturer().getId())
                .isEqualTo(manufacturerAmd.getId());
        assertThat(optGpu.get().getManufacturer().getName())
                .isEqualTo(manufacturerAmd.getName());
        assertThat(optGpu.get().getMemoryType().getId())
                .isEqualTo(memoryTypeGddr6.getId());
        assertThat(optGpu.get().getMemoryType().getName())
                .isEqualTo(memoryTypeGddr6.getName());
        assertThat(optGpu.get().getPowerConsumption())
                .isEqualTo(gpuRx5700.getPowerConsumption());
        assertThat(optGpu.get().getMemorySize())
                .isEqualTo(gpuRx5700.getMemorySize());
    }

    @Test
    void update_withExistentEntity_shouldReturnError() throws Exception {
        // given
        gpuRepository.save(
                mapper.convertFromDto(gpuRx5700)
        );
        assertThat(gpuRepository.findAll()).hasSize(1);
        final GpuRequestDto dto = GpuRequestDto.builder()
                .name(gpuRx5700.getName())
                .manufacturerId(manufacturerAmd.getId())
                .memoryTypeId(memoryTypeGddr6.getId())
                .powerConsumption(gpuRx5700.getPowerConsumption())
                .memorySize(gpuRx5700.getMemorySize())
                .build();

        final Gpu saved = gpuRepository.save(
                mapper.convertFromDto(gpuGtx1060)
        );
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_GPUS + "/{id}",
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
                        jsonPath("$.violations[0].paramNames", contains("name", "memorySize", "memoryType")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "GPU with name <{0}> memory size <{1}> and memory type <{2}> already exists!",
                                        gpuRx5700.getName(),
                                        gpuRx5700.getMemorySize(),
                                        memoryTypeGddr6.getName()
                                )
                        ))
                );

        final Optional<Gpu> optGpu = gpuRepository.findById(saved.getId());
        assertThat(optGpu).isPresent();
        assertThat(optGpu.get().getName())
                .isEqualTo(gpuGtx1060.getName());
        assertThat(optGpu.get().getManufacturer().getId())
                .isEqualTo(manufacturerNvidia.getId());
        assertThat(optGpu.get().getManufacturer().getName())
                .isEqualTo(manufacturerNvidia.getName());
        assertThat(optGpu.get().getMemoryType().getId())
                .isEqualTo(memoryTypeGddr5.getId());
        assertThat(optGpu.get().getMemoryType().getName())
                .isEqualTo(memoryTypeGddr5.getName());
        assertThat(optGpu.get().getPowerConsumption())
                .isEqualTo(gpuGtx1060.getPowerConsumption());
        assertThat(optGpu.get().getMemorySize())
                .isEqualTo(gpuGtx1060.getMemorySize());

    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID gpuGtx1060Id = gpuRepository.save(
                mapper.convertFromDto(gpuGtx1060)
        ).getId();
        assertThat(gpuRepository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_GPUS + "/{id}",
                gpuGtx1060Id
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(gpuRepository.findAll()).isEmpty();
    }
}
