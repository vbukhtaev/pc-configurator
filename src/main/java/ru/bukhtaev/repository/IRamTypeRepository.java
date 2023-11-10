package ru.bukhtaev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.RamType;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий типов оперативной памяти.
 */
@Repository
public interface IRamTypeRepository extends JpaRepository<RamType, UUID> {

    Optional<RamType> findByName(final String name);

    Optional<RamType> findByNameAndIdNot(final String name, final UUID id);
}
