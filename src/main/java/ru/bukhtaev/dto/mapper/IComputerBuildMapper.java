package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.mapper.cross.IComputerBuildToFanMapper;
import ru.bukhtaev.dto.mapper.cross.IComputerBuildToHddMapper;
import ru.bukhtaev.dto.mapper.cross.IComputerBuildToRamModuleMapper;
import ru.bukhtaev.dto.mapper.cross.IComputerBuildToSsdMapper;
import ru.bukhtaev.dto.request.ComputerBuildRequestDto;
import ru.bukhtaev.dto.response.ComputerBuildResponseDto;
import ru.bukhtaev.model.ComputerBuild;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link ComputerBuild}.
 */
@Mapper(
        componentModel = SPRING,
        uses = {
                IComputerBuildToRamModuleMapper.class,
                IComputerBuildToFanMapper.class,
                IComputerBuildToHddMapper.class,
                IComputerBuildToSsdMapper.class
        }
)
public interface IComputerBuildMapper {

    /**
     * Конвертирует {@link ComputerBuild} в DTO {@link ComputerBuildResponseDto}.
     *
     * @param entity {@link ComputerBuild}
     * @return DTO {@link ComputerBuildResponseDto}
     */
    ComputerBuildResponseDto convertToDto(final ComputerBuild entity);

    /**
     * Конвертирует DTO {@link ComputerBuildRequestDto} в {@link ComputerBuild},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link ComputerBuildRequestDto}
     * @return {@link ComputerBuild}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "cpuId", target = "cpu.id")
    @Mapping(source = "psuId", target = "psu.id")
    @Mapping(source = "coolerId", target = "cooler.id")
    @Mapping(source = "motherboardId", target = "motherboard.id")
    @Mapping(source = "graphicsCardId", target = "graphicsCard.id")
    @Mapping(source = "computerCaseId", target = "computerCase.id")
    ComputerBuild convertFromDto(final ComputerBuildRequestDto dto);
}
