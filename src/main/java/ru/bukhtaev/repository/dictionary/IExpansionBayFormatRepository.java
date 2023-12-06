package ru.bukhtaev.repository.dictionary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.dictionary.ExpansionBayFormat;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий форматов отсеков расширения.
 */
@Repository
public interface IExpansionBayFormatRepository extends JpaRepository<ExpansionBayFormat, UUID> {

    Optional<ExpansionBayFormat> findByName(final String name);

    Optional<ExpansionBayFormat> findByNameAndIdNot(final String name, final UUID id);
}
