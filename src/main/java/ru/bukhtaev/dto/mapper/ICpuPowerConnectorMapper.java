package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.dictionary.CpuPowerConnector;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link CpuPowerConnector}.
 */
@Mapper(componentModel = SPRING)
public interface ICpuPowerConnectorMapper {

    /**
     * Конвертирует {@link CpuPowerConnector} в DTO {@link NameableResponseDto}.
     *
     * @param entity {@link CpuPowerConnector}
     * @return DTO {@link NameableResponseDto}
     */
    NameableResponseDto convertToDto(final CpuPowerConnector entity);

    /**
     * Конвертирует DTO {@link NameableRequestDto} в {@link CpuPowerConnector},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link NameableRequestDto}
     * @return {@link CpuPowerConnector}
     */
    @Mapping(target = "id", ignore = true)
    CpuPowerConnector convertFromDto(final NameableRequestDto dto);
}
