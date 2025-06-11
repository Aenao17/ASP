package asp.repository;

import asp.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryItemRepository extends JpaRepository<InventoryItem,Integer> {
    List<InventoryItem> findByNameContainingIgnoreCase(String name);
}
