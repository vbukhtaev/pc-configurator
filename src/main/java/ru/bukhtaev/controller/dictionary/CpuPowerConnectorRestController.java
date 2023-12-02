package ru.bukhtaev.controller.dictionary;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.bukhtaev.dto.mapper.dictionary.ICpuPowerConnectorMapper;
import ru.bukhtaev.dto.request.dictionary.CpuPowerConnectorRequestDto;
import ru.bukhtaev.dto.response.dictionary.CpuPowerConnectorResponseDto;
import ru.bukhtaev.model.dictionary.CpuPowerConnector;
import ru.bukhtaev.service.crud.ICrudService;
import ru.bukhtaev.validation.handling.ErrorResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.bukhtaev.controller.dictionary.CpuPowerConnectorRestController.URL_API_V1_CPU_POWER_CONNECTORS;

/**
 * Контроллер обработки CRUD операций над коннекторами питания процессоров.
 */
@Tag(name = "Коннекторы питания процессоров")
@RestController
@RequestMapping(value = URL_API_V1_CPU_POWER_CONNECTORS, produces = "application/json")
public class CpuPowerConnectorRestController {

    /**
     * URL.
     */
    public static final String URL_API_V1_CPU_POWER_CONNECTORS = "/api/v1/cpu-power-connectors";

    /**
     * Сервис CRUD операций над коннекторами питания процессоров.
     */
    private final ICrudService<CpuPowerConnector, UUID> crudService;

    /**
     * Маппер для DTO коннекторов питания процессоров.
     */
    private final ICpuPowerConnectorMapper mapper;

    /**
     * Конструктор.
     *
     * @param crudService сервис CRUD операций над коннекторами питания процессоров
     * @param mapper      маппер для DTO коннекторов питания процессоров
     */
    @Autowired
    public CpuPowerConnectorRestController(
            final ICrudService<CpuPowerConnector, UUID> crudService,
            final ICpuPowerConnectorMapper mapper
    ) {
        this.crudService = crudService;
        this.mapper = mapper;
    }

    @Operation(summary = "Получение всех коннекторов питания процессоров")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Коннекторы питания процессоров получены"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @GetMapping
    public ResponseEntity<List<CpuPowerConnectorResponseDto>> handleGetAll() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        crudService.getAll()
                                .stream()
                                .map(mapper::convertToDto)
                                .toList()
                );
    }

    @Operation(summary = "Получение коннектора питания процессора по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Коннектор питания процессора получен"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Коннектор питания процессора не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CpuPowerConnectorResponseDto> handleGetById(@PathVariable("id") final UUID id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        mapper.convertToDto(crudService.getById(id))
                );
    }

    @Operation(summary = "Создание коннектора питания процессора")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Коннектор питания процессора создан"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Коннектор питания процессора не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PostMapping
    public ResponseEntity<CpuPowerConnectorResponseDto> handleCreate(
            @RequestBody final CpuPowerConnectorRequestDto dto,
            final UriComponentsBuilder uriBuilder
    ) {
        final var savedDto = mapper.convertToDto(
                crudService.create(
                        mapper.convertFromDto(dto)
                )
        );

        return ResponseEntity.created(uriBuilder
                        .path(URL_API_V1_CPU_POWER_CONNECTORS + "/{id}")
                        .build(Map.of("id", savedDto.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedDto);
    }

    @Operation(summary = "Изменение коннектора питания процессора")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Коннектор питания процессора изменен"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Коннектор питания процессора не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<CpuPowerConnectorResponseDto> handleUpdate(
            @PathVariable("id") final UUID id,
            @RequestBody final CpuPowerConnectorRequestDto dto
    ) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        mapper.convertToDto(
                                crudService.update(
                                        id,
                                        mapper.convertFromDto(dto)
                                )
                        )
                );
    }

    @Operation(summary = "Замена коннектора питания процессора")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Коннектор питания процессора заменен"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Коннектор питания процессора не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<CpuPowerConnectorResponseDto> handleReplace(
            @PathVariable("id") final UUID id,
            @RequestBody final CpuPowerConnectorRequestDto dto
    ) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        mapper.convertToDto(
                                crudService.replace(
                                        id,
                                        mapper.convertFromDto(dto)
                                )
                        )
                );
    }

    @Operation(summary = "Удаление коннектора питания процессора по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Коннектор питания процессора удален"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleDelete(@PathVariable("id") final UUID id) {
        crudService.delete(id);
    }
}
