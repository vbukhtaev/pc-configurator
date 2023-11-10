package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.MotherboardFormFactor;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link MotherboardFormFactor}.
 */
@Mapper(componentModel = SPRING)
public interface IMotherboardFormFactorMapper {

    /**
     * Конвертирует {@link MotherboardFormFactor} в DTO {@link NameableResponseDto}.
     *
     * @param entity {@link MotherboardFormFactor}
     * @return DTO {@link NameableResponseDto}
     */
    NameableResponseDto convertToDto(final MotherboardFormFactor entity);

    /**
     * Конвертирует DTO {@link NameableRequestDto} в {@link MotherboardFormFactor},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link NameableRequestDto}
     * @return {@link MotherboardFormFactor}
     */
    @Mapping(target = "id", ignore = true)
    MotherboardFormFactor convertFromDto(final NameableRequestDto dto);
}
