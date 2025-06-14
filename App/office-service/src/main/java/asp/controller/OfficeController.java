package asp.controller;

import asp.model.InventoryItem;
import asp.model.StorageUnit;
import asp.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/office")
public class OfficeController {
    @Autowired
    private InventoryService inventoryService;
    // DTO pentru creare unitate
    public record CreateUnitDto(String name, Long parentId) { }

    // DTO pentru item
    public record ItemDto(String name, Integer quantity) { }

    @PostMapping("/storage-unit")
    public ResponseEntity<StorageUnit> createUnit(@RequestBody CreateUnitDto dto) {
        return ResponseEntity.ok(
                inventoryService.createUnit(dto.name(), dto.parentId() != null ? dto.parentId().intValue() : null)
        );
    }

    @PostMapping("/storage-unit/{id}/item")
    public ResponseEntity<?> addItem(@PathVariable Integer id, @RequestBody ItemDto dto) {
        return ResponseEntity.ok(
                inventoryService.addItem(id, dto.name(), dto.quantity())
        );
    }

    @DeleteMapping("/storage-unit/{id}")
    public ResponseEntity<?> deleteUnit(@PathVariable Integer id) {
        inventoryService.deleteUnit(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/item/{id}/quantity")
    public ResponseEntity<?> updateItemQuantity(@PathVariable Integer id, @RequestBody Integer newQty) {
        return ResponseEntity.ok(
                inventoryService.updateQuantity(id, newQty)
        );
    }

    @PutMapping("/storage-unit/{id}/name")
    public ResponseEntity<StorageUnit> updateUnitName(@PathVariable Integer id, @RequestBody String newName) {
        return ResponseEntity.ok(inventoryService.updateStorageName(id, newName));
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<List<InventoryItem>> getUnit(@PathVariable Integer id) {
        List<InventoryItem> items = inventoryService.getAllItemsFromUnit(id);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/storage-unit/{id}")
    public ResponseEntity<StorageUnit> getUnitById(@PathVariable Integer id) {
        StorageUnit unit = inventoryService.getUnitById(id);
        return ResponseEntity.ok(unit);
    }

    @GetMapping("/root")
    public ResponseEntity<StorageUnit> getRootUnit() {
        StorageUnit root = inventoryService.getRoot();
        return ResponseEntity.ok(root);
    }

    @DeleteMapping("/item/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Integer id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("items/search/{name}")
    public ResponseEntity<List<InventoryItem>> searchItems(@PathVariable String name) {
        List<InventoryItem> items = inventoryService.searchItems(name);
        return ResponseEntity.ok(items);
    }

}
