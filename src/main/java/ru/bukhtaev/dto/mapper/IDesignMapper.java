package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.DesignRequestDto;
import ru.bukhtaev.dto.response.DesignResponseDto;
import ru.bukhtaev.model.Design;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link Design}.
 */
@Mapper(componentModel = SPRING)
public interface IDesignMapper {

    /**
     * Конвертирует {@link Design} в DTO {@link DesignResponseDto}.
     *
     * @param entity {@link Design}
     * @return DTO {@link DesignResponseDto}
     */
    DesignResponseDto convertToDto(final Design entity);

    /**
     * Конвертирует DTO {@link DesignRequestDto} в {@link Design},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link DesignRequestDto}
     * @return {@link Design}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "vendorId", target = "vendor.id")
    Design convertFromDto(final DesignRequestDto dto);
}
