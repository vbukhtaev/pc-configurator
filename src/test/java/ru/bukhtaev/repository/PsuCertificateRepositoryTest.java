package ru.bukhtaev.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.bukhtaev.AbstractContainerizedTest;
import ru.bukhtaev.model.dictionary.PsuCertificate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Модульные тесты репозитория сертификатов блоков питания.
 */
@DataJpaTest
class PsuCertificateRepositoryTest extends AbstractContainerizedTest {

    /**
     * Тестируемый репозиторий сертификатов блоков питания.
     */
    @Autowired
    private IPsuCertificateRepository underTest;

    private PsuCertificate certificateBronze;
    private PsuCertificate certificateGold;

    @BeforeEach
    void setUp() {
        certificateBronze = PsuCertificate.builder()
                .name("Bronze")
                .build();
        certificateGold = PsuCertificate.builder()
                .name("Gold")
                .build();
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByName_withExistentName_shouldReturnFoundEntity() {
        // given
        final var saved = underTest.save(certificateGold);
        underTest.save(certificateBronze);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optCertificate = underTest.findByName(certificateGold.getName());

        // then
        assertThat(optCertificate).isPresent();
        assertThat(optCertificate.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optCertificate.get().getName())
                .isEqualTo(certificateGold.getName());
    }

    @Test
    void findByName_withNonExistentName_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "Titanium";
        underTest.save(certificateGold);
        underTest.save(certificateBronze);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optCertificate = underTest.findByName(anotherName);

        // then
        assertThat(optCertificate).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndNonExistentId_shouldReturnFoundEntity() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final var saved = underTest.save(certificateBronze);
        underTest.save(certificateGold);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optCertificate = underTest.findByNameAndIdNot(
                certificateBronze.getName(),
                anotherId
        );

        // then
        assertThat(optCertificate).isPresent();
        assertThat(optCertificate.get().getId())
                .isEqualTo(saved.getId());
        assertThat(optCertificate.get().getName())
                .isEqualTo(certificateBronze.getName());
    }

    @Test
    void findByNameAndIdNot_withExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final var saved = underTest.save(certificateBronze);
        underTest.save(certificateGold);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optCertificate = underTest.findByNameAndIdNot(
                certificateBronze.getName(),
                saved.getId()
        );

        // then
        assertThat(optCertificate).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndNonExistentId_shouldReturnEmptyOptional() {
        // given
        final UUID anotherId = UUID.randomUUID();
        final String anotherName = "Titanium";
        underTest.save(certificateGold);
        underTest.save(certificateBronze);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optCertificate = underTest.findByNameAndIdNot(
                anotherName,
                anotherId
        );

        // then
        assertThat(optCertificate).isNotPresent();
    }

    @Test
    void findByNameAndIdNot_withNonExistentNameAndExistentId_shouldReturnEmptyOptional() {
        // given
        final String anotherName = "Titanium";
        final var saved = underTest.save(certificateGold);
        underTest.save(certificateBronze);
        assertThat(underTest.findAll()).hasSize(2);

        // when
        final var optCertificate = underTest.findByNameAndIdNot(
                anotherName,
                saved.getId()
        );

        // then
        assertThat(optCertificate).isNotPresent();
    }
}
