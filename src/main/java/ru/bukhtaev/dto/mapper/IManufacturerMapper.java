package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.Manufacturer;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link Manufacturer}.
 */
@Mapper(componentModel = SPRING)
public interface IManufacturerMapper {

    /**
     * Конвертирует {@link Manufacturer} в DTO {@link NameableResponseDto}.
     *
     * @param entity {@link Manufacturer}
     * @return DTO {@link NameableResponseDto}
     */
    NameableResponseDto convertToDto(final Manufacturer entity);

    /**
     * Конвертирует DTO {@link NameableRequestDto} в {@link Manufacturer},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link NameableRequestDto}
     * @return {@link Manufacturer}
     */
    @Mapping(target = "id", ignore = true)
    Manufacturer convertFromDto(final NameableRequestDto dto);
}
