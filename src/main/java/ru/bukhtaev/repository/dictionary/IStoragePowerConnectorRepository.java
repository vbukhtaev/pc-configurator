package ru.bukhtaev.repository.dictionary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.dictionary.StoragePowerConnector;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий коннекторов питания накопителей.
 */
@Repository
public interface IStoragePowerConnectorRepository extends JpaRepository<StoragePowerConnector, UUID> {

    Optional<StoragePowerConnector> findByName(final String name);

    Optional<StoragePowerConnector> findByNameAndIdNot(final String name, final UUID id);
}
