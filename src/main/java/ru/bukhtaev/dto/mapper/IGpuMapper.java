package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.GpuRequestDto;
import ru.bukhtaev.dto.response.GpuResponseDto;
import ru.bukhtaev.model.Gpu;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link Gpu}.
 */
@Mapper(componentModel = SPRING)
public interface IGpuMapper {

    /**
     * Конвертирует {@link Gpu} в DTO {@link GpuResponseDto}.
     *
     * @param entity {@link Gpu}
     * @return DTO {@link GpuResponseDto}
     */
    GpuResponseDto convertToDto(final Gpu entity);

    /**
     * Конвертирует DTO {@link GpuRequestDto} в {@link Gpu},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link GpuRequestDto}
     * @return {@link Gpu}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "manufacturerId", target = "manufacturer.id")
    @Mapping(source = "memoryTypeId", target = "memoryType.id")
    Gpu convertFromDto(final GpuRequestDto dto);
}
