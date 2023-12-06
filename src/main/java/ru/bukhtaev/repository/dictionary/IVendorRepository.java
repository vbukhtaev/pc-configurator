package ru.bukhtaev.repository.dictionary;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.dictionary.Vendor;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий вендоров.
 */
@Repository
public interface IVendorRepository extends JpaRepository<Vendor, UUID> {

    Slice<Vendor> findAllBy(final Pageable pageable);

    Optional<Vendor> findByName(final String name);

    Optional<Vendor> findByNameAndIdNot(final String name, final UUID id);
}
