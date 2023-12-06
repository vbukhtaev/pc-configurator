package ru.bukhtaev.dto.mapper.dictionary;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.bukhtaev.dto.request.dictionary.StorageConnectorRequestDto;
import ru.bukhtaev.dto.response.dictionary.StorageConnectorResponseDto;
import ru.bukhtaev.model.dictionary.StorageConnector;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link StorageConnector}.
 */
@Mapper(componentModel = SPRING)
public interface IStorageConnectorMapper {

    /**
     * Конвертирует {@link StorageConnector} в DTO {@link StorageConnectorResponseDto}.
     *
     * @param entity {@link StorageConnector}
     * @return DTO {@link StorageConnectorResponseDto}
     */
    @Mapping(
            source = "compatibleConnectors",
            target = "compatibleConnectors",
            qualifiedByName = "connectorSetToConnectorSet"
    )
    StorageConnectorResponseDto convertToDto(final StorageConnector entity);

    /**
     * Конвертирует DTO {@link StorageConnectorRequestDto} в {@link StorageConnector},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link StorageConnectorRequestDto}
     * @return {@link StorageConnector}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(
            source = "compatibleConnectorIds",
            target = "compatibleConnectors",
            qualifiedByName = "uuidSetToConnectorSet"
    )
    StorageConnector convertFromDto(final StorageConnectorRequestDto dto);

    @Named("uuidSetToConnectorSet")
    static Set<StorageConnector> uuidSetToConnectorSet(Set<UUID> connectorIds) {
        if (connectorIds == null) {
            return null;
        }

        return connectorIds.stream()
                .map(connectorId -> StorageConnector.builder()
                        .id(connectorId)
                        .build())
                .collect(Collectors.toSet());
    }

    @Named("connectorSetToConnectorSet")
    static Set<StorageConnectorResponseDto> connectorSetToConnectorSet(Set<StorageConnector> connectors) {
        if (connectors == null) {
            return null;
        }

        return connectors.stream()
                .map(connector -> StorageConnectorResponseDto.builder()
                        .id(connector.getId())
                        .name(connector.getName())
                        .build())
                .collect(Collectors.toSet());
    }
}
