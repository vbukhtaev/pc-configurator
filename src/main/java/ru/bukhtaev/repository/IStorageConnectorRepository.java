package ru.bukhtaev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.StorageConnector;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий коннекторов подключения накопителей.
 */
@Repository
public interface IStorageConnectorRepository extends JpaRepository<StorageConnector, UUID> {

    Optional<StorageConnector> findByName(final String name);

    Optional<StorageConnector> findByNameAndIdNot(final String name, final UUID id);
}
