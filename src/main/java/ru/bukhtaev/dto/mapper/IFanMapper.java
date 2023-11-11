package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.FanRequestDto;
import ru.bukhtaev.dto.response.FanResponseDto;
import ru.bukhtaev.model.Fan;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link Fan}.
 */
@Mapper(componentModel = SPRING)
public interface IFanMapper {

    /**
     * Конвертирует {@link Fan} в DTO {@link FanResponseDto}.
     *
     * @param entity {@link Fan}
     * @return DTO {@link FanResponseDto}
     */
    FanResponseDto convertToDto(final Fan entity);

    /**
     * Конвертирует DTO {@link FanRequestDto} в {@link Fan},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link FanRequestDto}
     * @return {@link Fan}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "sizeId", target = "size.id")
    @Mapping(source = "vendorId", target = "vendor.id")
    @Mapping(source = "powerConnectorId", target = "powerConnector.id")
    Fan convertFromDto(final FanRequestDto dto);
}
