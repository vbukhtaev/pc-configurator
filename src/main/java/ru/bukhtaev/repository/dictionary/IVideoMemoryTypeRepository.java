package ru.bukhtaev.repository.dictionary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bukhtaev.model.dictionary.VideoMemoryType;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-репозиторий типов видеопамяти.
 */
@Repository
public interface IVideoMemoryTypeRepository extends JpaRepository<VideoMemoryType, UUID> {

    Optional<VideoMemoryType> findByName(final String name);

    Optional<VideoMemoryType> findByNameAndIdNot(final String name, final UUID id);
}
