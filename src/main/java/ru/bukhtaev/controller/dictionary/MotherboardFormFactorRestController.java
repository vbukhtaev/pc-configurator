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
import ru.bukhtaev.dto.mapper.dictionary.IMotherboardFormFactorMapper;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.dictionary.MotherboardFormFactor;
import ru.bukhtaev.service.crud.ICrudService;
import ru.bukhtaev.validation.handling.ErrorResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.bukhtaev.controller.dictionary.MotherboardFormFactorRestController.URL_API_V1_MOTHERBOARD_FORM_FACTORS;

/**
 * Контроллер обработки CRUD операций над форм-факторами материнских плат.
 */
@Tag(name = "Форм-факторы материнских плат")
@RestController
@RequestMapping(value = URL_API_V1_MOTHERBOARD_FORM_FACTORS, produces = "application/json")
public class MotherboardFormFactorRestController {

    /**
     * URL.
     */
    public static final String URL_API_V1_MOTHERBOARD_FORM_FACTORS = "/api/v1/motherboard-form-factors";

    /**
     * Сервис CRUD операций над форм-факторами материнских плат.
     */
    private final ICrudService<MotherboardFormFactor, UUID> crudService;

    /**
     * Маппер для DTO форм-факторов материнских плат.
     */
    private final IMotherboardFormFactorMapper mapper;

    /**
     * Конструктор.
     *
     * @param crudService сервис CRUD операций над форм-факторами материнских плат
     * @param mapper      маппер для DTO форм-факторов материнских плат
     */
    @Autowired
    public MotherboardFormFactorRestController(
            final ICrudService<MotherboardFormFactor, UUID> crudService,
            final IMotherboardFormFactorMapper mapper
    ) {
        this.crudService = crudService;
        this.mapper = mapper;
    }

    @Operation(summary = "Получение всех форм-факторов материнских плат")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Форм-факторы материнских плат получены"
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

    @Operation(summary = "Получение форм-фактора материнской платы по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Форм-фактор материнской платы получен"
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
                    description = "Форм-фактор материнской платы не найден",
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

    @Operation(summary = "Создание форм-фактора материнской платы")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Форм-фактор материнской платы создан"
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
                        .path(URL_API_V1_MOTHERBOARD_FORM_FACTORS + "/{id}")
                        .build(Map.of("id", savedDto.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedDto);
    }

    @Operation(summary = "Изменение форм-фактора материнской платы")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Форм-фактор материнской платы изменен"
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
                    description = "Форм-фактор материнской платы не найден",
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

    @Operation(summary = "Замена форм-фактора материнской платы")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Форм-фактор материнской платы заменен"
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
                    description = "Форм-фактор материнской платы не найден",
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

    @Operation(summary = "Удаление форм-фактора материнской платы по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Форм-фактор материнской платы удален"
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
