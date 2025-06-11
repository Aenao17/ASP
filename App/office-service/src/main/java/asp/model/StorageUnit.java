package asp.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "storage_unit")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class StorageUnit implements Component {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Integer id;

    @Setter
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @Getter @Setter
    private StorageUnit parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    private List<StorageUnit> subUnits = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    private List<InventoryItem> items = new ArrayList<>();

    public StorageUnit() { }

    public StorageUnit(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Integer getId() { return id; }

    @Override
    public String getName() { return name; }

    public List<Component> getChildren() {
        List<Component> all = new ArrayList<>();
        all.addAll(subUnits);
        all.addAll(items);
        return all;
    }

    public void addSubUnit(StorageUnit u) {
        u.setParent(this);
        subUnits.add(u);
    }

    public void addItem(InventoryItem i) {
        i.setParent(this);
        items.add(i);
    }
}