package ru.bukhtaev.controller.dictionary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.controller.AbstractIntegrationTest;
import ru.bukhtaev.dto.mapper.dictionary.IFanPowerConnectorMapper;
import ru.bukhtaev.dto.request.dictionary.FanPowerConnectorRequestDto;
import ru.bukhtaev.model.dictionary.FanPowerConnector;
import ru.bukhtaev.repository.dictionary.IFanPowerConnectorRepository;
import ru.bukhtaev.service.TransactionService;

import java.text.MessageFormat;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.dictionary.FanPowerConnectorRestController.URL_API_V1_FAN_POWER_CONNECTORS;

/**
 * Интеграционные тесты для CRUD операций над коннекторами питания вентиляторов.
 */
class FanPowerConnectorRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO коннекторов питания вентиляторов.
     */
    @Autowired
    private IFanPowerConnectorMapper mapper;

    /**
     * Репозиторий коннекторов питания вентиляторов.
     */
    @Autowired
    private IFanPowerConnectorRepository repository;

    /**
     * Утилитный сервис выполнения кода в транзакции.
     */
    @Autowired
    private TransactionService transactionService;

    private FanPowerConnectorRequestDto connector2Pin;
    private FanPowerConnectorRequestDto connector3Pin;

    @BeforeEach
    void setUp() {
        connector2Pin = FanPowerConnectorRequestDto.builder()
                .name("2 pin")
                .compatibleConnectorIds(new HashSet<>())
                .build();
        connector3Pin = FanPowerConnectorRequestDto.builder()
                .name("3 pin")
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
        final var savedConnector2Pin = repository.save(
                mapper.convertFromDto(connector2Pin)
        );
        connector3Pin.getCompatibleConnectorIds().add(savedConnector2Pin.getId());
        repository.save(
                mapper.convertFromDto(connector3Pin)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_FAN_POWER_CONNECTORS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].name", is(connector2Pin.getName())),
                        jsonPath("$[0].compatibleConnectors", empty()),
                        jsonPath("$[1].name", is(connector3Pin.getName())),
                        jsonPath("$[1].compatibleConnectors", hasSize(1)),
                        jsonPath("$[1].compatibleConnectors[0].id",
                                is(savedConnector2Pin.getId().toString())),
                        jsonPath("$[1].compatibleConnectors[0].name",
                                is(connector2Pin.getName()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final var savedConnector2Pin = repository.save(
                mapper.convertFromDto(connector2Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_FAN_POWER_CONNECTORS + "/{id}",
                savedConnector2Pin.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(savedConnector2Pin.getName())),
                        jsonPath("$.compatibleConnectors", empty())
                );
    }

    @Test
    void getById_withNonExistentId_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connector2Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_FAN_POWER_CONNECTORS + "/{id}",
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
                                        "Fan power connector with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentName_shouldReturnCreatedEntity() throws Exception {
        // given
        final var savedConnector2Pin = repository.save(
                mapper.convertFromDto(connector2Pin)
        );
        connector3Pin.getCompatibleConnectorIds().add(savedConnector2Pin.getId());
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(connector3Pin);
        final var requestBuilder = post(URL_API_V1_FAN_POWER_CONNECTORS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(connector3Pin.getName())),
                        jsonPath("$.compatibleConnectors", hasSize(1)),
                        jsonPath("$.compatibleConnectors[0].id",
                                is(savedConnector2Pin.getId().toString())),
                        jsonPath("$.compatibleConnectors[0].name",
                                is(connector2Pin.getName()))
                );

        transactionService.doInTransaction(true, () -> {
            final List<FanPowerConnector> connectors = repository.findAll();
            assertThat(connectors).hasSize(2);
            final FanPowerConnector connector = connectors.get(1);
            assertThat(connector.getId()).isNotNull();
            assertThat(connector.getName()).isEqualTo(connector3Pin.getName());
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector2Pin);
        });
    }

    @Test
    void create_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connector2Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(connector2Pin);
        final var requestBuilder = post(URL_API_V1_FAN_POWER_CONNECTORS)
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
                                        "Fan power connector with name <{0}> already exists!",
                                        connector2Pin.getName()
                                )
                        ))
                );

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentName_shouldReturnReplacedEntity() throws Exception {
        // given
        final var savedConnector2Pin = repository.save(
                mapper.convertFromDto(connector2Pin)
        );
        connector3Pin.getCompatibleConnectorIds().add(savedConnector2Pin.getId());
        final var savedConnector3Pin = repository.save(
                mapper.convertFromDto(connector3Pin)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "3 pin (Wow!)";
        final var dto = FanPowerConnectorRequestDto.builder()
                .name(newName)
                .compatibleConnectorIds(Set.of(
                        savedConnector2Pin.getId()
                ))
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_FAN_POWER_CONNECTORS + "/{id}",
                savedConnector3Pin.getId())
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
                                is(savedConnector2Pin.getId().toString())),
                        jsonPath("$.compatibleConnectors[0].name",
                                is(connector2Pin.getName()))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<FanPowerConnector> optConnector = repository.findById(savedConnector3Pin.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(newName);
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector2Pin);
        });
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        final var savedConnector2Pin = repository.save(
                mapper.convertFromDto(connector2Pin)
        );
        connector3Pin.getCompatibleConnectorIds().add(savedConnector2Pin.getId());
        final var savedConnector3Pin = repository.save(
                mapper.convertFromDto(connector3Pin)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var dto = FanPowerConnectorRequestDto.builder()
                .name(connector2Pin.getName())
                .compatibleConnectorIds(new HashSet<>())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_FAN_POWER_CONNECTORS + "/{id}",
                savedConnector3Pin.getId())
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
                                        "Fan power connector with name <{0}> already exists!",
                                        connector2Pin.getName()
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<FanPowerConnector> optConnector = repository.findById(savedConnector3Pin.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(savedConnector3Pin.getName());
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector2Pin);
        });
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        final var savedConnector2Pin = repository.save(
                mapper.convertFromDto(connector2Pin)
        );
        connector3Pin.getCompatibleConnectorIds().add(savedConnector2Pin.getId());
        final var savedConnector3Pin = repository.save(
                mapper.convertFromDto(connector3Pin)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "3 pin (Wow!)";
        final var dto = FanPowerConnectorRequestDto.builder()
                .name(newName)
                .compatibleConnectorIds(Set.of(
                        savedConnector2Pin.getId()
                ))
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_FAN_POWER_CONNECTORS + "/{id}",
                savedConnector3Pin.getId())
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
                                is(savedConnector2Pin.getId().toString())),
                        jsonPath("$.compatibleConnectors[0].name",
                                is(connector2Pin.getName()))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<FanPowerConnector> optConnector = repository.findById(savedConnector3Pin.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(newName);
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector2Pin);
        });
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        final var savedConnector2Pin = repository.save(
                mapper.convertFromDto(connector2Pin)
        );
        connector3Pin.getCompatibleConnectorIds().add(savedConnector2Pin.getId());
        final var savedConnector3Pin = repository.save(
                mapper.convertFromDto(connector3Pin)
        );
        assertThat(repository.findAll()).hasSize(2);

        final var dto = FanPowerConnectorRequestDto.builder()
                .name(connector2Pin.getName())
                .compatibleConnectorIds(new HashSet<>())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_FAN_POWER_CONNECTORS + "/{id}",
                savedConnector3Pin.getId())
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
                                        "Fan power connector with name <{0}> already exists!",
                                        connector2Pin.getName()
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<FanPowerConnector> optConnector = repository.findById(savedConnector3Pin.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(savedConnector3Pin.getName());
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector2Pin);
        });
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final var savedConnector2Pin = repository.save(
                mapper.convertFromDto(connector2Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_FAN_POWER_CONNECTORS + "/{id}",
                savedConnector2Pin.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(repository.findAll()).isEmpty();
    }
}
