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
import ru.bukhtaev.dto.mapper.dictionary.IStorageConnectorMapper;
import ru.bukhtaev.dto.request.dictionary.StorageConnectorRequestDto;
import ru.bukhtaev.dto.response.dictionary.StorageConnectorResponseDto;
import ru.bukhtaev.model.dictionary.StorageConnector;
import ru.bukhtaev.service.crud.ICrudService;
import ru.bukhtaev.validation.handling.ErrorResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.bukhtaev.controller.dictionary.StorageConnectorRestController.URL_API_V1_STORAGE_CONNECTORS;

/**
 * Контроллер обработки CRUD операций над коннекторами подключения накопителя.
 */
@Tag(name = "Коннекторы подключения накопителей")
@RestController
@RequestMapping(value = URL_API_V1_STORAGE_CONNECTORS, produces = "application/json")
public class StorageConnectorRestController {

    /**
     * URL.
     */
    public static final String URL_API_V1_STORAGE_CONNECTORS = "/api/v1/storage-connectors";

    /**
     * Сервис CRUD операций над коннекторами подключения накопителей.
     */
    private final ICrudService<StorageConnector, UUID> crudService;

    /**
     * Маппер для DTO коннекторов подключения накопителей.
     */
    private final IStorageConnectorMapper mapper;

    /**
     * Конструктор.
     *
     * @param crudService сервис CRUD операций над коннекторами подключения накопителей
     * @param mapper      маппер для DTO коннекторов подключения накопителей
     */
    @Autowired
    public StorageConnectorRestController(
            final ICrudService<StorageConnector, UUID> crudService,
            final IStorageConnectorMapper mapper
    ) {
        this.crudService = crudService;
        this.mapper = mapper;
    }

    @Operation(summary = "Получение всех коннекторов подключения накопителей")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Коннекторы подключения накопителей получены"
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
    public ResponseEntity<List<StorageConnectorResponseDto>> handleGetAll() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        crudService.getAll()
                                .stream()
                                .map(mapper::convertToDto)
                                .toList()
                );
    }

    @Operation(summary = "Получение коннектора подключения накопителя по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Коннектор подключения накопителя получен"
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
                    description = "Коннектор подключения накопителя не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<StorageConnectorResponseDto> handleGetById(@PathVariable("id") final UUID id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        mapper.convertToDto(crudService.getById(id))
                );
    }

    @Operation(summary = "Создание коннектора подключения накопителя")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Коннектор подключения накопителя создан"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PostMapping
    public ResponseEntity<StorageConnectorResponseDto> handleCreate(
            @RequestBody final StorageConnectorRequestDto dto,
            final UriComponentsBuilder uriBuilder
    ) {
        final var savedDto = mapper.convertToDto(
                crudService.create(
                        mapper.convertFromDto(dto)
                )
        );

        return ResponseEntity.created(uriBuilder
                        .path(URL_API_V1_STORAGE_CONNECTORS + "/{id}")
                        .build(Map.of("id", savedDto.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedDto);
    }

    @Operation(summary = "Изменение коннектора подключения накопителя")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Коннектор подключения накопителя изменен"
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
                    description = "Коннектор подключения накопителя не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<StorageConnectorResponseDto> handleUpdate(
            @PathVariable("id") final UUID id,
            @RequestBody final StorageConnectorRequestDto dto
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

    @Operation(summary = "Замена коннектора подключения накопителя")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Коннектор подключения накопителя заменен"
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
                    description = "Коннектор подключения накопителя не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<StorageConnectorResponseDto> handleReplace(
            @PathVariable("id") final UUID id,
            @RequestBody final StorageConnectorRequestDto dto
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

    @Operation(summary = "Удаление коннектора подключения накопителя по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Коннектор подключения накопителя удален"
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
