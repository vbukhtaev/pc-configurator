package ru.bukhtaev.repository.dictionary;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.dictionary.Socket;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий сокетов.
 */
@Repository
public interface ISocketRepository extends JpaRepository<Socket, UUID> {

    Slice<Socket> findAllBy(final Pageable pageable);

    Optional<Socket> findByName(final String name);

    Optional<Socket> findByNameAndIdNot(final String name, final UUID id);
}
