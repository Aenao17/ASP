package asp.service;

import asp.model.InventoryItem;
import asp.model.StorageUnit;
import asp.repository.InventoryItemRepository;
import asp.repository.StorageUnitRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {
    @Autowired
    private InventoryItemRepository itemRepo;
    @Autowired
    private StorageUnitRepository suRepo;

    public StorageUnit createUnit(String name, Integer parentId) {
        StorageUnit unit = new StorageUnit();
        unit.setName(name);
        if (parentId != null) {
            StorageUnit parent = suRepo.findById(parentId)
                    .orElseThrow(() -> new EntityNotFoundException("Parent unit not found"));
            parent.addSubUnit(unit);
            return suRepo.save(parent);
        }
        return suRepo.save(unit);
    }

    public void deleteUnit(Integer unitId) {
        StorageUnit unit = suRepo.findById(unitId)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found"));
        if (unit.getParent() != null) {
            StorageUnit parent = unit.getParent();
            parent.getSubUnits().remove(unit);
            suRepo.save(parent);
        }
        suRepo.delete(unit);
    }

    public InventoryItem addItem(Integer unitId, String itemName, int qty) {
        StorageUnit su = suRepo.findById(unitId)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found"));
        InventoryItem item = new InventoryItem();
        item.setName(itemName);
        item.setQuantity(qty);
        su.addItem(item);
        suRepo.save(su);
        return item;
    }

    public InventoryItem updateQuantity(Integer itemId, int newQty) {
        InventoryItem it = itemRepo.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        it.setQuantity(newQty);
        return itemRepo.save(it);
    }

    public List<InventoryItem> searchItems(String term) {
        return itemRepo.findByNameContainingIgnoreCase(term);
    }

    public StorageUnit getUnitById(Integer unitId) {
        return suRepo.findById(unitId)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found"));
    }

    public List<InventoryItem> getAllItems() {
        return itemRepo.findAll();
    }

    public List<StorageUnit> getAllUnits() {
        return suRepo.findAll();
    }

    public StorageUnit getRoot(){
        //return the object without a parent
        return suRepo.findByParentIsNull()
                .orElseThrow(() -> new EntityNotFoundException("Root unit not found"));
    }

    public List<InventoryItem> getAllItemsFromUnit(Integer unitId) {
        StorageUnit su = suRepo.findById(unitId)
                .orElseThrow(() -> new EntityNotFoundException("Unit not found"));
        return su.getItems();
    }
}
