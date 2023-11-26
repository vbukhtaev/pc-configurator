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
import ru.bukhtaev.dto.mapper.IMotherboardMapper;
import ru.bukhtaev.dto.request.MotherboardRequestDto;
import ru.bukhtaev.dto.response.MotherboardResponseDto;
import ru.bukhtaev.model.Motherboard;
import ru.bukhtaev.service.IPagingCrudService;
import ru.bukhtaev.util.MotherboardSort;
import ru.bukhtaev.validation.handling.ErrorResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.bukhtaev.controller.MotherboardRestController.URL_API_V1_MOTHERBOARDS;

/**
 * Контроллер обработки CRUD операций над материнскими платами.
 */
@Tag(name = "Материнские платы")
@RestController
@RequestMapping(value = URL_API_V1_MOTHERBOARDS, produces = "application/json")
public class MotherboardRestController {

    /**
     * URL.
     */
    public static final String URL_API_V1_MOTHERBOARDS = "/api/v1/motherboards";

    /**
     * Сервис CRUD операций над материнскими платами.
     */
    private final IPagingCrudService<Motherboard, UUID> crudService;

    /**
     * Маппер для DTO материнских плат.
     */
    private final IMotherboardMapper mapper;

    /**
     * Конструктор.
     *
     * @param crudService сервис CRUD операций над материнскими платами
     * @param mapper      маппер для DTO материнских плат
     */
    @Autowired
    public MotherboardRestController(
            final IPagingCrudService<Motherboard, UUID> crudService,
            final IMotherboardMapper mapper
    ) {
        this.crudService = crudService;
        this.mapper = mapper;
    }

    @Operation(summary = "Получение всех материнских плат")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Материнские платы получены"
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
    public ResponseEntity<List<MotherboardResponseDto>> handleGetAll() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        crudService.getAll()
                                .stream()
                                .map(mapper::convertToDto)
                                .toList()
                );
    }

    @Operation(summary = "Получение всех материнских плат (с пагинацией)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Материнские платы получены"
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
    public ResponseEntity<Slice<MotherboardResponseDto>> handleGetAll(
            @RequestParam(value = "offset", defaultValue = "0") final Integer offset,
            @RequestParam(value = "limit", defaultValue = "20") final Integer limit,
            @RequestParam(value = "sort", defaultValue = "CHIPSET_NAME_ASC") final MotherboardSort sort
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

    @Operation(summary = "Получение материнской платы по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Материнская плата получена"
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
                    description = "Материнская плата не найдена",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<MotherboardResponseDto> handleGetById(@PathVariable("id") final UUID id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        mapper.convertToDto(crudService.getById(id))
                );
    }

    @Operation(summary = "Создание материнской платы")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Материнская плата создана"
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
                    description = "Вариант исполнения, чипсет, тип оперативной памяти, " +
                            "форм-фактор материнской платы, коннектор питания процессора, " +
                            "основной коннектор питания, коннектор питания процессорного кулера, " +
                            "версия коннектора PCI-Express или коннектор подключения накопителя",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PostMapping
    public ResponseEntity<MotherboardResponseDto> handleCreate(
            @RequestBody final MotherboardRequestDto dto,
            final UriComponentsBuilder uriBuilder
    ) {
        final MotherboardResponseDto savedDto = mapper.convertToDto(
                crudService.create(
                        mapper.convertFromDto(dto)
                )
        );

        return ResponseEntity.created(uriBuilder
                        .path(URL_API_V1_MOTHERBOARDS + "/{id}")
                        .build(Map.of("id", savedDto.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedDto);
    }

    @Operation(summary = "Изменение материнской платы")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Материнская плата изменена"
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
                    description = "Материнская плата, вариант исполнения, чипсет, тип оперативной памяти, " +
                            "форм-фактор материнской платы, коннектор питания процессора, " +
                            "основной коннектор питания, коннектор питания процессорного кулера, " +
                            "версия коннектора PCI-Express или коннектор подключения накопителя",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<MotherboardResponseDto> handleUpdate(
            @PathVariable("id") final UUID id,
            @RequestBody final MotherboardRequestDto dto
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

    @Operation(summary = "Замена материнской платы")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Материнская плата заменена"
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
                    description = "Материнская плата, вариант исполнения, чипсет, тип оперативной памяти, " +
                            "форм-фактор материнской платы, коннектор питания процессора, " +
                            "основной коннектор питания, коннектор питания процессорного кулера, " +
                            "версия коннектора PCI-Express или коннектор подключения накопителя",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<MotherboardResponseDto> handleReplace(
            @PathVariable("id") final UUID id,
            @RequestBody final MotherboardRequestDto dto
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

    @Operation(summary = "Удаление материнской платы по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Материнская плата удалена"
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
