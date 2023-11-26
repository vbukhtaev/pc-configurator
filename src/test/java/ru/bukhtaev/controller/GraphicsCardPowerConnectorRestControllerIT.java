package ru.bukhtaev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.dto.mapper.dictionary.IGraphicsCardPowerConnectorMapper;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.model.dictionary.GraphicsCardPowerConnector;
import ru.bukhtaev.repository.dictionary.IGraphicsCardPowerConnectorRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    private NameableRequestDto connector6Pin;
    private NameableRequestDto connector8Pin;

    @BeforeEach
    void setUp() {
        connector6Pin = NameableRequestDto.builder()
                .name("6 pin")
                .build();
        connector8Pin = NameableRequestDto.builder()
                .name("8 pin")
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
                mapper.convertFromDto(connector6Pin)
        );
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
                        jsonPath("$[0].name", is(connector6Pin.getName())),
                        jsonPath("$[1].name", is(connector8Pin.getName()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final GraphicsCardPowerConnector saved = repository.save(
                mapper.convertFromDto(connector6Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_GRAPHICS_CARD_POWER_CONNECTORS + "/{id}",
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
                mapper.convertFromDto(connector6Pin)
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
        repository.save(
                mapper.convertFromDto(connector8Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(connector6Pin);
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
                        jsonPath("$.name", is(connector6Pin.getName()))
                );

        final List<GraphicsCardPowerConnector> connectors = repository.findAll();
        assertThat(connectors).hasSize(2);
        final GraphicsCardPowerConnector connector = connectors.get(1);
        assertThat(connector.getId()).isNotNull();
        assertThat(connector.getName()).isEqualTo(connector6Pin.getName());
    }

    @Test
    void create_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connector6Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(connector6Pin);
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
                                        connector6Pin.getName()
                                )
                        ))
                );

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentName_shouldReturnReplacedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connector6Pin)
        );
        final GraphicsCardPowerConnector saved = repository.save(
                mapper.convertFromDto(connector8Pin)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "16 pin";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_GRAPHICS_CARD_POWER_CONNECTORS + "/{id}",
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

        final Optional<GraphicsCardPowerConnector> optConnector = repository.findById(saved.getId());
        assertThat(optConnector).isPresent();
        assertThat(optConnector.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connector6Pin)
        );
        final GraphicsCardPowerConnector saved = repository.save(
                mapper.convertFromDto(connector8Pin)
        );
        assertThat(repository.findAll()).hasSize(2);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(connector6Pin.getName())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_GRAPHICS_CARD_POWER_CONNECTORS + "/{id}",
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
                                        "Graphics card power connector with name <{0}> already exists!",
                                        connector6Pin.getName()
                                )
                        ))
                );

        final Optional<GraphicsCardPowerConnector> optConnector = repository.findById(saved.getId());
        assertThat(optConnector).isPresent();
        assertThat(optConnector.get().getName())
                .isEqualTo(connector8Pin.getName());
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connector6Pin)
        );
        final GraphicsCardPowerConnector saved = repository.save(
                mapper.convertFromDto(connector8Pin)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "16 pin";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_GRAPHICS_CARD_POWER_CONNECTORS + "/{id}",
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

        final Optional<GraphicsCardPowerConnector> optConnector = repository.findById(saved.getId());
        assertThat(optConnector).isPresent();
        assertThat(optConnector.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connector8Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(connector8Pin.getName())
                .build();

        final UUID connector6PinId = repository.save(
                mapper.convertFromDto(connector6Pin)
        ).getId();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_GRAPHICS_CARD_POWER_CONNECTORS + "/{id}",
                connector6PinId)
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
                                        connector8Pin.getName()
                                )
                        ))
                );

        final Optional<GraphicsCardPowerConnector> optConnector = repository.findById(connector6PinId);
        assertThat(optConnector).isPresent();
        assertThat(optConnector.get().getName())
                .isEqualTo(connector6Pin.getName());
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID connector6PinId = repository.save(
                mapper.convertFromDto(connector6Pin)
        ).getId();
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_GRAPHICS_CARD_POWER_CONNECTORS + "/{id}",
                connector6PinId
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(repository.findAll()).isEmpty();
    }
}
