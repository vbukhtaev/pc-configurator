package ru.bukhtaev.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.Chipset;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий чипсетов.
 */
@Repository
public interface IChipsetRepository extends JpaRepository<Chipset, UUID> {

    Slice<Chipset> findAllBy(final Pageable pageable);

    Optional<Chipset> findByName(final String name);

    Optional<Chipset> findByNameAndIdNot(final String name, final UUID id);
}
