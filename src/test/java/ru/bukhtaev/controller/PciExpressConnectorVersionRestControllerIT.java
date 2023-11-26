package ru.bukhtaev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.dto.mapper.IPciExpressConnectorVersionMapper;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.model.dictionary.PciExpressConnectorVersion;
import ru.bukhtaev.repository.dictionary.IPciExpressConnectorVersionRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.PciExpressConnectorVersionRestController.URL_API_V1_PCI_EXPRESS_CONNECTOR_VERSIONS;

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

    private NameableRequestDto version3;
    private NameableRequestDto version4;

    @BeforeEach
    void setUp() {
        version3 = NameableRequestDto.builder()
                .name("3.0")
                .build();
        version4 = NameableRequestDto.builder()
                .name("4.0")
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
                mapper.convertFromDto(version3)
        );
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
                        jsonPath("$[1].name", is(version4.getName()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final PciExpressConnectorVersion saved = repository.save(
                mapper.convertFromDto(version3)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_PCI_EXPRESS_CONNECTOR_VERSIONS + "/{id}",
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
        repository.save(
                mapper.convertFromDto(version4)
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
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(version3.getName()))
                );

        final List<PciExpressConnectorVersion> versions = repository.findAll();
        assertThat(versions).hasSize(2);
        final PciExpressConnectorVersion version = versions.get(1);
        assertThat(version.getId()).isNotNull();
        assertThat(version.getName()).isEqualTo(version3.getName());
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
        repository.save(
                mapper.convertFromDto(version3)
        );
        final PciExpressConnectorVersion saved = repository.save(
                mapper.convertFromDto(version4)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "5.0";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_PCI_EXPRESS_CONNECTOR_VERSIONS + "/{id}",
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

        final Optional<PciExpressConnectorVersion> optVersion = repository.findById(saved.getId());
        assertThat(optVersion).isPresent();
        assertThat(optVersion.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(version3)
        );
        final PciExpressConnectorVersion saved = repository.save(
                mapper.convertFromDto(version4)
        );
        assertThat(repository.findAll()).hasSize(2);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(version3.getName())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_PCI_EXPRESS_CONNECTOR_VERSIONS + "/{id}",
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
                                        "PCI-Express connector version with name <{0}> already exists!",
                                        version3.getName()
                                )
                        ))
                );

        final Optional<PciExpressConnectorVersion> optVersion = repository.findById(saved.getId());
        assertThat(optVersion).isPresent();
        assertThat(optVersion.get().getName())
                .isEqualTo(version4.getName());
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(version3)
        );
        final PciExpressConnectorVersion saved = repository.save(
                mapper.convertFromDto(version4)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "5.0";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_PCI_EXPRESS_CONNECTOR_VERSIONS + "/{id}",
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

        final Optional<PciExpressConnectorVersion> optVersion = repository.findById(saved.getId());
        assertThat(optVersion).isPresent();
        assertThat(optVersion.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(version4)
        );
        assertThat(repository.findAll()).hasSize(1);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(version4.getName())
                .build();

        final UUID version3Id = repository.save(
                mapper.convertFromDto(version3)
        ).getId();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_PCI_EXPRESS_CONNECTOR_VERSIONS + "/{id}",
                version3Id)
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
                                        version4.getName()
                                )
                        ))
                );

        final Optional<PciExpressConnectorVersion> optVersion = repository.findById(version3Id);
        assertThat(optVersion).isPresent();
        assertThat(optVersion.get().getName())
                .isEqualTo(version3.getName());
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID version3Id = repository.save(
                mapper.convertFromDto(version3)
        ).getId();
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_PCI_EXPRESS_CONNECTOR_VERSIONS + "/{id}",
                version3Id
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(repository.findAll()).isEmpty();
    }
}
