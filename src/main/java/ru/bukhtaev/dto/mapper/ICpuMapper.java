package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.mapper.cross.ICpuToRamTypeMapper;
import ru.bukhtaev.dto.request.CpuRequestDto;
import ru.bukhtaev.dto.response.CpuResponseDto;
import ru.bukhtaev.model.Cpu;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link Cpu}.
 */
@Mapper(
        componentModel = SPRING,
        uses = ICpuToRamTypeMapper.class
)
public interface ICpuMapper {

    /**
     * Конвертирует {@link Cpu} в DTO {@link CpuResponseDto}.
     *
     * @param entity {@link Cpu}
     * @return DTO {@link CpuResponseDto}
     */
    CpuResponseDto convertToDto(final Cpu entity);

    /**
     * Конвертирует DTO {@link CpuRequestDto} в {@link Cpu},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link CpuRequestDto}
     * @return {@link Cpu}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "socketId", target = "socket.id")
    @Mapping(source = "manufacturerId", target = "manufacturer.id")
    Cpu convertFromDto(final CpuRequestDto dto);
}
