package ru.bukhtaev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.dto.mapper.IManufacturerMapper;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.model.Manufacturer;
import ru.bukhtaev.repository.IManufacturerRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.ManufacturerRestController.URL_API_V1_MANUFACTURERS;

/**
 * Интеграционные тесты для CRUD операций над производителями.
 */
class ManufacturerRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO производителей.
     */
    @Autowired
    private IManufacturerMapper mapper;

    /**
     * Репозиторий производителей.
     */
    @Autowired
    private IManufacturerRepository repository;

    private NameableRequestDto manufacturerIntel;
    private NameableRequestDto manufacturerAmd;

    @BeforeEach
    void setUp() {
        manufacturerIntel = NameableRequestDto.builder()
                .name("Intel")
                .build();
        manufacturerAmd = NameableRequestDto.builder()
                .name("AMD")
                .build();
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void getAll_shouldReturnAllEntities() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(manufacturerIntel)
        );
        repository.save(
                mapper.convertFromDto(manufacturerAmd)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_MANUFACTURERS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].name", is(manufacturerIntel.getName())),
                        jsonPath("$[1].name", is(manufacturerAmd.getName()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final Manufacturer saved = repository.save(
                mapper.convertFromDto(manufacturerIntel)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_MANUFACTURERS + "/{id}",
                saved.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(saved.getName()))
                );
    }

    @Test
    void getById_withNonExistentId_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(manufacturerIntel)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_MANUFACTURERS + "/{id}",
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
                                        "Manufacturer with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentName_shouldReturnCreatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(manufacturerAmd)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(manufacturerIntel);
        final var requestBuilder = post(URL_API_V1_MANUFACTURERS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(manufacturerIntel.getName()))
                );

        final List<Manufacturer> manufacturers = repository.findAll();
        assertThat(manufacturers).hasSize(2);
        final Manufacturer manufacturer = manufacturers.get(1);
        assertThat(manufacturer.getId()).isNotNull();
        assertThat(manufacturer.getName()).isEqualTo(manufacturerIntel.getName());
    }

    @Test
    void create_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(manufacturerIntel)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(manufacturerIntel);
        final var requestBuilder = post(URL_API_V1_MANUFACTURERS)
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
                                        "Manufacturer with name <{0}> already exists!",
                                        manufacturerIntel.getName()
                                )
                        ))
                );

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentName_shouldReturnReplacedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(manufacturerIntel)
        );
        final Manufacturer saved = repository.save(
                mapper.convertFromDto(manufacturerAmd)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "Nvidia";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_MANUFACTURERS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(newName))
                );

        final Optional<Manufacturer> optManufacturer = repository.findById(saved.getId());
        assertThat(optManufacturer).isPresent();
        assertThat(optManufacturer.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(manufacturerIntel)
        );
        final Manufacturer saved = repository.save(
                mapper.convertFromDto(manufacturerAmd)
        );
        assertThat(repository.findAll()).hasSize(2);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(manufacturerIntel.getName())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_MANUFACTURERS + "/{id}",
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
                                        "Manufacturer with name <{0}> already exists!",
                                        manufacturerIntel.getName()
                                )
                        ))
                );

        final Optional<Manufacturer> optManufacturer = repository.findById(saved.getId());
        assertThat(optManufacturer).isPresent();
        assertThat(optManufacturer.get().getName())
                .isEqualTo(manufacturerAmd.getName());
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(manufacturerIntel)
        );
        final Manufacturer saved = repository.save(
                mapper.convertFromDto(manufacturerAmd)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "Nvidia";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_MANUFACTURERS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(newName))
                );

        final Optional<Manufacturer> optManufacturer = repository.findById(saved.getId());
        assertThat(optManufacturer).isPresent();
        assertThat(optManufacturer.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(manufacturerAmd)
        );
        assertThat(repository.findAll()).hasSize(1);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(manufacturerAmd.getName())
                .build();

        final UUID manufacturerIntelId = repository.save(
                mapper.convertFromDto(manufacturerIntel)
        ).getId();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_MANUFACTURERS + "/{id}",
                manufacturerIntelId)
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
                                        "Manufacturer with name <{0}> already exists!",
                                        manufacturerAmd.getName()
                                )
                        ))
                );

        final Optional<Manufacturer> optManufacturer = repository.findById(manufacturerIntelId);
        assertThat(optManufacturer).isPresent();
        assertThat(optManufacturer.get().getName())
                .isEqualTo(manufacturerIntel.getName());
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID manufacturerIntelId = repository.save(
                mapper.convertFromDto(manufacturerIntel)
        ).getId();
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_MANUFACTURERS + "/{id}",
                manufacturerIntelId
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(repository.findAll()).isEmpty();
    }
}
