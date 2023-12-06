package ru.bukhtaev.dto.mapper.cross;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.ComputerCaseToFanSizeRequestDto;
import ru.bukhtaev.dto.response.ComputerCaseToFanSizeResponseDto;
import ru.bukhtaev.model.cross.ComputerCaseToFanSize;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link ComputerCaseToFanSize}.
 */
@Mapper(componentModel = SPRING)
public interface IComputerCaseToFanSizeMapper {

    /**
     * Конвертирует {@link ComputerCaseToFanSize} в DTO {@link ComputerCaseToFanSizeResponseDto}.
     *
     * @param entity {@link ComputerCaseToFanSize}
     * @return DTO {@link ComputerCaseToFanSizeResponseDto}
     */
    ComputerCaseToFanSizeResponseDto convertToDto(final ComputerCaseToFanSize entity);

    /**
     * Конвертирует DTO {@link ComputerCaseToFanSizeRequestDto} в {@link ComputerCaseToFanSize},
     * игнорируя поле {@code id} и {@code computerCase}.
     *
     * @param dto DTO {@link ComputerCaseToFanSizeRequestDto}
     * @return {@link ComputerCaseToFanSize}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "computerCase", ignore = true)
    @Mapping(source = "fanSizeId", target = "fanSize.id")
    ComputerCaseToFanSize convertFromDto(final ComputerCaseToFanSizeRequestDto dto);
}
