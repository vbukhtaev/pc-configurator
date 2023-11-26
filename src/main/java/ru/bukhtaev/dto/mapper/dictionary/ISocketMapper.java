package ru.bukhtaev.dto.mapper.dictionary;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bukhtaev.dto.request.NameableRequestDto;
import ru.bukhtaev.dto.response.NameableResponseDto;
import ru.bukhtaev.model.dictionary.Socket;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link Socket}.
 */
@Mapper(componentModel = SPRING)
public interface ISocketMapper {

    /**
     * Конвертирует {@link Socket} в DTO {@link NameableResponseDto}.
     *
     * @param entity {@link Socket}
     * @return DTO {@link NameableResponseDto}
     */
    NameableResponseDto convertToDto(final Socket entity);

    /**
     * Конвертирует DTO {@link NameableRequestDto} в {@link Socket},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link NameableRequestDto}
     * @return {@link Socket}
     */
    @Mapping(target = "id", ignore = true)
    Socket convertFromDto(final NameableRequestDto dto);
}
