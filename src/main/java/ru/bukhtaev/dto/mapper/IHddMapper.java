package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.HddRequestDto;
import ru.bukhtaev.dto.response.HddResponseDto;
import ru.bukhtaev.model.Hdd;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link Hdd}.
 */
@Mapper(componentModel = SPRING)
public interface IHddMapper {

    /**
     * Конвертирует {@link Hdd} в DTO {@link HddResponseDto}.
     *
     * @param entity {@link Hdd}
     * @return DTO {@link HddResponseDto}
     */
    HddResponseDto convertToDto(final Hdd entity);

    /**
     * Конвертирует DTO {@link HddRequestDto} в {@link Hdd},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link HddRequestDto}
     * @return {@link Hdd}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "vendorId", target = "vendor.id")
    @Mapping(source = "connectorId", target = "connector.id")
    @Mapping(source = "powerConnectorId", target = "powerConnector.id")
    Hdd convertFromDto(final HddRequestDto dto);
}
