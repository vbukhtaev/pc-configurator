package ru.bukhtaev.controller.dictionary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.controller.AbstractIntegrationTest;
import ru.bukhtaev.dto.mapper.dictionary.IStorageConnectorMapper;
import ru.bukhtaev.dto.request.dictionary.StorageConnectorRequestDto;
import ru.bukhtaev.model.dictionary.StorageConnector;
import ru.bukhtaev.repository.dictionary.IStorageConnectorRepository;
import ru.bukhtaev.service.TransactionService;

import java.text.MessageFormat;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.dictionary.StorageConnectorRestController.URL_API_V1_STORAGE_CONNECTORS;

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

    /**
     * Утилитный сервис выполнения кода в транзакции.
     */
    @Autowired
    private TransactionService transactionService;

    private StorageConnectorRequestDto connectorSata1;
    private StorageConnectorRequestDto connectorSata2;

    @BeforeEach
    void setUp() {
        connectorSata1 = StorageConnectorRequestDto.builder()
                .name("SATA 1")
                .compatibleConnectorIds(new HashSet<>())
                .build();
        connectorSata2 = StorageConnectorRequestDto.builder()
                .name("SATA 2")
                .compatibleConnectorIds(new HashSet<>())
                .build();
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void getAll_shouldReturnAllEntities() throws Exception {
        // given
        final var savedConnectorSata1 = repository.save(
                mapper.convertFromDto(connectorSata1)
        );
        connectorSata2.getCompatibleConnectorIds().add(savedConnectorSata1.getId());
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
                        jsonPath("$[0].name", is(connectorSata1.getName())),
                        jsonPath("$[0].compatibleConnectors", empty()),
                        jsonPath("$[1].name", is(connectorSata2.getName())),
                        jsonPath("$[1].compatibleConnectors", hasSize(1)),
                        jsonPath("$[1].compatibleConnectors[0].id",
                                is(savedConnectorSata1.getId().toString())),
                        jsonPath("$[1].compatibleConnectors[0].name",
                                is(connectorSata1.getName()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final var savedConnectorSata1 = repository.save(
                mapper.convertFromDto(connectorSata1)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_STORAGE_CONNECTORS + "/{id}",
                savedConnectorSata1.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(savedConnectorSata1.getName())),
                        jsonPath("$.compatibleConnectors", empty())
                );
    }

    @Test
    void getById_withNonExistentId_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connectorSata1)
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
        final var savedConnectorSata1 = repository.save(
                mapper.convertFromDto(connectorSata1)
        );
        connectorSata2.getCompatibleConnectorIds().add(savedConnectorSata1.getId());
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(connectorSata2);
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
                        jsonPath("$.name", is(connectorSata2.getName())),
                        jsonPath("$.compatibleConnectors", hasSize(1)),
                        jsonPath("$.compatibleConnectors[0].id",
                                is(savedConnectorSata1.getId().toString())),
                        jsonPath("$.compatibleConnectors[0].name",
                                is(connectorSata1.getName()))
                );

        transactionService.doInTransaction(true, () -> {
            final List<StorageConnector> connectors = repository.findAll();
            assertThat(connectors).hasSize(2);
            final StorageConnector connector = connectors.get(1);
            assertThat(connector.getId()).isNotNull();
            assertThat(connector.getName()).isEqualTo(connectorSata2.getName());
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnectorSata1);
        });
    }

    @Test
    void create_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connectorSata1)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(connectorSata1);
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
                                        connectorSata1.getName()
                                )
                        ))
                );

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentName_shouldReturnReplacedEntity() throws Exception {
        // given
        final var savedConnectorSata1 = repository.save(
                mapper.convertFromDto(connectorSata1)
        );
        connectorSata2.getCompatibleConnectorIds().add(savedConnectorSata1.getId());
        final var savedConnectorSata2 = repository.save(
                mapper.convertFromDto(connectorSata2)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "SATA 2.1";
        final var dto = StorageConnectorRequestDto.builder()
                .name(newName)
                .compatibleConnectorIds(Set.of(
                        savedConnectorSata1.getId()
                ))
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_STORAGE_CONNECTORS + "/{id}",
                savedConnectorSata2.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(newName)),
                        jsonPath("$.compatibleConnectors", hasSize(1)),
                        jsonPath("$.compatibleConnectors[0].id",
                                is(savedConnectorSata1.getId().toString())),
                        jsonPath("$.compatibleConnectors[0].name",
                                is(connectorSata1.getName()))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<StorageConnector> optConnector = repository.findById(savedConnectorSata2.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(newName);
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnectorSata1);
        });
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        final var savedConnectorSata1 = repository.save(
                mapper.convertFromDto(connectorSata1)
        );
        connectorSata2.getCompatibleConnectorIds().add(savedConnectorSata1.getId());
        final var savedConnectorSata2 = repository.save(
                mapper.convertFromDto(connectorSata2)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var dto = StorageConnectorRequestDto.builder()
                .name(connectorSata1.getName())
                .compatibleConnectorIds(new HashSet<>())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_STORAGE_CONNECTORS + "/{id}",
                savedConnectorSata2.getId())
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
                                        connectorSata1.getName()
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<StorageConnector> optConnector = repository.findById(savedConnectorSata2.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(savedConnectorSata2.getName());
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnectorSata1);
        });
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        final var savedConnectorSata1 = repository.save(
                mapper.convertFromDto(connectorSata1)
        );
        connectorSata2.getCompatibleConnectorIds().add(savedConnectorSata1.getId());
        final var savedConnectorSata2 = repository.save(
                mapper.convertFromDto(connectorSata2)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "SATA 2.1";
        final var dto = StorageConnectorRequestDto.builder()
                .name(newName)
                .compatibleConnectorIds(Set.of(
                        savedConnectorSata1.getId()
                ))
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_STORAGE_CONNECTORS + "/{id}",
                savedConnectorSata2.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(newName)),
                        jsonPath("$.compatibleConnectors", hasSize(1)),
                        jsonPath("$.compatibleConnectors[0].id",
                                is(savedConnectorSata1.getId().toString())),
                        jsonPath("$.compatibleConnectors[0].name",
                                is(connectorSata1.getName()))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<StorageConnector> optConnector = repository.findById(savedConnectorSata2.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(newName);
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnectorSata1);
        });
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        final var savedConnectorSata1 = repository.save(
                mapper.convertFromDto(connectorSata1)
        );
        connectorSata2.getCompatibleConnectorIds().add(savedConnectorSata1.getId());
        final var savedConnectorSata2 = repository.save(
                mapper.convertFromDto(connectorSata2)
        );
        assertThat(repository.findAll()).hasSize(2);

        final var dto = StorageConnectorRequestDto.builder()
                .name(connectorSata1.getName())
                .compatibleConnectorIds(new HashSet<>())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_STORAGE_CONNECTORS + "/{id}",
                savedConnectorSata2.getId())
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
                                        connectorSata1.getName()
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<StorageConnector> optConnector = repository.findById(savedConnectorSata2.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(savedConnectorSata2.getName());
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnectorSata1);
        });
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final var savedConnectorSata1 = repository.save(
                mapper.convertFromDto(connectorSata1)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_STORAGE_CONNECTORS + "/{id}",
                savedConnectorSata1.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(repository.findAll()).isEmpty();
    }
}
