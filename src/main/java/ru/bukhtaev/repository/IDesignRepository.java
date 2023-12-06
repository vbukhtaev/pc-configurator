package ru.bukhtaev.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.Design;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий вариантов исполнения.
 */
@Repository
public interface IDesignRepository extends JpaRepository<Design, UUID> {

    Slice<Design> findAllBy(final Pageable pageable);

    Optional<Design> findByName(final String name);

    Optional<Design> findByNameAndIdNot(final String name, final UUID id);
}
