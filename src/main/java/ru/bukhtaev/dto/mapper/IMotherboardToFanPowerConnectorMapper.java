package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.MotherboardToFanPowerConnectorRequestDto;
import ru.bukhtaev.dto.response.MotherboardToFanPowerConnectorResponseDto;
import ru.bukhtaev.model.cross.MotherboardToFanPowerConnector;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link MotherboardToFanPowerConnector}.
 */
@Mapper(componentModel = SPRING)
public interface IMotherboardToFanPowerConnectorMapper {

    /**
     * Конвертирует {@link MotherboardToFanPowerConnector} в DTO {@link MotherboardToFanPowerConnectorResponseDto}.
     *
     * @param entity {@link MotherboardToFanPowerConnector}
     * @return DTO {@link MotherboardToFanPowerConnectorResponseDto}
     */
    MotherboardToFanPowerConnectorResponseDto convertToDto(final MotherboardToFanPowerConnector entity);

    /**
     * Конвертирует DTO {@link MotherboardToFanPowerConnectorRequestDto} в {@link MotherboardToFanPowerConnector},
     * игнорируя поле {@code id} и {@code graphicsCard}.
     *
     * @param dto DTO {@link MotherboardToFanPowerConnectorRequestDto}
     * @return {@link MotherboardToFanPowerConnector}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "motherboard", ignore = true)
    @Mapping(source = "fanPowerConnectorId", target = "fanPowerConnector.id")
    MotherboardToFanPowerConnector convertFromDto(final MotherboardToFanPowerConnectorRequestDto dto);
}
