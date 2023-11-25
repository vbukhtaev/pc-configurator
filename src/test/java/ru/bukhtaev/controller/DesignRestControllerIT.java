package ru.bukhtaev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.dto.mapper.IDesignMapper;
import ru.bukhtaev.dto.request.DesignRequestDto;
import ru.bukhtaev.model.Design;
import ru.bukhtaev.model.dictionary.Vendor;
import ru.bukhtaev.repository.IDesignRepository;
import ru.bukhtaev.repository.IVendorRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.DesignRestController.URL_API_V1_DESIGNS;

/**
 * Интеграционные тесты для CRUD операций над вариантами исполнения.
 */
class DesignRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO вариантов исполнения.
     */
    @Autowired
    private IDesignMapper mapper;

    /**
     * Репозиторий вариантов исполнения.
     */
    @Autowired
    private IDesignRepository designRepository;

    /**
     * Репозиторий вендоров.
     */
    @Autowired
    private IVendorRepository vendorRepository;

    private DesignRequestDto designVentus3X;
    private DesignRequestDto designEagle;

    private Vendor vendorMsi;
    private Vendor vendorGigabyte;

    @BeforeEach
    void setUp() {
        vendorMsi = vendorRepository.save(
                Vendor.builder()
                        .name("MSI")
                        .build()
        );
        vendorGigabyte = vendorRepository.save(
                Vendor.builder()
                        .name("GIGABYTE")
                        .build()
        );

        designVentus3X = DesignRequestDto.builder()
                .name("VENTUS 3X")
                .vendorId(vendorMsi.getId())
                .build();
        designEagle = DesignRequestDto.builder()
                .name("EAGLE")
                .vendorId(vendorGigabyte.getId())
                .build();
    }

    @AfterEach
    void tearDown() {
        designRepository.deleteAll();
        vendorRepository.deleteAll();
    }

    @Test
    void getAll_shouldReturnAllEntities() throws Exception {
        // given
        designRepository.save(
                mapper.convertFromDto(designVentus3X)
        );
        designRepository.save(
                mapper.convertFromDto(designEagle)
        );
        assertThat(designRepository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_DESIGNS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].name", is(designVentus3X.getName())),
                        jsonPath("$[0].vendor.id", is(vendorMsi.getId().toString())),
                        jsonPath("$[0].vendor.name", is(vendorMsi.getName())),
                        jsonPath("$[1].name", is(designEagle.getName())),
                        jsonPath("$[1].vendor.id", is(vendorGigabyte.getId().toString())),
                        jsonPath("$[1].vendor.name", is(vendorGigabyte.getName()))
                );
    }

    @Test
    void getAll_withPagination_shouldReturnAllEntitiesAsPage() throws Exception {
        // given
        designRepository.save(
                mapper.convertFromDto(designVentus3X)
        );
        designRepository.save(
                mapper.convertFromDto(designEagle)
        );
        assertThat(designRepository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_DESIGNS + "/pageable")
                .params(DESIGN_PAGE_REQUEST_PARAMS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.content", hasSize(2)),
                        jsonPath("$.content[0].name", is(designEagle.getName())),
                        jsonPath("$.content[0].vendor.id", is(vendorGigabyte.getId().toString())),
                        jsonPath("$.content[0].vendor.name", is(vendorGigabyte.getName())),
                        jsonPath("$.content[1].name", is(designVentus3X.getName())),
                        jsonPath("$.content[1].vendor.id", is(vendorMsi.getId().toString())),
                        jsonPath("$.content[1].vendor.name", is(vendorMsi.getName()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final Design saved = designRepository.save(
                mapper.convertFromDto(designVentus3X)
        );
        assertThat(designRepository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_DESIGNS + "/{id}",
                saved.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(designVentus3X.getName())),
                        jsonPath("$.vendor.id", is(vendorMsi.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorMsi.getName()))
                );
    }

    @Test
    void getById_withNonExistentId_shouldReturnError() throws Exception {
        // given
        designRepository.save(
                mapper.convertFromDto(designVentus3X)
        );
        assertThat(designRepository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_DESIGNS + "/{id}",
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
                                        "Design with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentName_shouldReturnCreatedEntity() throws Exception {
        // given
        designRepository.save(
                mapper.convertFromDto(designEagle)
        );
        assertThat(designRepository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(designVentus3X);
        final var requestBuilder = post(URL_API_V1_DESIGNS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(designVentus3X.getName())),
                        jsonPath("$.vendor.id", is(vendorMsi.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorMsi.getName()))
                );

        final List<Design> designs = designRepository.findAll();
        assertThat(designs).hasSize(2);
        final Design design = designs.get(1);
        assertThat(design.getId()).isNotNull();
        assertThat(design.getName()).isEqualTo(designVentus3X.getName());
        assertThat(design.getVendor().getId()).isEqualTo(vendorMsi.getId());
        assertThat(design.getVendor().getName()).isEqualTo(vendorMsi.getName());
    }

    @Test
    void create_withNonExistentVendorId_shouldReturnError() throws Exception {
        // given
        designRepository.save(
                mapper.convertFromDto(designEagle)
        );
        assertThat(designRepository.findAll()).hasSize(1);
        final UUID nonExistentVendorId = UUID.randomUUID();
        designVentus3X.setVendorId(nonExistentVendorId);
        final String jsonRequest = objectMapper.writeValueAsString(designVentus3X);
        final var requestBuilder = post(URL_API_V1_DESIGNS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("id")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Vendor with ID = <{0}> not found!",
                                        nonExistentVendorId
                                )
                        ))
                );

        assertThat(designRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectVendorParam_shouldReturnError() throws Exception {
        // given
        designRepository.save(
                mapper.convertFromDto(designEagle)
        );
        assertThat(designRepository.findAll()).hasSize(1);
        designVentus3X.setVendorId(null);
        final String jsonRequest = objectMapper.writeValueAsString(designVentus3X);
        final var requestBuilder = post(URL_API_V1_DESIGNS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("vendor")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        assertThat(designRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withExistentName_shouldReturnError() throws Exception {
        // given
        designRepository.save(
                mapper.convertFromDto(designVentus3X)
        );
        assertThat(designRepository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(designVentus3X);
        final var requestBuilder = post(URL_API_V1_DESIGNS)
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
                                        "Design with name <{0}> already exists!",
                                        designVentus3X.getName()
                                )
                        ))
                );

        assertThat(designRepository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentName_shouldReturnReplacedEntity() throws Exception {
        // given
        designRepository.save(
                mapper.convertFromDto(designVentus3X)
        );
        final Design saved = designRepository.save(
                mapper.convertFromDto(designEagle)
        );
        assertThat(designRepository.findAll()).hasSize(2);
        final String newName = "Gaming X";
        final DesignRequestDto dto = DesignRequestDto.builder()
                .name(newName)
                .vendorId(vendorGigabyte.getId())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_DESIGNS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(newName)),
                        jsonPath("$.vendor.id", is(vendorGigabyte.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorGigabyte.getName()))
                );

        final Optional<Design> optDesign = designRepository.findById(saved.getId());
        assertThat(optDesign).isPresent();
        assertThat(optDesign.get().getName())
                .isEqualTo(newName);
        assertThat(optDesign.get().getVendor().getId())
                .isEqualTo(vendorGigabyte.getId());
        assertThat(optDesign.get().getVendor().getName())
                .isEqualTo(vendorGigabyte.getName());
    }

    @Test
    void replace_withNonExistentVendorId_shouldReturnError() throws Exception {
        // given
        designRepository.save(
                mapper.convertFromDto(designVentus3X)
        );
        final Design saved = designRepository.save(
                mapper.convertFromDto(designEagle)
        );
        assertThat(designRepository.findAll()).hasSize(2);
        final String newName = "Gaming X";
        final UUID nonExistentVendorId = UUID.randomUUID();
        final DesignRequestDto dto = DesignRequestDto.builder()
                .name(newName)
                .vendorId(nonExistentVendorId)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_DESIGNS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("id")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Vendor with ID = <{0}> not found!",
                                        nonExistentVendorId
                                )
                        ))
                );

        final Optional<Design> optDesign = designRepository.findById(saved.getId());
        assertThat(optDesign).isPresent();
        assertThat(optDesign.get().getName())
                .isEqualTo(designEagle.getName());
        assertThat(optDesign.get().getVendor().getId())
                .isEqualTo(vendorGigabyte.getId());
        assertThat(optDesign.get().getVendor().getName())
                .isEqualTo(vendorGigabyte.getName());
    }

    @Test
    void replace_withIncorrectVendorParam_shouldReturnError() throws Exception {
        // given
        designRepository.save(
                mapper.convertFromDto(designVentus3X)
        );
        final Design saved = designRepository.save(
                mapper.convertFromDto(designEagle)
        );
        assertThat(designRepository.findAll()).hasSize(2);
        final String newName = "Gaming X";
        final DesignRequestDto dto = DesignRequestDto.builder()
                .name(newName)
                .vendorId(null)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_DESIGNS + "/{id}",
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
                        jsonPath("$.violations[0].paramNames", contains("vendor")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        final Optional<Design> optDesign = designRepository.findById(saved.getId());
        assertThat(optDesign).isPresent();
        assertThat(optDesign.get().getName())
                .isEqualTo(designEagle.getName());
        assertThat(optDesign.get().getVendor().getId())
                .isEqualTo(vendorGigabyte.getId());
        assertThat(optDesign.get().getVendor().getName())
                .isEqualTo(vendorGigabyte.getName());
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        designRepository.save(
                mapper.convertFromDto(designVentus3X)
        );
        final Design saved = designRepository.save(
                mapper.convertFromDto(designEagle)
        );
        assertThat(designRepository.findAll()).hasSize(2);
        final DesignRequestDto dto = DesignRequestDto.builder()
                .name(designVentus3X.getName())
                .vendorId(vendorMsi.getId())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_DESIGNS + "/{id}",
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
                                        "Design with name <{0}> already exists!",
                                        designVentus3X.getName()
                                )
                        ))
                );

        final Optional<Design> optDesign = designRepository.findById(saved.getId());
        assertThat(optDesign).isPresent();
        assertThat(optDesign.get().getName())
                .isEqualTo(designEagle.getName());
        assertThat(optDesign.get().getVendor().getId())
                .isEqualTo(vendorGigabyte.getId());
        assertThat(optDesign.get().getVendor().getName())
                .isEqualTo(vendorGigabyte.getName());
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        designRepository.save(
                mapper.convertFromDto(designVentus3X)
        );
        final Design saved = designRepository.save(
                mapper.convertFromDto(designEagle)
        );
        assertThat(designRepository.findAll()).hasSize(2);
        final String newName = "Gaming X";
        final DesignRequestDto dto = DesignRequestDto.builder()
                .name(newName)
                .vendorId(vendorMsi.getId())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_DESIGNS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(newName)),
                        jsonPath("$.vendor.id", is(vendorMsi.getId().toString())),
                        jsonPath("$.vendor.name", is(vendorMsi.getName()))
                );

        final Optional<Design> optDesign = designRepository.findById(saved.getId());
        assertThat(optDesign).isPresent();
        assertThat(optDesign.get().getName())
                .isEqualTo(newName);
        assertThat(optDesign.get().getVendor().getId())
                .isEqualTo(vendorMsi.getId());
        assertThat(optDesign.get().getVendor().getName())
                .isEqualTo(vendorMsi.getName());
    }

    @Test
    void update_withNonExistentVendorId_shouldReturnError() throws Exception {
        // given
        designRepository.save(
                mapper.convertFromDto(designVentus3X)
        );
        final Design saved = designRepository.save(
                mapper.convertFromDto(designEagle)
        );
        assertThat(designRepository.findAll()).hasSize(2);
        final String newName = "Gaming X";
        final UUID nonExistentVendorId = UUID.randomUUID();
        final DesignRequestDto dto = DesignRequestDto.builder()
                .name(newName)
                .vendorId(nonExistentVendorId)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_DESIGNS + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("id")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Vendor with ID = <{0}> not found!",
                                        nonExistentVendorId
                                )
                        ))
                );

        final Optional<Design> optDesign = designRepository.findById(saved.getId());
        assertThat(optDesign).isPresent();
        assertThat(optDesign.get().getName())
                .isEqualTo(designEagle.getName());
        assertThat(optDesign.get().getVendor().getId())
                .isEqualTo(vendorGigabyte.getId());
        assertThat(optDesign.get().getVendor().getName())
                .isEqualTo(vendorGigabyte.getName());
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        designRepository.save(
                mapper.convertFromDto(designEagle)
        );
        assertThat(designRepository.findAll()).hasSize(1);
        final DesignRequestDto dto = DesignRequestDto.builder()
                .name(designEagle.getName())
                .vendorId(vendorGigabyte.getId())
                .build();

        final UUID designVentus3XId = designRepository.save(
                mapper.convertFromDto(designVentus3X)
        ).getId();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_DESIGNS + "/{id}",
                designVentus3XId)
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
                                        "Design with name <{0}> already exists!",
                                        designEagle.getName()
                                )
                        ))
                );

        final Optional<Design> optDesign = designRepository.findById(designVentus3XId);
        assertThat(optDesign).isPresent();
        assertThat(optDesign.get().getName())
                .isEqualTo(designVentus3X.getName());
        assertThat(optDesign.get().getVendor().getId())
                .isEqualTo(vendorMsi.getId());
        assertThat(optDesign.get().getVendor().getName())
                .isEqualTo(vendorMsi.getName());
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID designVentus3XId = designRepository.save(
                mapper.convertFromDto(designVentus3X)
        ).getId();
        assertThat(designRepository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_DESIGNS + "/{id}",
                designVentus3XId
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(designRepository.findAll()).isEmpty();
    }
}
