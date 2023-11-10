package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.GraphicsCardPowerConnector;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link GraphicsCardPowerConnector}.
 */
@Mapper(componentModel = SPRING)
public interface IGraphicsCardPowerConnectorMapper {

    /**
     * Конвертирует {@link GraphicsCardPowerConnector} в DTO {@link NameableResponseDto}.
     *
     * @param entity {@link GraphicsCardPowerConnector}
     * @return DTO {@link NameableResponseDto}
     */
    NameableResponseDto convertToDto(final GraphicsCardPowerConnector entity);

    /**
     * Конвертирует DTO {@link NameableRequestDto} в {@link GraphicsCardPowerConnector},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link NameableRequestDto}
     * @return {@link GraphicsCardPowerConnector}
     */
    @Mapping(target = "id", ignore = true)
    GraphicsCardPowerConnector convertFromDto(final NameableRequestDto dto);
}
