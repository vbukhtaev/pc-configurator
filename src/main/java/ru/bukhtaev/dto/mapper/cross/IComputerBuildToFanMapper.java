package ru.bukhtaev.dto.mapper.cross;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.ComputerBuildToFanRequestDto;
import ru.bukhtaev.dto.response.ComputerBuildToFanResponseDto;
import ru.bukhtaev.model.cross.ComputerBuildToFan;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link ComputerBuildToFan}.
 */
@Mapper(componentModel = SPRING)
public interface IComputerBuildToFanMapper {

    /**
     * Конвертирует {@link ComputerBuildToFan} в DTO {@link ComputerBuildToFanResponseDto}.
     *
     * @param entity {@link ComputerBuildToFan}
     * @return DTO {@link ComputerBuildToFanResponseDto}
     */
    ComputerBuildToFanResponseDto convertToDto(final ComputerBuildToFan entity);

    /**
     * Конвертирует DTO {@link ComputerBuildToFanRequestDto} в {@link ComputerBuildToFan},
     * игнорируя поле {@code id} и {@code computerBuild}.
     *
     * @param dto DTO {@link ComputerBuildToFanRequestDto}
     * @return {@link ComputerBuildToFan}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "computerBuild", ignore = true)
    @Mapping(source = "fanId", target = "fan.id")
    ComputerBuildToFan convertFromDto(final ComputerBuildToFanRequestDto dto);
}
