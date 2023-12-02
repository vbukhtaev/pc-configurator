package ru.bukhtaev.controller.dictionary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.controller.AbstractIntegrationTest;
import ru.bukhtaev.dto.mapper.dictionary.IMotherboardFormFactorMapper;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.model.dictionary.MotherboardFormFactor;
import ru.bukhtaev.repository.dictionary.IMotherboardFormFactorRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.dictionary.MotherboardFormFactorRestController.URL_API_V1_MOTHERBOARD_FORM_FACTORS;

/**
 * Интеграционные тесты для CRUD операций над форм-факторами материнских плат.
 */
class MotherboardFormFactorRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO форм-факторов материнских плат.
     */
    @Autowired
    private IMotherboardFormFactorMapper mapper;

    /**
     * Репозиторий форм-факторов материнских плат.
     */
    @Autowired
    private IMotherboardFormFactorRepository repository;

    private NameableRequestDto formFactorMicroAtx;
    private NameableRequestDto formFactorStandardAtx;

    @BeforeEach
    void setUp() {
        formFactorMicroAtx = NameableRequestDto.builder()
                .name("Micro-ATX")
                .build();
        formFactorStandardAtx = NameableRequestDto.builder()
                .name("Standard-ATX")
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
                mapper.convertFromDto(formFactorMicroAtx)
        );
        repository.save(
                mapper.convertFromDto(formFactorStandardAtx)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_MOTHERBOARD_FORM_FACTORS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].name", is(formFactorMicroAtx.getName())),
                        jsonPath("$[1].name", is(formFactorStandardAtx.getName()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final MotherboardFormFactor saved = repository.save(
                mapper.convertFromDto(formFactorMicroAtx)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_MOTHERBOARD_FORM_FACTORS + "/{id}",
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
                mapper.convertFromDto(formFactorMicroAtx)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_MOTHERBOARD_FORM_FACTORS + "/{id}",
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
                                        "Motherboard form factor with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentName_shouldReturnCreatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(formFactorStandardAtx)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(formFactorMicroAtx);
        final var requestBuilder = post(URL_API_V1_MOTHERBOARD_FORM_FACTORS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(formFactorMicroAtx.getName()))
                );

        final List<MotherboardFormFactor> formFactors = repository.findAll();
        assertThat(formFactors).hasSize(2);
        final MotherboardFormFactor formFactor = formFactors.get(1);
        assertThat(formFactor.getId()).isNotNull();
        assertThat(formFactor.getName()).isEqualTo(formFactorMicroAtx.getName());
    }

    @Test
    void create_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(formFactorMicroAtx)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(formFactorMicroAtx);
        final var requestBuilder = post(URL_API_V1_MOTHERBOARD_FORM_FACTORS)
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
                                        "Motherboard form factor with name <{0}> already exists!",
                                        formFactorMicroAtx.getName()
                                )
                        ))
                );

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentName_shouldReturnReplacedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(formFactorMicroAtx)
        );
        final MotherboardFormFactor saved = repository.save(
                mapper.convertFromDto(formFactorStandardAtx)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "E-ATX";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_MOTHERBOARD_FORM_FACTORS + "/{id}",
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

        final Optional<MotherboardFormFactor> optFormFactor = repository.findById(saved.getId());
        assertThat(optFormFactor).isPresent();
        assertThat(optFormFactor.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(formFactorMicroAtx)
        );
        final MotherboardFormFactor saved = repository.save(
                mapper.convertFromDto(formFactorStandardAtx)
        );
        assertThat(repository.findAll()).hasSize(2);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(formFactorMicroAtx.getName())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_MOTHERBOARD_FORM_FACTORS + "/{id}",
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
                                        "Motherboard form factor with name <{0}> already exists!",
                                        formFactorMicroAtx.getName()
                                )
                        ))
                );

        final Optional<MotherboardFormFactor> optFormFactor = repository.findById(saved.getId());
        assertThat(optFormFactor).isPresent();
        assertThat(optFormFactor.get().getName())
                .isEqualTo(formFactorStandardAtx.getName());
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(formFactorMicroAtx)
        );
        final MotherboardFormFactor saved = repository.save(
                mapper.convertFromDto(formFactorStandardAtx)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "E-ATX";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_MOTHERBOARD_FORM_FACTORS + "/{id}",
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

        final Optional<MotherboardFormFactor> optFormFactor = repository.findById(saved.getId());
        assertThat(optFormFactor).isPresent();
        assertThat(optFormFactor.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(formFactorStandardAtx)
        );
        assertThat(repository.findAll()).hasSize(1);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(formFactorStandardAtx.getName())
                .build();

        final UUID formFactorMicroAtxId = repository.save(
                mapper.convertFromDto(formFactorMicroAtx)
        ).getId();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_MOTHERBOARD_FORM_FACTORS + "/{id}",
                formFactorMicroAtxId)
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
                                        "Motherboard form factor with name <{0}> already exists!",
                                        formFactorStandardAtx.getName()
                                )
                        ))
                );

        final Optional<MotherboardFormFactor> optFormFactor = repository.findById(formFactorMicroAtxId);
        assertThat(optFormFactor).isPresent();
        assertThat(optFormFactor.get().getName())
                .isEqualTo(formFactorMicroAtx.getName());
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID formFactorMicroAtxId = repository.save(
                mapper.convertFromDto(formFactorMicroAtx)
        ).getId();
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_MOTHERBOARD_FORM_FACTORS + "/{id}",
                formFactorMicroAtxId
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(repository.findAll()).isEmpty();
    }
}
