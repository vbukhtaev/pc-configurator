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
import ru.bukhtaev.dto.mapper.dictionary.IPsuFormFactorMapper;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.dictionary.PsuFormFactor;
import ru.bukhtaev.service.ICrudService;
import ru.bukhtaev.validation.handling.ErrorResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.bukhtaev.controller.dictionary.PsuFormFactorRestController.URL_API_V1_PSU_FORM_FACTORS;

/**
 * Контроллер обработки CRUD операций над форм-факторами блоков питания.
 */
@Tag(name = "Форм-факторы блоков питания")
@RestController
@RequestMapping(value = URL_API_V1_PSU_FORM_FACTORS, produces = "application/json")
public class PsuFormFactorRestController {

    /**
     * URL.
     */
    public static final String URL_API_V1_PSU_FORM_FACTORS = "/api/v1/psu-form-factors";

    /**
     * Сервис CRUD операций над форм-факторами блоков питания.
     */
    private final ICrudService<PsuFormFactor, UUID> crudService;

    /**
     * Маппер для DTO форм-факторов блоков питания.
     */
    private final IPsuFormFactorMapper mapper;

    /**
     * Конструктор.
     *
     * @param crudService сервис CRUD операций над форм-факторами блоков питания
     * @param mapper      маппер для DTO форм-факторов блоков питания
     */
    @Autowired
    public PsuFormFactorRestController(
            final ICrudService<PsuFormFactor, UUID> crudService,
            final IPsuFormFactorMapper mapper
    ) {
        this.crudService = crudService;
        this.mapper = mapper;
    }

    @Operation(summary = "Получение всех форм-факторов блоков питания")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Форм-факторы блоков питания получены"
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

    @Operation(summary = "Получение форм-фактора блока питания по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Форм-фактор блока питания получен"
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
                    description = "Форм-фактор блока питания не найден",
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

    @Operation(summary = "Создание форм-фактора блока питания")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Форм-фактор блока питания создан"
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
                        .path(URL_API_V1_PSU_FORM_FACTORS + "/{id}")
                        .build(Map.of("id", savedDto.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedDto);
    }

    @Operation(summary = "Изменение форм-фактора блока питания")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Форм-фактор блока питания изменен"
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
                    description = "Форм-фактор блока питания не найден",
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

    @Operation(summary = "Замена форм-фактора блока питания")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Форм-фактор блока питания заменен"
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
                    description = "Форм-фактор блока питания не найден",
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

    @Operation(summary = "Удаление форм-фактора блока питания по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Форм-фактор блока питания удален"
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
