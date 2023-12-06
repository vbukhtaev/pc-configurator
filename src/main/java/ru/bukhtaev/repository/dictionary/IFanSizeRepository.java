package ru.bukhtaev.repository.dictionary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.dictionary.FanSize;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий размеров вентиляторов.
 */
@Repository
public interface IFanSizeRepository extends JpaRepository<FanSize, UUID> {

    Optional<FanSize> findByLengthAndWidthAndHeight(
            final Integer length,
            final Integer width,
            final Integer height
    );

    Optional<FanSize> findByLengthAndWidthAndHeightAndIdNot(
            final Integer length,
            final Integer width,
            final Integer height,
            final UUID id
    );
}
