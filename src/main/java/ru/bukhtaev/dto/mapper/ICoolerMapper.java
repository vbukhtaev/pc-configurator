package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.bukhtaev.dto.request.CoolerRequestDto;
import ru.bukhtaev.dto.response.CoolerResponseDto;
import ru.bukhtaev.model.Cooler;
import ru.bukhtaev.model.dictionary.Socket;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link Cooler}.
 */
@Mapper(componentModel = SPRING)
public interface ICoolerMapper {

    /**
     * Конвертирует {@link Cooler} в DTO {@link CoolerResponseDto}.
     *
     * @param entity {@link Cooler}
     * @return DTO {@link CoolerResponseDto}
     */
    CoolerResponseDto convertToDto(final Cooler entity);

    /**
     * Конвертирует DTO {@link CoolerRequestDto} в {@link Cooler},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link CoolerRequestDto}
     * @return {@link Cooler}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "vendorId", target = "vendor.id")
    @Mapping(source = "fanSizeId", target = "fanSize.id")
    @Mapping(source = "powerConnectorId", target = "powerConnector.id")
    @Mapping(source = "supportedSocketIds", target = "supportedSockets", qualifiedByName = "toSocketSet")
    Cooler convertFromDto(final CoolerRequestDto dto);

    @Named("toSocketSet")
    static Set<Socket> socketIdSetToSocketSet(Set<UUID> socketIds) {
        if (socketIds == null) {
            return null;
        }

        return socketIds.stream()
                .map(socketId -> Socket.builder()
                        .id(socketId)
                        .build())
                .collect(Collectors.toSet());
    }
}
