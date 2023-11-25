package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.dictionary.Vendor;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link Vendor}.
 */
@Mapper(componentModel = SPRING)
public interface IVendorMapper {

    /**
     * Конвертирует {@link Vendor} в DTO {@link NameableResponseDto}.
     *
     * @param entity {@link Vendor}
     * @return DTO {@link NameableResponseDto}
     */
    NameableResponseDto convertToDto(final Vendor entity);

    /**
     * Конвертирует DTO {@link NameableRequestDto} в {@link Vendor},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link NameableRequestDto}
     * @return {@link Vendor}
     */
    @Mapping(target = "id", ignore = true)
    Vendor convertFromDto(final NameableRequestDto dto);
}
