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
import ru.bukhtaev.dto.mapper.ICoolerMapper;
import ru.bukhtaev.dto.request.CoolerRequestDto;
import ru.bukhtaev.dto.response.CoolerResponseDto;
import ru.bukhtaev.model.Cooler;
import ru.bukhtaev.service.crud.IPagingCrudService;
import ru.bukhtaev.util.CoolerSort;
import ru.bukhtaev.validation.handling.ErrorResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.bukhtaev.controller.CoolerRestController.URL_API_V1_COOLERS;

/**
 * Контроллер обработки CRUD операций над процессорными кулерами.
 */
@Tag(name = "Процессорные кулеры")
@RestController
@RequestMapping(value = URL_API_V1_COOLERS, produces = "application/json")
public class CoolerRestController {

    /**
     * URL.
     */
    public static final String URL_API_V1_COOLERS = "/api/v1/coolers";

    /**
     * Сервис CRUD операций над процессорными кулерами.
     */
    private final IPagingCrudService<Cooler, UUID> crudService;

    /**
     * Маппер для DTO процессорных кулеров.
     */
    private final ICoolerMapper mapper;

    /**
     * Конструктор.
     *
     * @param crudService сервис CRUD операций над процессорными кулерами
     * @param mapper      маппер для DTO процессорных кулеров
     */
    @Autowired
    public CoolerRestController(
            final IPagingCrudService<Cooler, UUID> crudService,
            final ICoolerMapper mapper
    ) {
        this.crudService = crudService;
        this.mapper = mapper;
    }

    @Operation(summary = "Получение всех процессорных кулеров")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Процессорные кулеры получены"
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
    public ResponseEntity<List<CoolerResponseDto>> handleGetAll() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        crudService.getAll()
                                .stream()
                                .map(mapper::convertToDto)
                                .toList()
                );
    }

    @Operation(summary = "Получение всех процессорных кулеров (с пагинацией)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Процессорные кулеры получены"
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
    public ResponseEntity<Slice<CoolerResponseDto>> handleGetAll(
            @RequestParam(value = "offset", defaultValue = "0") final Integer offset,
            @RequestParam(value = "limit", defaultValue = "20") final Integer limit,
            @RequestParam(value = "sort", defaultValue = "NAME_ASC") final CoolerSort sort
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

    @Operation(summary = "Получение процессорного кулера по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Процессорный кулер получен"
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
                    description = "Процессорный кулер не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CoolerResponseDto> handleGetById(@PathVariable("id") final UUID id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        mapper.convertToDto(crudService.getById(id))
                );
    }

    @Operation(summary = "Создание процессорного кулера")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Процессорный кулер создан"
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
                    description = "Вендор, сокет, размер вентилятора или " +
                            "коннектор питания вентилятора не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PostMapping
    public ResponseEntity<CoolerResponseDto> handleCreate(
            @RequestBody final CoolerRequestDto dto,
            final UriComponentsBuilder uriBuilder
    ) {
        final CoolerResponseDto savedDto = mapper.convertToDto(
                crudService.create(
                        mapper.convertFromDto(dto)
                )
        );

        return ResponseEntity.created(uriBuilder
                        .path(URL_API_V1_COOLERS + "/{id}")
                        .build(Map.of("id", savedDto.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedDto);
    }

    @Operation(summary = "Изменение процессорного кулера")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Процессорный кулер изменен"
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
                    description = "Процессорный кулер, вендор, сокет, размер вентилятора " +
                            "или коннектор питания вентилятора не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<CoolerResponseDto> handleUpdate(
            @PathVariable("id") final UUID id,
            @RequestBody final CoolerRequestDto dto
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

    @Operation(summary = "Замена процессорного кулера")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Процессорный кулер заменен"
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
                    description = "Процессорный кулер, вендор, сокет, размер вентилятора " +
                            "или коннектор питания вентилятора не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<CoolerResponseDto> handleReplace(
            @PathVariable("id") final UUID id,
            @RequestBody final CoolerRequestDto dto
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

    @Operation(summary = "Удаление процессорного кулера по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Процессорный кулер удален"
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
