package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.GraphicsCardToPowerConnectorRequestDto;
import ru.bukhtaev.dto.response.GraphicsCardToPowerConnectorResponseDto;
import ru.bukhtaev.model.cross.GraphicsCardToPowerConnector;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link GraphicsCardToPowerConnector}.
 */
@Mapper(componentModel = SPRING)
public interface IGraphicsCardToPowerConnectorMapper {

    /**
     * Конвертирует {@link GraphicsCardToPowerConnector} в DTO {@link GraphicsCardToPowerConnectorResponseDto}.
     *
     * @param entity {@link GraphicsCardToPowerConnector}
     * @return DTO {@link GraphicsCardToPowerConnectorResponseDto}
     */
    GraphicsCardToPowerConnectorResponseDto convertToDto(final GraphicsCardToPowerConnector entity);

    /**
     * Конвертирует DTO {@link GraphicsCardToPowerConnectorRequestDto} в {@link GraphicsCardToPowerConnector},
     * игнорируя поле {@code id} и {@code graphicsCard}.
     *
     * @param dto DTO {@link GraphicsCardToPowerConnectorRequestDto}
     * @return {@link GraphicsCardToPowerConnector}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "graphicsCard", ignore = true)
    @Mapping(source = "powerConnectorId", target = "powerConnector.id")
    GraphicsCardToPowerConnector convertFromDto(final GraphicsCardToPowerConnectorRequestDto dto);
}
