package ru.bukhtaev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.dto.mapper.IStorageConnectorMapper;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.model.StorageConnector;
import ru.bukhtaev.repository.IStorageConnectorRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.StorageConnectorRestController.URL_API_V1_STORAGE_CONNECTORS;

/**
 * Интеграционные тесты для CRUD операций над коннекторами подключения накопителя.
 */
class StorageConnectorRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO коннекторов подключения накопителей.
     */
    @Autowired
    private IStorageConnectorMapper mapper;

    /**
     * Репозиторий коннекторов подключения накопителей.
     */
    @Autowired
    private IStorageConnectorRepository repository;

    private NameableRequestDto connectorSata3;
    private NameableRequestDto connectorSata2;

    @BeforeEach
    void setUp() {
        connectorSata3 = NameableRequestDto.builder()
                .name("SATA 3")
                .build();
        connectorSata2 = NameableRequestDto.builder()
                .name("SATA 2")
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
                mapper.convertFromDto(connectorSata3)
        );
        repository.save(
                mapper.convertFromDto(connectorSata2)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_STORAGE_CONNECTORS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].name", is(connectorSata3.getName())),
                        jsonPath("$[1].name", is(connectorSata2.getName()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final StorageConnector saved = repository.save(
                mapper.convertFromDto(connectorSata3)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_STORAGE_CONNECTORS + "/{id}",
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
                mapper.convertFromDto(connectorSata3)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_STORAGE_CONNECTORS + "/{id}",
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
                                        "Storage connector with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentName_shouldReturnCreatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connectorSata2)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(connectorSata3);
        final var requestBuilder = post(URL_API_V1_STORAGE_CONNECTORS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(connectorSata3.getName()))
                );

        final List<StorageConnector> connectors = repository.findAll();
        assertThat(connectors).hasSize(2);
        final StorageConnector connector = connectors.get(1);
        assertThat(connector.getId()).isNotNull();
        assertThat(connector.getName()).isEqualTo(connectorSata3.getName());
    }

    @Test
    void create_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connectorSata3)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(connectorSata3);
        final var requestBuilder = post(URL_API_V1_STORAGE_CONNECTORS)
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
                                        "Storage connector with name <{0}> already exists!",
                                        connectorSata3.getName()
                                )
                        ))
                );

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentName_shouldReturnReplacedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connectorSata3)
        );
        final StorageConnector saved = repository.save(
                mapper.convertFromDto(connectorSata2)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "SATA 1";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_STORAGE_CONNECTORS + "/{id}",
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

        final Optional<StorageConnector> optConnector = repository.findById(saved.getId());
        assertThat(optConnector).isPresent();
        assertThat(optConnector.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connectorSata3)
        );
        final StorageConnector saved = repository.save(
                mapper.convertFromDto(connectorSata2)
        );
        assertThat(repository.findAll()).hasSize(2);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(connectorSata3.getName())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_STORAGE_CONNECTORS + "/{id}",
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
                                        "Storage connector with name <{0}> already exists!",
                                        connectorSata3.getName()
                                )
                        ))
                );

        final Optional<StorageConnector> optConnector = repository.findById(saved.getId());
        assertThat(optConnector).isPresent();
        assertThat(optConnector.get().getName())
                .isEqualTo(connectorSata2.getName());
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connectorSata3)
        );
        final StorageConnector saved = repository.save(
                mapper.convertFromDto(connectorSata2)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "SATA 1";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_STORAGE_CONNECTORS + "/{id}",
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

        final Optional<StorageConnector> optConnector = repository.findById(saved.getId());
        assertThat(optConnector).isPresent();
        assertThat(optConnector.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connectorSata2)
        );
        assertThat(repository.findAll()).hasSize(1);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(connectorSata2.getName())
                .build();

        final UUID connectorSata3Id = repository.save(
                mapper.convertFromDto(connectorSata3)
        ).getId();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_STORAGE_CONNECTORS + "/{id}",
                connectorSata3Id)
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
                                        "Storage connector with name <{0}> already exists!",
                                        connectorSata2.getName()
                                )
                        ))
                );

        final Optional<StorageConnector> optConnector = repository.findById(connectorSata3Id);
        assertThat(optConnector).isPresent();
        assertThat(optConnector.get().getName())
                .isEqualTo(connectorSata3.getName());
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID connectorSata3Id = repository.save(
                mapper.convertFromDto(connectorSata3)
        ).getId();
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_STORAGE_CONNECTORS + "/{id}",
                connectorSata3Id
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(repository.findAll()).isEmpty();
    }
}