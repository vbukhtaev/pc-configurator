package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.CpuToRamTypeRequestDto;
import ru.bukhtaev.dto.response.CpuToRamTypeResponseDto;
import ru.bukhtaev.model.cross.CpuToRamType;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link CpuToRamType}.
 */
@Mapper(componentModel = SPRING)
public interface ICpuToRamTypeMapper {

    /**
     * Конвертирует {@link CpuToRamType} в DTO {@link CpuToRamTypeResponseDto}.
     *
     * @param entity {@link CpuToRamType}
     * @return DTO {@link CpuToRamTypeResponseDto}
     */
    CpuToRamTypeResponseDto convertToDto(final CpuToRamType entity);

    /**
     * Конвертирует DTO {@link CpuToRamTypeRequestDto} в {@link CpuToRamType},
     * игнорируя поле {@code id} и {@code cpu}.
     *
     * @param dto DTO {@link CpuToRamTypeRequestDto}
     * @return {@link CpuToRamType}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cpu", ignore = true)
    @Mapping(source = "ramTypeId", target = "ramType.id")
    CpuToRamType convertFromDto(final CpuToRamTypeRequestDto dto);
}
