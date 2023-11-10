package ru.bukhtaev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.MotherboardFormFactor;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий форм-факторов материнских плат.
 */
@Repository
public interface IMotherboardFormFactorRepository extends JpaRepository<MotherboardFormFactor, UUID> {

    Optional<MotherboardFormFactor> findByName(final String name);

    Optional<MotherboardFormFactor> findByNameAndIdNot(final String name, final UUID id);
}
