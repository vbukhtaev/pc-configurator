package ru.bukhtaev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.CpuPowerConnector;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий коннекторов питания процессора.
 */
@Repository
public interface ICpuPowerConnectorRepository extends JpaRepository<CpuPowerConnector, UUID> {

    Optional<CpuPowerConnector> findByName(final String name);

    Optional<CpuPowerConnector> findByNameAndIdNot(final String name, final UUID id);
}
