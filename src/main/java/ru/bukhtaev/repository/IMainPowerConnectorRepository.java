package ru.bukhtaev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.dictionary.MainPowerConnector;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий основных коннекторов питания.
 */
@Repository
public interface IMainPowerConnectorRepository extends JpaRepository<MainPowerConnector, UUID> {

    Optional<MainPowerConnector> findByName(final String name);

    Optional<MainPowerConnector> findByNameAndIdNot(final String name, final UUID id);
}
