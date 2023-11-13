package ru.bukhtaev.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.Ssd;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий SSD накопителей.
 */
@Repository
public interface ISsdRepository extends JpaRepository<Ssd, UUID> {

    Slice<Ssd> findAllBy(final Pageable pageable);

    @Query("SELECT ssd FROM Ssd ssd " +
            "WHERE ssd.name = :name " +
            "AND ssd.capacity = :capacity")
    Optional<Ssd> findTheSame(
            @Param("name") final String name,
            @Param("capacity") final Integer capacity
    );

    @Query("SELECT ssd FROM Ssd ssd " +
            "WHERE ssd.name = :name " +
            "AND ssd.capacity = :capacity " +
            "AND ssd.id <> :id")
    Optional<Ssd> findTheSameWithAnotherId(
            @Param("name") final String name,
            @Param("capacity") final Integer capacity,
            @Param("id") final UUID id
    );
}
