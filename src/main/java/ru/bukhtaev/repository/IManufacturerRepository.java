package ru.bukhtaev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.dictionary.Manufacturer;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий производителей.
 */
@Repository
public interface IManufacturerRepository extends JpaRepository<Manufacturer, UUID> {

    Optional<Manufacturer> findByName(final String name);

    Optional<Manufacturer> findByNameAndIdNot(final String name, final UUID id);
}
