package ru.bukhtaev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.dto.mapper.IExpansionBayFormatMapper;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.model.dictionary.ExpansionBayFormat;
import ru.bukhtaev.repository.IExpansionBayFormatRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.ExpansionBayFormatRestController.URL_API_V1_EXPANSION_BAY_FORMATS;

/**
 * Интеграционные тесты для CRUD операций над форматами отсеков расширения.
 */
class ExpansionBayFormatRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO форматов отсеков расширения.
     */
    @Autowired
    private IExpansionBayFormatMapper mapper;

    /**
     * Репозиторий форматов отсеков расширения.
     */
    @Autowired
    private IExpansionBayFormatRepository repository;

    private NameableRequestDto format25;
    private NameableRequestDto format35;

    @BeforeEach
    void setUp() {
        format25 = NameableRequestDto.builder()
                .name("2.5")
                .build();
        format35 = NameableRequestDto.builder()
                .name("3.5")
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
                mapper.convertFromDto(format25)
        );
        repository.save(
                mapper.convertFromDto(format35)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_EXPANSION_BAY_FORMATS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].name", is(format25.getName())),
                        jsonPath("$[1].name", is(format35.getName()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final ExpansionBayFormat saved = repository.save(
                mapper.convertFromDto(format25)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_EXPANSION_BAY_FORMATS + "/{id}",
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
                mapper.convertFromDto(format25)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_EXPANSION_BAY_FORMATS + "/{id}",
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
                                        "Expansion bay format with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentName_shouldReturnCreatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(format35)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(format25);
        final var requestBuilder = post(URL_API_V1_EXPANSION_BAY_FORMATS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(format25.getName()))
                );

        final List<ExpansionBayFormat> formats = repository.findAll();
        assertThat(formats).hasSize(2);
        final ExpansionBayFormat format = formats.get(1);
        assertThat(format.getId()).isNotNull();
        assertThat(format.getName()).isEqualTo(format25.getName());
    }

    @Test
    void create_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(format25)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(format25);
        final var requestBuilder = post(URL_API_V1_EXPANSION_BAY_FORMATS)
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
                                        "Expansion bay format with name <{0}> already exists!",
                                        format25.getName()
                                )
                        ))
                );

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentName_shouldReturnReplacedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(format25)
        );
        final ExpansionBayFormat saved = repository.save(
                mapper.convertFromDto(format35)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "5.25";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_EXPANSION_BAY_FORMATS + "/{id}",
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

        final Optional<ExpansionBayFormat> optFormat = repository.findById(saved.getId());
        assertThat(optFormat).isPresent();
        assertThat(optFormat.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(format25)
        );
        final ExpansionBayFormat saved = repository.save(
                mapper.convertFromDto(format35)
        );
        assertThat(repository.findAll()).hasSize(2);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(format25.getName())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_EXPANSION_BAY_FORMATS + "/{id}",
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
                                        "Expansion bay format with name <{0}> already exists!",
                                        format25.getName()
                                )
                        ))
                );

        final Optional<ExpansionBayFormat> optFormat = repository.findById(saved.getId());
        assertThat(optFormat).isPresent();
        assertThat(optFormat.get().getName())
                .isEqualTo(format35.getName());
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(format25)
        );
        final ExpansionBayFormat saved = repository.save(
                mapper.convertFromDto(format35)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "5.25";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_EXPANSION_BAY_FORMATS + "/{id}",
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

        final Optional<ExpansionBayFormat> optFormat = repository.findById(saved.getId());
        assertThat(optFormat).isPresent();
        assertThat(optFormat.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(format35)
        );
        assertThat(repository.findAll()).hasSize(1);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(format35.getName())
                .build();

        final UUID format25Id = repository.save(
                mapper.convertFromDto(format25)
        ).getId();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_EXPANSION_BAY_FORMATS + "/{id}",
                format25Id)
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
                                        "Expansion bay format with name <{0}> already exists!",
                                        format35.getName()
                                )
                        ))
                );

        final Optional<ExpansionBayFormat> optFormat = repository.findById(format25Id);
        assertThat(optFormat).isPresent();
        assertThat(optFormat.get().getName())
                .isEqualTo(format25.getName());
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID format25Id = repository.save(
                mapper.convertFromDto(format25)
        ).getId();
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_EXPANSION_BAY_FORMATS + "/{id}",
                format25Id
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(repository.findAll()).isEmpty();
    }
}
