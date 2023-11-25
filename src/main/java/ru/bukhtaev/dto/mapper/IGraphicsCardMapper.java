package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.GraphicsCardRequestDto;
import ru.bukhtaev.dto.response.GraphicsCardResponseDto;
import ru.bukhtaev.model.GraphicsCard;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link GraphicsCard}.
 */
@Mapper(
        componentModel = SPRING,
        uses = IGraphicsCardToPowerConnectorMapper.class
)
public interface IGraphicsCardMapper {

    /**
     * Конвертирует {@link GraphicsCard} в DTO {@link GraphicsCardResponseDto}.
     *
     * @param entity {@link GraphicsCard}
     * @return DTO {@link GraphicsCardResponseDto}
     */
    GraphicsCardResponseDto convertToDto(final GraphicsCard entity);

    /**
     * Конвертирует DTO {@link GraphicsCardRequestDto} в {@link GraphicsCard},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link GraphicsCardRequestDto}
     * @return {@link GraphicsCard}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "gpuId", target = "gpu.id")
    @Mapping(source = "designId", target = "design.id")
    @Mapping(source = "pciExpressConnectorVersionId", target = "pciExpressConnectorVersion.id")
    GraphicsCard convertFromDto(final GraphicsCardRequestDto dto);
}
