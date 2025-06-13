// asp/dto/InventoryItemDto.java
package asp.dtos;

public class InventoryItemDto {
    private Integer id;
    private String name;
    private Integer quantity;
    private Integer parent;

    public InventoryItemDto() {}

    public InventoryItemDto(Integer id, String name, Integer quantity, Integer parent) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.parent = parent;
    }

    // getters & setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getparent() { return parent; }
    public void setparent(Integer parent) { this.parent = parent; }
}
