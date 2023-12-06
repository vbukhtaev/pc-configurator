package ru.bukhtaev.dto.mapper.cross;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.ComputerBuildToSsdRequestDto;
import ru.bukhtaev.dto.response.ComputerBuildToSsdResponseDto;
import ru.bukhtaev.model.cross.ComputerBuildToSsd;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link ComputerBuildToSsd}.
 */
@Mapper(componentModel = SPRING)
public interface IComputerBuildToSsdMapper {

    /**
     * Конвертирует {@link ComputerBuildToSsd} в DTO {@link ComputerBuildToSsdResponseDto}.
     *
     * @param entity {@link ComputerBuildToSsd}
     * @return DTO {@link ComputerBuildToSsdResponseDto}
     */
    ComputerBuildToSsdResponseDto convertToDto(final ComputerBuildToSsd entity);

    /**
     * Конвертирует DTO {@link ComputerBuildToSsdRequestDto} в {@link ComputerBuildToSsd},
     * игнорируя поле {@code id} и {@code computerBuild}.
     *
     * @param dto DTO {@link ComputerBuildToSsdRequestDto}
     * @return {@link ComputerBuildToSsd}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "computerBuild", ignore = true)
    @Mapping(source = "ssdId", target = "ssd.id")
    ComputerBuildToSsd convertFromDto(final ComputerBuildToSsdRequestDto dto);
}
