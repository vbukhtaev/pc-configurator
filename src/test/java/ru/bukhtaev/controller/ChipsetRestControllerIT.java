package ru.bukhtaev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.dto.mapper.IChipsetMapper;
import ru.bukhtaev.dto.request.ChipsetRequestDto;
import ru.bukhtaev.model.Chipset;
import ru.bukhtaev.model.Socket;
import ru.bukhtaev.repository.IChipsetRepository;
import ru.bukhtaev.repository.ISocketRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.ChipsetRestController.URL_API_V1_CHIPSETS;

/**
 * Интеграционные тесты для CRUD операций над чипсетами.
 */
class ChipsetRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO чипсетов.
     */
    @Autowired
    private IChipsetMapper mapper;

    /**
     * Репозиторий чипсетов.
     */
    @Autowired
    private IChipsetRepository chipsetRepository;

    /**
     * Репозиторий сокетов.
     */
    @Autowired
    private ISocketRepository socketRepository;

    private ChipsetRequestDto chipsetB660;
    private ChipsetRequestDto chipsetX670E;

    private Socket socketLga1700;
    private Socket socketAm5;

    @BeforeEach
    void setUp() {
        socketLga1700 = socketRepository.save(
                Socket.builder()
                        .name("LGA 1700")
                        .build()
        );
        socketAm5 = socketRepository.save(
                Socket.builder()
                        .name("AM5")
                        .build()
        );

        chipsetB660 = ChipsetRequestDto.builder()
                .name("B660")
                .socketId(socketLga1700.getId())
                .build();
        chipsetX670E = ChipsetRequestDto.builder()
                .name("X670E")
                .socketId(socketAm5.getId())
                .build();
    }

    @AfterEach
    void tearDown() {
        chipsetRepository.deleteAll();
        socketRepository.deleteAll();
    }

    @Test
    void getAll_shouldReturnAllEntities() throws Exception {
        // given
        chipsetRepository.save(
                mapper.convertFromDto(chipsetB660)
        );
        chipsetRepository.save(
                mapper.convertFromDto(chipsetX670E)
        );
        assertThat(chipsetRepository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_CHIPSETS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].name", is(chipsetB660.getName())),
                        jsonPath("$[0].socket.id", is(socketLga1700.getId().toString())),
                        jsonPath("$[0].socket.name", is(socketLga1700.getName())),
                        jsonPath("$[1].name", is(chipsetX670E.getName())),
                        jsonPath("$[1].socket.id", is(socketAm5.getId().toString())),
                        jsonPath("$[1].socket.name", is(socketAm5.getName()))
                );
    }

    @Test
    void getAll_withPagination_shouldReturnAllEntitiesAsPage() throws Exception {
        // given
        chipsetRepository.save(
                mapper.convertFromDto(chipsetB660)
        );
        chipsetRepository.save(
                mapper.convertFromDto(chipsetX670E)
        );
        assertThat(chipsetRepository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_CHIPSETS + "/pageable")
                .params(CHIPSET_PAGE_REQUEST_PARAMS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.content", hasSize(2)),
                        jsonPath("$.content[0].name", is(chipsetX670E.getName())),
                        jsonPath("$.content[0].socket.id", is(socketAm5.getId().toString())),
                        jsonPath("$.content[0].socket.name", is(socketAm5.getName())),
                        jsonPath("$.content[1].name", is(chipsetB660.getName())),
                        jsonPath("$.content[1].socket.id", is(socketLga1700.getId().toString())),
                        jsonPath("$.content[1].socket.name", is(socketLga1700.getName()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final Chipset saved = chipsetRepository.save(
                mapper.convertFromDto(chipsetB660)
        );
        assertThat(chipsetRepository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_CHIPSETS + "/{id}",
                saved.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(chipsetB660.getName())),
                        jsonPath("$.socket.id", is(socketLga1700.getId().toString())),
                        jsonPath("$.socket.name", is(socketLga1700.getName()))
                );
    }

    @Test
    void getById_withNonExistentId_shouldReturnError() throws Exception {
        // given
        chipsetRepository.save(
                mapper.convertFromDto(chipsetB660)
        );
        assertThat(chipsetRepository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_CHIPSETS + "/{id}",
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
                                        "Chipset with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentName_shouldReturnCreatedEntity() throws Exception {
        // given
        chipsetRepository.save(
                mapper.convertFromDto(chipsetX670E)
        );
        assertThat(chipsetRepository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(chipsetB660);
        final var requestBuilder = post(URL_API_V1_CHIPSETS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(chipsetB660.getName())),
                        jsonPath("$.socket.id", is(socketLga1700.getId().toString())),
                        jsonPath("$.socket.name", is(socketLga1700.getName()))
                );

        final List<Chipset> chipsets = chipsetRepository.findAll();
        assertThat(chipsets).hasSize(2);
        final Chipset chipset = chipsets.get(1);
        assertThat(chipset.getId()).isNotNull();
        assertThat(chipset.getName()).isEqualTo(chipsetB660.getName());
        assertThat(chipset.getSocket().getId()).isEqualTo(socketLga1700.getId());
        assertThat(chipset.getSocket().getName()).isEqualTo(socketLga1700.getName());
    }

    @Test
    void create_withNonExistentSocketId_shouldReturnError() throws Exception {
        // given
        chipsetRepository.save(
                mapper.convertFromDto(chipsetX670E)
        );
        assertThat(chipsetRepository.findAll()).hasSize(1);
        final UUID nonExistentSocketId = UUID.randomUUID();
        chipsetB660.setSocketId(nonExistentSocketId);
        final String jsonRequest = objectMapper.writeValueAsString(chipsetB660);
        final var requestBuilder = post(URL_API_V1_CHIPSETS)
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
                                        "Socket with ID = <{0}> not found!",
                                        nonExistentSocketId
                                )
                        ))
                );

        assertThat(chipsetRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectSocketParam_shouldReturnError() throws Exception {
        // given
        chipsetRepository.save(
                mapper.convertFromDto(chipsetX670E)
        );
        assertThat(chipsetRepository.findAll()).hasSize(1);
        chipsetB660.setSocketId(null);
        final String jsonRequest = objectMapper.writeValueAsString(chipsetB660);
        final var requestBuilder = post(URL_API_V1_CHIPSETS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("socket")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        assertThat(chipsetRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withExistentName_shouldReturnError() throws Exception {
        // given
        chipsetRepository.save(
                mapper.convertFromDto(chipsetB660)
        );
        assertThat(chipsetRepository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(chipsetB660);
        final var requestBuilder = post(URL_API_V1_CHIPSETS)
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
                                        "Chipset with name <{0}> already exists!",
                                        chipsetB660.getName()
                                )
                        ))
                );

        assertThat(chipsetRepository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentName_shouldReturnReplacedEntity() throws Exception {
        // given
        chipsetRepository.save(
                mapper.convertFromDto(chipsetB660)
        );
        final Chipset saved = chipsetRepository.save(
                mapper.convertFromDto(chipsetX670E)
        );
        assertThat(chipsetRepository.findAll()).hasSize(2);
        final String newName = "X99";
        final ChipsetRequestDto dto = ChipsetRequestDto.builder()
                .name(newName)
                .socketId(socketAm5.getId())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_CHIPSETS + "/{id}",
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
                        jsonPath("$.socket.id", is(socketAm5.getId().toString())),
                        jsonPath("$.socket.name", is(socketAm5.getName()))
                );

        final Optional<Chipset> optChipset = chipsetRepository.findById(saved.getId());
        assertThat(optChipset).isPresent();
        assertThat(optChipset.get().getName())
                .isEqualTo(newName);
        assertThat(optChipset.get().getSocket().getId())
                .isEqualTo(socketAm5.getId());
        assertThat(optChipset.get().getSocket().getName())
                .isEqualTo(socketAm5.getName());
    }

    @Test
    void replace_withNonExistentSocketId_shouldReturnError() throws Exception {
        // given
        chipsetRepository.save(
                mapper.convertFromDto(chipsetB660)
        );
        final Chipset saved = chipsetRepository.save(
                mapper.convertFromDto(chipsetX670E)
        );
        assertThat(chipsetRepository.findAll()).hasSize(2);
        final String newName = "X99";
        final UUID nonExistentSocketId = UUID.randomUUID();
        final ChipsetRequestDto dto = ChipsetRequestDto.builder()
                .name(newName)
                .socketId(nonExistentSocketId)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_CHIPSETS + "/{id}",
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
                                        "Socket with ID = <{0}> not found!",
                                        nonExistentSocketId
                                )
                        ))
                );

        final Optional<Chipset> optChipset = chipsetRepository.findById(saved.getId());
        assertThat(optChipset).isPresent();
        assertThat(optChipset.get().getName())
                .isEqualTo(chipsetX670E.getName());
        assertThat(optChipset.get().getSocket().getId())
                .isEqualTo(socketAm5.getId());
        assertThat(optChipset.get().getSocket().getName())
                .isEqualTo(socketAm5.getName());
    }

    @Test
    void replace_withIncorrectSocketParam_shouldReturnError() throws Exception {
        // given
        chipsetRepository.save(
                mapper.convertFromDto(chipsetB660)
        );
        final Chipset saved = chipsetRepository.save(
                mapper.convertFromDto(chipsetX670E)
        );
        assertThat(chipsetRepository.findAll()).hasSize(2);
        final String newName = "X99";
        final ChipsetRequestDto dto = ChipsetRequestDto.builder()
                .name(newName)
                .socketId(null)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_CHIPSETS + "/{id}",
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
                        jsonPath("$.violations[0].paramNames", contains("socket")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        final Optional<Chipset> optChipset = chipsetRepository.findById(saved.getId());
        assertThat(optChipset).isPresent();
        assertThat(optChipset.get().getName())
                .isEqualTo(chipsetX670E.getName());
        assertThat(optChipset.get().getSocket().getId())
                .isEqualTo(socketAm5.getId());
        assertThat(optChipset.get().getSocket().getName())
                .isEqualTo(socketAm5.getName());
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        chipsetRepository.save(
                mapper.convertFromDto(chipsetB660)
        );
        final Chipset saved = chipsetRepository.save(
                mapper.convertFromDto(chipsetX670E)
        );
        assertThat(chipsetRepository.findAll()).hasSize(2);
        final ChipsetRequestDto dto = ChipsetRequestDto.builder()
                .name(chipsetB660.getName())
                .socketId(socketLga1700.getId())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_CHIPSETS + "/{id}",
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
                                        "Chipset with name <{0}> already exists!",
                                        chipsetB660.getName()
                                )
                        ))
                );

        final Optional<Chipset> optChipset = chipsetRepository.findById(saved.getId());
        assertThat(optChipset).isPresent();
        assertThat(optChipset.get().getName())
                .isEqualTo(chipsetX670E.getName());
        assertThat(optChipset.get().getSocket().getId())
                .isEqualTo(socketAm5.getId());
        assertThat(optChipset.get().getSocket().getName())
                .isEqualTo(socketAm5.getName());
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        chipsetRepository.save(
                mapper.convertFromDto(chipsetB660)
        );
        final Chipset saved = chipsetRepository.save(
                mapper.convertFromDto(chipsetX670E)
        );
        assertThat(chipsetRepository.findAll()).hasSize(2);
        final String newName = "X99";
        final ChipsetRequestDto dto = ChipsetRequestDto.builder()
                .name(newName)
                .socketId(socketLga1700.getId())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_CHIPSETS + "/{id}",
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
                        jsonPath("$.socket.id", is(socketLga1700.getId().toString())),
                        jsonPath("$.socket.name", is(socketLga1700.getName()))
                );

        final Optional<Chipset> optChipset = chipsetRepository.findById(saved.getId());
        assertThat(optChipset).isPresent();
        assertThat(optChipset.get().getName())
                .isEqualTo(newName);
        assertThat(optChipset.get().getSocket().getId())
                .isEqualTo(socketLga1700.getId());
        assertThat(optChipset.get().getSocket().getName())
                .isEqualTo(socketLga1700.getName());
    }

    @Test
    void update_withNonExistentSocketId_shouldReturnError() throws Exception {
        // given
        chipsetRepository.save(
                mapper.convertFromDto(chipsetB660)
        );
        final Chipset saved = chipsetRepository.save(
                mapper.convertFromDto(chipsetX670E)
        );
        assertThat(chipsetRepository.findAll()).hasSize(2);
        final String newName = "X99";
        final UUID nonExistentSocketId = UUID.randomUUID();
        final ChipsetRequestDto dto = ChipsetRequestDto.builder()
                .name(newName)
                .socketId(nonExistentSocketId)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_CHIPSETS + "/{id}",
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
                                        "Socket with ID = <{0}> not found!",
                                        nonExistentSocketId
                                )
                        ))
                );

        final Optional<Chipset> optChipset = chipsetRepository.findById(saved.getId());
        assertThat(optChipset).isPresent();
        assertThat(optChipset.get().getName())
                .isEqualTo(chipsetX670E.getName());
        assertThat(optChipset.get().getSocket().getId())
                .isEqualTo(socketAm5.getId());
        assertThat(optChipset.get().getSocket().getName())
                .isEqualTo(socketAm5.getName());
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        chipsetRepository.save(
                mapper.convertFromDto(chipsetX670E)
        );
        assertThat(chipsetRepository.findAll()).hasSize(1);
        final ChipsetRequestDto dto = ChipsetRequestDto.builder()
                .name(chipsetX670E.getName())
                .socketId(socketAm5.getId())
                .build();

        final UUID chipsetB660Id = chipsetRepository.save(
                mapper.convertFromDto(chipsetB660)
        ).getId();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_CHIPSETS + "/{id}",
                chipsetB660Id)
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
                                        "Chipset with name <{0}> already exists!",
                                        chipsetX670E.getName()
                                )
                        ))
                );

        final Optional<Chipset> optChipset = chipsetRepository.findById(chipsetB660Id);
        assertThat(optChipset).isPresent();
        assertThat(optChipset.get().getName())
                .isEqualTo(chipsetB660.getName());
        assertThat(optChipset.get().getSocket().getId())
                .isEqualTo(socketLga1700.getId());
        assertThat(optChipset.get().getSocket().getName())
                .isEqualTo(socketLga1700.getName());
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID chipsetB660Id = chipsetRepository.save(
                mapper.convertFromDto(chipsetB660)
        ).getId();
        assertThat(chipsetRepository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_CHIPSETS + "/{id}",
                chipsetB660Id
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(chipsetRepository.findAll()).isEmpty();
    }
}
