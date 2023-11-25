package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.CpuRamTypeRequestDto;
import ru.bukhtaev.dto.response.CpuRamTypeResponseDto;
import ru.bukhtaev.model.CpuRamType;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link CpuRamType}.
 */
@Mapper(componentModel = SPRING)
public interface ICpuRamTypeMapper {

    /**
     * Конвертирует {@link CpuRamType} в DTO {@link CpuRamTypeResponseDto}.
     *
     * @param entity {@link CpuRamType}
     * @return DTO {@link CpuRamTypeResponseDto}
     */
    CpuRamTypeResponseDto convertToDto(final CpuRamType entity);

    /**
     * Конвертирует DTO {@link CpuRamTypeRequestDto} в {@link CpuRamType},
     * игнорируя поле {@code id} и {@code cpu}.
     *
     * @param dto DTO {@link CpuRamTypeRequestDto}
     * @return {@link CpuRamType}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cpu", ignore = true)
    @Mapping(source = "ramTypeId", target = "ramType.id")
    CpuRamType convertFromDto(final CpuRamTypeRequestDto dto);
}
