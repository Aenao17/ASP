// asp/dto/InventoryItemDto.java
package asp.dtos;

public class InventoryItemDto {
    private Integer id;
    private String name;
    private Integer quantity;
    private Integer parentId;

    public InventoryItemDto() {}

    public InventoryItemDto(Integer id, String name, Integer quantity, Integer parentId) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.parentId = parentId;
    }

    // getters & setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }
}
