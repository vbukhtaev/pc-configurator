package ru.bukhtaev.dto.mapper.dictionary;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.bukhtaev.dto.request.dictionary.FanPowerConnectorRequestDto;
import ru.bukhtaev.dto.response.dictionary.FanPowerConnectorResponseDto;
import ru.bukhtaev.model.dictionary.FanPowerConnector;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link FanPowerConnector}.
 */
@Mapper(componentModel = SPRING)
public interface IFanPowerConnectorMapper {

    /**
     * Конвертирует {@link FanPowerConnector} в DTO {@link FanPowerConnectorResponseDto}.
     *
     * @param entity {@link FanPowerConnector}
     * @return DTO {@link FanPowerConnectorResponseDto}
     */
    @Mapping(
            source = "compatibleConnectors",
            target = "compatibleConnectors",
            qualifiedByName = "connectorSetToConnectorSet"
    )
    FanPowerConnectorResponseDto convertToDto(final FanPowerConnector entity);

    /**
     * Конвертирует DTO {@link FanPowerConnectorRequestDto} в {@link FanPowerConnector},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link FanPowerConnectorRequestDto}
     * @return {@link FanPowerConnector}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(
            source = "compatibleConnectorIds",
            target = "compatibleConnectors",
            qualifiedByName = "uuidSetToConnectorSet"
    )
    FanPowerConnector convertFromDto(final FanPowerConnectorRequestDto dto);

    @Named("uuidSetToConnectorSet")
    static Set<FanPowerConnector> uuidSetToConnectorSet(Set<UUID> connectorIds) {
        if (connectorIds == null) {
            return null;
        }

        return connectorIds.stream()
                .map(connectorId -> FanPowerConnector.builder()
                        .id(connectorId)
                        .build())
                .collect(Collectors.toSet());
    }

    @Named("connectorSetToConnectorSet")
    static Set<FanPowerConnectorResponseDto> connectorSetToConnectorSet(Set<FanPowerConnector> connectors) {
        if (connectors == null) {
            return null;
        }

        return connectors.stream()
                .map(connector -> FanPowerConnectorResponseDto.builder()
                        .id(connector.getId())
                        .name(connector.getName())
                        .build())
                .collect(Collectors.toSet());
    }
}
