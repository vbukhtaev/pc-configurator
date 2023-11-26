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
import ru.bukhtaev.dto.mapper.dictionary.IStoragePowerConnectorMapper;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.dictionary.StoragePowerConnector;
import ru.bukhtaev.service.ICrudService;
import ru.bukhtaev.validation.handling.ErrorResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.bukhtaev.controller.dictionary.StoragePowerConnectorRestController.URL_API_V1_STORAGE_POWER_CONNECTORS;

/**
 * Контроллер обработки CRUD операций над коннекторами питания накопителя.
 */
@Tag(name = "Коннекторы питания накопителей")
@RestController
@RequestMapping(value = URL_API_V1_STORAGE_POWER_CONNECTORS, produces = "application/json")
public class StoragePowerConnectorRestController {

    /**
     * URL.
     */
    public static final String URL_API_V1_STORAGE_POWER_CONNECTORS = "/api/v1/storage-power-connectors";

    /**
     * Сервис CRUD операций над коннекторами питания накопителей.
     */
    private final ICrudService<StoragePowerConnector, UUID> crudService;

    /**
     * Маппер для DTO коннекторов питания накопителей.
     */
    private final IStoragePowerConnectorMapper mapper;

    /**
     * Конструктор.
     *
     * @param crudService сервис CRUD операций над коннекторами питания накопителей
     * @param mapper      маппер для DTO коннекторов питания накопителей
     */
    @Autowired
    public StoragePowerConnectorRestController(
            final ICrudService<StoragePowerConnector, UUID> crudService,
            final IStoragePowerConnectorMapper mapper
    ) {
        this.crudService = crudService;
        this.mapper = mapper;
    }

    @Operation(summary = "Получение всех коннекторов питания накопителей")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Коннекторы питания накопителей получены"
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
    public ResponseEntity<List<NameableResponseDto>> handleGetAll() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        crudService.getAll()
                                .stream()
                                .map(mapper::convertToDto)
                                .toList()
                );
    }

    @Operation(summary = "Получение коннектора питания накопителя по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Коннектор питания накопителя получен"
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
                    description = "Коннектор питания накопителя не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<NameableResponseDto> handleGetById(@PathVariable("id") final UUID id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        mapper.convertToDto(crudService.getById(id))
                );
    }

    @Operation(summary = "Создание коннектора питания накопителя")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Коннектор питания накопителя создан"
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
    public ResponseEntity<NameableResponseDto> handleCreate(
            @RequestBody final NameableRequestDto dto,
            final UriComponentsBuilder uriBuilder
    ) {
        final NameableResponseDto savedDto = mapper.convertToDto(
                crudService.create(
                        mapper.convertFromDto(dto)
                )
        );

        return ResponseEntity.created(uriBuilder
                        .path(URL_API_V1_STORAGE_POWER_CONNECTORS + "/{id}")
                        .build(Map.of("id", savedDto.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedDto);
    }

    @Operation(summary = "Изменение коннектора питания накопителя")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Коннектор питания накопителя изменен"
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
                    description = "Коннектор питания накопителя не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<NameableResponseDto> handleUpdate(
            @PathVariable("id") final UUID id,
            @RequestBody final NameableRequestDto dto
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

    @Operation(summary = "Замена коннектора питания накопителя")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Коннектор питания накопителя заменен"
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
                    description = "Коннектор питания накопителя не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<NameableResponseDto> handleReplace(
            @PathVariable("id") final UUID id,
            @RequestBody final NameableRequestDto dto
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

    @Operation(summary = "Удаление коннектора питания накопителя по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Коннектор питания накопителя удален"
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
