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
import ru.bukhtaev.dto.mapper.IPsuMapper;
import ru.bukhtaev.dto.request.PsuRequestDto;
import ru.bukhtaev.dto.response.PsuResponseDto;
import ru.bukhtaev.model.Psu;
import ru.bukhtaev.service.IPagingCrudService;
import ru.bukhtaev.util.PsuSort;
import ru.bukhtaev.validation.handling.ErrorResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.bukhtaev.controller.PsuRestController.URL_API_V1_PSUS;

/**
 * Контроллер обработки CRUD операций над блоками питания.
 */
@Tag(name = "Блоки питания")
@RestController
@RequestMapping(value = URL_API_V1_PSUS, produces = "application/json")
public class PsuRestController {

    /**
     * URL.
     */
    public static final String URL_API_V1_PSUS = "/api/v1/psus";

    /**
     * Сервис CRUD операций над блоками питания.
     */
    private final IPagingCrudService<Psu, UUID> crudService;

    /**
     * Маппер для DTO блоков питания.
     */
    private final IPsuMapper mapper;

    /**
     * Конструктор.
     *
     * @param crudService сервис CRUD операций над блоками питания
     * @param mapper      маппер для DTO блоков питания
     */
    @Autowired
    public PsuRestController(
            final IPagingCrudService<Psu, UUID> crudService,
            final IPsuMapper mapper
    ) {
        this.crudService = crudService;
        this.mapper = mapper;
    }

    @Operation(summary = "Получение всех блоков питания")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Блоки питания получены"
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
    public ResponseEntity<List<PsuResponseDto>> handleGetAll() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        crudService.getAll()
                                .stream()
                                .map(mapper::convertToDto)
                                .toList()
                );
    }

    @Operation(summary = "Получение всех блоков питания (с пагинацией)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Блоки питания получены"
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
    public ResponseEntity<Slice<PsuResponseDto>> handleGetAll(
            @RequestParam(value = "offset", defaultValue = "0") final Integer offset,
            @RequestParam(value = "limit", defaultValue = "20") final Integer limit,
            @RequestParam(value = "sort", defaultValue = "VENDOR_NAME_ASC") final PsuSort sort
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

    @Operation(summary = "Получение блока питания по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Блок питания получен"
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
                    description = "Блок питания не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<PsuResponseDto> handleGetById(@PathVariable("id") final UUID id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        mapper.convertToDto(crudService.getById(id))
                );
    }

    @Operation(summary = "Создание блока питания")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Блок питания создан"
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
                    description = "Вендор, форм-фактор, сертификат, основной коннектор питания, " +
                            "коннектор питания процессора, коннектор питания накопителя " +
                            "или коннектор питания видеокарты не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PostMapping
    public ResponseEntity<PsuResponseDto> handleCreate(
            @RequestBody final PsuRequestDto dto,
            final UriComponentsBuilder uriBuilder
    ) {
        final PsuResponseDto savedDto = mapper.convertToDto(
                crudService.create(
                        mapper.convertFromDto(dto)
                )
        );

        return ResponseEntity.created(uriBuilder
                        .path(URL_API_V1_PSUS + "/{id}")
                        .build(Map.of("id", savedDto.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedDto);
    }

    @Operation(summary = "Изменение блока питания")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Блок питания изменен"
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
                    description = "Блок питания, вендор, форм-фактор, сертификат, " +
                            "основной коннектор питания, коннектор питания процессора, " +
                            "коннектор питания накопителя или коннектор питания видеокарты не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<PsuResponseDto> handleUpdate(
            @PathVariable("id") final UUID id,
            @RequestBody final PsuRequestDto dto
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

    @Operation(summary = "Замена блока питания")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Блок питания заменен"
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
                    description = "Блок питания, вендор, форм-фактор, сертификат, " +
                            "основной коннектор питания, коннектор питания процессора, " +
                            "коннектор питания накопителя или коннектор питания видеокарты не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<PsuResponseDto> handleReplace(
            @PathVariable("id") final UUID id,
            @RequestBody final PsuRequestDto dto
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

    @Operation(summary = "Удаление блока питания по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Блок питания удален"
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
