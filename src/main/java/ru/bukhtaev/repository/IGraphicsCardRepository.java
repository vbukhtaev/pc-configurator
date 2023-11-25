package ru.bukhtaev.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.Design;
import ru.bukhtaev.model.Gpu;
import ru.bukhtaev.model.GraphicsCard;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий видеокарт.
 */
@Repository
public interface IGraphicsCardRepository extends JpaRepository<GraphicsCard, UUID> {

    Slice<GraphicsCard> findAllBy(final Pageable pageable);

    @Query("SELECT card FROM GraphicsCard card " +
            "WHERE card.gpu.name = :#{#gpu.name} " +
            "AND card.gpu.memorySize = :#{#gpu.memorySize} " +
            "AND card.gpu.memoryType.name = :#{#gpu.memoryType.name} " +
            "AND card.design.name = :#{#design.name}")
    Optional<GraphicsCard> findTheSame(
            @Param("gpu") final Gpu gpu,
            @Param("design") final Design design
    );

    @Query("SELECT card FROM GraphicsCard card " +
            "WHERE card.gpu.name = :#{#gpu.name} " +
            "AND card.gpu.memorySize = :#{#gpu.memorySize} " +
            "AND card.gpu.memoryType.name = :#{#gpu.memoryType.name} " +
            "AND card.design.name = :#{#design.name} " +
            "AND card.id <> :id")
    Optional<GraphicsCard> findTheSameWithAnotherId(
            @Param("gpu") final Gpu gpu,
            @Param("design") final Design design,
            @Param("id") final UUID id
    );
}
