package ru.bukhtaev.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.Fan;
import ru.bukhtaev.model.dictionary.FanSize;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий вентиляторов.
 */
@Repository
public interface IFanRepository extends JpaRepository<Fan, UUID> {

    Slice<Fan> findAllBy(final Pageable pageable);

    @Query("SELECT f FROM Fan f " +
            "WHERE f.name = :name " +
            "AND f.size.length = :#{#size.length} " +
            "AND f.size.width = :#{#size.width} " +
            "AND f.size.height = :#{#size.height}")
    Optional<Fan> findTheSame(
            @Param("name") final String name,
            @Param("size") final FanSize size
    );

    @Query("SELECT f FROM Fan f " +
            "WHERE f.name = :name " +
            "AND f.size.length = :#{#size.length} " +
            "AND f.size.width = :#{#size.width} " +
            "AND f.size.height = :#{#size.height} " +
            "AND f.id <> :id")
    Optional<Fan> findTheSameWithAnotherId(
            @Param("name") final String name,
            @Param("size") final FanSize size,
            @Param("id") final UUID id
    );
}
