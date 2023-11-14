package ru.bukhtaev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.bukhtaev.dto.mapper.IRamModuleMapper;
import ru.bukhtaev.dto.request.RamModuleRequestDto;
import ru.bukhtaev.dto.response.RamModuleResponseDto;
import ru.bukhtaev.model.RamModule;
import ru.bukhtaev.service.IPagingCrudService;
import ru.bukhtaev.util.RamModuleSort;
import ru.bukhtaev.validation.handling.ErrorResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.bukhtaev.controller.RamModuleRestController.URL_API_V1_RAM_MODULES;

/**
 * Контроллер обработки CRUD операций над модулями оперативной памяти.
 */
@Tag(name = "Модули оперативной памяти")
@RestController
@RequestMapping(value = URL_API_V1_RAM_MODULES, produces = "application/json")
public class RamModuleRestController {

    /**
     * URL.
     */
    public static final String URL_API_V1_RAM_MODULES = "/api/v1/ram-modules";

    /**
     * Сервис CRUD операций над модулями оперативной памяти.
     */
    private final IPagingCrudService<RamModule, UUID> crudService;

    /**
     * Маппер для DTO модулей оперативной памяти.
     */
    private final IRamModuleMapper mapper;

    /**
     * Конструктор.
     *
     * @param crudService сервис CRUD операций над модулями оперативной памяти
     * @param mapper      маппер для DTO модулей оперативной памяти
     */
    @Autowired
    public RamModuleRestController(
            final IPagingCrudService<RamModule, UUID> crudService,
            final IRamModuleMapper mapper
    ) {
        this.crudService = crudService;
        this.mapper = mapper;
    }

    @Operation(summary = "Получение всех модулей оперативной памяти")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Модули оперативной памяти получены"
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
    public ResponseEntity<List<RamModuleResponseDto>> handleGetAll() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        crudService.getAll()
                                .stream()
                                .map(mapper::convertToDto)
                                .toList()
                );
    }

    @Operation(summary = "Получение всех модулей оперативной памяти (с пагинацией)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Модули оперативной памяти получены"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @GetMapping("/pageable")
    public ResponseEntity<Slice<RamModuleResponseDto>> handleGetAll(
            @RequestParam(value = "offset", defaultValue = "0") final Integer offset,
            @RequestParam(value = "limit", defaultValue = "20") final Integer limit,
            @RequestParam(value = "sort", defaultValue = "TYPE_NAME_DESC") final RamModuleSort sort
    ) {
        return ResponseEntity.ok(
                crudService.getAll(
                        PageRequest.of(offset, limit, sort.getSortValue())
                ).map(mapper::convertToDto)
        );
    }

    @Operation(summary = "Получение модуля оперативной памяти по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Модуль оперативной памяти получен"
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
                    description = "Модуль оперативной памяти не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<RamModuleResponseDto> handleGetById(@PathVariable("id") final UUID id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        mapper.convertToDto(crudService.getById(id))
                );
    }

    @Operation(summary = "Создание модуля оперативной памяти")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Модуль оперативной памяти создан"
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
                    description = "Вариант исполнения или тип оперативной памяти не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PostMapping
    public ResponseEntity<RamModuleResponseDto> handleCreate(
            @RequestBody final RamModuleRequestDto dto,
            final UriComponentsBuilder uriBuilder
    ) {
        final RamModuleResponseDto savedDto = mapper.convertToDto(
                crudService.create(
                        mapper.convertFromDto(dto)
                )
        );

        return ResponseEntity.created(uriBuilder
                        .path(URL_API_V1_RAM_MODULES + "/{id}")
                        .build(Map.of("id", savedDto.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedDto);
    }

    @Operation(summary = "Изменение модуля оперативной памяти")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Модуль оперативной памяти изменен"
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
                    description = "Модуль оперативной памяти, вариант исполнения или тип оперативной памяти не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<RamModuleResponseDto> handleUpdate(
            @PathVariable("id") final UUID id,
            @RequestBody final RamModuleRequestDto dto
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

    @Operation(summary = "Замена модуля оперативной памяти")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Модуль оперативной памяти заменен"
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
                    description = "Модуль оперативной памяти, вариант исполнения или тип оперативной памяти не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<RamModuleResponseDto> handleReplace(
            @PathVariable("id") final UUID id,
            @RequestBody final RamModuleRequestDto dto
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

    @Operation(summary = "Удаление модуля оперативной памяти по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Модуль оперативной памяти удален"
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
