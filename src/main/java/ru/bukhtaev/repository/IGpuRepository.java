package ru.bukhtaev.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.Gpu;
import ru.bukhtaev.model.dictionary.VideoMemoryType;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий графических процессоров.
 */
@Repository
public interface IGpuRepository extends JpaRepository<Gpu, UUID> {

    Slice<Gpu> findAllBy(final Pageable pageable);

    @Query("SELECT gpu FROM Gpu gpu " +
            "WHERE gpu.name = :name " +
            "AND gpu.memorySize = :memorySize " +
            "AND gpu.memoryType.name = :#{#memoryType.name}")
    Optional<Gpu> findTheSame(
            @Param("name") final String name,
            @Param("memorySize") final Integer memorySize,
            @Param("memoryType") final VideoMemoryType memoryType
    );

    @Query("SELECT gpu FROM Gpu gpu " +
            "WHERE gpu.name = :name " +
            "AND gpu.memorySize = :memorySize " +
            "AND gpu.memoryType.name = :#{#memoryType.name} " +
            "AND gpu.id <> :id")
    Optional<Gpu> findTheSameWithAnotherId(
            @Param("name") final String name,
            @Param("memorySize") final Integer memorySize,
            @Param("memoryType") final VideoMemoryType memoryType,
            @Param("id") final UUID id
    );
}
