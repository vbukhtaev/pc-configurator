package ru.bukhtaev.model.cross;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.Motherboard;
import ru.bukhtaev.model.dictionary.StorageConnector;

import java.util.UUID;

/**
 * Модель имеющихся у материнской платы коннекторов подключения накопителей.
 */
@Getter
@Setter
@Entity
@Table(
        name = "motherboard_to_storage_connector",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "motherboard_id",
                "storage_connector_id"
        })
)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MotherboardToStorageConnector {

    /**
     * Название поля, хранящего материнскую плату.
     */
    public static final String FIELD_MOTHERBOARD = "motherboard";

    /**
     * Название поля, хранящего коннектор подключения накопителя.
     */
    public static final String FIELD_STORAGE_CONNECTOR = "storageConnector";

    /**
     * Название поля, хранящего количество коннекторов подключения накопителей.
     */
    public static final String FIELD_COUNT = "count";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;

    /**
     * Материнская плата.
     */
    @ManyToOne
    @JoinColumn(name = "motherboard_id", referencedColumnName = "id", nullable = false)
    protected Motherboard motherboard;

    /**
     * Коннектор подключения накопителя.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "storage_connector_id", referencedColumnName = "id", nullable = false)
    protected StorageConnector storageConnector;

    /**
     * Количество.
     */
    @Min(1)
    @NotNull
    @Column(name = "count", nullable = false)
    protected Integer count;
}
