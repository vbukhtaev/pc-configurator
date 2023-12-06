package ru.bukhtaev.controller.dictionary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.controller.AbstractIntegrationTest;
import ru.bukhtaev.dto.mapper.dictionary.IPciExpressConnectorVersionMapper;
import ru.bukhtaev.dto.request.dictionary.PciExpressConnectorVersionRequestDto;
import ru.bukhtaev.model.dictionary.PciExpressConnectorVersion;
import ru.bukhtaev.repository.dictionary.IPciExpressConnectorVersionRepository;
import ru.bukhtaev.service.TransactionService;

import java.text.MessageFormat;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.dictionary.PciExpressConnectorVersionRestController.URL_API_V1_PCI_EXPRESS_CONNECTOR_VERSIONS;

/**
 * Интеграционные тесты для CRUD операций над версиями коннектора PCI-Express.
 */
class PciExpressConnectorVersionRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO версий коннектора PCI-Express.
     */
    @Autowired
    private IPciExpressConnectorVersionMapper mapper;

    /**
     * Репозиторий версий коннектора PCI-Express.
     */
    @Autowired
    private IPciExpressConnectorVersionRepository repository;

    /**
     * Утилитный сервис выполнения кода в транзакции.
     */
    @Autowired
    private TransactionService transactionService;

    private PciExpressConnectorVersionRequestDto version3;
    private PciExpressConnectorVersionRequestDto version4;

    @BeforeEach
    void setUp() {
        version3 = PciExpressConnectorVersionRequestDto.builder()
                .name("3.0")
                .lowerVersionIds(new HashSet<>())
                .build();
        version4 = PciExpressConnectorVersionRequestDto.builder()
                .name("4.0")
                .lowerVersionIds(new HashSet<>())
                .build();
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void getAll_shouldReturnAllEntities() throws Exception {
        // given
        final var savedVersion3 = repository.save(
                mapper.convertFromDto(version3)
        );
        version4.getLowerVersionIds().add(savedVersion3.getId());
        repository.save(
                mapper.convertFromDto(version4)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_PCI_EXPRESS_CONNECTOR_VERSIONS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].name", is(version3.getName())),
                        jsonPath("$[0].lowerVersions", empty()),
                        jsonPath("$[1].name", is(version4.getName())),
                        jsonPath("$[1].lowerVersions", hasSize(1)),
                        jsonPath("$[1].lowerVersions[0].id",
                                is(savedVersion3.getId().toString())),
                        jsonPath("$[1].lowerVersions[0].name",
                                is(version3.getName()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final var savedVersion3 = repository.save(
                mapper.convertFromDto(version3)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_PCI_EXPRESS_CONNECTOR_VERSIONS + "/{id}",
                savedVersion3.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(savedVersion3.getName())),
                        jsonPath("$.lowerVersions", empty())
                );
    }

    @Test
    void getById_withNonExistentId_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(version3)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_PCI_EXPRESS_CONNECTOR_VERSIONS + "/{id}",
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
                                        "PCI-Express connector version with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentName_shouldReturnCreatedEntity() throws Exception {
        // given
        final var savedVersion3 = repository.save(
                mapper.convertFromDto(version3)
        );
        version4.getLowerVersionIds().add(savedVersion3.getId());
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(version4);
        final var requestBuilder = post(URL_API_V1_PCI_EXPRESS_CONNECTOR_VERSIONS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(version4.getName())),
                        jsonPath("$.lowerVersions", hasSize(1)),
                        jsonPath("$.lowerVersions[0].id",
                                is(savedVersion3.getId().toString())),
                        jsonPath("$.lowerVersions[0].name",
                                is(version3.getName()))
                );

        transactionService.doInTransaction(true, () -> {
            final List<PciExpressConnectorVersion> versions = repository.findAll();
            assertThat(versions).hasSize(2);
            final PciExpressConnectorVersion version = versions.get(1);
            assertThat(version.getId()).isNotNull();
            assertThat(version.getName()).isEqualTo(version4.getName());
            assertThat(version.getLowerVersions()).hasSize(1);
            assertThat(version.getLowerVersions()).containsExactly(savedVersion3);
        });
    }

    @Test
    void create_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(version3)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(version3);
        final var requestBuilder = post(URL_API_V1_PCI_EXPRESS_CONNECTOR_VERSIONS)
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
                                        "PCI-Express connector version with name <{0}> already exists!",
                                        version3.getName()
                                )
                        ))
                );

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentName_shouldReturnReplacedEntity() throws Exception {
        // given
        final var savedVersion3 = repository.save(
                mapper.convertFromDto(version3)
        );
        version4.getLowerVersionIds().add(savedVersion3.getId());
        final var savedVersion4 = repository.save(
                mapper.convertFromDto(version4)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "4.1";
        final var dto = PciExpressConnectorVersionRequestDto.builder()
                .name(newName)
                .lowerVersionIds(Set.of(
                        savedVersion3.getId()
                ))
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_PCI_EXPRESS_CONNECTOR_VERSIONS + "/{id}",
                savedVersion4.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(newName)),
                        jsonPath("$.lowerVersions", hasSize(1)),
                        jsonPath("$.lowerVersions[0].id",
                                is(savedVersion3.getId().toString())),
                        jsonPath("$.lowerVersions[0].name",
                                is(version3.getName()))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<PciExpressConnectorVersion> optVersion = repository.findById(savedVersion4.getId());
            assertThat(optVersion).isPresent();
            final var version = optVersion.get();
            assertThat(version.getName()).isEqualTo(newName);
            assertThat(version.getLowerVersions()).hasSize(1);
            assertThat(version.getLowerVersions()).containsExactly(savedVersion3);
        });
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        final var savedVersion3 = repository.save(
                mapper.convertFromDto(version3)
        );
        version4.getLowerVersionIds().add(savedVersion3.getId());
        final var savedVersion4 = repository.save(
                mapper.convertFromDto(version4)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var dto = PciExpressConnectorVersionRequestDto.builder()
                .name(version3.getName())
                .lowerVersionIds(new HashSet<>())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_PCI_EXPRESS_CONNECTOR_VERSIONS + "/{id}",
                savedVersion4.getId())
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
                                        "PCI-Express connector version with name <{0}> already exists!",
                                        version3.getName()
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<PciExpressConnectorVersion> optVersion = repository.findById(savedVersion4.getId());
            assertThat(optVersion).isPresent();
            final var version = optVersion.get();
            assertThat(version.getName()).isEqualTo(savedVersion4.getName());
            assertThat(version.getLowerVersions()).hasSize(1);
            assertThat(version.getLowerVersions()).containsExactly(savedVersion3);
        });
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        final var savedVersion3 = repository.save(
                mapper.convertFromDto(version3)
        );
        version4.getLowerVersionIds().add(savedVersion3.getId());
        final var savedVersion4 = repository.save(
                mapper.convertFromDto(version4)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "4.1";
        final var dto = PciExpressConnectorVersionRequestDto.builder()
                .name(newName)
                .lowerVersionIds(Set.of(
                        savedVersion3.getId()
                ))
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_PCI_EXPRESS_CONNECTOR_VERSIONS + "/{id}",
                savedVersion4.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(newName)),
                        jsonPath("$.lowerVersions", hasSize(1)),
                        jsonPath("$.lowerVersions[0].id",
                                is(savedVersion3.getId().toString())),
                        jsonPath("$.lowerVersions[0].name",
                                is(version3.getName()))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<PciExpressConnectorVersion> optVersion = repository.findById(savedVersion4.getId());
            assertThat(optVersion).isPresent();
            final var version = optVersion.get();
            assertThat(version.getName()).isEqualTo(newName);
            assertThat(version.getLowerVersions()).hasSize(1);
            assertThat(version.getLowerVersions()).containsExactly(savedVersion3);
        });
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        final var savedVersion3 = repository.save(
                mapper.convertFromDto(version3)
        );
        version4.getLowerVersionIds().add(savedVersion3.getId());
        final var savedVersion4 = repository.save(
                mapper.convertFromDto(version4)
        );
        assertThat(repository.findAll()).hasSize(2);

        final var dto = PciExpressConnectorVersionRequestDto.builder()
                .name(version3.getName())
                .lowerVersionIds(new HashSet<>())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_PCI_EXPRESS_CONNECTOR_VERSIONS + "/{id}",
                savedVersion4.getId())
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
                                        "PCI-Express connector version with name <{0}> already exists!",
                                        version3.getName()
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {
            final Optional<PciExpressConnectorVersion> optVersion = repository.findById(savedVersion4.getId());
            assertThat(optVersion).isPresent();
            final var version = optVersion.get();
            assertThat(version.getName()).isEqualTo(savedVersion4.getName());
            assertThat(version.getLowerVersions()).hasSize(1);
            assertThat(version.getLowerVersions()).containsExactly(savedVersion3);
        });
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final var savedVersion3 = repository.save(
                mapper.convertFromDto(version3)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_PCI_EXPRESS_CONNECTOR_VERSIONS + "/{id}",
                savedVersion3.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(repository.findAll()).isEmpty();
    }
}
