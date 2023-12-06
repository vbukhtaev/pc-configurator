package ru.bukhtaev.dto.mapper.dictionary;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.bukhtaev.dto.request.dictionary.GraphicsCardPowerConnectorRequestDto;
import ru.bukhtaev.dto.response.dictionary.GraphicsCardPowerConnectorResponseDto;
import ru.bukhtaev.model.dictionary.GraphicsCardPowerConnector;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link GraphicsCardPowerConnector}.
 */
@Mapper(componentModel = SPRING)
public interface IGraphicsCardPowerConnectorMapper {

    /**
     * Конвертирует {@link GraphicsCardPowerConnector} в DTO {@link GraphicsCardPowerConnectorResponseDto}.
     *
     * @param entity {@link GraphicsCardPowerConnector}
     * @return DTO {@link GraphicsCardPowerConnectorResponseDto}
     */
    @Mapping(
            source = "compatibleConnectors",
            target = "compatibleConnectors",
            qualifiedByName = "connectorSetToConnectorSet"
    )
    GraphicsCardPowerConnectorResponseDto convertToDto(final GraphicsCardPowerConnector entity);

    /**
     * Конвертирует DTO {@link GraphicsCardPowerConnectorRequestDto} в {@link GraphicsCardPowerConnector},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link GraphicsCardPowerConnectorRequestDto}
     * @return {@link GraphicsCardPowerConnector}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(
            source = "compatibleConnectorIds",
            target = "compatibleConnectors",
            qualifiedByName = "uuidSetToConnectorSet"
    )
    GraphicsCardPowerConnector convertFromDto(final GraphicsCardPowerConnectorRequestDto dto);

    @Named("uuidSetToConnectorSet")
    static Set<GraphicsCardPowerConnector> uuidSetToConnectorSet(Set<UUID> connectorIds) {
        if (connectorIds == null) {
            return null;
        }

        return connectorIds.stream()
                .map(connectorId -> GraphicsCardPowerConnector.builder()
                        .id(connectorId)
                        .build())
                .collect(Collectors.toSet());
    }

    @Named("connectorSetToConnectorSet")
    static Set<GraphicsCardPowerConnectorResponseDto> connectorSetToConnectorSet(Set<GraphicsCardPowerConnector> connectors) {
        if (connectors == null) {
            return null;
        }

        return connectors.stream()
                .map(connector -> GraphicsCardPowerConnectorResponseDto.builder()
                        .id(connector.getId())
                        .name(connector.getName())
                        .build())
                .collect(Collectors.toSet());
    }
}
