package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.dictionary.PciExpressConnectorVersion;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link PciExpressConnectorVersion}.
 */
@Mapper(componentModel = SPRING)
public interface IPciExpressConnectorVersionMapper {

    /**
     * Конвертирует {@link PciExpressConnectorVersion} в DTO {@link NameableResponseDto}.
     *
     * @param entity {@link PciExpressConnectorVersion}
     * @return DTO {@link NameableResponseDto}
     */
    NameableResponseDto convertToDto(final PciExpressConnectorVersion entity);

    /**
     * Конвертирует DTO {@link NameableRequestDto} в {@link PciExpressConnectorVersion},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link NameableRequestDto}
     * @return {@link PciExpressConnectorVersion}
     */
    @Mapping(target = "id", ignore = true)
    PciExpressConnectorVersion convertFromDto(final NameableRequestDto dto);
}
