package ru.bukhtaev.dto.mapper.cross;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.PsuToStoragePowerConnectorRequestDto;
import ru.bukhtaev.dto.response.PsuToStoragePowerConnectorResponseDto;
import ru.bukhtaev.model.cross.PsuToStoragePowerConnector;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link PsuToStoragePowerConnector}.
 */
@Mapper(componentModel = SPRING)
public interface IPsuToStoragePowerConnectorMapper {

    /**
     * Конвертирует {@link PsuToStoragePowerConnector} в DTO {@link PsuToStoragePowerConnectorResponseDto}.
     *
     * @param entity {@link PsuToStoragePowerConnector}
     * @return DTO {@link PsuToStoragePowerConnectorResponseDto}
     */
    PsuToStoragePowerConnectorResponseDto convertToDto(final PsuToStoragePowerConnector entity);

    /**
     * Конвертирует DTO {@link PsuToStoragePowerConnectorRequestDto} в {@link PsuToStoragePowerConnector},
     * игнорируя поле {@code id} и {@code psu}.
     *
     * @param dto DTO {@link PsuToStoragePowerConnectorRequestDto}
     * @return {@link PsuToStoragePowerConnector}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "psu", ignore = true)
    @Mapping(source = "storagePowerConnectorId", target = "storagePowerConnector.id")
    PsuToStoragePowerConnector convertFromDto(final PsuToStoragePowerConnectorRequestDto dto);
}
