package ru.bukhtaev.dto.mapper.cross;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.ComputerCaseToExpansionBayFormatRequestDto;
import ru.bukhtaev.dto.response.ComputerCaseToExpansionBayFormatResponseDto;
import ru.bukhtaev.model.cross.ComputerCaseToExpansionBayFormat;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link ComputerCaseToExpansionBayFormat}.
 */
@Mapper(componentModel = SPRING)
public interface IComputerCaseToExpansionBayFormatMapper {

    /**
     * Конвертирует {@link ComputerCaseToExpansionBayFormat} в DTO {@link ComputerCaseToExpansionBayFormatResponseDto}.
     *
     * @param entity {@link ComputerCaseToExpansionBayFormat}
     * @return DTO {@link ComputerCaseToExpansionBayFormatResponseDto}
     */
    ComputerCaseToExpansionBayFormatResponseDto convertToDto(final ComputerCaseToExpansionBayFormat entity);

    /**
     * Конвертирует DTO {@link ComputerCaseToExpansionBayFormatRequestDto} в {@link ComputerCaseToExpansionBayFormat},
     * игнорируя поле {@code id} и {@code computerCase}.
     *
     * @param dto DTO {@link ComputerCaseToExpansionBayFormatRequestDto}
     * @return {@link ComputerCaseToExpansionBayFormat}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "computerCase", ignore = true)
    @Mapping(source = "expansionBayFormatId", target = "expansionBayFormat.id")
    ComputerCaseToExpansionBayFormat convertFromDto(final ComputerCaseToExpansionBayFormatRequestDto dto);
}
