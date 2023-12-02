package ru.bukhtaev.dto.mapper.dictionary;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.bukhtaev.dto.request.dictionary.CpuPowerConnectorRequestDto;
import ru.bukhtaev.dto.response.dictionary.CpuPowerConnectorResponseDto;
import ru.bukhtaev.model.dictionary.CpuPowerConnector;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link CpuPowerConnector}.
 */
@Mapper(componentModel = SPRING)
public interface ICpuPowerConnectorMapper {

    /**
     * Конвертирует {@link CpuPowerConnector} в DTO {@link CpuPowerConnectorResponseDto}.
     *
     * @param entity {@link CpuPowerConnector}
     * @return DTO {@link CpuPowerConnectorResponseDto}
     */
    @Mapping(
            source = "compatibleConnectors",
            target = "compatibleConnectors",
            qualifiedByName = "connectorSetToConnectorSet"
    )
    CpuPowerConnectorResponseDto convertToDto(final CpuPowerConnector entity);

    /**
     * Конвертирует DTO {@link CpuPowerConnectorRequestDto} в {@link CpuPowerConnector},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link CpuPowerConnectorRequestDto}
     * @return {@link CpuPowerConnector}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(
            source = "compatibleConnectorIds",
            target = "compatibleConnectors",
            qualifiedByName = "uuidSetToConnectorSet"
    )
    CpuPowerConnector convertFromDto(final CpuPowerConnectorRequestDto dto);

    @Named("uuidSetToConnectorSet")
    static Set<CpuPowerConnector> uuidSetToConnectorSet(Set<UUID> connectorIds) {
        if (connectorIds == null) {
            return null;
        }

        return connectorIds.stream()
                .map(connectorId -> CpuPowerConnector.builder()
                        .id(connectorId)
                        .build())
                .collect(Collectors.toSet());
    }

    @Named("connectorSetToConnectorSet")
    static Set<CpuPowerConnectorResponseDto> connectorSetToConnectorSet(Set<CpuPowerConnector> connectors) {
        if (connectors == null) {
            return null;
        }

        return connectors.stream()
                .map(connector -> CpuPowerConnectorResponseDto.builder()
                        .id(connector.getId())
                        .name(connector.getName())
                        .build())
                .collect(Collectors.toSet());
    }
}
