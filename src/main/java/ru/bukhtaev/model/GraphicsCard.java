package ru.bukhtaev.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.bukhtaev.model.cross.GraphicsCardToPowerConnector;
import ru.bukhtaev.model.dictionary.GraphicsCardPowerConnector;
import ru.bukhtaev.model.dictionary.PciExpressConnectorVersion;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Модель видеокарты.
 */
@Getter
@Setter
@Entity
@Table(
        name = "graphics_card",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "gpu_id",
                "design_id"
        })
)
@SuperBuilder
@NoArgsConstructor
public class GraphicsCard extends BaseEntity {

    /**
     * Название поля, хранящего длину.
     */
    public static final String FIELD_LENGTH = "length";

    /**
     * Название поля, хранящего графический процессор.
     */
    public static final String FIELD_GPU = "gpu";

    /**
     * Название поля, хранящего вариант исполнения.
     */
    public static final String FIELD_DESIGN = "design";

    /**
     * Название поля, хранящего версию коннектора PCI-Express.
     */
    public static final String FIELD_PCI_EXPRESS_CONNECTOR_VERSION = "pciExpressConnectorVersion";

    /**
     * Название поля, хранящего коннекторы питания.
     */
    public static final String FIELD_POWER_CONNECTORS = "powerConnectors";

    /**
     * Длина (мм).
     */
    @Min(40)
    @NotNull
    @Column(name = "length", nullable = false)
    protected Integer length;

    /**
     * Графический процессор.
     */
    @ManyToOne
    @JoinColumn(name = "gpu_id", referencedColumnName = "id", nullable = false)
    protected Gpu gpu;

    /**
     * Вариант исполнения.
     */
    @ManyToOne
    @JoinColumn(name = "design_id", referencedColumnName = "id", nullable = false)
    protected Design design;

    /**
     * Версия коннектора PCI-Express.
     */
    @ManyToOne
    @JoinColumn(name = "pci_express_connector_version_id", referencedColumnName = "id", nullable = false)
    protected PciExpressConnectorVersion pciExpressConnectorVersion;

    /**
     * Коннекторы питания.
     */
    @NotNull
    @Size(min = 1)
    @OneToMany(
            mappedBy = "graphicsCard",
            cascade = CascadeType.ALL
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected Set<GraphicsCardToPowerConnector> powerConnectors = new HashSet<>();

    /**
     * Добавляет коннектор питания видеокарты в указанном количестве.
     *
     * @param connector коннектор питания видеокарты
     * @param count          количество
     */
    public void addPowerConnector(final GraphicsCardPowerConnector connector, final Integer count) {
        final var cardToConnector = new GraphicsCardToPowerConnector();

        cardToConnector.setGraphicsCard(this);
        cardToConnector.setPowerConnector(connector);
        cardToConnector.setCount(count);

        this.powerConnectors.add(cardToConnector);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GraphicsCard that = (GraphicsCard) o;

        if (!Objects.equals(gpu, that.gpu)) return false;
        return Objects.equals(design, that.design);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (gpu != null ? gpu.hashCode() : 0);
        result = 31 * result + (design != null ? design.hashCode() : 0);
        return result;
    }
}
