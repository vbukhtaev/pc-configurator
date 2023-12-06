package ru.bukhtaev.dto.mapper.dictionary;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.bukhtaev.dto.request.dictionary.PciExpressConnectorVersionRequestDto;
import ru.bukhtaev.dto.response.dictionary.PciExpressConnectorVersionResponseDto;
import ru.bukhtaev.model.dictionary.PciExpressConnectorVersion;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link PciExpressConnectorVersion}.
 */
@Mapper(componentModel = SPRING)
public interface IPciExpressConnectorVersionMapper {

    /**
     * Конвертирует {@link PciExpressConnectorVersion} в DTO {@link PciExpressConnectorVersionResponseDto}.
     *
     * @param entity {@link PciExpressConnectorVersion}
     * @return DTO {@link PciExpressConnectorVersionResponseDto}
     */
    @Mapping(
            source = "lowerVersions",
            target = "lowerVersions",
            qualifiedByName = "versionSetToVersionSet"
    )
    PciExpressConnectorVersionResponseDto convertToDto(final PciExpressConnectorVersion entity);

    /**
     * Конвертирует DTO {@link PciExpressConnectorVersionRequestDto} в {@link PciExpressConnectorVersion},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link PciExpressConnectorVersionRequestDto}
     * @return {@link PciExpressConnectorVersion}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(
            source = "lowerVersionIds",
            target = "lowerVersions",
            qualifiedByName = "uuidSetToVersionSet"
    )
    PciExpressConnectorVersion convertFromDto(final PciExpressConnectorVersionRequestDto dto);

    @Named("uuidSetToVersionSet")
    static Set<PciExpressConnectorVersion> uuidSetToVersionSet(Set<UUID> versionIds) {
        if (versionIds == null) {
            return null;
        }

        return versionIds.stream()
                .map(versionId -> PciExpressConnectorVersion.builder()
                        .id(versionId)
                        .build())
                .collect(Collectors.toSet());
    }

    @Named("versionSetToVersionSet")
    static Set<PciExpressConnectorVersionResponseDto> versionSetToVersionSet(Set<PciExpressConnectorVersion> versions) {
        if (versions == null) {
            return null;
        }

        return versions.stream()
                .map(version -> PciExpressConnectorVersionResponseDto.builder()
                        .id(version.getId())
                        .name(version.getName())
                        .build())
                .collect(Collectors.toSet());
    }
}
