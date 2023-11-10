package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.StorageConnector;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link StorageConnector}.
 */
@Mapper(componentModel = SPRING)
public interface IStorageConnectorMapper {

    /**
     * Конвертирует {@link StorageConnector} в DTO {@link NameableResponseDto}.
     *
     * @param entity {@link StorageConnector}
     * @return DTO {@link NameableResponseDto}
     */
    NameableResponseDto convertToDto(final StorageConnector entity);

    /**
     * Конвертирует DTO {@link NameableRequestDto} в {@link StorageConnector},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link NameableRequestDto}
     * @return {@link StorageConnector}
     */
    @Mapping(target = "id", ignore = true)
    StorageConnector convertFromDto(final NameableRequestDto dto);
}
