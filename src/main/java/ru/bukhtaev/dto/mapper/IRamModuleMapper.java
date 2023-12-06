package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.RamModuleRequestDto;
import ru.bukhtaev.dto.response.RamModuleResponseDto;
import ru.bukhtaev.model.RamModule;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link RamModule}.
 */
@Mapper(componentModel = SPRING)
public interface IRamModuleMapper {

    /**
     * Конвертирует {@link RamModule} в DTO {@link RamModuleResponseDto}.
     *
     * @param entity {@link RamModule}
     * @return DTO {@link RamModuleResponseDto}
     */
    RamModuleResponseDto convertToDto(final RamModule entity);

    /**
     * Конвертирует DTO {@link RamModuleRequestDto} в {@link RamModule},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link RamModuleRequestDto}
     * @return {@link RamModule}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "typeId", target = "type.id")
    @Mapping(source = "designId", target = "design.id")
    RamModule convertFromDto(final RamModuleRequestDto dto);
}
