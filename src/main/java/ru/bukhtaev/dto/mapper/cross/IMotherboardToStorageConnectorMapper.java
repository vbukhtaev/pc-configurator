package ru.bukhtaev.dto.mapper.cross;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.MotherboardToStorageConnectorRequestDto;
import ru.bukhtaev.dto.response.MotherboardToStorageConnectorResponseDto;
import ru.bukhtaev.model.cross.MotherboardToStorageConnector;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link MotherboardToStorageConnector}.
 */
@Mapper(componentModel = SPRING)
public interface IMotherboardToStorageConnectorMapper {

    /**
     * Конвертирует {@link MotherboardToStorageConnector} в DTO {@link MotherboardToStorageConnectorResponseDto}.
     *
     * @param entity {@link MotherboardToStorageConnector}
     * @return DTO {@link MotherboardToStorageConnectorResponseDto}
     */
    MotherboardToStorageConnectorResponseDto convertToDto(final MotherboardToStorageConnector entity);

    /**
     * Конвертирует DTO {@link MotherboardToStorageConnectorRequestDto} в {@link MotherboardToStorageConnector},
     * игнорируя поле {@code id} и {@code motherboard}.
     *
     * @param dto DTO {@link MotherboardToStorageConnectorRequestDto}
     * @return {@link MotherboardToStorageConnector}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "motherboard", ignore = true)
    @Mapping(source = "storageConnectorId", target = "storageConnector.id")
    MotherboardToStorageConnector convertFromDto(final MotherboardToStorageConnectorRequestDto dto);
}
