package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.MainPowerConnector;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link MainPowerConnector}.
 */
@Mapper(componentModel = SPRING)
public interface IMainPowerConnectorMapper {

    /**
     * Конвертирует {@link MainPowerConnector} в DTO {@link NameableResponseDto}.
     *
     * @param entity {@link MainPowerConnector}
     * @return DTO {@link NameableResponseDto}
     */
    NameableResponseDto convertToDto(final MainPowerConnector entity);

    /**
     * Конвертирует DTO {@link NameableRequestDto} в {@link MainPowerConnector},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link NameableRequestDto}
     * @return {@link MainPowerConnector}
     */
    @Mapping(target = "id", ignore = true)
    MainPowerConnector convertFromDto(final NameableRequestDto dto);
}
