package ru.bukhtaev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.GraphicsCardPowerConnector;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий коннекторов питания видеокарты.
 */
@Repository
public interface IGraphicsCardPowerConnectorRepository extends JpaRepository<GraphicsCardPowerConnector, UUID> {

    Optional<GraphicsCardPowerConnector> findByName(final String name);

    Optional<GraphicsCardPowerConnector> findByNameAndIdNot(final String name, final UUID id);
}
