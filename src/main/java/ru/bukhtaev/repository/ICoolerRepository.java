package ru.bukhtaev.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.Cooler;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий процессорных кулеров.
 */
@Repository
public interface ICoolerRepository extends JpaRepository<Cooler, UUID> {

    Slice<Cooler> findAllBy(final Pageable pageable);

    Optional<Cooler> findByName(final String name);

    Optional<Cooler> findByNameAndIdNot(final String name, final UUID id);
}
