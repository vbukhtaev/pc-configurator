package ru.bukhtaev.dto.mapper.cross;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.PsuToGraphicsCardPowerConnectorRequestDto;
import ru.bukhtaev.dto.response.PsuToGraphicsCardPowerConnectorResponseDto;
import ru.bukhtaev.model.cross.PsuToGraphicsCardPowerConnector;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link PsuToGraphicsCardPowerConnector}.
 */
@Mapper(componentModel = SPRING)
public interface IPsuToGraphicsCardPowerConnectorMapper {

    /**
     * Конвертирует {@link PsuToGraphicsCardPowerConnector} в DTO {@link PsuToGraphicsCardPowerConnectorResponseDto}.
     *
     * @param entity {@link PsuToGraphicsCardPowerConnector}
     * @return DTO {@link PsuToGraphicsCardPowerConnectorResponseDto}
     */
    PsuToGraphicsCardPowerConnectorResponseDto convertToDto(final PsuToGraphicsCardPowerConnector entity);

    /**
     * Конвертирует DTO {@link PsuToGraphicsCardPowerConnectorRequestDto} в {@link PsuToGraphicsCardPowerConnector},
     * игнорируя поле {@code id} и {@code psu}.
     *
     * @param dto DTO {@link PsuToGraphicsCardPowerConnectorRequestDto}
     * @return {@link PsuToGraphicsCardPowerConnector}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "psu", ignore = true)
    @Mapping(source = "graphicsCardPowerConnectorId", target = "graphicsCardPowerConnector.id")
    PsuToGraphicsCardPowerConnector convertFromDto(final PsuToGraphicsCardPowerConnectorRequestDto dto);
}
