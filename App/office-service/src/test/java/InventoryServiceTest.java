import asp.model.InventoryItem;
import asp.model.StorageUnit;
import asp.repository.InventoryItemRepository;
import asp.repository.StorageUnitRepository;
import asp.service.InventoryService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    @Mock
    private InventoryItemRepository itemRepo;

    @Mock
    private StorageUnitRepository suRepo;

    @InjectMocks
    private InventoryService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUnitWithoutParent_shouldCreateAndSaveUnit() {
        StorageUnit savedUnit = new StorageUnit();
        savedUnit.setId(1);
        savedUnit.setName("Root");

        when(suRepo.save(any(StorageUnit.class))).thenReturn(savedUnit);

        StorageUnit result = service.createUnit("Root", null);

        assertNotNull(result);
        assertEquals("Root", result.getName());
        verify(suRepo).save(any(StorageUnit.class));
    }

    @Test
    void createUnitWithParent_shouldAddToParent() {
        StorageUnit parent = new StorageUnit();
        parent.setId(1);
        parent.setName("Parent");
        parent.setSubUnits(new ArrayList<>());

        when(suRepo.findById(1)).thenReturn(Optional.of(parent));
        when(suRepo.save(any(StorageUnit.class))).thenReturn(parent);

        StorageUnit result = service.createUnit("Child", 1);

        assertEquals(1, parent.getSubUnits().size());
        assertEquals("Child", parent.getSubUnits().get(0).getName());
    }

    @Test
    void deleteUnit_shouldRemoveFromParentAndDelete() {
        StorageUnit parent = new StorageUnit();
        StorageUnit child = new StorageUnit();
        parent.setSubUnits(new ArrayList<>(List.of(child)));
        child.setParent(parent);
        child.setId(2);

        when(suRepo.findById(2)).thenReturn(Optional.of(child));

        service.deleteUnit(2);

        assertFalse(parent.getSubUnits().contains(child));
        verify(suRepo).delete(child);
    }

    @Test
    void addItem_shouldAddItemToStorageUnit() {
        StorageUnit unit = new StorageUnit();
        unit.setId(1);
        unit.setItems(new ArrayList<>());

        when(suRepo.findById(1)).thenReturn(Optional.of(unit));
        when(suRepo.save(any(StorageUnit.class))).thenReturn(unit);

        InventoryItem item = service.addItem(1, "Mouse", 3);

        assertEquals("Mouse", item.getName());
        assertEquals(3, item.getQuantity());
        assertTrue(unit.getItems().contains(item));
    }

    @Test
    void updateStorageName_shouldUpdateAndReturnUnit() {
        StorageUnit unit = new StorageUnit();
        unit.setId(1);
        unit.setName("Old");

        when(suRepo.findById(1)).thenReturn(Optional.of(unit));
        when(suRepo.save(unit)).thenReturn(unit);

        StorageUnit updated = service.updateStorageName(1, "New");

        assertEquals("New", updated.getName());
    }

    @Test
    void renameStorageUnit_shouldUpdateName() {
        StorageUnit unit = new StorageUnit();
        unit.setId(1);
        unit.setName("Old");

        when(suRepo.findById(1)).thenReturn(Optional.of(unit));

        service.renameStorageUnit(1, "Renamed");

        assertEquals("Renamed", unit.getName());
        verify(suRepo).save(unit);
    }

    @Test
    void updateQuantity_shouldUpdateAndReturnItem() {
        InventoryItem item = new InventoryItem();
        item.setId(1);
        item.setQuantity(5);

        when(itemRepo.findById(1)).thenReturn(Optional.of(item));
        when(itemRepo.save(item)).thenReturn(item);

        InventoryItem updated = service.updateQuantity(1, 10);

        assertEquals(10, updated.getQuantity());
    }

    @Test
    void searchItems_shouldReturnResults() {
        List<InventoryItem> items = List.of(new InventoryItem(), new InventoryItem());
        when(itemRepo.findByNameContainingIgnoreCase("pen")).thenReturn(items);

        List<InventoryItem> result = service.searchItems("pen");

        assertEquals(2, result.size());
    }

    @Test
    void getUnitById_shouldReturnUnit() {
        StorageUnit unit = new StorageUnit();
        unit.setId(1);

        when(suRepo.findById(1)).thenReturn(Optional.of(unit));

        StorageUnit result = service.getUnitById(1);

        assertEquals(1, result.getId());
    }

    @Test
    void getAllItems_shouldReturnAll() {
        List<InventoryItem> items = List.of(new InventoryItem());
        when(itemRepo.findAll()).thenReturn(items);

        assertEquals(1, service.getAllItems().size());
    }

    @Test
    void getAllUnits_shouldReturnAll() {
        List<StorageUnit> units = List.of(new StorageUnit());
        when(suRepo.findAll()).thenReturn(units);

        assertEquals(1, service.getAllUnits().size());
    }

    @Test
    void getRoot_shouldReturnUnitWithoutParent() {
        StorageUnit root = new StorageUnit();
        when(suRepo.findByParentIsNull()).thenReturn(Optional.of(root));

        StorageUnit result = service.getRoot();

        assertEquals(root, result);
    }

    @Test
    void getAllItemsFromUnit_shouldReturnItems() {
        InventoryItem item = new InventoryItem();
        StorageUnit unit = new StorageUnit();
        unit.setItems(new ArrayList<>(List.of(item)));

        when(suRepo.findById(1)).thenReturn(Optional.of(unit));

        List<InventoryItem> result = service.getAllItemsFromUnit(1);

        assertEquals(1, result.size());
        assertEquals(item, result.get(0));
    }

    @Test
    void deleteItem_shouldRemoveFromParentAndDelete() {
        StorageUnit unit = new StorageUnit();
        InventoryItem item = new InventoryItem();
        item.setId(1);
        item.setName("Box");
        unit.setItems(new ArrayList<>(List.of(item)));
        item.setParent(unit);

        when(itemRepo.findById(1)).thenReturn(Optional.of(item));

        service.deleteItem(1);

        assertFalse(unit.getItems().contains(item));
        verify(itemRepo).delete(item);
    }
}
