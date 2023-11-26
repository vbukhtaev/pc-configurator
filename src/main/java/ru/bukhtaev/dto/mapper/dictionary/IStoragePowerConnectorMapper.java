package ru.bukhtaev.dto.mapper.dictionary;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.dictionary.StoragePowerConnector;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link StoragePowerConnector}.
 */
@Mapper(componentModel = SPRING)
public interface IStoragePowerConnectorMapper {

    /**
     * Конвертирует {@link StoragePowerConnector} в DTO {@link NameableResponseDto}.
     *
     * @param entity {@link StoragePowerConnector}
     * @return DTO {@link NameableResponseDto}
     */
    NameableResponseDto convertToDto(final StoragePowerConnector entity);

    /**
     * Конвертирует DTO {@link NameableRequestDto} в {@link StoragePowerConnector},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link NameableRequestDto}
     * @return {@link StoragePowerConnector}
     */
    @Mapping(target = "id", ignore = true)
    StoragePowerConnector convertFromDto(final NameableRequestDto dto);
}
