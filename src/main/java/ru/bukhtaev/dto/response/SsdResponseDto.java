package ru.bukhtaev.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Ssd;

/**
 * DTO для модели {@link Ssd}, используемый в качестве тела HTTP-ответа.
 */
@Schema(description = "SSD накопитель")
@Getter
@SuperBuilder
public class SsdResponseDto extends StorageDeviceResponseDto {
}
