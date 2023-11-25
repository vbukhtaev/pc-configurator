package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.dictionary.PsuFormFactor;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link PsuFormFactor}.
 */
@Mapper(componentModel = SPRING)
public interface IPsuFormFactorMapper {

    /**
     * Конвертирует {@link PsuFormFactor} в DTO {@link NameableResponseDto}.
     *
     * @param entity {@link PsuFormFactor}
     * @return DTO {@link NameableResponseDto}
     */
    NameableResponseDto convertToDto(final PsuFormFactor entity);

    /**
     * Конвертирует DTO {@link NameableRequestDto} в {@link PsuFormFactor},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link NameableRequestDto}
     * @return {@link PsuFormFactor}
     */
    @Mapping(target = "id", ignore = true)
    PsuFormFactor convertFromDto(final NameableRequestDto dto);
}
