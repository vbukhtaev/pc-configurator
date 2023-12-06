package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.mapper.cross.IMotherboardToFanPowerConnectorMapper;
import ru.bukhtaev.dto.mapper.cross.IMotherboardToStorageConnectorMapper;
import ru.bukhtaev.dto.request.MotherboardRequestDto;
import ru.bukhtaev.dto.response.MotherboardResponseDto;
import ru.bukhtaev.model.Motherboard;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link Motherboard}.
 */
@Mapper(
        componentModel = SPRING,
        uses = {
                IMotherboardToFanPowerConnectorMapper.class,
                IMotherboardToStorageConnectorMapper.class
        }
)
public interface IMotherboardMapper {

    /**
     * Конвертирует {@link Motherboard} в DTO {@link MotherboardResponseDto}.
     *
     * @param entity {@link Motherboard}
     * @return DTO {@link MotherboardResponseDto}
     */
    MotherboardResponseDto convertToDto(final Motherboard entity);

    /**
     * Конвертирует DTO {@link MotherboardRequestDto} в {@link Motherboard},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link MotherboardRequestDto}
     * @return {@link Motherboard}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "designId", target = "design.id")
    @Mapping(source = "chipsetId", target = "chipset.id")
    @Mapping(source = "ramTypeId", target = "ramType.id")
    @Mapping(source = "formFactorId", target = "formFactor.id")
    @Mapping(source = "cpuPowerConnectorId", target = "cpuPowerConnector.id")
    @Mapping(source = "mainPowerConnectorId", target = "mainPowerConnector.id")
    @Mapping(source = "coolerPowerConnectorId", target = "coolerPowerConnector.id")
    @Mapping(source = "pciExpressConnectorVersionId", target = "pciExpressConnectorVersion.id")
    Motherboard convertFromDto(final MotherboardRequestDto dto);
}
