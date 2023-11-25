package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.bukhtaev.dto.request.SsdRequestDto;
import ru.bukhtaev.dto.response.SsdResponseDto;
import ru.bukhtaev.model.Ssd;
import ru.bukhtaev.model.dictionary.StoragePowerConnector;

import java.util.UUID;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link Ssd}.
 */
@Mapper(componentModel = SPRING)
public interface ISsdMapper {

    /**
     * Конвертирует {@link Ssd} в DTO {@link SsdResponseDto}.
     *
     * @param entity {@link Ssd}
     * @return DTO {@link SsdResponseDto}
     */
    SsdResponseDto convertToDto(final Ssd entity);

    /**
     * Конвертирует DTO {@link SsdRequestDto} в {@link Ssd},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link SsdRequestDto}
     * @return {@link Ssd}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "vendorId", target = "vendor.id")
    @Mapping(source = "connectorId", target = "connector.id")
    @Mapping(source = "powerConnectorId", target = "powerConnector", qualifiedByName = "toPowerConnector")
    Ssd convertFromDto(final SsdRequestDto dto);

    @Named("toPowerConnector")
    static StoragePowerConnector toPowerConnector(final UUID powerConnectorId) {
        if (powerConnectorId == null) {
            return null;
        }

        return StoragePowerConnector.builder()
                .id(powerConnectorId)
                .build();
    }
}
