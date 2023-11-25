package ru.bukhtaev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.dictionary.PciExpressConnectorVersion;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий версий коннектора PCI-Express.
 */
@Repository
public interface IPciExpressConnectorVersionRepository extends JpaRepository<PciExpressConnectorVersion, UUID> {

    Optional<PciExpressConnectorVersion> findByName(final String name);

    Optional<PciExpressConnectorVersion> findByNameAndIdNot(final String name, final UUID id);
}
