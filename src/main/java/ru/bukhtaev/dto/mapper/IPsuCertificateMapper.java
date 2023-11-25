package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.dictionary.PsuCertificate;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link PsuCertificate}.
 */
@Mapper(componentModel = SPRING)
public interface IPsuCertificateMapper {

    /**
     * Конвертирует {@link PsuCertificate} в DTO {@link NameableResponseDto}.
     *
     * @param entity {@link PsuCertificate}
     * @return DTO {@link NameableResponseDto}
     */
    NameableResponseDto convertToDto(final PsuCertificate entity);

    /**
     * Конвертирует DTO {@link NameableRequestDto} в {@link PsuCertificate},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link NameableRequestDto}
     * @return {@link PsuCertificate}
     */
    @Mapping(target = "id", ignore = true)
    PsuCertificate convertFromDto(final NameableRequestDto dto);
}
