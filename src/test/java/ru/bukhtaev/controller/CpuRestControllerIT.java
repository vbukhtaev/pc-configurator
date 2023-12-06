package ru.bukhtaev.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import ru.bukhtaev.dto.request.CpuRequestDto;
import ru.bukhtaev.model.*;
import ru.bukhtaev.model.cross.CpuToRamType;
import ru.bukhtaev.model.dictionary.Manufacturer;
import ru.bukhtaev.model.dictionary.RamType;
import ru.bukhtaev.model.dictionary.Socket;
import ru.bukhtaev.repository.ICpuRepository;
import ru.bukhtaev.repository.dictionary.IManufacturerRepository;
import ru.bukhtaev.repository.dictionary.IRamTypeRepository;
import ru.bukhtaev.repository.dictionary.ISocketRepository;
import ru.bukhtaev.service.TransactionService;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.bukhtaev.controller.CpuRestController.URL_API_V1_CPUS;

/**
 * Интеграционные тесты для CRUD операций над процессорами.
 */
class CpuRestControllerIT extends AbstractIntegrationTest {

    /**
     * Репозиторий процессоров.
     */
    @Autowired
    private ICpuRepository cpuRepository;

    /**
     * Репозиторий производителей.
     */
    @Autowired
    private IManufacturerRepository manufacturerRepository;

    /**
     * Репозиторий типов оперативной памяти.
     */
    @Autowired
    private IRamTypeRepository ramTypeRepository;

    /**
     * Репозиторий сокетов.
     */
    @Autowired
    private ISocketRepository socketRepository;

    /**
     * Утилитный сервис выполнения кода в транзакции.
     */
    @Autowired
    private TransactionService transactionService;

    private CpuRequestDto cpuDtoI512400F;
    private CpuRequestDto cpuDtoR55600X;

    private Cpu cpuI512400F;
    private Cpu cpuR55600X;

    private RamType typeDdr3;
    private RamType typeDdr4;
    private RamType typeDdr5;

    private Socket socketLga1700;
    private Socket socketAm4;

    private Manufacturer manufacturerIntel;
    private Manufacturer manufacturerAmd;

    @BeforeEach
    void setUp() {
        manufacturerIntel = manufacturerRepository.save(
                Manufacturer.builder()
                        .name("Intel")
                        .build()
        );
        manufacturerAmd = manufacturerRepository.save(
                Manufacturer.builder()
                        .name("AMD")
                        .build()
        );

        typeDdr3 = ramTypeRepository.save(
                RamType.builder()
                        .name("DDR3")
                        .build()
        );
        typeDdr4 = ramTypeRepository.save(
                RamType.builder()
                        .name("DDR4")
                        .build()
        );
        typeDdr5 = ramTypeRepository.save(
                RamType.builder()
                        .name("DDR5")
                        .build()
        );

        socketLga1700 = socketRepository.save(
                Socket.builder()
                        .name("LGA 1700")
                        .build()
        );
        socketAm4 = socketRepository.save(
                Socket.builder()
                        .name("AM4")
                        .build()
        );

        cpuI512400F = Cpu.builder()
                .name("i5 12400F")
                .coreCount(6)
                .threadCount(12)
                .baseClock(2500)
                .maxClock(4000)
                .l3CacheSize(18)
                .maxTdp(117)
                .maxMemorySize(131072)
                .manufacturer(manufacturerIntel)
                .socket(socketLga1700)
                .build();

        cpuI512400F.addRamType(typeDdr4, 3200);
        cpuI512400F.addRamType(typeDdr5, 4800);

        cpuR55600X = Cpu.builder()
                .name("Ryzen 5 5600X")
                .coreCount(8)
                .threadCount(16)
                .baseClock(3600)
                .maxClock(4400)
                .l3CacheSize(32)
                .maxTdp(65)
                .maxMemorySize(131072)
                .manufacturer(manufacturerAmd)
                .socket(socketAm4)
                .build();

        cpuR55600X.addRamType(typeDdr3, 1866);
        cpuR55600X.addRamType(typeDdr4, 3200);

        cpuDtoI512400F = CpuRequestDto.builder()
                .name(cpuI512400F.getName())
                .coreCount(cpuI512400F.getCoreCount())
                .threadCount(cpuI512400F.getThreadCount())
                .baseClock(cpuI512400F.getBaseClock())
                .maxClock(cpuI512400F.getMaxClock())
                .l3CacheSize(cpuI512400F.getL3CacheSize())
                .maxTdp(cpuI512400F.getMaxTdp())
                .maxMemorySize(cpuI512400F.getMaxMemorySize())
                .manufacturerId(manufacturerIntel.getId())
                .socketId(socketLga1700.getId())
                .build();

        cpuDtoI512400F.addRamType(typeDdr4.getId(), 3200);
        cpuDtoI512400F.addRamType(typeDdr5.getId(), 4800);

        cpuDtoR55600X = CpuRequestDto.builder()
                .name(cpuR55600X.getName())
                .coreCount(cpuR55600X.getCoreCount())
                .threadCount(cpuR55600X.getThreadCount())
                .baseClock(cpuR55600X.getBaseClock())
                .maxClock(cpuR55600X.getMaxClock())
                .l3CacheSize(cpuR55600X.getL3CacheSize())
                .maxTdp(cpuR55600X.getMaxTdp())
                .maxMemorySize(cpuR55600X.getMaxMemorySize())
                .manufacturerId(manufacturerAmd.getId())
                .socketId(socketAm4.getId())
                .build();

        cpuDtoR55600X.addRamType(typeDdr3.getId(), 1866);
        cpuDtoR55600X.addRamType(typeDdr4.getId(), 3200);
    }

    @AfterEach
    void tearDown() {
        cpuRepository.deleteAll();
        socketRepository.deleteAll();
        ramTypeRepository.deleteAll();
        manufacturerRepository.deleteAll();
    }

    @Test
    void getAll_shouldReturnAllEntities() throws Exception {
        // given
        cpuRepository.save(cpuI512400F);
        cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_CPUS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),

                        jsonPath("$[0].name", is(cpuDtoI512400F.getName())),
                        jsonPath("$[0].coreCount", is(cpuDtoI512400F.getCoreCount())),
                        jsonPath("$[0].threadCount", is(cpuDtoI512400F.getThreadCount())),
                        jsonPath("$[0].baseClock", is(cpuDtoI512400F.getBaseClock())),
                        jsonPath("$[0].maxClock", is(cpuDtoI512400F.getMaxClock())),
                        jsonPath("$[0].l3CacheSize", is(cpuDtoI512400F.getL3CacheSize())),
                        jsonPath("$[0].maxTdp", is(cpuDtoI512400F.getMaxTdp())),
                        jsonPath("$[0].maxMemorySize", is(cpuDtoI512400F.getMaxMemorySize())),
                        jsonPath("$[0].manufacturer.id", is(manufacturerIntel.getId().toString())),
                        jsonPath("$[0].manufacturer.name", is(manufacturerIntel.getName())),
                        jsonPath("$[0].socket.id", is(socketLga1700.getId().toString())),
                        jsonPath("$[0].socket.name", is(socketLga1700.getName())),
                        jsonPath("$[0].supportedRamTypes", hasSize(2)),
                        jsonPath("$[0].supportedRamTypes[*].ramType.id").value(containsInAnyOrder(
                                typeDdr4.getId().toString(),
                                typeDdr5.getId().toString()
                        )),
                        jsonPath("$[0].supportedRamTypes[*].ramType.name").value(containsInAnyOrder(
                                typeDdr4.getName(),
                                typeDdr5.getName()
                        )),
                        jsonPath("$[0].supportedRamTypes[*].maxMemoryClock").value(containsInAnyOrder(
                                3200, 4800
                        )),

                        jsonPath("$[1].name", is(cpuDtoR55600X.getName())),
                        jsonPath("$[1].coreCount", is(cpuDtoR55600X.getCoreCount())),
                        jsonPath("$[1].threadCount", is(cpuDtoR55600X.getThreadCount())),
                        jsonPath("$[1].baseClock", is(cpuDtoR55600X.getBaseClock())),
                        jsonPath("$[1].maxClock", is(cpuDtoR55600X.getMaxClock())),
                        jsonPath("$[1].l3CacheSize", is(cpuDtoR55600X.getL3CacheSize())),
                        jsonPath("$[1].maxTdp", is(cpuDtoR55600X.getMaxTdp())),
                        jsonPath("$[1].maxMemorySize", is(cpuDtoR55600X.getMaxMemorySize())),
                        jsonPath("$[1].manufacturer.id", is(manufacturerAmd.getId().toString())),
                        jsonPath("$[1].manufacturer.name", is(manufacturerAmd.getName())),
                        jsonPath("$[1].socket.id", is(socketAm4.getId().toString())),
                        jsonPath("$[1].socket.name", is(socketAm4.getName())),
                        jsonPath("$[1].supportedRamTypes", hasSize(2)),
                        jsonPath("$[1].supportedRamTypes[*].ramType.id").value(containsInAnyOrder(
                                typeDdr4.getId().toString(),
                                typeDdr3.getId().toString()
                        )),
                        jsonPath("$[1].supportedRamTypes[*].ramType.name").value(containsInAnyOrder(
                                typeDdr4.getName(),
                                typeDdr3.getName()
                        )),
                        jsonPath("$[1].supportedRamTypes[*].maxMemoryClock").value(containsInAnyOrder(
                                3200, 1866
                        ))
                );
    }

    @Test
    void getAll_withPagination_shouldReturnAllEntitiesAsPage() throws Exception {
        // given
        cpuRepository.save(cpuI512400F);
        cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(2);
        final var requestBuilder = get(URL_API_V1_CPUS + "/pageable")
                .params(COOLER_PAGE_REQUEST_PARAMS);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.content", hasSize(2)),

                        jsonPath("$.content.[0].name", is(cpuDtoI512400F.getName())),
                        jsonPath("$.content.[0].coreCount", is(cpuDtoI512400F.getCoreCount())),
                        jsonPath("$.content.[0].threadCount", is(cpuDtoI512400F.getThreadCount())),
                        jsonPath("$.content.[0].baseClock", is(cpuDtoI512400F.getBaseClock())),
                        jsonPath("$.content.[0].maxClock", is(cpuDtoI512400F.getMaxClock())),
                        jsonPath("$.content.[0].l3CacheSize", is(cpuDtoI512400F.getL3CacheSize())),
                        jsonPath("$.content.[0].maxTdp", is(cpuDtoI512400F.getMaxTdp())),
                        jsonPath("$.content.[0].maxMemorySize", is(cpuDtoI512400F.getMaxMemorySize())),
                        jsonPath("$.content.[0].manufacturer.id", is(manufacturerIntel.getId().toString())),
                        jsonPath("$.content.[0].manufacturer.name", is(manufacturerIntel.getName())),
                        jsonPath("$.content.[0].socket.id", is(socketLga1700.getId().toString())),
                        jsonPath("$.content.[0].socket.name", is(socketLga1700.getName())),
                        jsonPath("$.content.[0].supportedRamTypes", hasSize(2)),
                        jsonPath("$.content.[0].supportedRamTypes[*].ramType.id").value(containsInAnyOrder(
                                typeDdr4.getId().toString(),
                                typeDdr5.getId().toString()
                        )),
                        jsonPath("$.content.[0].supportedRamTypes[*].ramType.name").value(containsInAnyOrder(
                                typeDdr4.getName(),
                                typeDdr5.getName()
                        )),
                        jsonPath("$.content.[0].supportedRamTypes[*].maxMemoryClock").value(containsInAnyOrder(
                                3200, 4800
                        )),

                        jsonPath("$.content.[1].name", is(cpuDtoR55600X.getName())),
                        jsonPath("$.content.[1].coreCount", is(cpuDtoR55600X.getCoreCount())),
                        jsonPath("$.content.[1].threadCount", is(cpuDtoR55600X.getThreadCount())),
                        jsonPath("$.content.[1].baseClock", is(cpuDtoR55600X.getBaseClock())),
                        jsonPath("$.content.[1].maxClock", is(cpuDtoR55600X.getMaxClock())),
                        jsonPath("$.content.[1].l3CacheSize", is(cpuDtoR55600X.getL3CacheSize())),
                        jsonPath("$.content.[1].maxTdp", is(cpuDtoR55600X.getMaxTdp())),
                        jsonPath("$.content.[1].maxMemorySize", is(cpuDtoR55600X.getMaxMemorySize())),
                        jsonPath("$.content.[1].manufacturer.id", is(manufacturerAmd.getId().toString())),
                        jsonPath("$.content.[1].manufacturer.name", is(manufacturerAmd.getName())),
                        jsonPath("$.content.[1].socket.id", is(socketAm4.getId().toString())),
                        jsonPath("$.content.[1].socket.name", is(socketAm4.getName())),
                        jsonPath("$.content.[1].supportedRamTypes", hasSize(2)),
                        jsonPath("$.content.[1].supportedRamTypes[*].ramType.id").value(containsInAnyOrder(
                                typeDdr4.getId().toString(),
                                typeDdr3.getId().toString()
                        )),
                        jsonPath("$.content.[1].supportedRamTypes[*].ramType.name").value(containsInAnyOrder(
                                typeDdr4.getName(),
                                typeDdr3.getName()
                        )),
                        jsonPath("$.content.[1].supportedRamTypes[*].maxMemoryClock").value(containsInAnyOrder(
                                3200, 1866
                        ))
                );
    }

    @Test
    void getById_withExistentId_shouldReturnFoundEntity() throws Exception {
        // given
        final Cpu saved = cpuRepository.save(cpuI512400F);
        assertThat(cpuRepository.findAll()).hasSize(1);
        final var requestBuilder = get(
                URL_API_V1_CPUS + "/{id}",
                saved.getId()
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),

                        jsonPath("$.name", is(cpuDtoI512400F.getName())),
                        jsonPath("$.coreCount", is(cpuDtoI512400F.getCoreCount())),
                        jsonPath("$.threadCount", is(cpuDtoI512400F.getThreadCount())),
                        jsonPath("$.baseClock", is(cpuDtoI512400F.getBaseClock())),
                        jsonPath("$.maxClock", is(cpuDtoI512400F.getMaxClock())),
                        jsonPath("$.l3CacheSize", is(cpuDtoI512400F.getL3CacheSize())),
                        jsonPath("$.maxTdp", is(cpuDtoI512400F.getMaxTdp())),
                        jsonPath("$.maxMemorySize", is(cpuDtoI512400F.getMaxMemorySize())),
                        jsonPath("$.manufacturer.id", is(manufacturerIntel.getId().toString())),
                        jsonPath("$.manufacturer.name", is(manufacturerIntel.getName())),
                        jsonPath("$.socket.id", is(socketLga1700.getId().toString())),
                        jsonPath("$.socket.name", is(socketLga1700.getName())),
                        jsonPath("$.supportedRamTypes", hasSize(2)),
                        jsonPath("$.supportedRamTypes[*].ramType.id").value(containsInAnyOrder(
                                typeDdr4.getId().toString(),
                                typeDdr5.getId().toString()
                        )),
                        jsonPath("$.supportedRamTypes[*].ramType.name").value(containsInAnyOrder(
                                typeDdr4.getName(),
                                typeDdr5.getName()
                        )),
                        jsonPath("$.supportedRamTypes[*].maxMemoryClock").value(containsInAnyOrder(
                                3200, 4800
                        ))
                );
    }

    @Test
    void getById_withNonExistentId_shouldReturnError() throws Exception {
        // given
        cpuRepository.save(cpuI512400F);
        assertThat(cpuRepository.findAll()).hasSize(1);
        final String nonExistentId = UUID.randomUUID().toString();
        final var requestBuilder = get(
                URL_API_V1_CPUS + "/{id}",
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
                                        "CPU with ID = <{0}> not found!",
                                        nonExistentId
                                )
                        ))
                );
    }

    @Test
    void create_withNonExistentEntity_shouldReturnCreatedEntity() throws Exception {
        // given
        cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(cpuDtoI512400F);
        final var requestBuilder = post(URL_API_V1_CPUS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name", is(cpuDtoI512400F.getName())),
                        jsonPath("$.coreCount", is(cpuDtoI512400F.getCoreCount())),
                        jsonPath("$.threadCount", is(cpuDtoI512400F.getThreadCount())),
                        jsonPath("$.baseClock", is(cpuDtoI512400F.getBaseClock())),
                        jsonPath("$.maxClock", is(cpuDtoI512400F.getMaxClock())),
                        jsonPath("$.l3CacheSize", is(cpuDtoI512400F.getL3CacheSize())),
                        jsonPath("$.maxTdp", is(cpuDtoI512400F.getMaxTdp())),
                        jsonPath("$.maxMemorySize", is(cpuDtoI512400F.getMaxMemorySize())),
                        jsonPath("$.manufacturer.id", is(manufacturerIntel.getId().toString())),
                        jsonPath("$.manufacturer.name", is(manufacturerIntel.getName())),
                        jsonPath("$.socket.id", is(socketLga1700.getId().toString())),
                        jsonPath("$.socket.name", is(socketLga1700.getName())),
                        jsonPath("$.supportedRamTypes", hasSize(2)),
                        jsonPath("$.supportedRamTypes[*].ramType.id").value(containsInAnyOrder(
                                typeDdr4.getId().toString(),
                                typeDdr5.getId().toString()
                        )),
                        jsonPath("$.supportedRamTypes[*].ramType.name").value(containsInAnyOrder(
                                typeDdr4.getName(),
                                typeDdr5.getName()
                        )),
                        jsonPath("$.supportedRamTypes[*].maxMemoryClock").value(containsInAnyOrder(
                                3200, 4800
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final List<Cpu> coolers = cpuRepository.findAll();
            assertThat(coolers).hasSize(2);
            final Cpu cooler = coolers.get(1);
            assertThat(cooler.getId()).isNotNull();
            assertThat(cooler.getName())
                    .isEqualTo(cpuDtoI512400F.getName());
            assertThat(cooler.getCoreCount())
                    .isEqualTo(cpuDtoI512400F.getCoreCount());
            assertThat(cooler.getThreadCount())
                    .isEqualTo(cpuDtoI512400F.getThreadCount());
            assertThat(cooler.getBaseClock())
                    .isEqualTo(cpuDtoI512400F.getBaseClock());
            assertThat(cooler.getMaxClock())
                    .isEqualTo(cpuDtoI512400F.getMaxClock());
            assertThat(cooler.getL3CacheSize())
                    .isEqualTo(cpuDtoI512400F.getL3CacheSize());
            assertThat(cooler.getMaxTdp())
                    .isEqualTo(cpuDtoI512400F.getMaxTdp());
            assertThat(cooler.getMaxMemorySize())
                    .isEqualTo(cpuDtoI512400F.getMaxMemorySize());
            assertThat(cooler.getManufacturer().getId())
                    .isEqualTo(manufacturerIntel.getId());
            assertThat(cooler.getManufacturer().getName())
                    .isEqualTo(manufacturerIntel.getName());
            assertThat(cooler.getSocket().getId())
                    .isEqualTo(socketLga1700.getId());
            assertThat(cooler.getSocket().getName())
                    .isEqualTo(socketLga1700.getName());
            assertThat(cooler.getSupportedRamTypes())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            cpuI512400F.getSupportedRamTypes().toArray(new CpuToRamType[0])
                    );
        });
    }

    @Test
    void create_withNonExistentManufacturerId_shouldReturnError() throws Exception {
        // given
        cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(1);
        final UUID nonExistentManufacturerId = UUID.randomUUID();
        cpuDtoI512400F.setManufacturerId(nonExistentManufacturerId);
        final String jsonRequest = objectMapper.writeValueAsString(cpuDtoI512400F);
        final var requestBuilder = post(URL_API_V1_CPUS)
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
                                        "Manufacturer with ID = <{0}> not found!",
                                        nonExistentManufacturerId
                                )
                        ))
                );

        assertThat(cpuRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withNonExistentSocketId_shouldReturnError() throws Exception {
        // given
        cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(1);
        final UUID nonExistentSocketId = UUID.randomUUID();
        cpuDtoI512400F.setSocketId(nonExistentSocketId);
        final String jsonRequest = objectMapper.writeValueAsString(cpuDtoI512400F);
        final var requestBuilder = post(URL_API_V1_CPUS)
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

        assertThat(cpuRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withNonExistentRamTypeId_shouldReturnError() throws Exception {
        // given
        cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(1);
        final UUID nonExistentRamTypeId = UUID.randomUUID();
        cpuDtoI512400F.addRamType(nonExistentRamTypeId, 3200);
        final String jsonRequest = objectMapper.writeValueAsString(cpuDtoI512400F);
        final var requestBuilder = post(URL_API_V1_CPUS)
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
                                        "RAM type with ID = <{0}> not found!",
                                        nonExistentRamTypeId
                                )
                        ))
                );

        assertThat(cpuRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectManufacturerParam_shouldReturnError() throws Exception {
        // given
        cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(1);
        cpuDtoI512400F.setManufacturerId(null);
        final String jsonRequest = objectMapper.writeValueAsString(cpuDtoI512400F);
        final var requestBuilder = post(URL_API_V1_CPUS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("manufacturer")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        assertThat(cpuRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectSocketParam_shouldReturnError() throws Exception {
        // given
        cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(1);
        cpuDtoI512400F.setSocketId(null);
        final String jsonRequest = objectMapper.writeValueAsString(cpuDtoI512400F);
        final var requestBuilder = post(URL_API_V1_CPUS)
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

        assertThat(cpuRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withIncorrectSupportedRamTypesParam_shouldReturnError() throws Exception {
        // given
        cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(1);
        cpuDtoI512400F.addRamType(null, 3200);
        final String jsonRequest = objectMapper.writeValueAsString(cpuDtoI512400F);
        final var requestBuilder = post(URL_API_V1_CPUS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest);

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.violations", hasSize(1)),
                        jsonPath("$.violations[0].paramNames", contains("supportedRamTypes")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        assertThat(cpuRepository.findAll()).hasSize(1);
    }

    @Test
    void create_withExistentEntity_shouldReturnError() throws Exception {
        // given
        cpuRepository.save(cpuI512400F);
        assertThat(cpuRepository.findAll()).hasSize(1);
        final String jsonRequest = objectMapper.writeValueAsString(cpuDtoI512400F);
        final var requestBuilder = post(URL_API_V1_CPUS)
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
                                        "CPU with name <{0}> already exists!",
                                        cpuDtoI512400F.getName()
                                )
                        ))
                );

        assertThat(cpuRepository.findAll()).hasSize(1);
    }

    @Test
    void replace_withNonExistentEntity_shouldReturnReplacedEntity() throws Exception {
        // given
        cpuRepository.save(cpuI512400F);
        final Cpu saved = cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(2);
        final String newName = "i3 6100";
        final CpuRequestDto dto = CpuRequestDto.builder()
                .name(newName)
                .coreCount(cpuDtoI512400F.getCoreCount())
                .threadCount(cpuDtoI512400F.getThreadCount())
                .baseClock(cpuDtoI512400F.getBaseClock())
                .maxClock(cpuDtoI512400F.getMaxClock())
                .l3CacheSize(cpuDtoI512400F.getL3CacheSize())
                .maxTdp(cpuDtoI512400F.getMaxTdp())
                .maxMemorySize(cpuDtoI512400F.getMaxMemorySize())
                .manufacturerId(cpuDtoI512400F.getManufacturerId())
                .socketId(cpuDtoI512400F.getSocketId())
                .supportedRamTypes(cpuDtoI512400F.getSupportedRamTypes())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_CPUS + "/{id}",
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
                        jsonPath("$.coreCount", is(cpuDtoI512400F.getCoreCount())),
                        jsonPath("$.threadCount", is(cpuDtoI512400F.getThreadCount())),
                        jsonPath("$.baseClock", is(cpuDtoI512400F.getBaseClock())),
                        jsonPath("$.maxClock", is(cpuDtoI512400F.getMaxClock())),
                        jsonPath("$.l3CacheSize", is(cpuDtoI512400F.getL3CacheSize())),
                        jsonPath("$.maxTdp", is(cpuDtoI512400F.getMaxTdp())),
                        jsonPath("$.maxMemorySize", is(cpuDtoI512400F.getMaxMemorySize())),
                        jsonPath("$.manufacturer.id", is(manufacturerIntel.getId().toString())),
                        jsonPath("$.manufacturer.name", is(manufacturerIntel.getName())),
                        jsonPath("$.socket.id", is(socketLga1700.getId().toString())),
                        jsonPath("$.socket.name", is(socketLga1700.getName())),
                        jsonPath("$.supportedRamTypes", hasSize(2)),
                        jsonPath("$.supportedRamTypes[*].ramType.id").value(containsInAnyOrder(
                                typeDdr4.getId().toString(),
                                typeDdr5.getId().toString()
                        )),
                        jsonPath("$.supportedRamTypes[*].ramType.name").value(containsInAnyOrder(
                                typeDdr4.getName(),
                                typeDdr5.getName()
                        )),
                        jsonPath("$.supportedRamTypes[*].maxMemoryClock").value(containsInAnyOrder(
                                3200, 4800
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cpu> optCpu = cpuRepository.findById(saved.getId());
            assertThat(optCpu).isPresent();
            final Cpu cooler = optCpu.get();
            assertThat(cooler.getName())
                    .isEqualTo(newName);
            assertThat(cooler.getCoreCount())
                    .isEqualTo(cpuDtoI512400F.getCoreCount());
            assertThat(cooler.getThreadCount())
                    .isEqualTo(cpuDtoI512400F.getThreadCount());
            assertThat(cooler.getBaseClock())
                    .isEqualTo(cpuDtoI512400F.getBaseClock());
            assertThat(cooler.getMaxClock())
                    .isEqualTo(cpuDtoI512400F.getMaxClock());
            assertThat(cooler.getL3CacheSize())
                    .isEqualTo(cpuDtoI512400F.getL3CacheSize());
            assertThat(cooler.getMaxTdp())
                    .isEqualTo(cpuDtoI512400F.getMaxTdp());
            assertThat(cooler.getMaxMemorySize())
                    .isEqualTo(cpuDtoI512400F.getMaxMemorySize());
            assertThat(cooler.getManufacturer().getId())
                    .isEqualTo(manufacturerIntel.getId());
            assertThat(cooler.getManufacturer().getName())
                    .isEqualTo(manufacturerIntel.getName());
            assertThat(cooler.getSocket().getId())
                    .isEqualTo(socketLga1700.getId());
            assertThat(cooler.getSocket().getName())
                    .isEqualTo(socketLga1700.getName());
            assertThat(cooler.getSupportedRamTypes())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            cpuI512400F.getSupportedRamTypes().toArray(new CpuToRamType[0])
                    );
        });
    }

    @Test
    void replace_withNonExistentManufacturerId_shouldReturnError() throws Exception {
        // given
        cpuRepository.save(cpuI512400F);
        final Cpu saved = cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(2);
        final String newName = "i3 6100";
        final UUID nonExistentManufacturerId = UUID.randomUUID();
        final CpuRequestDto dto = CpuRequestDto.builder()
                .name(newName)
                .coreCount(cpuDtoI512400F.getCoreCount())
                .threadCount(cpuDtoI512400F.getThreadCount())
                .baseClock(cpuDtoI512400F.getBaseClock())
                .maxClock(cpuDtoI512400F.getMaxClock())
                .l3CacheSize(cpuDtoI512400F.getL3CacheSize())
                .maxTdp(cpuDtoI512400F.getMaxTdp())
                .maxMemorySize(cpuDtoI512400F.getMaxMemorySize())
                .manufacturerId(nonExistentManufacturerId)
                .socketId(cpuDtoI512400F.getSocketId())
                .supportedRamTypes(cpuDtoI512400F.getSupportedRamTypes())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_CPUS + "/{id}",
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
                                        "Manufacturer with ID = <{0}> not found!",
                                        nonExistentManufacturerId
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cpu> optCpu = cpuRepository.findById(saved.getId());
            assertThat(optCpu).isPresent();
            final Cpu cooler = optCpu.get();
            assertThat(cooler.getName())
                    .isEqualTo(cpuR55600X.getName());
            assertThat(cooler.getCoreCount())
                    .isEqualTo(cpuR55600X.getCoreCount());
            assertThat(cooler.getThreadCount())
                    .isEqualTo(cpuR55600X.getThreadCount());
            assertThat(cooler.getBaseClock())
                    .isEqualTo(cpuR55600X.getBaseClock());
            assertThat(cooler.getMaxClock())
                    .isEqualTo(cpuR55600X.getMaxClock());
            assertThat(cooler.getL3CacheSize())
                    .isEqualTo(cpuR55600X.getL3CacheSize());
            assertThat(cooler.getMaxTdp())
                    .isEqualTo(cpuR55600X.getMaxTdp());
            assertThat(cooler.getMaxMemorySize())
                    .isEqualTo(cpuR55600X.getMaxMemorySize());
            assertThat(cooler.getManufacturer().getId())
                    .isEqualTo(manufacturerAmd.getId());
            assertThat(cooler.getManufacturer().getName())
                    .isEqualTo(manufacturerAmd.getName());
            assertThat(cooler.getSocket().getId())
                    .isEqualTo(socketAm4.getId());
            assertThat(cooler.getSocket().getName())
                    .isEqualTo(socketAm4.getName());
            assertThat(cooler.getSupportedRamTypes())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            cpuR55600X.getSupportedRamTypes().toArray(new CpuToRamType[0])
                    );
        });
    }

    @Test
    void replace_withNonExistentSocketId_shouldReturnError() throws Exception {
        // given
        cpuRepository.save(cpuI512400F);
        final Cpu saved = cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(2);
        final String newName = "i3 6100";
        final UUID nonExistentSocketId = UUID.randomUUID();
        final CpuRequestDto dto = CpuRequestDto.builder()
                .name(newName)
                .coreCount(cpuDtoI512400F.getCoreCount())
                .threadCount(cpuDtoI512400F.getThreadCount())
                .baseClock(cpuDtoI512400F.getBaseClock())
                .maxClock(cpuDtoI512400F.getMaxClock())
                .l3CacheSize(cpuDtoI512400F.getL3CacheSize())
                .maxTdp(cpuDtoI512400F.getMaxTdp())
                .maxMemorySize(cpuDtoI512400F.getMaxMemorySize())
                .manufacturerId(cpuDtoI512400F.getManufacturerId())
                .socketId(nonExistentSocketId)
                .supportedRamTypes(cpuDtoI512400F.getSupportedRamTypes())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_CPUS + "/{id}",
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

        transactionService.doInTransaction(true, () -> {

            final Optional<Cpu> optCpu = cpuRepository.findById(saved.getId());
            assertThat(optCpu).isPresent();
            final Cpu cooler = optCpu.get();
            assertThat(cooler.getName())
                    .isEqualTo(cpuR55600X.getName());
            assertThat(cooler.getCoreCount())
                    .isEqualTo(cpuR55600X.getCoreCount());
            assertThat(cooler.getThreadCount())
                    .isEqualTo(cpuR55600X.getThreadCount());
            assertThat(cooler.getBaseClock())
                    .isEqualTo(cpuR55600X.getBaseClock());
            assertThat(cooler.getMaxClock())
                    .isEqualTo(cpuR55600X.getMaxClock());
            assertThat(cooler.getL3CacheSize())
                    .isEqualTo(cpuR55600X.getL3CacheSize());
            assertThat(cooler.getMaxTdp())
                    .isEqualTo(cpuR55600X.getMaxTdp());
            assertThat(cooler.getMaxMemorySize())
                    .isEqualTo(cpuR55600X.getMaxMemorySize());
            assertThat(cooler.getManufacturer().getId())
                    .isEqualTo(manufacturerAmd.getId());
            assertThat(cooler.getManufacturer().getName())
                    .isEqualTo(manufacturerAmd.getName());
            assertThat(cooler.getSocket().getId())
                    .isEqualTo(socketAm4.getId());
            assertThat(cooler.getSocket().getName())
                    .isEqualTo(socketAm4.getName());
            assertThat(cooler.getSupportedRamTypes())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            cpuR55600X.getSupportedRamTypes().toArray(new CpuToRamType[0])
                    );
        });
    }

    @Test
    void replace_withNonExistentRamTypeId_shouldReturnError() throws Exception {
        // given
        cpuRepository.save(cpuI512400F);
        final Cpu saved = cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(2);
        final String newName = "i3 6100";
        final UUID nonExistentRamTypeId = UUID.randomUUID();
        final CpuRequestDto dto = CpuRequestDto.builder()
                .name(newName)
                .coreCount(cpuDtoI512400F.getCoreCount())
                .threadCount(cpuDtoI512400F.getThreadCount())
                .baseClock(cpuDtoI512400F.getBaseClock())
                .maxClock(cpuDtoI512400F.getMaxClock())
                .l3CacheSize(cpuDtoI512400F.getL3CacheSize())
                .maxTdp(cpuDtoI512400F.getMaxTdp())
                .maxMemorySize(cpuDtoI512400F.getMaxMemorySize())
                .manufacturerId(cpuDtoI512400F.getManufacturerId())
                .socketId(cpuDtoI512400F.getSocketId())
                .supportedRamTypes(cpuDtoI512400F.getSupportedRamTypes())
                .build();
        dto.addRamType(nonExistentRamTypeId, 3200);
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_CPUS + "/{id}",
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
                                        "RAM type with ID = <{0}> not found!",
                                        nonExistentRamTypeId
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cpu> optCpu = cpuRepository.findById(saved.getId());
            assertThat(optCpu).isPresent();
            final Cpu cooler = optCpu.get();
            assertThat(cooler.getName())
                    .isEqualTo(cpuR55600X.getName());
            assertThat(cooler.getCoreCount())
                    .isEqualTo(cpuR55600X.getCoreCount());
            assertThat(cooler.getThreadCount())
                    .isEqualTo(cpuR55600X.getThreadCount());
            assertThat(cooler.getBaseClock())
                    .isEqualTo(cpuR55600X.getBaseClock());
            assertThat(cooler.getMaxClock())
                    .isEqualTo(cpuR55600X.getMaxClock());
            assertThat(cooler.getL3CacheSize())
                    .isEqualTo(cpuR55600X.getL3CacheSize());
            assertThat(cooler.getMaxTdp())
                    .isEqualTo(cpuR55600X.getMaxTdp());
            assertThat(cooler.getMaxMemorySize())
                    .isEqualTo(cpuR55600X.getMaxMemorySize());
            assertThat(cooler.getManufacturer().getId())
                    .isEqualTo(manufacturerAmd.getId());
            assertThat(cooler.getManufacturer().getName())
                    .isEqualTo(manufacturerAmd.getName());
            assertThat(cooler.getSocket().getId())
                    .isEqualTo(socketAm4.getId());
            assertThat(cooler.getSocket().getName())
                    .isEqualTo(socketAm4.getName());
            assertThat(cooler.getSupportedRamTypes())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            cpuR55600X.getSupportedRamTypes().toArray(new CpuToRamType[0])
                    );
        });
    }

    @Test
    void replace_withIncorrectManufacturerParam_shouldReturnError() throws Exception {
        // given
        cpuRepository.save(cpuI512400F);
        final Cpu saved = cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(2);
        final String newName = "i3 6100";
        final CpuRequestDto dto = CpuRequestDto.builder()
                .name(newName)
                .coreCount(cpuDtoI512400F.getCoreCount())
                .threadCount(cpuDtoI512400F.getThreadCount())
                .baseClock(cpuDtoI512400F.getBaseClock())
                .maxClock(cpuDtoI512400F.getMaxClock())
                .l3CacheSize(cpuDtoI512400F.getL3CacheSize())
                .maxTdp(cpuDtoI512400F.getMaxTdp())
                .maxMemorySize(cpuDtoI512400F.getMaxMemorySize())
                .manufacturerId(null)
                .socketId(cpuDtoI512400F.getSocketId())
                .supportedRamTypes(cpuDtoI512400F.getSupportedRamTypes())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_CPUS + "/{id}",
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
                        jsonPath("$.violations[0].paramNames", contains("manufacturer")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cpu> optCpu = cpuRepository.findById(saved.getId());
            assertThat(optCpu).isPresent();
            final Cpu cooler = optCpu.get();
            assertThat(cooler.getName())
                    .isEqualTo(cpuR55600X.getName());
            assertThat(cooler.getCoreCount())
                    .isEqualTo(cpuR55600X.getCoreCount());
            assertThat(cooler.getThreadCount())
                    .isEqualTo(cpuR55600X.getThreadCount());
            assertThat(cooler.getBaseClock())
                    .isEqualTo(cpuR55600X.getBaseClock());
            assertThat(cooler.getMaxClock())
                    .isEqualTo(cpuR55600X.getMaxClock());
            assertThat(cooler.getL3CacheSize())
                    .isEqualTo(cpuR55600X.getL3CacheSize());
            assertThat(cooler.getMaxTdp())
                    .isEqualTo(cpuR55600X.getMaxTdp());
            assertThat(cooler.getMaxMemorySize())
                    .isEqualTo(cpuR55600X.getMaxMemorySize());
            assertThat(cooler.getManufacturer().getId())
                    .isEqualTo(manufacturerAmd.getId());
            assertThat(cooler.getManufacturer().getName())
                    .isEqualTo(manufacturerAmd.getName());
            assertThat(cooler.getSocket().getId())
                    .isEqualTo(socketAm4.getId());
            assertThat(cooler.getSocket().getName())
                    .isEqualTo(socketAm4.getName());
            assertThat(cooler.getSupportedRamTypes())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            cpuR55600X.getSupportedRamTypes().toArray(new CpuToRamType[0])
                    );
        });
    }

    @Test
    void replace_withIncorrectSocketParam_shouldReturnError() throws Exception {
        // given
        cpuRepository.save(cpuI512400F);
        final Cpu saved = cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(2);
        final String newName = "i3 6100";
        final CpuRequestDto dto = CpuRequestDto.builder()
                .name(newName)
                .coreCount(cpuDtoI512400F.getCoreCount())
                .threadCount(cpuDtoI512400F.getThreadCount())
                .baseClock(cpuDtoI512400F.getBaseClock())
                .maxClock(cpuDtoI512400F.getMaxClock())
                .l3CacheSize(cpuDtoI512400F.getL3CacheSize())
                .maxTdp(cpuDtoI512400F.getMaxTdp())
                .maxMemorySize(cpuDtoI512400F.getMaxMemorySize())
                .manufacturerId(cpuDtoI512400F.getManufacturerId())
                .socketId(null)
                .supportedRamTypes(cpuDtoI512400F.getSupportedRamTypes())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_CPUS + "/{id}",
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

        transactionService.doInTransaction(true, () -> {

            final Optional<Cpu> optCpu = cpuRepository.findById(saved.getId());
            assertThat(optCpu).isPresent();
            final Cpu cooler = optCpu.get();
            assertThat(cooler.getName())
                    .isEqualTo(cpuR55600X.getName());
            assertThat(cooler.getCoreCount())
                    .isEqualTo(cpuR55600X.getCoreCount());
            assertThat(cooler.getThreadCount())
                    .isEqualTo(cpuR55600X.getThreadCount());
            assertThat(cooler.getBaseClock())
                    .isEqualTo(cpuR55600X.getBaseClock());
            assertThat(cooler.getMaxClock())
                    .isEqualTo(cpuR55600X.getMaxClock());
            assertThat(cooler.getL3CacheSize())
                    .isEqualTo(cpuR55600X.getL3CacheSize());
            assertThat(cooler.getMaxTdp())
                    .isEqualTo(cpuR55600X.getMaxTdp());
            assertThat(cooler.getMaxMemorySize())
                    .isEqualTo(cpuR55600X.getMaxMemorySize());
            assertThat(cooler.getManufacturer().getId())
                    .isEqualTo(manufacturerAmd.getId());
            assertThat(cooler.getManufacturer().getName())
                    .isEqualTo(manufacturerAmd.getName());
            assertThat(cooler.getSocket().getId())
                    .isEqualTo(socketAm4.getId());
            assertThat(cooler.getSocket().getName())
                    .isEqualTo(socketAm4.getName());
            assertThat(cooler.getSupportedRamTypes())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            cpuR55600X.getSupportedRamTypes().toArray(new CpuToRamType[0])
                    );
        });
    }

    @Test
    void replace_withIncorrectSupportedRamTypesParam_shouldReturnError() throws Exception {
        // given
        cpuRepository.save(cpuI512400F);
        final Cpu saved = cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(2);
        final String newName = "i3 6100";
        final CpuRequestDto dto = CpuRequestDto.builder()
                .name(newName)
                .coreCount(cpuDtoI512400F.getCoreCount())
                .threadCount(cpuDtoI512400F.getThreadCount())
                .baseClock(cpuDtoI512400F.getBaseClock())
                .maxClock(cpuDtoI512400F.getMaxClock())
                .l3CacheSize(cpuDtoI512400F.getL3CacheSize())
                .maxTdp(cpuDtoI512400F.getMaxTdp())
                .maxMemorySize(cpuDtoI512400F.getMaxMemorySize())
                .manufacturerId(cpuDtoI512400F.getManufacturerId())
                .socketId(cpuDtoI512400F.getSocketId())
                .build();
        dto.addRamType(null, 3200);
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_CPUS + "/{id}",
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
                        jsonPath("$.violations[0].paramNames", contains("supportedRamTypes")),
                        jsonPath("$.violations[0].message", is("Invalid param value!"))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cpu> optCpu = cpuRepository.findById(saved.getId());
            assertThat(optCpu).isPresent();
            final Cpu cooler = optCpu.get();
            assertThat(cooler.getName())
                    .isEqualTo(cpuR55600X.getName());
            assertThat(cooler.getCoreCount())
                    .isEqualTo(cpuR55600X.getCoreCount());
            assertThat(cooler.getThreadCount())
                    .isEqualTo(cpuR55600X.getThreadCount());
            assertThat(cooler.getBaseClock())
                    .isEqualTo(cpuR55600X.getBaseClock());
            assertThat(cooler.getMaxClock())
                    .isEqualTo(cpuR55600X.getMaxClock());
            assertThat(cooler.getL3CacheSize())
                    .isEqualTo(cpuR55600X.getL3CacheSize());
            assertThat(cooler.getMaxTdp())
                    .isEqualTo(cpuR55600X.getMaxTdp());
            assertThat(cooler.getMaxMemorySize())
                    .isEqualTo(cpuR55600X.getMaxMemorySize());
            assertThat(cooler.getManufacturer().getId())
                    .isEqualTo(manufacturerAmd.getId());
            assertThat(cooler.getManufacturer().getName())
                    .isEqualTo(manufacturerAmd.getName());
            assertThat(cooler.getSocket().getId())
                    .isEqualTo(socketAm4.getId());
            assertThat(cooler.getSocket().getName())
                    .isEqualTo(socketAm4.getName());
            assertThat(cooler.getSupportedRamTypes())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            cpuR55600X.getSupportedRamTypes().toArray(new CpuToRamType[0])
                    );
        });
    }

    @Test
    void replace_withExistentEntity_shouldReturnError() throws Exception {
        // given
        cpuRepository.save(cpuI512400F);
        final Cpu saved = cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(2);
        final CpuRequestDto dto = CpuRequestDto.builder()
                .name(cpuDtoI512400F.getName())
                .coreCount(cpuDtoI512400F.getCoreCount())
                .threadCount(cpuDtoI512400F.getThreadCount())
                .baseClock(cpuDtoI512400F.getBaseClock())
                .maxClock(cpuDtoI512400F.getMaxClock())
                .l3CacheSize(cpuDtoI512400F.getL3CacheSize())
                .maxTdp(cpuDtoI512400F.getMaxTdp())
                .maxMemorySize(cpuDtoI512400F.getMaxMemorySize())
                .manufacturerId(cpuDtoI512400F.getManufacturerId())
                .socketId(cpuDtoI512400F.getSocketId())
                .supportedRamTypes(cpuDtoI512400F.getSupportedRamTypes())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = put(
                URL_API_V1_CPUS + "/{id}",
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
                                        "CPU with name <{0}> already exists!",
                                        cpuDtoI512400F.getName()
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cpu> optCpu = cpuRepository.findById(saved.getId());
            assertThat(optCpu).isPresent();
            final Cpu cooler = optCpu.get();
            assertThat(cooler.getName())
                    .isEqualTo(cpuR55600X.getName());
            assertThat(cooler.getCoreCount())
                    .isEqualTo(cpuR55600X.getCoreCount());
            assertThat(cooler.getThreadCount())
                    .isEqualTo(cpuR55600X.getThreadCount());
            assertThat(cooler.getBaseClock())
                    .isEqualTo(cpuR55600X.getBaseClock());
            assertThat(cooler.getMaxClock())
                    .isEqualTo(cpuR55600X.getMaxClock());
            assertThat(cooler.getL3CacheSize())
                    .isEqualTo(cpuR55600X.getL3CacheSize());
            assertThat(cooler.getMaxTdp())
                    .isEqualTo(cpuR55600X.getMaxTdp());
            assertThat(cooler.getMaxMemorySize())
                    .isEqualTo(cpuR55600X.getMaxMemorySize());
            assertThat(cooler.getManufacturer().getId())
                    .isEqualTo(manufacturerAmd.getId());
            assertThat(cooler.getManufacturer().getName())
                    .isEqualTo(manufacturerAmd.getName());
            assertThat(cooler.getSocket().getId())
                    .isEqualTo(socketAm4.getId());
            assertThat(cooler.getSocket().getName())
                    .isEqualTo(socketAm4.getName());
            assertThat(cooler.getSupportedRamTypes())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            cpuR55600X.getSupportedRamTypes().toArray(new CpuToRamType[0])
                    );
        });
    }

    @Test
    void update_withNonExistentEntity_shouldReturnUpdatedEntity() throws Exception {
        // given
        cpuRepository.save(cpuI512400F);
        final Cpu saved = cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(2);
        final String newName = "i3 6100";
        final CpuRequestDto dto = CpuRequestDto.builder()
                .name(newName)
                .coreCount(cpuDtoI512400F.getCoreCount())
                .threadCount(cpuDtoI512400F.getThreadCount())
                .baseClock(cpuDtoI512400F.getBaseClock())
                .maxClock(cpuDtoI512400F.getMaxClock())
                .l3CacheSize(cpuDtoI512400F.getL3CacheSize())
                .maxTdp(cpuDtoI512400F.getMaxTdp())
                .maxMemorySize(cpuDtoI512400F.getMaxMemorySize())
                .manufacturerId(cpuDtoI512400F.getManufacturerId())
                .socketId(cpuDtoI512400F.getSocketId())
                .supportedRamTypes(cpuDtoI512400F.getSupportedRamTypes())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_CPUS + "/{id}",
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
                        jsonPath("$.coreCount", is(cpuDtoI512400F.getCoreCount())),
                        jsonPath("$.threadCount", is(cpuDtoI512400F.getThreadCount())),
                        jsonPath("$.baseClock", is(cpuDtoI512400F.getBaseClock())),
                        jsonPath("$.maxClock", is(cpuDtoI512400F.getMaxClock())),
                        jsonPath("$.l3CacheSize", is(cpuDtoI512400F.getL3CacheSize())),
                        jsonPath("$.maxTdp", is(cpuDtoI512400F.getMaxTdp())),
                        jsonPath("$.manufacturer.id", is(manufacturerIntel.getId().toString())),
                        jsonPath("$.manufacturer.name", is(manufacturerIntel.getName())),
                        jsonPath("$.socket.id", is(socketLga1700.getId().toString())),
                        jsonPath("$.socket.name", is(socketLga1700.getName())),
                        jsonPath("$.supportedRamTypes", hasSize(2)),
                        jsonPath("$.supportedRamTypes[*].ramType.id").value(containsInAnyOrder(
                                typeDdr4.getId().toString(),
                                typeDdr5.getId().toString()
                        )),
                        jsonPath("$.supportedRamTypes[*].ramType.name").value(containsInAnyOrder(
                                typeDdr4.getName(),
                                typeDdr5.getName()
                        )),
                        jsonPath("$.supportedRamTypes[*].maxMemoryClock").value(containsInAnyOrder(
                                3200, 4800
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cpu> optCpu = cpuRepository.findById(saved.getId());
            assertThat(optCpu).isPresent();
            final Cpu cooler = optCpu.get();
            assertThat(cooler.getName())
                    .isEqualTo(newName);
            assertThat(cooler.getCoreCount())
                    .isEqualTo(cpuDtoI512400F.getCoreCount());
            assertThat(cooler.getThreadCount())
                    .isEqualTo(cpuDtoI512400F.getThreadCount());
            assertThat(cooler.getBaseClock())
                    .isEqualTo(cpuDtoI512400F.getBaseClock());
            assertThat(cooler.getMaxClock())
                    .isEqualTo(cpuDtoI512400F.getMaxClock());
            assertThat(cooler.getL3CacheSize())
                    .isEqualTo(cpuDtoI512400F.getL3CacheSize());
            assertThat(cooler.getMaxTdp())
                    .isEqualTo(cpuDtoI512400F.getMaxTdp());
            assertThat(cooler.getMaxMemorySize())
                    .isEqualTo(cpuDtoI512400F.getMaxMemorySize());
            assertThat(cooler.getManufacturer().getId())
                    .isEqualTo(manufacturerIntel.getId());
            assertThat(cooler.getManufacturer().getName())
                    .isEqualTo(manufacturerIntel.getName());
            assertThat(cooler.getSocket().getId())
                    .isEqualTo(socketLga1700.getId());
            assertThat(cooler.getSocket().getName())
                    .isEqualTo(socketLga1700.getName());
            assertThat(cooler.getSupportedRamTypes())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            cpuI512400F.getSupportedRamTypes().toArray(new CpuToRamType[0])
                    );
        });
    }

    @Test
    void update_withNonExistentManufacturerId_shouldReturnError() throws Exception {
        // given
        cpuRepository.save(cpuI512400F);
        final Cpu saved = cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(2);
        final String newName = "i3 6100";
        final UUID nonExistentManufacturerId = UUID.randomUUID();
        final CpuRequestDto dto = CpuRequestDto.builder()
                .name(newName)
                .coreCount(cpuDtoI512400F.getCoreCount())
                .threadCount(cpuDtoI512400F.getThreadCount())
                .baseClock(cpuDtoI512400F.getBaseClock())
                .maxClock(cpuDtoI512400F.getMaxClock())
                .l3CacheSize(cpuDtoI512400F.getL3CacheSize())
                .maxTdp(cpuDtoI512400F.getMaxTdp())
                .maxMemorySize(cpuDtoI512400F.getMaxMemorySize())
                .manufacturerId(nonExistentManufacturerId)
                .socketId(cpuDtoI512400F.getSocketId())
                .supportedRamTypes(cpuDtoI512400F.getSupportedRamTypes())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_CPUS + "/{id}",
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
                                        "Manufacturer with ID = <{0}> not found!",
                                        nonExistentManufacturerId
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cpu> optCpu = cpuRepository.findById(saved.getId());
            assertThat(optCpu).isPresent();
            final Cpu cooler = optCpu.get();
            assertThat(cooler.getName())
                    .isEqualTo(cpuR55600X.getName());
            assertThat(cooler.getCoreCount())
                    .isEqualTo(cpuR55600X.getCoreCount());
            assertThat(cooler.getThreadCount())
                    .isEqualTo(cpuR55600X.getThreadCount());
            assertThat(cooler.getBaseClock())
                    .isEqualTo(cpuR55600X.getBaseClock());
            assertThat(cooler.getMaxClock())
                    .isEqualTo(cpuR55600X.getMaxClock());
            assertThat(cooler.getL3CacheSize())
                    .isEqualTo(cpuR55600X.getL3CacheSize());
            assertThat(cooler.getMaxTdp())
                    .isEqualTo(cpuR55600X.getMaxTdp());
            assertThat(cooler.getMaxMemorySize())
                    .isEqualTo(cpuR55600X.getMaxMemorySize());
            assertThat(cooler.getManufacturer().getId())
                    .isEqualTo(manufacturerAmd.getId());
            assertThat(cooler.getManufacturer().getName())
                    .isEqualTo(manufacturerAmd.getName());
            assertThat(cooler.getSocket().getId())
                    .isEqualTo(socketAm4.getId());
            assertThat(cooler.getSocket().getName())
                    .isEqualTo(socketAm4.getName());
            assertThat(cooler.getSupportedRamTypes())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            cpuR55600X.getSupportedRamTypes().toArray(new CpuToRamType[0])
                    );
        });
    }

    @Test
    void update_withNonExistentSocketId_shouldReturnError() throws Exception {
        // given
        cpuRepository.save(cpuI512400F);
        final Cpu saved = cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(2);
        final String newName = "i3 6100";
        final UUID nonExistentSocketId = UUID.randomUUID();
        final CpuRequestDto dto = CpuRequestDto.builder()
                .name(newName)
                .coreCount(cpuDtoI512400F.getCoreCount())
                .threadCount(cpuDtoI512400F.getThreadCount())
                .baseClock(cpuDtoI512400F.getBaseClock())
                .maxClock(cpuDtoI512400F.getMaxClock())
                .l3CacheSize(cpuDtoI512400F.getL3CacheSize())
                .maxTdp(cpuDtoI512400F.getMaxTdp())
                .maxMemorySize(cpuDtoI512400F.getMaxMemorySize())
                .manufacturerId(cpuDtoI512400F.getManufacturerId())
                .socketId(nonExistentSocketId)
                .supportedRamTypes(cpuDtoI512400F.getSupportedRamTypes())
                .build();
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_CPUS + "/{id}",
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

        transactionService.doInTransaction(true, () -> {

            final Optional<Cpu> optCpu = cpuRepository.findById(saved.getId());
            assertThat(optCpu).isPresent();
            final Cpu cooler = optCpu.get();
            assertThat(cooler.getName())
                    .isEqualTo(cpuR55600X.getName());
            assertThat(cooler.getCoreCount())
                    .isEqualTo(cpuR55600X.getCoreCount());
            assertThat(cooler.getThreadCount())
                    .isEqualTo(cpuR55600X.getThreadCount());
            assertThat(cooler.getBaseClock())
                    .isEqualTo(cpuR55600X.getBaseClock());
            assertThat(cooler.getMaxClock())
                    .isEqualTo(cpuR55600X.getMaxClock());
            assertThat(cooler.getL3CacheSize())
                    .isEqualTo(cpuR55600X.getL3CacheSize());
            assertThat(cooler.getMaxTdp())
                    .isEqualTo(cpuR55600X.getMaxTdp());
            assertThat(cooler.getMaxMemorySize())
                    .isEqualTo(cpuR55600X.getMaxMemorySize());
            assertThat(cooler.getManufacturer().getId())
                    .isEqualTo(manufacturerAmd.getId());
            assertThat(cooler.getManufacturer().getName())
                    .isEqualTo(manufacturerAmd.getName());
            assertThat(cooler.getSocket().getId())
                    .isEqualTo(socketAm4.getId());
            assertThat(cooler.getSocket().getName())
                    .isEqualTo(socketAm4.getName());
            assertThat(cooler.getSupportedRamTypes())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            cpuR55600X.getSupportedRamTypes().toArray(new CpuToRamType[0])
                    );
        });
    }

    @Test
    void update_withNonExistentRamTypeId_shouldReturnError() throws Exception {
        // given
        cpuRepository.save(cpuI512400F);
        final Cpu saved = cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(2);
        final String newName = "i3 6100";
        final UUID nonExistentRamTypeId = UUID.randomUUID();
        final CpuRequestDto dto = CpuRequestDto.builder()
                .name(newName)
                .coreCount(cpuDtoI512400F.getCoreCount())
                .threadCount(cpuDtoI512400F.getThreadCount())
                .baseClock(cpuDtoI512400F.getBaseClock())
                .maxClock(cpuDtoI512400F.getMaxClock())
                .l3CacheSize(cpuDtoI512400F.getL3CacheSize())
                .maxTdp(cpuDtoI512400F.getMaxTdp())
                .maxMemorySize(cpuDtoI512400F.getMaxMemorySize())
                .manufacturerId(cpuDtoI512400F.getManufacturerId())
                .socketId(cpuDtoI512400F.getSocketId())
                .build();
        dto.addRamType(nonExistentRamTypeId, 3200);
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_CPUS + "/{id}",
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
                                        "RAM type with ID = <{0}> not found!",
                                        nonExistentRamTypeId
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cpu> optCpu = cpuRepository.findById(saved.getId());
            assertThat(optCpu).isPresent();
            final Cpu cooler = optCpu.get();
            assertThat(cooler.getName())
                    .isEqualTo(cpuR55600X.getName());
            assertThat(cooler.getCoreCount())
                    .isEqualTo(cpuR55600X.getCoreCount());
            assertThat(cooler.getThreadCount())
                    .isEqualTo(cpuR55600X.getThreadCount());
            assertThat(cooler.getBaseClock())
                    .isEqualTo(cpuR55600X.getBaseClock());
            assertThat(cooler.getMaxClock())
                    .isEqualTo(cpuR55600X.getMaxClock());
            assertThat(cooler.getL3CacheSize())
                    .isEqualTo(cpuR55600X.getL3CacheSize());
            assertThat(cooler.getMaxTdp())
                    .isEqualTo(cpuR55600X.getMaxTdp());
            assertThat(cooler.getMaxMemorySize())
                    .isEqualTo(cpuR55600X.getMaxMemorySize());
            assertThat(cooler.getManufacturer().getId())
                    .isEqualTo(manufacturerAmd.getId());
            assertThat(cooler.getManufacturer().getName())
                    .isEqualTo(manufacturerAmd.getName());
            assertThat(cooler.getSocket().getId())
                    .isEqualTo(socketAm4.getId());
            assertThat(cooler.getSocket().getName())
                    .isEqualTo(socketAm4.getName());
            assertThat(cooler.getSupportedRamTypes())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            cpuR55600X.getSupportedRamTypes().toArray(new CpuToRamType[0])
                    );
        });
    }

    @Test
    void update_withExistentEntity_shouldReturnError() throws Exception {
        // given
        cpuRepository.save(cpuR55600X);
        assertThat(cpuRepository.findAll()).hasSize(1);
        final CpuRequestDto dto = CpuRequestDto.builder()
                .name(cpuDtoR55600X.getName())
                .coreCount(cpuDtoR55600X.getCoreCount())
                .threadCount(cpuDtoR55600X.getThreadCount())
                .baseClock(cpuDtoR55600X.getBaseClock())
                .maxClock(cpuDtoR55600X.getMaxClock())
                .l3CacheSize(cpuDtoR55600X.getL3CacheSize())
                .maxTdp(cpuDtoR55600X.getMaxTdp())
                .maxMemorySize(cpuDtoR55600X.getMaxMemorySize())
                .manufacturerId(cpuDtoR55600X.getManufacturerId())
                .socketId(cpuDtoR55600X.getSocketId())
                .supportedRamTypes(cpuDtoR55600X.getSupportedRamTypes())
                .build();

        final Cpu saved = cpuRepository.save(cpuI512400F);
        final String jsonRequest = objectMapper.writeValueAsString(dto);
        final var requestBuilder = patch(
                URL_API_V1_CPUS + "/{id}",
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
                                        "CPU with name <{0}> already exists!",
                                        cpuDtoR55600X.getName()
                                )
                        ))
                );

        transactionService.doInTransaction(true, () -> {

            final Optional<Cpu> optCpu = cpuRepository.findById(saved.getId());
            assertThat(optCpu).isPresent();
            final Cpu cooler = optCpu.get();
            assertThat(cooler.getName())
                    .isEqualTo(cpuDtoI512400F.getName());
            assertThat(cooler.getCoreCount())
                    .isEqualTo(cpuDtoI512400F.getCoreCount());
            assertThat(cooler.getThreadCount())
                    .isEqualTo(cpuDtoI512400F.getThreadCount());
            assertThat(cooler.getBaseClock())
                    .isEqualTo(cpuDtoI512400F.getBaseClock());
            assertThat(cooler.getMaxClock())
                    .isEqualTo(cpuDtoI512400F.getMaxClock());
            assertThat(cooler.getL3CacheSize())
                    .isEqualTo(cpuDtoI512400F.getL3CacheSize());
            assertThat(cooler.getMaxTdp())
                    .isEqualTo(cpuDtoI512400F.getMaxTdp());
            assertThat(cooler.getMaxMemorySize())
                    .isEqualTo(cpuDtoI512400F.getMaxMemorySize());
            assertThat(cooler.getManufacturer().getId())
                    .isEqualTo(manufacturerIntel.getId());
            assertThat(cooler.getManufacturer().getName())
                    .isEqualTo(manufacturerIntel.getName());
            assertThat(cooler.getSocket().getId())
                    .isEqualTo(socketLga1700.getId());
            assertThat(cooler.getSocket().getName())
                    .isEqualTo(socketLga1700.getName());
            assertThat(cooler.getSupportedRamTypes())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            cpuI512400F.getSupportedRamTypes().toArray(new CpuToRamType[0])
                    );
        });
    }

    @Test
    void delete_shouldDeleteEntityAndReturnStatusNoContent() throws Exception {
        // given
        final UUID cpuDtoI512400FId = cpuRepository.save(cpuI512400F).getId();
        assertThat(cpuRepository.findAll()).hasSize(1);
        final var requestBuilder = delete(
                URL_API_V1_CPUS + "/{id}",
                cpuDtoI512400FId
        );

        // when
        mockMvc.perform(requestBuilder)

                // then
                .andExpect(status().isNoContent());

        assertThat(cpuRepository.findAll()).isEmpty();
    }
}
