package ru.bukhtaev.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.Design;
import ru.bukhtaev.model.RamModule;
import ru.bukhtaev.model.RamType;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий модулей оперативной памяти.
 */
@Repository
public interface IRamModuleRepository extends JpaRepository<RamModule, UUID> {

    Slice<RamModule> findAllBy(final Pageable pageable);

    @Query("SELECT module FROM RamModule module " +
            "WHERE module.clock = :clock " +
            "AND module.capacity = :capacity " +
            "AND module.type.name = :#{#type.name} " +
            "AND module.design.name = :#{#design.name}")
    Optional<RamModule> findTheSame(
            @Param("clock") final Integer clock,
            @Param("capacity") final Integer capacity,
            @Param("type") final RamType type,
            @Param("design") final Design design
    );

    @Query("SELECT module FROM RamModule module " +
            "WHERE module.clock = :clock " +
            "AND module.capacity = :capacity " +
            "AND module.type.name = :#{#type.name} " +
            "AND module.design.name = :#{#design.name} " +
            "AND module.id <> :id")
    Optional<RamModule> findTheSameWithAnotherId(
            @Param("clock") final Integer clock,
            @Param("capacity") final Integer capacity,
            @Param("type") final RamType type,
            @Param("design") final Design design,
            @Param("id") final UUID id
    );
}
