package ru.bukhtaev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.dictionary.FanPowerConnector;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий коннекторов питания вентиляторов.
 */
@Repository
public interface IFanPowerConnectorRepository extends JpaRepository<FanPowerConnector, UUID> {

    Optional<FanPowerConnector> findByName(final String name);

    Optional<FanPowerConnector> findByNameAndIdNot(final String name, final UUID id);
}
