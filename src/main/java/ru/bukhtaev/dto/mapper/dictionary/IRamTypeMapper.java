package ru.bukhtaev.dto.mapper.dictionary;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.dictionary.RamType;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link RamType}.
 */
@Mapper(componentModel = SPRING)
public interface IRamTypeMapper {

    /**
     * Конвертирует {@link RamType} в DTO {@link NameableResponseDto}.
     *
     * @param entity {@link RamType}
     * @return DTO {@link NameableResponseDto}
     */
    NameableResponseDto convertToDto(final RamType entity);

    /**
     * Конвертирует DTO {@link NameableRequestDto} в {@link RamType},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link NameableRequestDto}
     * @return {@link RamType}
     */
    @Mapping(target = "id", ignore = true)
    RamType convertFromDto(final NameableRequestDto dto);
}
