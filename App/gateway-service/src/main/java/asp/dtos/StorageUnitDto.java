// asp/dto/StorageUnitDto.java
package asp.dtos;

import java.util.List;

public class StorageUnitDto {
    private Integer id;
    private String name;
    private Integer parent;
    private List<StorageUnitDto> subUnits;
    private List<InventoryItemDto> items;

    public StorageUnitDto() {}

    public StorageUnitDto(Integer id, String name, Integer parent,
                          List<StorageUnitDto> subUnits,
                          List<InventoryItemDto> items) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.subUnits = subUnits;
        this.items = items;
    }

    // getters & setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getparent() { return parent; }
    public void setparent(Integer parent) { this.parent = parent; }

    public List<StorageUnitDto> getSubUnits() { return subUnits; }
    public void setSubUnits(List<StorageUnitDto> subUnits) { this.subUnits = subUnits; }

    public List<InventoryItemDto> getItems() { return items; }
    public void setItems(List<InventoryItemDto> items) { this.items = items; }
}
