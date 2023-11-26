package ru.bukhtaev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.dto.mapper.dictionary.ISocketMapper;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.model.dictionary.Socket;
import ru.bukhtaev.repository.dictionary.ISocketRepository;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.dictionary.SocketRestController.URL_API_V1_SOCKETS;

/**
 * Интеграционные тесты для CRUD операций над сокетами.
 */
class SocketRestControllerIT extends AbstractIntegrationTest {

    /**
     * Маппер для DTO сокетов.
     */
    @Autowired
    private ISocketMapper mapper;

    /**
     * Репозиторий сокетов.
     */
    @Autowired
    private ISocketRepository repository;

    private NameableRequestDto socketLga1700;
    private NameableRequestDto socketAm5;

    @BeforeEach
    void setUp() {
        socketLga1700 = NameableRequestDto.builder()
                .name("LGA 1700")
                .build();
        socketAm5 = NameableRequestDto.builder()
                .name("AM5")
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
                mapper.convertFromDto(socketLga1700)
        );
        repository.save(
                mapper.convertFromDto(socketAm5)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_SOCKETS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].name", is(socketLga1700.getName())),
                        jsonPath("$[1].name", is(socketAm5.getName()))
                );
    }

    @Test
    void getAll_withPagination_shouldReturnAllEntitiesAsPage() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(socketLga1700)
        );
        repository.save(
                mapper.convertFromDto(socketAm5)
        );
        assertThat(repository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_SOCKETS + "/pageable")
                .params(NAMEABLE_PAGE_REQUEST_PARAMS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.content", hasSize(2)),
                        jsonPath("$.content[0].name", is(socketAm5.getName())),
                        jsonPath("$.content[1].name", is(socketLga1700.getName()))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final Socket saved = repository.save(
                mapper.convertFromDto(socketLga1700)
        );
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_SOCKETS + "/{id}",
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
                mapper.convertFromDto(socketLga1700)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_SOCKETS + "/{id}",
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
                                        "Socket with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentName_shouldReturnCreatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(socketAm5)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(socketLga1700);
        final var requestBuilder = post(URL_API_V1_SOCKETS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(socketLga1700.getName()))
                );

        final List<Socket> sockets = repository.findAll();
        assertThat(sockets).hasSize(2);
        final Socket socket = sockets.get(1);
        assertThat(socket.getId()).isNotNull();
        assertThat(socket.getName()).isEqualTo(socketLga1700.getName());
    }

    @Test
    void create_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(socketLga1700)
        );
        assertThat(repository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(socketLga1700);
        final var requestBuilder = post(URL_API_V1_SOCKETS)
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
                                        "Socket with name <{0}> already exists!",
                                        socketLga1700.getName()
                                )
                        ))
                );

        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentName_shouldReturnReplacedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(socketLga1700)
        );
        final Socket saved = repository.save(
                mapper.convertFromDto(socketAm5)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "LGA 2011 V3";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_SOCKETS + "/{id}",
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

        final Optional<Socket> optSocket = repository.findById(saved.getId());
        assertThat(optSocket).isPresent();
        assertThat(optSocket.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void replace_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(socketLga1700)
        );
        final Socket saved = repository.save(
                mapper.convertFromDto(socketAm5)
        );
        assertThat(repository.findAll()).hasSize(2);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(socketLga1700.getName())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_SOCKETS + "/{id}",
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
                                        "Socket with name <{0}> already exists!",
                                        socketLga1700.getName()
                                )
                        ))
                );

        final Optional<Socket> optSocket = repository.findById(saved.getId());
        assertThat(optSocket).isPresent();
        assertThat(optSocket.get().getName())
                .isEqualTo(socketAm5.getName());
    }

    @Test
    void update_withNonExistentName_shouldReturnUpdatedEntity() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(socketLga1700)
        );
        final Socket saved = repository.save(
                mapper.convertFromDto(socketAm5)
        );
        assertThat(repository.findAll()).hasSize(2);
        final String newName = "LGA 2011 V3";
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(newName)
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_SOCKETS + "/{id}",
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

        final Optional<Socket> optSocket = repository.findById(saved.getId());
        assertThat(optSocket).isPresent();
        assertThat(optSocket.get().getName())
                .isEqualTo(newName);
    }

    @Test
    void update_withExistentName_shouldReturnError() throws Exception {
        // given
        repository.save(
                mapper.convertFromDto(socketAm5)
        );
        assertThat(repository.findAll()).hasSize(1);
        final NameableRequestDto dto = NameableRequestDto.builder()
                .name(socketAm5.getName())
                .build();

        final UUID socketLga1700Id = repository.save(
                mapper.convertFromDto(socketLga1700)
        ).getId();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_SOCKETS + "/{id}",
                socketLga1700Id)
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
                                        "Socket with name <{0}> already exists!",
                                        socketAm5.getName()
                                )
                        ))
                );

        final Optional<Socket> optSocket = repository.findById(socketLga1700Id);
        assertThat(optSocket).isPresent();
        assertThat(optSocket.get().getName())
                .isEqualTo(socketLga1700.getName());
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID socketLga1700Id = repository.save(
                mapper.convertFromDto(socketLga1700)
        ).getId();
        assertThat(repository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_SOCKETS + "/{id}",
                socketLga1700Id
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(repository.findAll()).isEmpty();
    }
}
