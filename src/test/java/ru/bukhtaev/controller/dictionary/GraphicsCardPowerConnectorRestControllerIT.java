package ru.bukhtaev.controller.dictionary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.controller.AbstractIntegrationTest;
import ru.bukhtaev.dto.mapper.dictionary.IGraphicsCardPowerConnectorMapper;
import ru.bukhtaev.dto.request.dictionary.GraphicsCardPowerConnectorRequestDto;
import ru.bukhtaev.model.dictionary.GraphicsCardPowerConnector;
import ru.bukhtaev.repository.dictionary.IGraphicsCardPowerConnectorRepository;
import ru.bukhtaev.service.TransactionService;

import java.text.MessageFormat;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.dictionary.GraphicsCardPowerConnectorRestController.URL_API_V1_GRAPHICS_CARD_POWER_CONNECTORS;

/**
 * Интеграционные тесты для CRUD операций над коннекторами питания видеокарт.
 */
class GraphicsCardPowerConnectorRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO коннекторов питания видеокарт.
     */
    @Autowired
    private IGraphicsCardPowerConnectorMapper mapper;

    /**
     * Репозиторий коннекторов питания видеокарт.
     */
    @Autowired
    private IGraphicsCardPowerConnectorRepository repository;

    /**
     * Утилитный сервис выполнения кода в транзакции.
     */
    @Autowired
    private TransactionService transactionService;

    private GraphicsCardPowerConnectorRequestDto connector6Plus2Pin;
    private GraphicsCardPowerConnectorRequestDto connector8Pin;

    @BeforeEach
    void setUp() {
        connector6Plus2Pin = GraphicsCardPowerConnectorRequestDto.builder()
                .name("6 + 2 pin")
                .compatibleConnectorIds(new HashSet<>())
                .build();
        connector8Pin = GraphicsCardPowerConnectorRequestDto.builder()
                .name("8 pin")
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
        final var savedConnector6Plus2Pin = repository.save(
                mapper.convertFromDto(connector6Plus2Pin)
        );
        connector8Pin.getCompatibleConnectorIds().add(savedConnector6Plus2Pin.getId());
        repository.save(
                mapper.convertFromDto(connector8Pin)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_GRAPHICS_CARD_POWER_CONNECTORS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].name", is(connector6Plus2Pin.getName())),
                        jsonPath("$[0].compatibleConnectors", empty()),
                        jsonPath("$[1].name", is(connector8Pin.getName())),
                        jsonPath("$[1].compatibleConnectors", hasSize(1)),
                        jsonPath("$[1].compatibleConnectors[0].id",
                                is(savedConnector6Plus2Pin.getId().toString())),
                        jsonPath("$[1].compatibleConnectors[0].name",
                                is(connector6Plus2Pin.getName()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final var savedConnector6Plus2Pin = repository.save(
                mapper.convertFromDto(connector6Plus2Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_GRAPHICS_CARD_POWER_CONNECTORS + "/{id}",
                savedConnector6Plus2Pin.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(savedConnector6Plus2Pin.getName())),
                        jsonPath("$.compatibleConnectors", empty())
                );
    }

    @Test
    void getById_withNonExistentId_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connector6Plus2Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_GRAPHICS_CARD_POWER_CONNECTORS + "/{id}",
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
                                        "Graphics card power connector with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentName_shouldReturnCreatedEntity() throws Exception {
        // given
        final var savedConnector6Plus2Pin = repository.save(
                mapper.convertFromDto(connector6Plus2Pin)
        );
        connector8Pin.getCompatibleConnectorIds().add(savedConnector6Plus2Pin.getId());
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(connector8Pin);
        final var requestBuilder = post(URL_API_V1_GRAPHICS_CARD_POWER_CONNECTORS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(connector8Pin.getName())),
                        jsonPath("$.compatibleConnectors", hasSize(1)),
                        jsonPath("$.compatibleConnectors[0].id",
                                is(savedConnector6Plus2Pin.getId().toString())),
                        jsonPath("$.compatibleConnectors[0].name",
                                is(connector6Plus2Pin.getName()))
                );

        transactionService.doInTransaction(true, () -> {
            final List<GraphicsCardPowerConnector> connectors = repository.findAll();
            assertThat(connectors).hasSize(2);
            final GraphicsCardPowerConnector connector = connectors.get(1);
            assertThat(connector.getId()).isNotNull();
            assertThat(connector.getName()).isEqualTo(connector8Pin.getName());
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector6Plus2Pin);
        });
    }

    @Test
    void create_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connector6Plus2Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(connector6Plus2Pin);
        final var requestBuilder = post(URL_API_V1_GRAPHICS_CARD_POWER_CONNECTORS)
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
                                        "Graphics card power connector with name <{0}> already exists!",
                                        connector6Plus2Pin.getName()
                                )
                        ))
                );

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentName_shouldReturnReplacedEntity() throws Exception {
        // given
        final var savedConnector6Plus2Pin = repository.save(
                mapper.convertFromDto(connector6Plus2Pin)
        );
        connector8Pin.getCompatibleConnectorIds().add(savedConnector6Plus2Pin.getId());
        final var savedConnector8Pin = repository.save(
                mapper.convertFromDto(connector8Pin)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "6 pin";
        final var dto = GraphicsCardPowerConnectorRequestDto.builder()
                .name(newName)
                .compatibleConnectorIds(Set.of(
                        savedConnector6Plus2Pin.getId()
                ))
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_GRAPHICS_CARD_POWER_CONNECTORS + "/{id}",
                savedConnector8Pin.getId())
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
                                is(savedConnector6Plus2Pin.getId().toString())),
                        jsonPath("$.compatibleConnectors[0].name",
                                is(connector6Plus2Pin.getName()))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<GraphicsCardPowerConnector> optConnector = repository.findById(savedConnector8Pin.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(newName);
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector6Plus2Pin);
        });
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        final var savedConnector6Plus2Pin = repository.save(
                mapper.convertFromDto(connector6Plus2Pin)
        );
        connector8Pin.getCompatibleConnectorIds().add(savedConnector6Plus2Pin.getId());
        final var savedConnector8Pin = repository.save(
                mapper.convertFromDto(connector8Pin)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var dto = GraphicsCardPowerConnectorRequestDto.builder()
                .name(connector6Plus2Pin.getName())
                .compatibleConnectorIds(new HashSet<>())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_GRAPHICS_CARD_POWER_CONNECTORS + "/{id}",
                savedConnector8Pin.getId())
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
                                        "Graphics card power connector with name <{0}> already exists!",
                                        connector6Plus2Pin.getName()
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<GraphicsCardPowerConnector> optConnector = repository.findById(savedConnector8Pin.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(savedConnector8Pin.getName());
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector6Plus2Pin);
        });
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        final var savedConnector6Plus2Pin = repository.save(
                mapper.convertFromDto(connector6Plus2Pin)
        );
        connector8Pin.getCompatibleConnectorIds().add(savedConnector6Plus2Pin.getId());
        final var savedConnector8Pin = repository.save(
                mapper.convertFromDto(connector8Pin)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "6 pin";
        final var dto = GraphicsCardPowerConnectorRequestDto.builder()
                .name(newName)
                .compatibleConnectorIds(Set.of(
                        savedConnector6Plus2Pin.getId()
                ))
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_GRAPHICS_CARD_POWER_CONNECTORS + "/{id}",
                savedConnector8Pin.getId())
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
                                is(savedConnector6Plus2Pin.getId().toString())),
                        jsonPath("$.compatibleConnectors[0].name",
                                is(connector6Plus2Pin.getName()))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<GraphicsCardPowerConnector> optConnector = repository.findById(savedConnector8Pin.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(newName);
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector6Plus2Pin);
        });
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        final var savedConnector6Plus2Pin = repository.save(
                mapper.convertFromDto(connector6Plus2Pin)
        );
        connector8Pin.getCompatibleConnectorIds().add(savedConnector6Plus2Pin.getId());
        final var savedConnector8Pin = repository.save(
                mapper.convertFromDto(connector8Pin)
        );
        assertThat(repository.findAll()).hasSize(2);

        final var dto = GraphicsCardPowerConnectorRequestDto.builder()
                .name(connector6Plus2Pin.getName())
                .compatibleConnectorIds(new HashSet<>())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_GRAPHICS_CARD_POWER_CONNECTORS + "/{id}",
                savedConnector8Pin.getId())
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
                                        "Graphics card power connector with name <{0}> already exists!",
                                        connector6Plus2Pin.getName()
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<GraphicsCardPowerConnector> optConnector = repository.findById(savedConnector8Pin.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(savedConnector8Pin.getName());
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector6Plus2Pin);
        });
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final var savedConnector6Plus2Pin = repository.save(
                mapper.convertFromDto(connector6Plus2Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_GRAPHICS_CARD_POWER_CONNECTORS + "/{id}",
                savedConnector6Plus2Pin.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(repository.findAll()).isEmpty();
    }
}
