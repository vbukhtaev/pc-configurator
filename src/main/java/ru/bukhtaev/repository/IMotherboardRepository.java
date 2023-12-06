package ru.bukhtaev.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.Chipset;
import ru.bukhtaev.model.Design;
import ru.bukhtaev.model.Motherboard;
import ru.bukhtaev.model.dictionary.RamType;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий материнских плат.
 */
@Repository
public interface IMotherboardRepository extends JpaRepository<Motherboard, UUID> {

    Slice<Motherboard> findAllBy(final Pageable pageable);

    @Query("SELECT mb FROM Motherboard mb " +
            "WHERE mb.name = :name " +
            "AND mb.design.name = :#{#design.name} " +
            "AND mb.chipset.name = :#{#chipset.name} " +
            "AND mb.ramType.name = :#{#ramType.name}")
    Optional<Motherboard> findTheSame(
            @Param("name") final String name,
            @Param("design") final Design design,
            @Param("chipset") final Chipset chipset,
            @Param("ramType") final RamType ramType
    );

    @Query("SELECT mb FROM Motherboard mb " +
            "WHERE mb.name = :name " +
            "AND mb.design.name = :#{#design.name} " +
            "AND mb.chipset.name = :#{#chipset.name} " +
            "AND mb.ramType.name = :#{#ramType.name} " +
            "AND mb.id <> :id")
    Optional<Motherboard> findTheSameWithAnotherId(
            @Param("name") final String name,
            @Param("design") final Design design,
            @Param("chipset") final Chipset chipset,
            @Param("ramType") final RamType ramType,
            @Param("id") final UUID id
    );
}
