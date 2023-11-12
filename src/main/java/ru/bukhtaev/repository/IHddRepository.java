package ru.bukhtaev.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.Hdd;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий жестких дисков.
 */
@Repository
public interface IHddRepository extends JpaRepository<Hdd, UUID> {

    Slice<Hdd> findAllBy(final Pageable pageable);

    @Query("SELECT hdd FROM Hdd hdd " +
            "WHERE hdd.name = :name " +
            "AND hdd.capacity = :capacity " +
            "AND hdd.spindleSpeed = :spindleSpeed " +
            "AND hdd.cacheSize = :cacheSize")
    Optional<Hdd> findTheSame(
            @Param("name") final String name,
            @Param("capacity") final Integer capacity,
            @Param("spindleSpeed") final Integer spindleSpeed,
            @Param("cacheSize") final Integer cacheSize
    );

    @Query("SELECT hdd FROM Hdd hdd " +
            "WHERE hdd.name = :name " +
            "AND hdd.capacity = :capacity " +
            "AND hdd.spindleSpeed = :spindleSpeed " +
            "AND hdd.cacheSize = :cacheSize " +
            "AND hdd.id <> :id")
    Optional<Hdd> findTheSameWithAnotherId(
            @Param("name") final String name,
            @Param("capacity") final Integer capacity,
            @Param("spindleSpeed") final Integer spindleSpeed,
            @Param("cacheSize") final Integer cacheSize,
            @Param("id") final UUID id
    );
}
