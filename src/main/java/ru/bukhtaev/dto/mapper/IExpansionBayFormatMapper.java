package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.ExpansionBayFormat;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link ExpansionBayFormat}.
 */
@Mapper(componentModel = SPRING)
public interface IExpansionBayFormatMapper {

    /**
     * Конвертирует {@link ExpansionBayFormat} в DTO {@link NameableResponseDto}.
     *
     * @param entity {@link ExpansionBayFormat}
     * @return DTO {@link NameableResponseDto}
     */
    NameableResponseDto convertToDto(final ExpansionBayFormat entity);

    /**
     * Конвертирует DTO {@link NameableRequestDto} в {@link ExpansionBayFormat},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link NameableRequestDto}
     * @return {@link ExpansionBayFormat}
     */
    @Mapping(target = "id", ignore = true)
    ExpansionBayFormat convertFromDto(final NameableRequestDto dto);
}
