package ru.bukhtaev.dto.mapper.cross;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.PsuToCpuPowerConnectorRequestDto;
import ru.bukhtaev.dto.response.PsuToCpuPowerConnectorResponseDto;
import ru.bukhtaev.model.cross.PsuToCpuPowerConnector;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link PsuToCpuPowerConnector}.
 */
@Mapper(componentModel = SPRING)
public interface IPsuToCpuPowerConnectorMapper {

    /**
     * Конвертирует {@link PsuToCpuPowerConnector} в DTO {@link PsuToCpuPowerConnectorResponseDto}.
     *
     * @param entity {@link PsuToCpuPowerConnector}
     * @return DTO {@link PsuToCpuPowerConnectorResponseDto}
     */
    PsuToCpuPowerConnectorResponseDto convertToDto(final PsuToCpuPowerConnector entity);

    /**
     * Конвертирует DTO {@link PsuToCpuPowerConnectorRequestDto} в {@link PsuToCpuPowerConnector},
     * игнорируя поле {@code id} и {@code psu}.
     *
     * @param dto DTO {@link PsuToCpuPowerConnectorRequestDto}
     * @return {@link PsuToCpuPowerConnector}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "psu", ignore = true)
    @Mapping(source = "cpuPowerConnectorId", target = "cpuPowerConnector.id")
    PsuToCpuPowerConnector convertFromDto(final PsuToCpuPowerConnectorRequestDto dto);
}
