package ru.bukhtaev.dto.mapper.cross;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.ComputerBuildToRamModuleRequestDto;
import ru.bukhtaev.dto.response.ComputerBuildToRamModuleResponseDto;
import ru.bukhtaev.model.cross.ComputerBuildToRamModule;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link ComputerBuildToRamModule}.
 */
@Mapper(componentModel = SPRING)
public interface IComputerBuildToRamModuleMapper {

    /**
     * Конвертирует {@link ComputerBuildToRamModule} в DTO {@link ComputerBuildToRamModuleResponseDto}.
     *
     * @param entity {@link ComputerBuildToRamModule}
     * @return DTO {@link ComputerBuildToRamModuleResponseDto}
     */
    ComputerBuildToRamModuleResponseDto convertToDto(final ComputerBuildToRamModule entity);

    /**
     * Конвертирует DTO {@link ComputerBuildToRamModuleRequestDto} в {@link ComputerBuildToRamModule},
     * игнорируя поле {@code id} и {@code computerBuild}.
     *
     * @param dto DTO {@link ComputerBuildToRamModuleRequestDto}
     * @return {@link ComputerBuildToRamModule}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "computerBuild", ignore = true)
    @Mapping(source = "ramModuleId", target = "ramModule.id")
    ComputerBuildToRamModule convertFromDto(final ComputerBuildToRamModuleRequestDto dto);
}
