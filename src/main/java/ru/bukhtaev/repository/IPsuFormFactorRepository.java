package ru.bukhtaev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.PsuFormFactor;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий форм-факторов блоков питания.
 */
@Repository
public interface IPsuFormFactorRepository extends JpaRepository<PsuFormFactor, UUID> {

    Optional<PsuFormFactor> findByName(final String name);

    Optional<PsuFormFactor> findByNameAndIdNot(final String name, final UUID id);
}
