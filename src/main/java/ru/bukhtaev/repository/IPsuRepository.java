package ru.bukhtaev.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.Psu;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий блоков питания.
 */
@Repository
public interface IPsuRepository extends JpaRepository<Psu, UUID> {

    Slice<Psu> findAllBy(final Pageable pageable);

    Optional<Psu> findByName(final String name);

    Optional<Psu> findByNameAndIdNot(final String name, final UUID id);
}
