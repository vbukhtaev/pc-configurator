package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.ChipsetRequestDto;
import ru.bukhtaev.dto.response.ChipsetResponseDto;
import ru.bukhtaev.model.Chipset;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link Chipset}.
 */
@Mapper(componentModel = SPRING)
public interface IChipsetMapper {

    /**
     * Конвертирует {@link Chipset} в DTO {@link ChipsetResponseDto}.
     *
     * @param entity {@link Chipset}
     * @return DTO {@link ChipsetResponseDto}
     */
    ChipsetResponseDto convertToDto(final Chipset entity);

    /**
     * Конвертирует DTO {@link ChipsetRequestDto} в {@link Chipset},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link ChipsetRequestDto}
     * @return {@link Chipset}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "socketId", target = "socket.id")
    Chipset convertFromDto(final ChipsetRequestDto dto);
}
