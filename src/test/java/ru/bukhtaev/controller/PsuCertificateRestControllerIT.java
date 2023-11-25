package ru.bukhtaev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.dto.mapper.IPsuCertificateMapper;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.model.dictionary.PsuCertificate;
import ru.bukhtaev.repository.IPsuCertificateRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.PsuCertificateRestController.URL_API_V1_PSU_CERTIFICATES;

/**
 * Интеграционные тесты для CRUD операций над сертификатами блоков питания.
 */
class PsuCertificateRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO сертификатов блоков питания.
     */
    @Autowired
    private IPsuCertificateMapper mapper;

    /**
     * Репозиторий сертификатов блоков питания.
     */
    @Autowired
    private IPsuCertificateRepository repository;

    private NameableRequestDto certificateBronze;
    private NameableRequestDto certificateGold;

    @BeforeEach
    void setUp() {
        certificateBronze = NameableRequestDto.builder()
                .name("Bronze")
                .build();
        certificateGold = NameableRequestDto.builder()
                .name("Gold")
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
                mapper.convertFromDto(certificateBronze)
        );
        repository.save(
                mapper.convertFromDto(certificateGold)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_PSU_CERTIFICATES);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].name", is(certificateBronze.getName())),
                        jsonPath("$[1].name", is(certificateGold.getName()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final PsuCertificate saved = repository.save(
                mapper.convertFromDto(certificateBronze)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_PSU_CERTIFICATES + "/{id}",
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
                mapper.convertFromDto(certificateBronze)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_PSU_CERTIFICATES + "/{id}",
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
                                        "PSU certificate with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentName_shouldReturnCreatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(certificateGold)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(certificateBronze);
        final var requestBuilder = post(URL_API_V1_PSU_CERTIFICATES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(certificateBronze.getName()))
                );

        final List<PsuCertificate> certificates = repository.findAll();
        assertThat(certificates).hasSize(2);
        final PsuCertificate certificate = certificates.get(1);
        assertThat(certificate.getId()).isNotNull();
        assertThat(certificate.getName()).isEqualTo(certificateBronze.getName());
    }

    @Test
    void create_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(certificateBronze)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(certificateBronze);
        final var requestBuilder = post(URL_API_V1_PSU_CERTIFICATES)
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
                                        "PSU certificate with name <{0}> already exists!",
                                        certificateBronze.getName()
                                )
                        ))
                );

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentName_shouldReturnReplacedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(certificateBronze)
        );
        final PsuCertificate saved = repository.save(
                mapper.convertFromDto(certificateGold)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "Titanium";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_PSU_CERTIFICATES + "/{id}",
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

        final Optional<PsuCertificate> optCertificate = repository.findById(saved.getId());
        assertThat(optCertificate).isPresent();
        assertThat(optCertificate.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(certificateBronze)
        );
        final PsuCertificate saved = repository.save(
                mapper.convertFromDto(certificateGold)
        );
        assertThat(repository.findAll()).hasSize(2);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(certificateBronze.getName())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_PSU_CERTIFICATES + "/{id}",
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
                                        "PSU certificate with name <{0}> already exists!",
                                        certificateBronze.getName()
                                )
                        ))
                );

        final Optional<PsuCertificate> optCertificate = repository.findById(saved.getId());
        assertThat(optCertificate).isPresent();
        assertThat(optCertificate.get().getName())
                .isEqualTo(certificateGold.getName());
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(certificateBronze)
        );
        final PsuCertificate saved = repository.save(
                mapper.convertFromDto(certificateGold)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "Titanium";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_PSU_CERTIFICATES + "/{id}",
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

        final Optional<PsuCertificate> optCertificate = repository.findById(saved.getId());
        assertThat(optCertificate).isPresent();
        assertThat(optCertificate.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(certificateGold)
        );
        assertThat(repository.findAll()).hasSize(1);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(certificateGold.getName())
                .build();

        final UUID certificateBronzeId = repository.save(
                mapper.convertFromDto(certificateBronze)
        ).getId();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_PSU_CERTIFICATES + "/{id}",
                certificateBronzeId)
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
                                        "PSU certificate with name <{0}> already exists!",
                                        certificateGold.getName()
                                )
                        ))
                );

        final Optional<PsuCertificate> optCertificate = repository.findById(certificateBronzeId);
        assertThat(optCertificate).isPresent();
        assertThat(optCertificate.get().getName())
                .isEqualTo(certificateBronze.getName());
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID certificateBronzeId = repository.save(
                mapper.convertFromDto(certificateBronze)
        ).getId();
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_PSU_CERTIFICATES + "/{id}",
                certificateBronzeId
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(repository.findAll()).isEmpty();
    }
}
