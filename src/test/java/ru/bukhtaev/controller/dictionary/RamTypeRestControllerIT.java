package ru.bukhtaev.controller.dictionary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.controller.AbstractIntegrationTest;
import ru.bukhtaev.dto.mapper.dictionary.IRamTypeMapper;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.model.dictionary.RamType;
import ru.bukhtaev.repository.dictionary.IRamTypeRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.dictionary.RamTypeRestController.URL_API_V1_RAM_TYPES;

/**
 * Интеграционные тесты для CRUD операций над типами оперативной памяти.
 */
class RamTypeRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO типов оперативной памяти.
     */
    @Autowired
    private IRamTypeMapper mapper;

    /**
     * Репозиторий типов оперативной памяти.
     */
    @Autowired
    private IRamTypeRepository repository;

    private NameableRequestDto typeDdr4;
    private NameableRequestDto typeDdr5;

    @BeforeEach
    void setUp() {
        typeDdr4 = NameableRequestDto.builder()
                .name("DDR4")
                .build();
        typeDdr5 = NameableRequestDto.builder()
                .name("DDR5")
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
                mapper.convertFromDto(typeDdr4)
        );
        repository.save(
                mapper.convertFromDto(typeDdr5)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_RAM_TYPES);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].name", is(typeDdr4.getName())),
                        jsonPath("$[1].name", is(typeDdr5.getName()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final RamType saved = repository.save(
                mapper.convertFromDto(typeDdr4)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_RAM_TYPES + "/{id}",
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
                mapper.convertFromDto(typeDdr4)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_RAM_TYPES + "/{id}",
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
                                        "RAM type with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentName_shouldReturnCreatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(typeDdr5)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(typeDdr4);
        final var requestBuilder = post(URL_API_V1_RAM_TYPES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(typeDdr4.getName()))
                );

        final List<RamType> types = repository.findAll();
        assertThat(types).hasSize(2);
        final RamType type = types.get(1);
        assertThat(type.getId()).isNotNull();
        assertThat(type.getName()).isEqualTo(typeDdr4.getName());
    }

    @Test
    void create_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(typeDdr4)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(typeDdr4);
        final var requestBuilder = post(URL_API_V1_RAM_TYPES)
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
                                        "RAM type with name <{0}> already exists!",
                                        typeDdr4.getName()
                                )
                        ))
                );

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentName_shouldReturnReplacedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(typeDdr4)
        );
        final RamType saved = repository.save(
                mapper.convertFromDto(typeDdr5)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "DDR3";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_RAM_TYPES + "/{id}",
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

        final Optional<RamType> optType = repository.findById(saved.getId());
        assertThat(optType).isPresent();
        assertThat(optType.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(typeDdr4)
        );
        final RamType saved = repository.save(
                mapper.convertFromDto(typeDdr5)
        );
        assertThat(repository.findAll()).hasSize(2);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(typeDdr4.getName())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_RAM_TYPES + "/{id}",
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
                                        "RAM type with name <{0}> already exists!",
                                        typeDdr4.getName()
                                )
                        ))
                );

        final Optional<RamType> optType = repository.findById(saved.getId());
        assertThat(optType).isPresent();
        assertThat(optType.get().getName())
                .isEqualTo(typeDdr5.getName());
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(typeDdr4)
        );
        final RamType saved = repository.save(
                mapper.convertFromDto(typeDdr5)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "DDR3";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_RAM_TYPES + "/{id}",
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

        final Optional<RamType> optType = repository.findById(saved.getId());
        assertThat(optType).isPresent();
        assertThat(optType.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(typeDdr5)
        );
        assertThat(repository.findAll()).hasSize(1);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(typeDdr5.getName())
                .build();

        final UUID typeDdr4Id = repository.save(
                mapper.convertFromDto(typeDdr4)
        ).getId();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_RAM_TYPES + "/{id}",
                typeDdr4Id)
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
                                        "RAM type with name <{0}> already exists!",
                                        typeDdr5.getName()
                                )
                        ))
                );

        final Optional<RamType> optType = repository.findById(typeDdr4Id);
        assertThat(optType).isPresent();
        assertThat(optType.get().getName())
                .isEqualTo(typeDdr4.getName());
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID typeDdr4Id = repository.save(
                mapper.convertFromDto(typeDdr4)
        ).getId();
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_RAM_TYPES + "/{id}",
                typeDdr4Id
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(repository.findAll()).isEmpty();
    }
}
