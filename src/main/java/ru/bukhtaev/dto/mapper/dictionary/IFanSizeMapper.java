package ru.bukhtaev.dto.mapper.dictionary;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.FanSizeRequestDto;
import ru.bukhtaev.dto.response.FanSizeResponseDto;
import ru.bukhtaev.model.dictionary.FanSize;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link FanSize}.
 */
@Mapper(componentModel = SPRING)
public interface IFanSizeMapper {

    /**
     * Конвертирует {@link FanSize} в DTO {@link FanSizeResponseDto}.
     *
     * @param entity {@link FanSize}
     * @return DTO {@link FanSizeResponseDto}
     */
    FanSizeResponseDto convertToDto(final FanSize entity);

    /**
     * Конвертирует DTO {@link FanSizeRequestDto} в {@link FanSize},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link FanSizeRequestDto}
     * @return {@link FanSize}
     */
    @Mapping(target = "id", ignore = true)
    FanSize convertFromDto(final FanSizeRequestDto dto);
}
