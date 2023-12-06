package ru.bukhtaev.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.ComputerBuild;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий сборок ПК.
 */
@Repository
public interface IComputerBuildRepository extends JpaRepository<ComputerBuild, UUID> {

    Slice<ComputerBuild> findAllBy(final Pageable pageable);

    Optional<ComputerBuild> findByName(final String name);

    Optional<ComputerBuild> findByNameAndIdNot(final String name, final UUID id);
}
