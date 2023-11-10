package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.VideoMemoryType;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link VideoMemoryType}.
 */
@Mapper(componentModel = SPRING)
public interface IVideoMemoryTypeMapper {

    /**
     * Конвертирует {@link VideoMemoryType} в DTO {@link NameableResponseDto}.
     *
     * @param entity {@link VideoMemoryType}
     * @return DTO {@link NameableResponseDto}
     */
    NameableResponseDto convertToDto(final VideoMemoryType entity);

    /**
     * Конвертирует DTO {@link NameableRequestDto} в {@link VideoMemoryType},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link NameableRequestDto}
     * @return {@link VideoMemoryType}
     */
    @Mapping(target = "id", ignore = true)
    VideoMemoryType convertFromDto(final NameableRequestDto dto);
}
