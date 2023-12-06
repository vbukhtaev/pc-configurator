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
import ru.bukhtaev.dto.mapper.IComputerBuildMapper;
import ru.bukhtaev.dto.request.ComputerBuildRequestDto;
import ru.bukhtaev.dto.response.ComputerBuildResponseDto;
import ru.bukhtaev.model.ComputerBuild;
import ru.bukhtaev.service.checker.ComputerBuildVerifyingService;
import ru.bukhtaev.service.checker.ComputerVerifyResult;
import ru.bukhtaev.service.crud.IPagingCrudService;
import ru.bukhtaev.util.ComputerBuildSort;
import ru.bukhtaev.validation.handling.ErrorResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.bukhtaev.controller.ComputerBuildRestController.URL_API_V1_COMPUTER_BUILDS;

/**
 * Контроллер обработки CRUD операций над сборками ПК.
 */
@Tag(name = "Сборки ПК")
@RestController
@RequestMapping(value = URL_API_V1_COMPUTER_BUILDS, produces = "application/json")
public class ComputerBuildRestController {

    /**
     * URL.
     */
    public static final String URL_API_V1_COMPUTER_BUILDS = "/api/v1/computer-builds";

    /**
     * Сервис CRUD операций над сборками ПК.
     */
    private final IPagingCrudService<ComputerBuild, UUID> crudService;

    /**
     * Сервис проверки сборок ПК на совместимость комплектующих.
     */
    private final ComputerBuildVerifyingService checkingService;

    /**
     * Маппер для DTO сборок ПК.
     */
    private final IComputerBuildMapper mapper;

    /**
     * Конструктор.
     *
     * @param crudService     сервис CRUD операций над сборками ПК
     * @param checkingService сервис проверки сборок ПК на совместимость комплектующих
     * @param mapper          маппер для DTO сборок ПК
     */
    @Autowired
    public ComputerBuildRestController(
            final IPagingCrudService<ComputerBuild, UUID> crudService,
            final ComputerBuildVerifyingService checkingService,
            final IComputerBuildMapper mapper
    ) {
        this.crudService = crudService;
        this.checkingService = checkingService;
        this.mapper = mapper;
    }

    @Operation(summary = "Получение всех сборок ПК")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Сборки ПК получены"
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
    public ResponseEntity<List<ComputerBuildResponseDto>> handleGetAll() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        crudService.getAll()
                                .stream()
                                .map(mapper::convertToDto)
                                .toList()
                );
    }

    @Operation(summary = "Получение всех сборок ПК (с пагинацией)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Сборки ПК получены"
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
    public ResponseEntity<Slice<ComputerBuildResponseDto>> handleGetAll(
            @RequestParam(value = "offset", defaultValue = "0") final Integer offset,
            @RequestParam(value = "limit", defaultValue = "20") final Integer limit,
            @RequestParam(value = "sort", defaultValue = "NAME_ASC") final ComputerBuildSort sort
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

    @Operation(summary = "Получение сборки ПК по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Сборка ПК получена"
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
                    description = "Сборка ПК не найдена",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ComputerBuildResponseDto> handleGetById(@PathVariable("id") final UUID id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        mapper.convertToDto(crudService.getById(id))
                );
    }

    @Operation(summary = "Создание сборки ПК")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Сборка ПК создана"
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
                    description = "Процессор, блок питания, процессорный кулер, материнская плата, " +
                            "видеокарта, корпус, вентилятор, модуль оперативной памяти, " +
                            "жесткий диск или SSD-накопитель не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PostMapping
    public ResponseEntity<ComputerBuildResponseDto> handleCreate(
            @RequestBody final ComputerBuildRequestDto dto,
            final UriComponentsBuilder uriBuilder
    ) {
        final ComputerBuildResponseDto savedDto = mapper.convertToDto(
                crudService.create(
                        mapper.convertFromDto(dto)
                )
        );

        return ResponseEntity.created(uriBuilder
                        .path(URL_API_V1_COMPUTER_BUILDS + "/{id}")
                        .build(Map.of("id", savedDto.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedDto);
    }

    @Operation(summary = "Изменение сборки ПК")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Сборка ПК изменена"
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
                    description = "Сборка ПК, процессор, блок питания, процессорный кулер, " +
                            "материнская плата, видеокарта, корпус, вентилятор, модуль оперативной памяти, " +
                            "жесткий диск или SSD-накопитель не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ComputerBuildResponseDto> handleUpdate(
            @PathVariable("id") final UUID id,
            @RequestBody final ComputerBuildRequestDto dto
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

    @Operation(summary = "Замена сборки ПК")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Сборка ПК заменена"
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
                    description = "Сборка ПК, процессор, блок питания, процессорный кулер, " +
                            "материнская плата, видеокарта, корпус, вентилятор, модуль оперативной памяти, " +
                            "жесткий диск или SSD-накопитель не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ComputerBuildResponseDto> handleReplace(
            @PathVariable("id") final UUID id,
            @RequestBody final ComputerBuildRequestDto dto
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

    @Operation(summary = "Удаление сборки ПК по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Сборка ПК удалена"
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

    @Operation(summary = "Проверка сборки ПК на совместимость комплектующих")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Результат проверки получен"
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
                    description = "Сборка ПК не найдена",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @GetMapping("/compatibility/{id}")
    public ResponseEntity<ComputerVerifyResult> handleCheck(@PathVariable("id") final UUID id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        checkingService.verify(id)
                );
    }
}
