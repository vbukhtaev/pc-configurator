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
import ru.bukhtaev.dto.mapper.IDesignMapper;
import ru.bukhtaev.dto.request.DesignRequestDto;
import ru.bukhtaev.dto.response.DesignResponseDto;
import ru.bukhtaev.model.Design;
import ru.bukhtaev.service.crud.IPagingCrudService;
import ru.bukhtaev.util.DesignSort;
import ru.bukhtaev.validation.handling.ErrorResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.bukhtaev.controller.DesignRestController.URL_API_V1_DESIGNS;

/**
 * Контроллер обработки CRUD операций над вариантами исполнения.
 */
@Tag(name = "Варианты исполнения")
@RestController
@RequestMapping(value = URL_API_V1_DESIGNS, produces = "application/json")
public class DesignRestController {

    /**
     * URL.
     */
    public static final String URL_API_V1_DESIGNS = "/api/v1/designs";

    /**
     * Сервис CRUD операций над вариантами исполнения.
     */
    private final IPagingCrudService<Design, UUID> crudService;

    /**
     * Маппер для DTO вариантов исполнения.
     */
    private final IDesignMapper mapper;

    /**
     * Конструктор.
     *
     * @param crudService сервис CRUD операций над вариантами исполнения
     * @param mapper      маппер для DTO вариантов исполнения
     */
    @Autowired
    public DesignRestController(
            final IPagingCrudService<Design, UUID> crudService,
            final IDesignMapper mapper
    ) {
        this.crudService = crudService;
        this.mapper = mapper;
    }

    @Operation(summary = "Получение всех вариантов исполнения")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Варианты исполнения получены"
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
    public ResponseEntity<List<DesignResponseDto>> handleGetAll() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        crudService.getAll()
                                .stream()
                                .map(mapper::convertToDto)
                                .toList()
                );
    }

    @Operation(summary = "Получение всех вариантов исполнения (с пагинацией)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Варианты исполнения получены"
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
    public ResponseEntity<Slice<DesignResponseDto>> handleGetAll(
            @RequestParam(value = "offset", defaultValue = "0") final Integer offset,
            @RequestParam(value = "limit", defaultValue = "20") final Integer limit,
            @RequestParam(value = "sort", defaultValue = "NAME_ASC") final DesignSort sort
    ) {
        return ResponseEntity.ok(
                crudService.getAll(
                        PageRequest.of(
                                offset,
                                limit,
                                sort.getSortValue()
                        )
                ).map(mapper::convertToDto)
        );
    }

    @Operation(summary = "Получение варианта исполнения по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Вариант исполнения получен"
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
                    description = "Вариант исполнения не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<DesignResponseDto> handleGetById(@PathVariable("id") final UUID id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        mapper.convertToDto(crudService.getById(id))
                );
    }

    @Operation(summary = "Создание варианта исполнения")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Вариант исполнения создан"
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
                    description = "Вендор не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PostMapping
    public ResponseEntity<DesignResponseDto> handleCreate(
            @RequestBody final DesignRequestDto dto,
            final UriComponentsBuilder uriBuilder
    ) {
        final DesignResponseDto savedDto = mapper.convertToDto(
                crudService.create(
                        mapper.convertFromDto(dto)
                )
        );

        return ResponseEntity.created(uriBuilder
                        .path(URL_API_V1_DESIGNS + "/{id}")
                        .build(Map.of("id", savedDto.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedDto);
    }

    @Operation(summary = "Изменение варианта исполнения")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Вариант исполнения изменен"
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
                    description = "Вариант исполнения или вендор не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<DesignResponseDto> handleUpdate(
            @PathVariable("id") final UUID id,
            @RequestBody final DesignRequestDto dto
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

    @Operation(summary = "Замена варианта исполнения")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Вариант исполнения заменен"
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
                    description = "Вариант исполнения или вендор не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<DesignResponseDto> handleReplace(
            @PathVariable("id") final UUID id,
            @RequestBody final DesignRequestDto dto
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

    @Operation(summary = "Удаление варианта исполнения по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Вариант исполнения удален"
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
