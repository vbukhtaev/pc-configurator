package ru.bukhtaev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.dto.mapper.IFanSizeMapper;
import ru.bukhtaev.dto.request.FanSizeRequestDto;
import ru.bukhtaev.model.FanSize;
import ru.bukhtaev.repository.IFanSizeRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.FanSizeRestController.URL_API_V1_FAN_SIZES;

/**
 * Интеграционные тесты для CRUD операций над размерами вентиляторов.
 */
class FanSizeRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO размеров вентиляторов.
     */
    @Autowired
    private IFanSizeMapper mapper;

    /**
     * Репозиторий размеров вентиляторов.
     */
    @Autowired
    private IFanSizeRepository repository;

    private FanSizeRequestDto size120;
    private FanSizeRequestDto size140;

    @BeforeEach
    void setUp() {
        size120 = FanSizeRequestDto.builder()
                .length(120)
                .width(120)
                .height(25)
                .build();
        size140 = FanSizeRequestDto.builder()
                .length(140)
                .width(140)
                .height(25)
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
                mapper.convertFromDto(size120)
        );
        repository.save(
                mapper.convertFromDto(size140)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_FAN_SIZES);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].length", is(size120.getLength())),
                        jsonPath("$[0].width", is(size120.getWidth())),
                        jsonPath("$[0].height", is(size120.getHeight())),
                        jsonPath("$[1].length", is(size140.getLength())),
                        jsonPath("$[1].width", is(size140.getWidth())),
                        jsonPath("$[1].height", is(size140.getHeight()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final FanSize saved = repository.save(
                mapper.convertFromDto(size120)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_FAN_SIZES + "/{id}",
                saved.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length", is(saved.getLength())),
                        jsonPath("$.width", is(saved.getWidth())),
                        jsonPath("$.height", is(saved.getHeight()))
                );
    }

    @Test
    void getById_withNonExistentId_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(size120)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_FAN_SIZES + "/{id}",
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
                                        "Fan size with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentName_shouldReturnCreatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(size140)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(size120);
        final var requestBuilder = post(URL_API_V1_FAN_SIZES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length", is(size120.getLength())),
                        jsonPath("$.width", is(size120.getWidth())),
                        jsonPath("$.height", is(size120.getHeight()))
                );

        final List<FanSize> manufacturers = repository.findAll();
        assertThat(manufacturers).hasSize(2);
        final FanSize manufacturer = manufacturers.get(1);
        assertThat(manufacturer.getId()).isNotNull();
        assertThat(manufacturer.getLength()).isEqualTo(size120.getLength());
        assertThat(manufacturer.getWidth()).isEqualTo(size120.getWidth());
        assertThat(manufacturer.getHeight()).isEqualTo(size120.getHeight());
    }

    @Test
    void create_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(size120)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(size120);
        final var requestBuilder = post(URL_API_V1_FAN_SIZES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames",
                                contains("length", "width", "height")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Fan size <{0} x {1} x {2}> already exists!",
                                        size120.getLength(),
                                        size120.getWidth(),
                                        size120.getHeight()
                                )
                        ))
                );

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentName_shouldReturnReplacedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(size120)
        );
        final FanSize saved = repository.save(
                mapper.convertFromDto(size140)
        );
        assertThat(repository.findAll()).hasSize(2);
        final Integer newLength = 90;
        final Integer newWidth = 90;
        final Integer newHeight = 25;
        final FanSizeRequestDto dto = FanSizeRequestDto.builder()
                .length(newLength)
                .width(newWidth)
                .height(newHeight)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_FAN_SIZES + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length", is(newLength)),
                        jsonPath("$.width", is(newWidth)),
                        jsonPath("$.height", is(newHeight))
                );

        final Optional<FanSize> optFanSize = repository.findById(saved.getId());
        assertThat(optFanSize).isPresent();
        assertThat(optFanSize.get().getLength())
                .isEqualTo(newLength);
        assertThat(optFanSize.get().getWidth())
                .isEqualTo(newWidth);
        assertThat(optFanSize.get().getHeight())
                .isEqualTo(newHeight);
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(size120)
        );
        final FanSize saved = repository.save(
                mapper.convertFromDto(size140)
        );
        assertThat(repository.findAll()).hasSize(2);
        final FanSizeRequestDto dto = FanSizeRequestDto.builder()
                .length(size120.getLength())
                .width(size120.getWidth())
                .height(size120.getHeight())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_FAN_SIZES + "/{id}",
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
                        jsonPath("$.violations[0].paramNames",
                                contains("length", "width", "height")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Fan size <{0} x {1} x {2}> already exists!",
                                        size120.getLength(),
                                        size120.getWidth(),
                                        size120.getHeight()
                                )
                        ))
                );

        final Optional<FanSize> optFanSize = repository.findById(saved.getId());
        assertThat(optFanSize).isPresent();
        assertThat(optFanSize.get().getLength())
                .isEqualTo(size140.getLength());
        assertThat(optFanSize.get().getWidth())
                .isEqualTo(size140.getWidth());
        assertThat(optFanSize.get().getHeight())
                .isEqualTo(size140.getHeight());
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(size120)
        );
        final FanSize saved = repository.save(
                mapper.convertFromDto(size140)
        );
        assertThat(repository.findAll()).hasSize(2);
        final Integer newLength = 90;
        final Integer newWidth = 90;
        final Integer newHeight = 25;
        final FanSizeRequestDto dto = FanSizeRequestDto.builder()
                .length(newLength)
                .width(newWidth)
                .height(newHeight)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_FAN_SIZES + "/{id}",
                saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length", is(newLength)),
                        jsonPath("$.width", is(newWidth)),
                        jsonPath("$.height", is(newHeight))
                );

        final Optional<FanSize> optFanSize = repository.findById(saved.getId());
        assertThat(optFanSize).isPresent();
        assertThat(optFanSize.get().getLength())
                .isEqualTo(newLength);
        assertThat(optFanSize.get().getWidth())
                .isEqualTo(newWidth);
        assertThat(optFanSize.get().getHeight())
                .isEqualTo(newHeight);
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(size140)
        );
        assertThat(repository.findAll()).hasSize(1);
        final FanSizeRequestDto dto = FanSizeRequestDto.builder()
                .length(size140.getLength())
                .width(size140.getWidth())
                .height(size140.getHeight())
                .build();

        final UUID size120Id = repository.save(
                mapper.convertFromDto(size120)
        ).getId();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_FAN_SIZES + "/{id}",
                size120Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames",
                                contains("length", "width", "height")),
                        jsonPath("$.violations[0].message", is(
                                MessageFormat.format(
                                        "Fan size <{0} x {1} x {2}> already exists!",
                                        size140.getLength(),
                                        size140.getWidth(),
                                        size140.getHeight()
                                )
                        ))
                );

        final Optional<FanSize> optFanSize = repository.findById(size120Id);
        assertThat(optFanSize).isPresent();
        assertThat(optFanSize.get().getLength())
                .isEqualTo(size120.getLength());
        assertThat(optFanSize.get().getWidth())
                .isEqualTo(size120.getWidth());
        assertThat(optFanSize.get().getHeight())
                .isEqualTo(size120.getHeight());
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID size120Id = repository.save(
                mapper.convertFromDto(size120)
        ).getId();
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_FAN_SIZES + "/{id}",
                size120Id
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(repository.findAll()).isEmpty();
    }
}
