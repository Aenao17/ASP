package asp.model;


import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

public class Item {
    private String id;
    private String name;
    private String description;
    private int quantity;
    private String storageId;
    private String ownerId;
    private LocalDateTime lastUpdated;

    @Document(collection="items")
    public Item(String id, String name, String description, int quantity, String storageId, String ownerId, LocalDateTime lastUpdated) {
        @Id
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.storageId = storageId;
        this.ownerId = ownerId;
        this.lastUpdated = lastUpdated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStorageId() {
        return storageId;
    }

    public void setStorageId(String storageId) {
        this.storageId = storageId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
