package ru.bukhtaev.dto.mapper.dictionary;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.bukhtaev.dto.request.dictionary.MainPowerConnectorRequestDto;
import ru.bukhtaev.dto.response.dictionary.MainPowerConnectorResponseDto;
import ru.bukhtaev.model.dictionary.MainPowerConnector;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link MainPowerConnector}.
 */
@Mapper(componentModel = SPRING)
public interface IMainPowerConnectorMapper {

    /**
     * Конвертирует {@link MainPowerConnector} в DTO {@link MainPowerConnectorResponseDto}.
     *
     * @param entity {@link MainPowerConnector}
     * @return DTO {@link MainPowerConnectorResponseDto}
     */
    @Mapping(
            source = "compatibleConnectors",
            target = "compatibleConnectors",
            qualifiedByName = "connectorSetToConnectorSet"
    )
    MainPowerConnectorResponseDto convertToDto(final MainPowerConnector entity);

    /**
     * Конвертирует DTO {@link MainPowerConnectorRequestDto} в {@link MainPowerConnector},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link MainPowerConnectorRequestDto}
     * @return {@link MainPowerConnector}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(
            source = "compatibleConnectorIds",
            target = "compatibleConnectors",
            qualifiedByName = "uuidSetToConnectorSet"
    )
    MainPowerConnector convertFromDto(final MainPowerConnectorRequestDto dto);

    @Named("uuidSetToConnectorSet")
    static Set<MainPowerConnector> uuidSetToConnectorSet(Set<UUID> connectorIds) {
        if (connectorIds == null) {
            return null;
        }

        return connectorIds.stream()
                .map(connectorId -> MainPowerConnector.builder()
                        .id(connectorId)
                        .build())
                .collect(Collectors.toSet());
    }

    @Named("connectorSetToConnectorSet")
    static Set<MainPowerConnectorResponseDto> connectorSetToConnectorSet(Set<MainPowerConnector> connectors) {
        if (connectors == null) {
            return null;
        }

        return connectors.stream()
                .map(connector -> MainPowerConnectorResponseDto.builder()
                        .id(connector.getId())
                        .name(connector.getName())
                        .build())
                .collect(Collectors.toSet());
    }
}
