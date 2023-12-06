package ru.bukhtaev.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.ComputerCase;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий корпусов.
 */
@Repository
public interface IComputerCaseRepository extends JpaRepository<ComputerCase, UUID> {

    Slice<ComputerCase> findAllBy(final Pageable pageable);

    Optional<ComputerCase> findByName(final String name);

    Optional<ComputerCase> findByNameAndIdNot(final String name, final UUID id);
}
