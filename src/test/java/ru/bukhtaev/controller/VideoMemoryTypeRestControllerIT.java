package ru.bukhtaev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.dto.mapper.dictionary.IVideoMemoryTypeMapper;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.model.dictionary.VideoMemoryType;
import ru.bukhtaev.repository.dictionary.IVideoMemoryTypeRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.dictionary.VideoMemoryTypeRestController.URL_API_V1_VIDEO_MEMORY_TYPES;

/**
 * Интеграционные тесты для CRUD операций над типами видеопамяти.
 */
class VideoMemoryTypeRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO типов видеопамяти.
     */
    @Autowired
    private IVideoMemoryTypeMapper mapper;

    /**
     * Репозиторий типов видеопамяти.
     */
    @Autowired
    private IVideoMemoryTypeRepository repository;

    private NameableRequestDto typeGddr5;
    private NameableRequestDto typeGddr6X;

    @BeforeEach
    void setUp() {
        typeGddr5 = NameableRequestDto.builder()
                .name("GDDR5")
                .build();
        typeGddr6X = NameableRequestDto.builder()
                .name("GDDR6X")
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
                mapper.convertFromDto(typeGddr5)
        );
        repository.save(
                mapper.convertFromDto(typeGddr6X)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_VIDEO_MEMORY_TYPES);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].name", is(typeGddr5.getName())),
                        jsonPath("$[1].name", is(typeGddr6X.getName()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final VideoMemoryType saved = repository.save(
                mapper.convertFromDto(typeGddr5)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_VIDEO_MEMORY_TYPES + "/{id}",
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
                mapper.convertFromDto(typeGddr5)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_VIDEO_MEMORY_TYPES + "/{id}",
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
                                        "Video memory type with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentName_shouldReturnCreatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(typeGddr6X)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(typeGddr5);
        final var requestBuilder = post(URL_API_V1_VIDEO_MEMORY_TYPES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(typeGddr5.getName()))
                );

        final List<VideoMemoryType> types = repository.findAll();
        assertThat(types).hasSize(2);
        final VideoMemoryType type = types.get(1);
        assertThat(type.getId()).isNotNull();
        assertThat(type.getName()).isEqualTo(typeGddr5.getName());
    }

    @Test
    void create_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(typeGddr5)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(typeGddr5);
        final var requestBuilder = post(URL_API_V1_VIDEO_MEMORY_TYPES)
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
                                        "Video memory type with name <{0}> already exists!",
                                        typeGddr5.getName()
                                )
                        ))
                );

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentName_shouldReturnReplacedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(typeGddr5)
        );
        final VideoMemoryType saved = repository.save(
                mapper.convertFromDto(typeGddr6X)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "GDDR7";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_VIDEO_MEMORY_TYPES + "/{id}",
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

        final Optional<VideoMemoryType> optType = repository.findById(saved.getId());
        assertThat(optType).isPresent();
        assertThat(optType.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(typeGddr5)
        );
        final VideoMemoryType saved = repository.save(
                mapper.convertFromDto(typeGddr6X)
        );
        assertThat(repository.findAll()).hasSize(2);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(typeGddr5.getName())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_VIDEO_MEMORY_TYPES + "/{id}",
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
                                        "Video memory type with name <{0}> already exists!",
                                        typeGddr5.getName()
                                )
                        ))
                );

        final Optional<VideoMemoryType> optType = repository.findById(saved.getId());
        assertThat(optType).isPresent();
        assertThat(optType.get().getName())
                .isEqualTo(typeGddr6X.getName());
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(typeGddr5)
        );
        final VideoMemoryType saved = repository.save(
                mapper.convertFromDto(typeGddr6X)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "GDDR7";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_VIDEO_MEMORY_TYPES + "/{id}",
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

        final Optional<VideoMemoryType> optType = repository.findById(saved.getId());
        assertThat(optType).isPresent();
        assertThat(optType.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(typeGddr6X)
        );
        assertThat(repository.findAll()).hasSize(1);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(typeGddr6X.getName())
                .build();

        final UUID typeGddr5Id = repository.save(
                mapper.convertFromDto(typeGddr5)
        ).getId();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_VIDEO_MEMORY_TYPES + "/{id}",
                typeGddr5Id)
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
                                        "Video memory type with name <{0}> already exists!",
                                        typeGddr6X.getName()
                                )
                        ))
                );

        final Optional<VideoMemoryType> optType = repository.findById(typeGddr5Id);
        assertThat(optType).isPresent();
        assertThat(optType.get().getName())
                .isEqualTo(typeGddr5.getName());
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID typeGddr5Id = repository.save(
                mapper.convertFromDto(typeGddr5)
        ).getId();
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_VIDEO_MEMORY_TYPES + "/{id}",
                typeGddr5Id
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(repository.findAll()).isEmpty();
    }
}
