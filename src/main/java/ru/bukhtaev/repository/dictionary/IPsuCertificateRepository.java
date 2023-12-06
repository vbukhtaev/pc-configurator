package ru.bukhtaev.repository.dictionary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.dictionary.PsuCertificate;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий сертификатов блоков питания.
 */
@Repository
public interface IPsuCertificateRepository extends JpaRepository<PsuCertificate, UUID> {

    Optional<PsuCertificate> findByName(final String name);

    Optional<PsuCertificate> findByNameAndIdNot(final String name, final UUID id);
}
