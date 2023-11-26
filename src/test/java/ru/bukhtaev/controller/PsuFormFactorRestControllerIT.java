package ru.bukhtaev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.dto.mapper.dictionary.IPsuFormFactorMapper;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.model.dictionary.PsuFormFactor;
import ru.bukhtaev.repository.dictionary.IPsuFormFactorRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.dictionary.PsuFormFactorRestController.URL_API_V1_PSU_FORM_FACTORS;

/**
 * Интеграционные тесты для CRUD операций над форм-факторами блоков питания.
 */
class PsuFormFactorRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO форм-факторов блоков питания.
     */
    @Autowired
    private IPsuFormFactorMapper mapper;

    /**
     * Репозиторий форм-факторов блоков питания.
     */
    @Autowired
    private IPsuFormFactorRepository repository;

    private NameableRequestDto formFactorAtx;
    private NameableRequestDto formFactorEps;

    @BeforeEach
    void setUp() {
        formFactorAtx = NameableRequestDto.builder()
                .name("ATX")
                .build();
        formFactorEps = NameableRequestDto.builder()
                .name("EPS")
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
                mapper.convertFromDto(formFactorAtx)
        );
        repository.save(
                mapper.convertFromDto(formFactorEps)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_PSU_FORM_FACTORS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].name", is(formFactorAtx.getName())),
                        jsonPath("$[1].name", is(formFactorEps.getName()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final PsuFormFactor saved = repository.save(
                mapper.convertFromDto(formFactorAtx)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_PSU_FORM_FACTORS + "/{id}",
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
                mapper.convertFromDto(formFactorAtx)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_PSU_FORM_FACTORS + "/{id}",
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
                                        "PSU form factor with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentName_shouldReturnCreatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(formFactorEps)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(formFactorAtx);
        final var requestBuilder = post(URL_API_V1_PSU_FORM_FACTORS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(formFactorAtx.getName()))
                );

        final List<PsuFormFactor> formFactors = repository.findAll();
        assertThat(formFactors).hasSize(2);
        final PsuFormFactor formFactor = formFactors.get(1);
        assertThat(formFactor.getId()).isNotNull();
        assertThat(formFactor.getName()).isEqualTo(formFactorAtx.getName());
    }

    @Test
    void create_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(formFactorAtx)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(formFactorAtx);
        final var requestBuilder = post(URL_API_V1_PSU_FORM_FACTORS)
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
                                        "PSU form factor with name <{0}> already exists!",
                                        formFactorAtx.getName()
                                )
                        ))
                );

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentName_shouldReturnReplacedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(formFactorAtx)
        );
        final PsuFormFactor saved = repository.save(
                mapper.convertFromDto(formFactorEps)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "FLEX";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_PSU_FORM_FACTORS + "/{id}",
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

        final Optional<PsuFormFactor> optFormFactor = repository.findById(saved.getId());
        assertThat(optFormFactor).isPresent();
        assertThat(optFormFactor.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(formFactorAtx)
        );
        final PsuFormFactor saved = repository.save(
                mapper.convertFromDto(formFactorEps)
        );
        assertThat(repository.findAll()).hasSize(2);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(formFactorAtx.getName())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_PSU_FORM_FACTORS + "/{id}",
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
                                        "PSU form factor with name <{0}> already exists!",
                                        formFactorAtx.getName()
                                )
                        ))
                );

        final Optional<PsuFormFactor> optFormFactor = repository.findById(saved.getId());
        assertThat(optFormFactor).isPresent();
        assertThat(optFormFactor.get().getName())
                .isEqualTo(formFactorEps.getName());
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(formFactorAtx)
        );
        final PsuFormFactor saved = repository.save(
                mapper.convertFromDto(formFactorEps)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "FLEX";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_PSU_FORM_FACTORS + "/{id}",
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

        final Optional<PsuFormFactor> optFormFactor = repository.findById(saved.getId());
        assertThat(optFormFactor).isPresent();
        assertThat(optFormFactor.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(formFactorEps)
        );
        assertThat(repository.findAll()).hasSize(1);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(formFactorEps.getName())
                .build();

        final UUID formFactorAtxId = repository.save(
                mapper.convertFromDto(formFactorAtx)
        ).getId();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_PSU_FORM_FACTORS + "/{id}",
                formFactorAtxId)
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
                                        "PSU form factor with name <{0}> already exists!",
                                        formFactorEps.getName()
                                )
                        ))
                );

        final Optional<PsuFormFactor> optFormFactor = repository.findById(formFactorAtxId);
        assertThat(optFormFactor).isPresent();
        assertThat(optFormFactor.get().getName())
                .isEqualTo(formFactorAtx.getName());
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID formFactorAtxId = repository.save(
                mapper.convertFromDto(formFactorAtx)
        ).getId();
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_PSU_FORM_FACTORS + "/{id}",
                formFactorAtxId
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(repository.findAll()).isEmpty();
    }
}
