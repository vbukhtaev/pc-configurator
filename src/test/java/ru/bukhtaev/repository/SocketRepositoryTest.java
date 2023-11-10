package ru.bukhtaev.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.Socket;
import ru.bukhtaev.util.NameableSort;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.bukhtaev.TestUtils.NAMEABLE_PAGEABLE;

/**
 * Модульные тесты репозитория сокетов.
 */
@DataJpaTest
class SocketRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий сокетов.
     */
    @Autowired
    private ISocketRepository underTest;

    private Socket socketLga1700;
    private Socket socketAm5;

    @BeforeEach
    void setUp() {
        socketLga1700 = Socket.builder()
                .name("LGA 1700")
                .build();
        socketAm5 = Socket.builder()
                .name("AM5")
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findAllBy_withAllEntitiesAtPage_shouldReturnAllEntitiesAsPage() {
        // given
        underTest.save(socketLga1700);
        underTest.save(socketAm5);

        // when
        final Slice<Socket> sockets = underTest.findAllBy(NAMEABLE_PAGEABLE);

        // then
        Assertions.assertThat(sockets.getSize())
                .isEqualTo(NAMEABLE_PAGEABLE.getPageSize());
        Assertions.assertThat(sockets.getNumberOfElements())
                .isEqualTo(2);
        Assertions.assertThat(sockets.getContent().get(0).getName())
                .isEqualTo(socketAm5.getName());
        Assertions.assertThat(sockets.getContent().get(1).getName())
                .isEqualTo(socketLga1700.getName());
    }

    @Test
    void findAllBy_withNotAllEntitiesAtPage_shouldReturnNotAllEntitiesAsPage() {
        // given
        underTest.save(socketLga1700);
        underTest.save(socketAm5);
        final Pageable singleElementPageable = PageRequest.of(
                0,
                1,
                NameableSort.NAME_ASC.getSortValue()
        );

        // when
        final Slice<Socket> sockets = underTest.findAllBy(singleElementPageable);

        // then
        Assertions.assertThat(sockets.getSize())
                .isEqualTo(singleElementPageable.getPageSize());
        Assertions.assertThat(sockets.getNumberOfElements())
                .isEqualTo(1);
        Assertions.assertThat(sockets.getContent().get(0).getName())
                .isEqualTo(socketAm5.getName());
    }

    @Test
    void findByName_withExistentName_shouldReturnFoundEntity() {
        // given
        final Socket saved = underTest.save(socketAm5);
        underTest.save(socketLga1700);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optSocket = underTest.findByName(socketAm5.getName());

        // then
        assertThat(optSocket).isPresent();
        assertThat(optSocket.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optSocket.get().getName())
                .isEqualTo(socketAm5.getName());
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "X99";
        underTest.save(socketAm5);
        underTest.save(socketLga1700);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optSocket = underTest.findByName(anotherName);

        // then
        assertThat(optSocket).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndNonExistentId_shouldReturnFoundEntity() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final Socket saved = underTest.save(socketLga1700);
        underTest.save(socketAm5);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optSocket = underTest.findByNameAndIdNot(
                socketLga1700.getName(),
                anotherId
        );

        // then
        assertThat(optSocket).isPresent();
        assertThat(optSocket.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optSocket.get().getName())
                .isEqualTo(socketLga1700.getName());
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final Socket saved = underTest.save(socketLga1700);
        underTest.save(socketAm5);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optSocket = underTest.findByNameAndIdNot(
                socketLga1700.getName(),
                saved.getId()
        );

        // then
        assertThat(optSocket).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "X99";
        underTest.save(socketAm5);
        underTest.save(socketLga1700);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optSocket = underTest.findByNameAndIdNot(
                anotherName,
                anotherId
        );

        // then
        assertThat(optSocket).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "X99";
        final Socket saved = underTest.save(socketAm5);
        underTest.save(socketLga1700);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optSocket = underTest.findByNameAndIdNot(
                anotherName,
                saved.getId()
        );

        // then
        assertThat(optSocket).isNotPresent();
    }
}