package ru.bukhtaev.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Ssd;

/**
 * DTO для модели {@link Ssd}, используемый в качестве тела HTTP-запроса.
 */
@Schema(description = "SSD накопитель")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class SsdRequestDto extends StorageDeviceRequestDto {
}
