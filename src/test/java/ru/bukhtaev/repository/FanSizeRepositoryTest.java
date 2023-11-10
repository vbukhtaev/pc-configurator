package ru.bukhtaev.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.FanSize;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Модульные тесты репозитория размеров вентиляторов.
 */
@DataJpaTest
class FanSizeRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий размеров вентиляторов.
     */
    @Autowired
    private IFanSizeRepository underTest;

    private FanSize size120;
    private FanSize size140;

    @BeforeEach
    void setUp() {
        size120 = FanSize.builder()
                .length(120)
                .width(120)
                .height(25)
                .build();
        size140 = FanSize.builder()
                .length(140)
                .width(140)
                .height(25)
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByName_withExistentName_shouldReturnFoundEntity() {
        // given
        final var saved = underTest.save(size140);
        underTest.save(size120);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optSize = underTest.findByLengthAndWidthAndHeight(
                size140.getLength(),
                size140.getWidth(),
                size140.getHeight()
        );

        // then
        assertThat(optSize).isPresent();
        assertThat(optSize.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optSize.get().getLength())
                .isEqualTo(size140.getLength());
        assertThat(optSize.get().getWidth())
                .isEqualTo(size140.getWidth());
        assertThat(optSize.get().getHeight())
                .isEqualTo(size140.getHeight());
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final Integer anotherLength = 90;
        final Integer anotherWidth = 90;
        final Integer anotherHeight = 25;
        underTest.save(size140);
        underTest.save(size120);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optSize = underTest.findByLengthAndWidthAndHeight(
                anotherLength,
                anotherWidth,
                anotherHeight
        );

        // then
        assertThat(optSize).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndNonExistentId_shouldReturnFoundEntity() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final var saved = underTest.save(size120);
        underTest.save(size140);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optSize = underTest.findByLengthAndWidthAndHeightAndIdNot(
                size120.getLength(),
                size120.getWidth(),
                size120.getHeight(),
                anotherId
        );

        // then
        assertThat(optSize).isPresent();
        assertThat(optSize.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optSize.get().getLength())
                .isEqualTo(size120.getLength());
        assertThat(optSize.get().getWidth())
                .isEqualTo(size120.getWidth());
        assertThat(optSize.get().getHeight())
                .isEqualTo(size120.getHeight());
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final var saved = underTest.save(size120);
        underTest.save(size140);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optSize = underTest.findByLengthAndWidthAndHeightAndIdNot(
                size120.getLength(),
                size120.getWidth(),
                size120.getHeight(),
                saved.getId()
        );

        // then
        assertThat(optSize).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final Integer anotherLength = 90;
        final Integer anotherWidth = 90;
        final Integer anotherHeight = 25;
        underTest.save(size140);
        underTest.save(size120);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optSize = underTest.findByLengthAndWidthAndHeightAndIdNot(
                anotherLength,
                anotherWidth,
                anotherHeight,
                anotherId
        );

        // then
        assertThat(optSize).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final Integer anotherLength = 90;
        final Integer anotherWidth = 90;
        final Integer anotherHeight = 25;
        final var saved = underTest.save(size140);
        underTest.save(size120);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optSize = underTest.findByLengthAndWidthAndHeightAndIdNot(
                anotherLength,
                anotherWidth,
                anotherHeight,
                saved.getId()
        );

        // then
        assertThat(optSize).isNotPresent();
    }
}
