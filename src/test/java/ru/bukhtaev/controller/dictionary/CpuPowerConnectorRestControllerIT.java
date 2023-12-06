package ru.bukhtaev.controller.dictionary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.controller.AbstractIntegrationTest;
import ru.bukhtaev.dto.mapper.dictionary.ICpuPowerConnectorMapper;
import ru.bukhtaev.dto.request.dictionary.CpuPowerConnectorRequestDto;
import ru.bukhtaev.model.dictionary.CpuPowerConnector;
import ru.bukhtaev.repository.dictionary.ICpuPowerConnectorRepository;
import ru.bukhtaev.service.TransactionService;

import java.text.MessageFormat;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.dictionary.CpuPowerConnectorRestController.URL_API_V1_CPU_POWER_CONNECTORS;

/**
 * Интеграционные тесты для CRUD операций над коннекторами питания процессоров.
 */
class CpuPowerConnectorRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO коннекторов питания процессоров.
     */
    @Autowired
    private ICpuPowerConnectorMapper mapper;

    /**
     * Репозиторий коннекторов питания процессоров.
     */
    @Autowired
    private ICpuPowerConnectorRepository repository;

    /**
     * Утилитный сервис выполнения кода в транзакции.
     */
    @Autowired
    private TransactionService transactionService;

    private CpuPowerConnectorRequestDto connector4Plus4Pin;
    private CpuPowerConnectorRequestDto connector8Pin;

    @BeforeEach
    void setUp() {
        connector4Plus4Pin = CpuPowerConnectorRequestDto.builder()
                .name("4 + 4 pin")
                .compatibleConnectorIds(new HashSet<>())
                .build();
        connector8Pin = CpuPowerConnectorRequestDto.builder()
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
        final var savedConnector4Plus4Pin = repository.save(
                mapper.convertFromDto(connector4Plus4Pin)
        );
        connector8Pin.getCompatibleConnectorIds().add(savedConnector4Plus4Pin.getId());
        repository.save(
                mapper.convertFromDto(connector8Pin)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_CPU_POWER_CONNECTORS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].name", is(connector4Plus4Pin.getName())),
                        jsonPath("$[0].compatibleConnectors", empty()),
                        jsonPath("$[1].name", is(connector8Pin.getName())),
                        jsonPath("$[1].compatibleConnectors", hasSize(1)),
                        jsonPath("$[1].compatibleConnectors[0].id",
                                is(savedConnector4Plus4Pin.getId().toString())),
                        jsonPath("$[1].compatibleConnectors[0].name",
                                is(connector4Plus4Pin.getName()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final var savedConnector4Plus4Pin = repository.save(
                mapper.convertFromDto(connector4Plus4Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_CPU_POWER_CONNECTORS + "/{id}",
                savedConnector4Plus4Pin.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(savedConnector4Plus4Pin.getName())),
                        jsonPath("$.compatibleConnectors", empty())
                );
    }

    @Test
    void getById_withNonExistentId_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connector4Plus4Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_CPU_POWER_CONNECTORS + "/{id}",
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
                                        "CPU power connector with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentName_shouldReturnCreatedEntity() throws Exception {
        // given
        final var savedConnector4Plus4Pin = repository.save(
                mapper.convertFromDto(connector4Plus4Pin)
        );
        connector8Pin.getCompatibleConnectorIds().add(savedConnector4Plus4Pin.getId());
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(connector8Pin);
        final var requestBuilder = post(URL_API_V1_CPU_POWER_CONNECTORS)
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
                                is(savedConnector4Plus4Pin.getId().toString())),
                        jsonPath("$.compatibleConnectors[0].name",
                                is(connector4Plus4Pin.getName()))
                );

        transactionService.doInTransaction(true, () -> {
            final List<CpuPowerConnector> connectors = repository.findAll();
            assertThat(connectors).hasSize(2);
            final CpuPowerConnector connector = connectors.get(1);
            assertThat(connector.getId()).isNotNull();
            assertThat(connector.getName()).isEqualTo(connector8Pin.getName());
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector4Plus4Pin);
        });
    }

    @Test
    void create_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(connector4Plus4Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(connector4Plus4Pin);
        final var requestBuilder = post(URL_API_V1_CPU_POWER_CONNECTORS)
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
                                        "CPU power connector with name <{0}> already exists!",
                                        connector4Plus4Pin.getName()
                                )
                        ))
                );

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentName_shouldReturnReplacedEntity() throws Exception {
        // given
        final var savedConnector4Plus4Pin = repository.save(
                mapper.convertFromDto(connector4Plus4Pin)
        );
        connector8Pin.getCompatibleConnectorIds().add(savedConnector4Plus4Pin.getId());
        final var savedConnector8Pin = repository.save(
                mapper.convertFromDto(connector8Pin)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "8 pin (Wow!)";
        final var dto = CpuPowerConnectorRequestDto.builder()
                .name(newName)
                .compatibleConnectorIds(Set.of(
                        savedConnector4Plus4Pin.getId()
                ))
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_CPU_POWER_CONNECTORS + "/{id}",
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
                                is(savedConnector4Plus4Pin.getId().toString())),
                        jsonPath("$.compatibleConnectors[0].name",
                                is(connector4Plus4Pin.getName()))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<CpuPowerConnector> optConnector = repository.findById(savedConnector8Pin.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(newName);
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector4Plus4Pin);
        });
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        final var savedConnector4Plus4Pin = repository.save(
                mapper.convertFromDto(connector4Plus4Pin)
        );
        connector8Pin.getCompatibleConnectorIds().add(savedConnector4Plus4Pin.getId());
        final var savedConnector8Pin = repository.save(
                mapper.convertFromDto(connector8Pin)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var dto = CpuPowerConnectorRequestDto.builder()
                .name(connector4Plus4Pin.getName())
                .compatibleConnectorIds(new HashSet<>())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_CPU_POWER_CONNECTORS + "/{id}",
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
                                        "CPU power connector with name <{0}> already exists!",
                                        connector4Plus4Pin.getName()
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<CpuPowerConnector> optConnector = repository.findById(savedConnector8Pin.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(savedConnector8Pin.getName());
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector4Plus4Pin);
        });
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        final var savedConnector4Plus4Pin = repository.save(
                mapper.convertFromDto(connector4Plus4Pin)
        );
        connector8Pin.getCompatibleConnectorIds().add(savedConnector4Plus4Pin.getId());
        final var savedConnector8Pin = repository.save(
                mapper.convertFromDto(connector8Pin)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "8 pin (Wow!)";
        final var dto = CpuPowerConnectorRequestDto.builder()
                .name(newName)
                .compatibleConnectorIds(Set.of(
                        savedConnector4Plus4Pin.getId()
                ))
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_CPU_POWER_CONNECTORS + "/{id}",
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
                                is(savedConnector4Plus4Pin.getId().toString())),
                        jsonPath("$.compatibleConnectors[0].name",
                                is(connector4Plus4Pin.getName()))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<CpuPowerConnector> optConnector = repository.findById(savedConnector8Pin.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(newName);
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector4Plus4Pin);
        });
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        final var savedConnector4Plus4Pin = repository.save(
                mapper.convertFromDto(connector4Plus4Pin)
        );
        connector8Pin.getCompatibleConnectorIds().add(savedConnector4Plus4Pin.getId());
        final var savedConnector8Pin = repository.save(
                mapper.convertFromDto(connector8Pin)
        );
        assertThat(repository.findAll()).hasSize(2);

        final var dto = CpuPowerConnectorRequestDto.builder()
                .name(connector4Plus4Pin.getName())
                .compatibleConnectorIds(new HashSet<>())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_CPU_POWER_CONNECTORS + "/{id}",
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
                                        "CPU power connector with name <{0}> already exists!",
                                        connector4Plus4Pin.getName()
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<CpuPowerConnector> optConnector = repository.findById(savedConnector8Pin.getId());
            assertThat(optConnector).isPresent();
            final var connector = optConnector.get();
            assertThat(connector.getName()).isEqualTo(savedConnector8Pin.getName());
            assertThat(connector.getCompatibleConnectors()).hasSize(1);
            assertThat(connector.getCompatibleConnectors()).containsExactly(savedConnector4Plus4Pin);
        });
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final var savedConnector4Plus4Pin = repository.save(
                mapper.convertFromDto(connector4Plus4Pin)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_CPU_POWER_CONNECTORS + "/{id}",
                savedConnector4Plus4Pin.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(repository.findAll()).isEmpty();
    }
}
