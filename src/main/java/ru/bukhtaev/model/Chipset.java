package ru.bukhtaev.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Модель чипсета.
 */
@Getter
@Setter
@Entity
@Table(
        name = "chipset",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@SuperBuilder
@NoArgsConstructor
public class Chipset extends NameableEntity {

    /**
     * Название поля, хранящего сокет.
     */
    public static final String FIELD_SOCKET = "socket";

    /**
     * Сокет.
     */
    @ManyToOne
    @JoinColumn(name = "socket_id", referencedColumnName = "id", nullable = false)
    protected Socket socket;
}
