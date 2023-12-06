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
import ru.bukhtaev.dto.mapper.IComputerCaseMapper;
import ru.bukhtaev.dto.request.ComputerCaseRequestDto;
import ru.bukhtaev.dto.response.ComputerCaseResponseDto;
import ru.bukhtaev.model.ComputerCase;
import ru.bukhtaev.service.crud.IPagingCrudService;
import ru.bukhtaev.util.ComputerCaseSort;
import ru.bukhtaev.validation.handling.ErrorResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.bukhtaev.controller.ComputerCaseRestController.URL_API_V1_COMPUTER_CASES;

/**
 * Контроллер обработки CRUD операций над корпусами.
 */
@Tag(name = "Корпуса")
@RestController
@RequestMapping(value = URL_API_V1_COMPUTER_CASES, produces = "application/json")
public class ComputerCaseRestController {

    /**
     * URL.
     */
    public static final String URL_API_V1_COMPUTER_CASES = "/api/v1/computer-cases";

    /**
     * Сервис CRUD операций над корпусами.
     */
    private final IPagingCrudService<ComputerCase, UUID> crudService;

    /**
     * Маппер для DTO корпусов.
     */
    private final IComputerCaseMapper mapper;

    /**
     * Конструктор.
     *
     * @param crudService сервис CRUD операций над корпусами
     * @param mapper      маппер для DTO корпусов
     */
    @Autowired
    public ComputerCaseRestController(
            final IPagingCrudService<ComputerCase, UUID> crudService,
            final IComputerCaseMapper mapper
    ) {
        this.crudService = crudService;
        this.mapper = mapper;
    }

    @Operation(summary = "Получение всех корпусов")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Корпуса получены"
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
    public ResponseEntity<List<ComputerCaseResponseDto>> handleGetAll() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        crudService.getAll()
                                .stream()
                                .map(mapper::convertToDto)
                                .toList()
                );
    }

    @Operation(summary = "Получение всех корпусов (с пагинацией)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Корпуса получены"
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
    public ResponseEntity<Slice<ComputerCaseResponseDto>> handleGetAll(
            @RequestParam(value = "offset", defaultValue = "0") final Integer offset,
            @RequestParam(value = "limit", defaultValue = "20") final Integer limit,
            @RequestParam(value = "sort", defaultValue = "VENDOR_NAME_ASC") final ComputerCaseSort sort
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

    @Operation(summary = "Получение корпуса по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Корпус получен"
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
                    description = "Корпус не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ComputerCaseResponseDto> handleGetById(@PathVariable("id") final UUID id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        mapper.convertToDto(crudService.getById(id))
                );
    }

    @Operation(summary = "Создание корпуса")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Корпус создан"
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
                    description = "Вендор, форм-фактор материнской платы, форм-фактор блока питания, " +
                            "формат отсека расширения или размер вентилятора не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PostMapping
    public ResponseEntity<ComputerCaseResponseDto> handleCreate(
            @RequestBody final ComputerCaseRequestDto dto,
            final UriComponentsBuilder uriBuilder
    ) {
        final ComputerCaseResponseDto savedDto = mapper.convertToDto(
                crudService.create(
                        mapper.convertFromDto(dto)
                )
        );

        return ResponseEntity.created(uriBuilder
                        .path(URL_API_V1_COMPUTER_CASES + "/{id}")
                        .build(Map.of("id", savedDto.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedDto);
    }

    @Operation(summary = "Изменение корпуса")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Корпус изменен"
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
                    description = "Корпус, вендор, форм-фактор материнской платы, форм-фактор блока питания, " +
                            "формат отсека расширения или размер вентилятора не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ComputerCaseResponseDto> handleUpdate(
            @PathVariable("id") final UUID id,
            @RequestBody final ComputerCaseRequestDto dto
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

    @Operation(summary = "Замена корпуса")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Корпус заменен"
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
                    description = "Корпус, вендор, форм-фактор материнской платы, форм-фактор блока питания, " +
                            "формат отсека расширения или размер вентилятора не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )}
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ComputerCaseResponseDto> handleReplace(
            @PathVariable("id") final UUID id,
            @RequestBody final ComputerCaseRequestDto dto
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

    @Operation(summary = "Удаление корпуса по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Корпус удален"
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
