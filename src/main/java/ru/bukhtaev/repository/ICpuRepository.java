package ru.bukhtaev.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.Cpu;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий процессоров.
 */
@Repository
public interface ICpuRepository extends JpaRepository<Cpu, UUID> {

    Slice<Cpu> findAllBy(final Pageable pageable);

    Optional<Cpu> findByName(final String name);

    Optional<Cpu> findByNameAndIdNot(final String name, final UUID id);
}
