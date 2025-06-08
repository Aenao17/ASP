package asp.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "storages")
public class StorageEntity {
    @Setter
    @Getter
    @Id
    private String id;
    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private String description;
    @Setter
    @Getter
    private List<Item> items;
    private List<StorageEntity> subStorageEntities;

    public StorageEntity(String id, String name, String description, List<Item> items, List<StorageEntity> subStorageEntities) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.items = items;
        this.subStorageEntities = subStorageEntities;
    }

    public List<StorageEntity> getsubStorageEntities() {
        return subStorageEntities;
    }

    public void setsubStorageEntities(List<StorageEntity> subStorageEntities) {
        this.subStorageEntities = subStorageEntities;
    }
}
