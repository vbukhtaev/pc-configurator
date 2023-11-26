package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.mapper.cross.IPsuToCpuPowerConnectorMapper;
import ru.bukhtaev.dto.mapper.cross.IPsuToGraphicsCardPowerConnectorMapper;
import ru.bukhtaev.dto.mapper.cross.IPsuToStoragePowerConnectorMapper;
import ru.bukhtaev.dto.request.PsuRequestDto;
import ru.bukhtaev.dto.response.PsuResponseDto;
import ru.bukhtaev.model.Psu;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link Psu}.
 */
@Mapper(
        componentModel = SPRING,
        uses = {
                IPsuToCpuPowerConnectorMapper.class,
                IPsuToStoragePowerConnectorMapper.class,
                IPsuToGraphicsCardPowerConnectorMapper.class
        }
)
public interface IPsuMapper {

    /**
     * Конвертирует {@link Psu} в DTO {@link PsuResponseDto}.
     *
     * @param entity {@link Psu}
     * @return DTO {@link PsuResponseDto}
     */
    PsuResponseDto convertToDto(final Psu entity);

    /**
     * Конвертирует DTO {@link PsuRequestDto} в {@link Psu},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link PsuRequestDto}
     * @return {@link Psu}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "vendorId", target = "vendor.id")
    @Mapping(source = "formFactorId", target = "formFactor.id")
    @Mapping(source = "certificateId", target = "certificate.id")
    @Mapping(source = "mainPowerConnectorId", target = "mainPowerConnector.id")
    Psu convertFromDto(final PsuRequestDto dto);
}
