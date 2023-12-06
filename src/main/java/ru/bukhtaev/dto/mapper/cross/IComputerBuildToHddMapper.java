package ru.bukhtaev.dto.mapper.cross;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.ComputerBuildToHddRequestDto;
import ru.bukhtaev.dto.response.ComputerBuildToHddResponseDto;
import ru.bukhtaev.model.cross.ComputerBuildToHdd;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link ComputerBuildToHdd}.
 */
@Mapper(componentModel = SPRING)
public interface IComputerBuildToHddMapper {

    /**
     * Конвертирует {@link ComputerBuildToHdd} в DTO {@link ComputerBuildToHddResponseDto}.
     *
     * @param entity {@link ComputerBuildToHdd}
     * @return DTO {@link ComputerBuildToHddResponseDto}
     */
    ComputerBuildToHddResponseDto convertToDto(final ComputerBuildToHdd entity);

    /**
     * Конвертирует DTO {@link ComputerBuildToHddRequestDto} в {@link ComputerBuildToHdd},
     * игнорируя поле {@code id} и {@code computerBuild}.
     *
     * @param dto DTO {@link ComputerBuildToHddRequestDto}
     * @return {@link ComputerBuildToHdd}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "computerBuild", ignore = true)
    @Mapping(source = "hddId", target = "hdd.id")
    ComputerBuildToHdd convertFromDto(final ComputerBuildToHddRequestDto dto);
}
