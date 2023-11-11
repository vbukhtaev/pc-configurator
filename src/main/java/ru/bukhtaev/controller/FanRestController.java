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
import ru.bukhtaev.dto.mapper.IFanMapper;
import ru.bukhtaev.dto.request.FanRequestDto;
import ru.bukhtaev.dto.response.FanResponseDto;
import ru.bukhtaev.model.Fan;
import ru.bukhtaev.service.IPagingCrudService;
import ru.bukhtaev.util.FanSort;
import ru.bukhtaev.validation.handling.ErrorResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.bukhtaev.controller.FanRestController.URL_API_V1_FANS;

/**
 * Контроллер обработки CRUD операций над вентиляторами.
 */
@Tag(name = "Вентиляторы")
@RestController
@RequestMapping(value = URL_API_V1_FANS, produces = "application/json")
public class FanRestController {

    /**
     * URL.
     */
    public static final String URL_API_V1_FANS = "/api/v1/fans";

    /**
     * Сервис CRUD операций над вентиляторами.
     */
    private final IPagingCrudService<Fan, UUID> crudService;

    /**
     * Маппер для DTO вентиляторов.
     */
    private final IFanMapper mapper;

    /**
     * Конструктор.
     *
     * @param crudService сервис CRUD операций над вентиляторами
     * @param mapper      маппер для DTO вентиляторов
     */
    @Autowired
    public FanRestController(
            final IPagingCrudService<Fan, UUID> crudService,
            final IFanMapper mapper
    ) {
        this.crudService = crudService;
        this.mapper = mapper;
    }

    @Operation(summary = "Получение всех вентиляторов")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Вентиляторы получены"
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
    public ResponseEntity<List<FanResponseDto>> handleGetAll() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        crudService.getAll()
                                .stream()
                                .map(mapper::convertToDto)
                                .toList()
                );
    }

    @Operation(summary = "Получение всех вентиляторов (с пагинацией)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Вентиляторы получены"
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
    public ResponseEntity<Slice<FanResponseDto>> handleGetAll(
            @RequestParam(value = "offset", defaultValue = "0") final Integer offset,
            @RequestParam(value = "limit", defaultValue = "20") final Integer limit,
            @RequestParam(value = "sort", defaultValue = "NAME_ASC") final FanSort sort
    ) {
        return ResponseEntity.ok(
                crudService.getAll(
                        PageRequest.of(offset, limit, sort.getSortValue())
                ).map(mapper::convertToDto)
        );
    }

    @Operation(summary = "Получение вентилятора по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Вентилятор получен"
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
                    description = "Вентилятор не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<FanResponseDto> handleGetById(@PathVariable("id") final UUID id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        mapper.convertToDto(crudService.getById(id))
                );
    }

    @Operation(summary = "Создание вентилятора")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Вентилятор создан"
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
                    description = "Вендор, размер вентилятора или " +
                            "коннектор питания вентилятора не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PostMapping
    public ResponseEntity<FanResponseDto> handleCreate(
            @RequestBody final FanRequestDto dto,
            final UriComponentsBuilder uriBuilder
    ) {
        final FanResponseDto savedDto = mapper.convertToDto(
                crudService.create(
                        mapper.convertFromDto(dto)
                )
        );

        return ResponseEntity.created(uriBuilder
                        .path(URL_API_V1_FANS + "/{id}")
                        .build(Map.of("id", savedDto.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedDto);
    }

    @Operation(summary = "Изменение вентилятора")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Вентилятор изменен"
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
                    description = "Вентилятор, вендор, размер вентилятора " +
                            "или коннектор питания вентилятора не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<FanResponseDto> handleUpdate(
            @PathVariable("id") final UUID id,
            @RequestBody final FanRequestDto dto
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

    @Operation(summary = "Замена вентилятора")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Вентилятор заменен"
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
                    description = "Вентилятор, вендор, размер вентилятора " +
                            "или коннектор питания вентилятора не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<FanResponseDto> handleReplace(
            @PathVariable("id") final UUID id,
            @RequestBody final FanRequestDto dto
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

    @Operation(summary = "Удаление вентилятора по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Вентилятор удален"
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
