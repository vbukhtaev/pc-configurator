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
import ru.bukhtaev.dto.mapper.dictionary.IVideoMemoryTypeMapper;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.dictionary.VideoMemoryType;
import ru.bukhtaev.service.ICrudService;
import ru.bukhtaev.validation.handling.ErrorResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.bukhtaev.controller.dictionary.VideoMemoryTypeRestController.URL_API_V1_VIDEO_MEMORY_TYPES;

/**
 * Контроллер обработки CRUD операций над типами видеопамяти.
 */
@Tag(name = "Типы видеопамяти")
@RestController
@RequestMapping(value = URL_API_V1_VIDEO_MEMORY_TYPES, produces = "application/json")
public class VideoMemoryTypeRestController {

    /**
     * URL.
     */
    public static final String URL_API_V1_VIDEO_MEMORY_TYPES = "/api/v1/video-memory-types";

    /**
     * Сервис CRUD операций над типами видеопамяти.
     */
    private final ICrudService<VideoMemoryType, UUID> crudService;

    /**
     * Маппер для DTO типов видеопамяти.
     */
    private final IVideoMemoryTypeMapper mapper;

    /**
     * Конструктор.
     *
     * @param crudService сервис CRUD операций над типами видеопамяти
     * @param mapper      маппер для DTO типов видеопамяти
     */
    @Autowired
    public VideoMemoryTypeRestController(
            final ICrudService<VideoMemoryType, UUID> crudService,
            final IVideoMemoryTypeMapper mapper
    ) {
        this.crudService = crudService;
        this.mapper = mapper;
    }

    @Operation(summary = "Получение всех типов видеопамяти")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Типы видеопамяти получены"
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

    @Operation(summary = "Получение типа видеопамяти по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Тип видеопамяти получен"
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
                    description = "Тип видеопамяти не найден",
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

    @Operation(summary = "Создание типа видеопамяти")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Тип видеопамяти создан"
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
                        .path(URL_API_V1_VIDEO_MEMORY_TYPES + "/{id}")
                        .build(Map.of("id", savedDto.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedDto);
    }

    @Operation(summary = "Изменение типа видеопамяти")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Тип видеопамяти изменен"
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
                    description = "Тип видеопамяти не найден",
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

    @Operation(summary = "Замена типа видеопамяти")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Тип видеопамяти заменен"
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
                    description = "Тип видеопамяти не найден",
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

    @Operation(summary = "Удаление типа видеопамяти по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Тип видеопамяти удален"
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
