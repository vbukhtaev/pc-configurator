package ru.bukhtaev.controller.dictionary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.controller.AbstractIntegrationTest;
import ru.bukhtaev.dto.mapper.dictionary.IMainPowerConnectorMapper;
import ru.bukhtaev.dto.request.dictionary.MainPowerConnectorRequestDto;
import ru.bukhtaev.model.dictionary.MainPowerConnector;
import ru.bukhtaev.repository.dictionary.IMainPowerConnectorRepository;
import ru.bukhtaev.service.TransactionService;

import java.text.MessageFormat;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.dictionary.MainPowerConnectorRestController.URL_API_V1_MAIN_POWER_CONNECTORS;

/**
 * Интеграционные тесты для CRUD операций над основными коннекторами питания.
 */
class MainPowerConnectorRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO основных коннекторов питания.
     */
    @Autowired
    private IMainPowerConnectorMapper mapper;

    /**
     * Репозиторий основных коннекторов питания.
     */
    @Autowired
    private IMainPowerConnectorRepository repository;

    /**
     * Утилитный сервис выполнения кода в транзакции.
     */
    @Autowired
    private TransactionService transactionService;

    private MainPowerConnectorRequestDto connector20Plus4Pin;
    private MainPowerConnectorRequestDto connector24Pin;

    @BeforeEach
    void setUp() {
        connector20Plus4Pin = MainPowerConnectorRequestDto.builder()
                .name("20 + 4 pin")
                .compatibleConnectorIds(new HashSet<>())
                .build();
        connector24Pin = MainPowerConnectorRequestDto.builder()
                .name("24 pin")
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
        final var savedConnector20Plus4Pin = repository.save(
                mapper.convertFromDto(connector20Plus4Pin)
        );
        connector24Pin.getCompatibleConnectorIds().add(savedConnector20Plus4Pin.getId());
        repository.save(
                mapper.convertFromDto(connector24Pin)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_MAIN_POWER_CONNECTORS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].name", is(connector20Plus4Pin.getName())),
                        jsonPath("$[0].compatibleConnectors", empty()),
                        jsonPath("$[1].name", is(connector24Pin.getName())),
                        jsonPath("$[1].compatibleConnectors", hasSize(1)),
                        jsonPath("$[1].compatibleConnectors[0].id",
                                is(savedConnector20Plus4Pin.getId().toString())),
                        jsonPath("$[1].compatibleConnectors[0].name",
                                is(connector20Plus4Pin.getName()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final var savedConnector20Plus4Pin = repository.save(
                mapper.convertFromDto(connector20Plus4Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_MAIN_POWER_CONNECTORS + "/{id}",
                savedConnector20Plus4Pin.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(savedConnector20Plus4Pin.getName())),
                        jsonPath("$.compatibleConnectors", empty())
                );
    }

    @Test
    void getById_withNonExistentId_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connector20Plus4Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_MAIN_POWER_CONNECTORS + "/{id}",
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
                                        "Main power connector with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentName_shouldReturnCreatedEntity() throws Exception {
        // given
        final var savedConnector20Plus4Pin = repository.save(
                mapper.convertFromDto(connector20Plus4Pin)
        );
        connector24Pin.getCompatibleConnectorIds().add(savedConnector20Plus4Pin.getId());
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(connector24Pin);
        final var requestBuilder = post(URL_API_V1_MAIN_POWER_CONNECTORS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(connector24Pin.getName())),
                        jsonPath("$.compatibleConnectors", hasSize(1)),
                        jsonPath("$.compatibleConnectors[0].id",
                                is(savedConnector20Plus4Pin.getId().toString())),
                        jsonPath("$.compatibleConnectors[0].name",
                                is(connector20Plus4Pin.getName()))
                );

        transactionService.doInTransaction(true, () -> {
            final List<MainPowerConnector> connectors = repository.findAll();
            assertThat(connectors).hasSize(2);
            final MainPowerConnector connector = connectors.get(1);
            assertThat(connector.getId()).isNotNull();
            assertThat(connector.getName()).isEqualTo(connector24Pin.getName());
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector20Plus4Pin);
        });
    }

    @Test
    void create_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connector20Plus4Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(connector20Plus4Pin);
        final var requestBuilder = post(URL_API_V1_MAIN_POWER_CONNECTORS)
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
                                        "Main power connector with name <{0}> already exists!",
                                        connector20Plus4Pin.getName()
                                )
                        ))
                );

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentName_shouldReturnReplacedEntity() throws Exception {
        // given
        final var savedConnector20Plus4Pin = repository.save(
                mapper.convertFromDto(connector20Plus4Pin)
        );
        connector24Pin.getCompatibleConnectorIds().add(savedConnector20Plus4Pin.getId());
        final var savedConnector24Pin = repository.save(
                mapper.convertFromDto(connector24Pin)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "20 pin";
        final var dto = MainPowerConnectorRequestDto.builder()
                .name(newName)
                .compatibleConnectorIds(Set.of(
                        savedConnector20Plus4Pin.getId()
                ))
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_MAIN_POWER_CONNECTORS + "/{id}",
                savedConnector24Pin.getId())
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
                                is(savedConnector20Plus4Pin.getId().toString())),
                        jsonPath("$.compatibleConnectors[0].name",
                                is(connector20Plus4Pin.getName()))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<MainPowerConnector> optConnector = repository.findById(savedConnector24Pin.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(newName);
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector20Plus4Pin);
        });
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        final var savedConnector20Plus4Pin = repository.save(
                mapper.convertFromDto(connector20Plus4Pin)
        );
        connector24Pin.getCompatibleConnectorIds().add(savedConnector20Plus4Pin.getId());
        final var savedConnector24Pin = repository.save(
                mapper.convertFromDto(connector24Pin)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var dto = MainPowerConnectorRequestDto.builder()
                .name(connector20Plus4Pin.getName())
                .compatibleConnectorIds(new HashSet<>())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_MAIN_POWER_CONNECTORS + "/{id}",
                savedConnector24Pin.getId())
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
                                        "Main power connector with name <{0}> already exists!",
                                        connector20Plus4Pin.getName()
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<MainPowerConnector> optConnector = repository.findById(savedConnector24Pin.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(savedConnector24Pin.getName());
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector20Plus4Pin);
        });
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        final var savedConnector20Plus4Pin = repository.save(
                mapper.convertFromDto(connector20Plus4Pin)
        );
        connector24Pin.getCompatibleConnectorIds().add(savedConnector20Plus4Pin.getId());
        final var savedConnector24Pin = repository.save(
                mapper.convertFromDto(connector24Pin)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "20 pin";
        final var dto = MainPowerConnectorRequestDto.builder()
                .name(newName)
                .compatibleConnectorIds(Set.of(
                        savedConnector20Plus4Pin.getId()
                ))
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_MAIN_POWER_CONNECTORS + "/{id}",
                savedConnector24Pin.getId())
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
                                is(savedConnector20Plus4Pin.getId().toString())),
                        jsonPath("$.compatibleConnectors[0].name",
                                is(connector20Plus4Pin.getName()))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<MainPowerConnector> optConnector = repository.findById(savedConnector24Pin.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(newName);
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector20Plus4Pin);
        });
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        final var savedConnector20Plus4Pin = repository.save(
                mapper.convertFromDto(connector20Plus4Pin)
        );
        connector24Pin.getCompatibleConnectorIds().add(savedConnector20Plus4Pin.getId());
        final var savedConnector24Pin = repository.save(
                mapper.convertFromDto(connector24Pin)
        );
        assertThat(repository.findAll()).hasSize(2);

        final var dto = MainPowerConnectorRequestDto.builder()
                .name(connector20Plus4Pin.getName())
                .compatibleConnectorIds(new HashSet<>())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_MAIN_POWER_CONNECTORS + "/{id}",
                savedConnector24Pin.getId())
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
                                        "Main power connector with name <{0}> already exists!",
                                        connector20Plus4Pin.getName()
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<MainPowerConnector> optConnector = repository.findById(savedConnector24Pin.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(savedConnector24Pin.getName());
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector20Plus4Pin);
        });
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final var savedConnector20Plus4Pin = repository.save(
                mapper.convertFromDto(connector20Plus4Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_MAIN_POWER_CONNECTORS + "/{id}",
                savedConnector20Plus4Pin.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(repository.findAll()).isEmpty();
    }
}
