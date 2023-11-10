package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.FanPowerConnector;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link FanPowerConnector}.
 */
@Mapper(componentModel = SPRING)
public interface IFanPowerConnectorMapper {

    /**
     * Конвертирует {@link FanPowerConnector} в DTO {@link NameableResponseDto}.
     *
     * @param entity {@link FanPowerConnector}
     * @return DTO {@link NameableResponseDto}
     */
    NameableResponseDto convertToDto(final FanPowerConnector entity);

    /**
     * Конвертирует DTO {@link NameableRequestDto} в {@link FanPowerConnector},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link NameableRequestDto}
     * @return {@link FanPowerConnector}
     */
    @Mapping(target = "id", ignore = true)
    FanPowerConnector convertFromDto(final NameableRequestDto dto);
}
