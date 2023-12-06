package ru.bukhtaev.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.bukhtaev.dto.mapper.cross.IComputerCaseToExpansionBayFormatMapper;
import ru.bukhtaev.dto.mapper.cross.IComputerCaseToFanSizeMapper;
import ru.bukhtaev.dto.request.ComputerCaseRequestDto;
import ru.bukhtaev.dto.response.ComputerCaseResponseDto;
import ru.bukhtaev.model.ComputerCase;
import ru.bukhtaev.model.dictionary.MotherboardFormFactor;
import ru.bukhtaev.model.dictionary.PsuFormFactor;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Маппер для объектов типа {@link ComputerCase}.
 */
@Mapper(
        componentModel = SPRING,
        uses = {
                IComputerCaseToExpansionBayFormatMapper.class,
                IComputerCaseToFanSizeMapper.class
        }
)
public interface IComputerCaseMapper {

    /**
     * Конвертирует {@link ComputerCase} в DTO {@link ComputerCaseResponseDto}.
     *
     * @param entity {@link ComputerCase}
     * @return DTO {@link ComputerCaseResponseDto}
     */
    ComputerCaseResponseDto convertToDto(final ComputerCase entity);

    /**
     * Конвертирует DTO {@link ComputerCaseRequestDto} в {@link ComputerCase},
     * игнорируя поле {@code id}.
     *
     * @param dto DTO {@link ComputerCaseRequestDto}
     * @return {@link ComputerCase}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "vendorId", target = "vendor.id")
    @Mapping(
            source = "psuFormFactorIds",
            target = "psuFormFactors",
            qualifiedByName = "toPsuFormFactorSet"
    )
    @Mapping(
            source = "motherboardFormFactorIds",
            target = "motherboardFormFactors",
            qualifiedByName = "toMotherboardFormFactorSet"
    )
    ComputerCase convertFromDto(final ComputerCaseRequestDto dto);

    @Named("toPsuFormFactorSet")
    static Set<PsuFormFactor> toPsuFormFactorSet(Set<UUID> psuFormFactorIds) {
        if (psuFormFactorIds == null) {
            return null;
        }

        return psuFormFactorIds.stream()
                .map(formFactorId -> PsuFormFactor.builder()
                        .id(formFactorId)
                        .build())
                .collect(Collectors.toSet());
    }

    @Named("toMotherboardFormFactorSet")
    static Set<MotherboardFormFactor> toMotherboardFormFactorSet(Set<UUID> motherboardFormFactorIds) {
        if (motherboardFormFactorIds == null) {
            return null;
        }

        return motherboardFormFactorIds.stream()
                .map(formFactorId -> MotherboardFormFactor.builder()
                        .id(formFactorId)
                        .build())
                .collect(Collectors.toSet());
    }
}
